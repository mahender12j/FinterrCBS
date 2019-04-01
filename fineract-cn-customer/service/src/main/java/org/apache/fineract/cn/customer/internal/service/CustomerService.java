/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.customer.internal.service;

import org.apache.fineract.cn.accounting.api.v1.domain.Account;
import org.apache.fineract.cn.accounting.api.v1.domain.AccountEntry;
import org.apache.fineract.cn.customer.api.v1.domain.*;
import org.apache.fineract.cn.customer.catalog.internal.repository.FieldValueRepository;
import org.apache.fineract.cn.customer.internal.mapper.*;
import org.apache.fineract.cn.customer.internal.repository.*;
import org.apache.fineract.cn.customer.internal.service.helperService.AccountingAdaptor;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final IdentificationCardRepository identificationCardRepository;
    private final IdentificationCardScanRepository identificationCardScanRepository;
    private final PortraitRepository portraitRepository;
    private final ContactDetailRepository contactDetailRepository;
    private final FieldValueRepository fieldValueRepository;
    private final AccountingAdaptor accountingAdaptor;
    private final CommandRepository commandRepository;
    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskInstanceRepository taskInstanceRepository;

    @Autowired
    public CustomerService(final CustomerRepository customerRepository,
                           final IdentificationCardRepository identificationCardRepository,
                           final IdentificationCardScanRepository identificationCardScanRepository,
                           final PortraitRepository portraitRepository,
                           final ContactDetailRepository contactDetailRepository,
                           final FieldValueRepository fieldValueRepository,
                           final AccountingAdaptor accountingAdaptor,
                           final CommandRepository commandRepository,
                           final TaskDefinitionRepository taskDefinitionRepository,
                           final TaskInstanceRepository taskInstanceRepository
    ) {
        super();
        this.customerRepository = customerRepository;
        this.identificationCardRepository = identificationCardRepository;
        this.identificationCardScanRepository = identificationCardScanRepository;
        this.portraitRepository = portraitRepository;
        this.contactDetailRepository = contactDetailRepository;
        this.fieldValueRepository = fieldValueRepository;
        this.commandRepository = commandRepository;
        this.accountingAdaptor = accountingAdaptor;
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskInstanceRepository = taskInstanceRepository;
    }

    public Boolean customerExists(final String identifier) {
        return this.customerRepository.existsByIdentifier(identifier);
    }

    public Boolean identificationCardExists(final String number) {
        return this.identificationCardRepository.existsByNumber(number);
    }

    public Boolean identificationCardScanExists(final String number, final String identifier) {
        return this.identificationCardRepository.findByNumber(number)
                .map(cardEntity -> this.identificationCardScanRepository.existsByIdentifierAndIdentificationCard(identifier, cardEntity))
                .orElse(false);
    }

    public Optional<Customer> findCustomer(final String identifier) {
        Optional<CustomerEntity> customerEntity = customerRepository.findByIdentifier(identifier);
        if (customerEntity.isPresent()) {
            return customerEntity.map(entity -> {
                final Customer customer = CustomerMapper.map(entity);
                customer.setAddress(AddressMapper.map(entity.getAddress()));
                SocialMatrix socialMatrix = new SocialMatrix();
                String accountNumber = customerEntity.get().getAccountNumbers();

                if (accountNumber != null) {
                    List<AccountEntry> accountEntryList = accountingAdaptor.fetchAccountEntries(accountNumber);
                    final LocalDateTime localDateTime = LocalDateTime.now();
                    Double totalBCDP = accountEntryList.stream().filter(d -> d.getTransactionType().equals("BCDP") && d.getType().equals("CREDIT"))
                            .filter(d -> Integer.parseInt(d.getTransactionDate().substring(0, 4)) == localDateTime.getYear() &&
                                    Integer.parseInt(d.getTransactionDate().substring(5, 7)) == localDateTime.getMonth().getValue())
                            .mapToDouble(d -> d.getAmount()).sum();


                    Double totalCHRP = accountEntryList.stream().filter(d -> d.getTransactionType().equals("CHRP") && d.getType().equals("DEBIT"))
                            .filter(d -> Integer.parseInt(d.getTransactionDate().substring(0, 4)) == localDateTime.getYear() &&
                                    Integer.parseInt(d.getTransactionDate().substring(5, 7)) == localDateTime.getMonth().getValue())
                            .mapToDouble(d -> d.getAmount()).sum();

                    socialMatrix.setMyPower((totalBCDP / 20 > 5) ? 5 : (totalBCDP / 20));
                    socialMatrix.setMyPowerPercentage(socialMatrix.getMyPower() * 20);


                    socialMatrix.setTotalTrees((int) Math.floor(totalCHRP / 200));
                    socialMatrix.setGoldenDonor((totalCHRP / 10) > 5 ? 5 : totalCHRP / 10);
                    socialMatrix.setGreenContribution((totalCHRP / 40) > 5 ? 5 : totalCHRP / 40);


                } else {
                    socialMatrix.setMyPower(0.0);
                    socialMatrix.setMyPowerPercentage(0.0);
                    socialMatrix.setTotalTrees(0);
                    socialMatrix.setGoldenDonor(0.0);
                    socialMatrix.setGreenContribution(0.0);
                }


                socialMatrix.setGoldenDonorPercentage(socialMatrix.getGoldenDonor() * 20);
                socialMatrix.setMyInfluence(customerRepository.findAllByRefferalCodeIdentifier(customer.getRefferalCodeIdentifier()));
                customer.setSocialMatrix(socialMatrix);

                System.out.println("----------------------social matrix---------------------" + socialMatrix.toString());

                if (customer.getRefAccountNumber() != null) {
                    Account account = accountingAdaptor.findAccountByIdentifier(customer.getRefAccountNumber());
                    customer.setRefferalBalance(account.getBalance());
                }
                final List<ContactDetailEntity> contactDetailEntities = this.contactDetailRepository.findByCustomer(entity);
                if (contactDetailEntities != null) {
                    customer.setContactDetails(contactDetailEntities.stream().map(ContactDetailMapper::map).collect(Collectors.toList()));
                }
                return customer;
            });

        } else {
            throw ServiceException.notFound("Customer with identifier {0} not found in this system", identifier);
        }
    }

    public CustomerPage fetchCustomer(final String term, final Boolean includeClosed, final Pageable pageable) {
        final Page<CustomerEntity> customerEntities;
        if (includeClosed) {
            if (term != null) {
                customerEntities =
                        this.customerRepository.findByIdentifierContainingOrGivenNameContainingOrSurnameContaining(term, term, term, pageable);
            } else {
                customerEntities = this.customerRepository.findAll(pageable);
            }
        } else {
            if (term != null) {
                customerEntities =
                        this.customerRepository.findByCurrentStateNotAndIdentifierContainingOrGivenNameContainingOrSurnameContaining(
                                Customer.State.CLOSED.name(), term, term, term, pageable);
            } else {
                customerEntities = this.customerRepository.findByCurrentStateNot(Customer.State.CLOSED.name(), pageable);
            }
        }

        final CustomerPage customerPage = new CustomerPage();
        customerPage.setTotalPages(customerEntities.getTotalPages());
        customerPage.setTotalElements(customerEntities.getTotalElements());
        if (customerEntities.getSize() > 0) {
            final ArrayList<Customer> customers = new ArrayList<>(customerEntities.getSize());
            customerPage.setCustomers(customers);
            customerEntities.forEach(customerEntity -> customers.add(CustomerMapper.map(customerEntity)));
        }

        return customerPage;
    }

    public CustomerRefPage fetchCustomerReferrals(final String refferalcode, final String term, final Boolean includeClosed, final Pageable pageable) {
        final Page<CustomerEntity> customerEntities;
        Optional<CustomerEntity> customerEntity = customerRepository.findByRefferalCodeIdentifier(refferalcode);
        Customer customer = CustomerMapper.map(customerEntity.get());
        if (includeClosed) {
            if (term != null) {
                customerEntities =
                        this.customerRepository.findByReferenceCustomerAndIdentifierContainingOrGivenNameContainingOrSurnameContaining(refferalcode, term, term, term, pageable);
            } else {
                customerEntities = this.customerRepository.findAllByReferenceCustomer(refferalcode, pageable);
            }
        } else {
            if (term != null) {
                customerEntities =
                        this.customerRepository.findByReferenceCustomerAndCurrentStateNotAndIdentifierContainingOrGivenNameContainingOrSurnameContaining(
                                refferalcode, Customer.State.CLOSED.name(), term, term, term, pageable);
            } else {
                customerEntities = this.customerRepository.findByReferenceCustomerAndCurrentStateNot(refferalcode, Customer.State.CLOSED.name(), pageable);
            }
        }

        final CustomerRefPage customerRefPage = new CustomerRefPage();
        customerRefPage.setTotalPages(customerEntities.getTotalPages());
        customerRefPage.setTotalElements(customerEntities.getTotalElements());
        customerRefPage.setRefAccountNumber(customer.getRefAccountNumber());

        if (customer.getRefAccountNumber() != null) {
            Account account = accountingAdaptor.findAccountByIdentifier(customer.getRefAccountNumber());
            customerRefPage.setRefferalBalance(account.getBalance());
            if (customerEntities.getSize() > 0) {
                final ArrayList<Customer> customers = new ArrayList<>(customerEntities.getSize());
                customerEntities.forEach(entity -> {
                    Customer tCustomer = CustomerMapper.map(entity);
                    if (entity.getAccountNumbers() != null) {
                        Account acc = accountingAdaptor.findAccountByIdentifier(entity.getRefAccountNumber());
                        tCustomer.setRefferalBalance(acc.getBalance());
                    }
                    customers.add(tCustomer);
                });
                customerRefPage.setCustomers(customers);
            }
        }
        return customerRefPage;
    }

    public Optional<Customer> fetchCustomerByReferralcode(final String refferalCode) {

        return customerRepository.findByRefferalCodeIdentifier(refferalCode)
                .map(customerEntity -> {
                    final Customer customer = CustomerMapper.map(customerEntity);
                    customer.setAddress(AddressMapper.map(customerEntity.getAddress()));

                    final List<ContactDetailEntity> contactDetailEntities = this.contactDetailRepository.findByCustomer(customerEntity);
                    if (contactDetailEntities != null) {
                        customer.setContactDetails(
                                contactDetailEntities
                                        .stream()
                                        .map(ContactDetailMapper::map)
                                        .collect(Collectors.toList())
                        );
                    }

                    return customer;
                });
    }

    public Optional<Customer> findNgo(final String identifier) {
        Optional<CustomerEntity> customerEntity = customerRepository.findByIdentifierAndAType(identifier, "BUSINESS");
        if (customerEntity.isPresent()) {
            return customerEntity.map(entity -> {
                final Customer customerNgo = CustomerMapper.map(entity);
                
                // final List<ContactDetailEntity> contactDetailEntities = this.contactDetailRepository.findByCustomer(entity);
                // if (contactDetailEntities != null) {
                //     customer.setContactDetails(contactDetailEntities.stream().map(ContactDetailMapper::map).collect(Collectors.toList()));
                // }
                return customerNgo;
            });

        } else {
            throw ServiceException.notFound("Oops! We cant find you...");
        }
    }

    public final Stream<Command> fetchCommandsByCustomer(final String identifier) {
        return customerRepository.findByIdentifier(identifier)
                .map(commandRepository::findByCustomer)
                .orElse(Stream.empty())
                .map(CommandMapper::map);
    }

    public final Optional<PortraitEntity> findPortrait(final String identifier) {
        return customerRepository.findByIdentifier(identifier)
                .map(portraitRepository::findByCustomer);
    }

    public final Stream<IdentificationCard> fetchIdentificationCardsByCustomer(final String identifier) {
        return customerRepository.findByIdentifier(identifier)
                .map(identificationCardRepository::findByCustomer)
                .orElse(Stream.empty())
                .map(IdentificationCardMapper::map);
    }

    public Optional<IdentificationCard> findIdentificationCard(final String number) {
        final Optional<IdentificationCardEntity> identificationCardEntity = this.identificationCardRepository.findByNumber(number);

        return identificationCardEntity.map(IdentificationCardMapper::map);
    }

    public final List<IdentificationCardScan> fetchScansByIdentificationCard(final String number) {
        final Optional<IdentificationCardEntity> identificationCard = this.identificationCardRepository.findByNumber(number);

        return identificationCard.map(this.identificationCardScanRepository::findByIdentificationCard)
                .map(x -> x.stream().map(IdentificationCardScanMapper::map).collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
    }

    private Optional<IdentificationCardScanEntity> findIdentificationCardEntity(final String number, final String identifier) {
        final Optional<IdentificationCardEntity> cardEntity = this.identificationCardRepository.findByNumber(number);
        return cardEntity.flatMap(card -> this.identificationCardScanRepository.findByIdentifierAndIdentificationCard(identifier, card));
    }

    public Optional<IdentificationCardScan> findIdentificationCardScan(final String number, final String identifier) {
        return this.findIdentificationCardEntity(number, identifier).map(IdentificationCardScanMapper::map);
    }

    public Optional<byte[]> findIdentificationCardScanImage(final String number, final String identifier) {
        return this.findIdentificationCardEntity(number, identifier).map(IdentificationCardScanEntity::getImage);
    }

    public List<ProcessStep> getProcessSteps(final String customerIdentifier) {
        return customerRepository.findByIdentifier(customerIdentifier)
                .map(customerEntity -> {
                    final List<ProcessStep> processSteps = new ArrayList<>();

                    final Customer.State state = Customer.State.valueOf(customerEntity.getCurrentState());
                    switch (state) {
                        case PENDING:
                            processSteps.add(this.buildProcessStep(customerEntity, Command.Action.ACTIVATE));
                            processSteps.add(this.buildProcessStep(customerEntity, Command.Action.CLOSE));
                            break;
                        case ACTIVE:
                            processSteps.add(this.buildProcessStep(customerEntity, Command.Action.LOCK));
                            processSteps.add(this.buildProcessStep(customerEntity, Command.Action.CLOSE));
                            break;
                        case LOCKED:
                            processSteps.add(this.buildProcessStep(customerEntity, Command.Action.UNLOCK));
                            processSteps.add(this.buildProcessStep(customerEntity, Command.Action.CLOSE));
                            break;
                        case CLOSED:
                            processSteps.add(this.buildProcessStep(customerEntity, Command.Action.REOPEN));
                            break;
                    }

                    return processSteps;
                })
                .orElse(Collections.emptyList());
    }

    private ProcessStep buildProcessStep(final CustomerEntity customerEntity, final Command.Action action) {
        final ProcessStep processStep = new ProcessStep();

        final Command command = new Command();
        command.setAction(action.name());
        processStep.setCommand(command);

        final ArrayList<TaskDefinition> taskDefinitions = new ArrayList<>();
        this.taskDefinitionRepository.findByAssignedCommandsContaining(action.name())
                .forEach(taskDefinitionEntity ->
                        this.taskInstanceRepository.findByCustomerAndTaskDefinition(customerEntity, taskDefinitionEntity)
                                .forEach(taskInstanceEntity -> {
                                    if (taskInstanceEntity.getExecutedBy() == null) {
                                        taskDefinitions.add(TaskDefinitionMapper.map(taskDefinitionEntity));
                                    }
                                }));
        processStep.setTaskDefinitions(taskDefinitions);

        return processStep;
    }
}

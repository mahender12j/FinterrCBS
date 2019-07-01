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
import org.apache.fineract.cn.customer.internal.mapper.*;
import org.apache.fineract.cn.customer.internal.repository.*;
import org.apache.fineract.cn.customer.internal.service.helperService.AccountingAdaptor;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final IdentificationCardRepository identificationCardRepository;
    private final IdentificationCardScanRepository identificationCardScanRepository;
    private final PortraitRepository portraitRepository;
    private final ContactDetailRepository contactDetailRepository;
    private final AccountingAdaptor accountingAdaptor;
    private final CommandRepository commandRepository;
    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskInstanceRepository taskInstanceRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentSubTypeRepository documentSubTypeRepository;

    @Autowired
    public CustomerService(final CustomerRepository customerRepository,
                           final IdentificationCardRepository identificationCardRepository,
                           final IdentificationCardScanRepository identificationCardScanRepository,
                           final PortraitRepository portraitRepository,
                           final ContactDetailRepository contactDetailRepository,
                           final AccountingAdaptor accountingAdaptor,
                           final CommandRepository commandRepository,
                           final TaskDefinitionRepository taskDefinitionRepository,
                           final TaskInstanceRepository taskInstanceRepository,
                           final DocumentTypeRepository documentTypeRepository,
                           final DocumentSubTypeRepository documentSubTypeRepository) {
        super();
        this.customerRepository = customerRepository;
        this.identificationCardRepository = identificationCardRepository;
        this.identificationCardScanRepository = identificationCardScanRepository;
        this.portraitRepository = portraitRepository;
        this.contactDetailRepository = contactDetailRepository;
        this.commandRepository = commandRepository;
        this.accountingAdaptor = accountingAdaptor;
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskInstanceRepository = taskInstanceRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.documentSubTypeRepository = documentSubTypeRepository;
    }


    public HashMap<String, String> fetchBankList(PaynetDetails paynetDetails) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        HashMap<String, String> respMap = new HashMap<>();
//        default value
//        String final_checkSum = "566DACEE09ADFA8D65733CC05E7599964556E8FE1E7396A84717CAEA79DEC96022C226593B35B1E4EF441A8052C636861E1DC298CB3BA3C5FA1F6F7D409AE01DB0A9BBD26EA27F6DC98BFFE1758C1746922C6A9A8BA18120C15B4B8C05F994767A715C834C09B313895AEDB25E8CBA36B5CB7A82CB5496BA1857F4AB0BAEDD3E5239B5B5441729A683199B90C7AD9B537AD9DBE9168EDA1D1E82ECC0F111BA33DD4A6FB097FDA38DB80CFBF9FB8B7773E062C11545F6C7B94FBAC3707AF72297D11DF4A21C5E70C07F242ADA8F597F0C3BC16C14D840A0010B46BE96F8B5BA6CDAF21B9514B71D332B3543B19DBDDF6DCAF8A4EBE31A0445F9AD4A0C5C9BDC60";
//        String fpx_msgType = "BE";
//        String fpx_msgToken = "01";
//        String fpx_sellerExId = "EX00009694";
//        String fpx_version = "7.0";

        StringBuilder postDataStrBuilder = new StringBuilder();
        postDataStrBuilder.append("fpx_msgType=").append(URLEncoder.encode(paynetDetails.getFpx_msgType(), "UTF-8"));
        postDataStrBuilder.append("&fpx_msgToken=").append(URLEncoder.encode(paynetDetails.getFpx_msgToken(), "UTF-8"));
        postDataStrBuilder.append("&fpx_sellerExId=").append(URLEncoder.encode(paynetDetails.getFpx_sellerExId(), "UTF-8"));
        postDataStrBuilder.append("&fpx_version=").append(URLEncoder.encode(paynetDetails.getFpx_version(), "UTF-8"));
        postDataStrBuilder.append("&fpx_checkSum=").append(URLEncoder.encode(paynetDetails.getFinal_checkSum(), "UTF-8"));

//Create a trust manager that does not validate certificate chains only for testing environment
        TrustManager[] trustAllCerts = new TrustManager[]
                {
                        new X509TrustManager() {
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                            public void checkClientTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }

                            public void checkServerTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }

                            public boolean isServerTrusted(java.security.cert.X509Certificate[] chain) {
                                return true;
                            }

                            public boolean isClientTrusted(java.security.cert.X509Certificate[] chain) {
                                return true;
                            }
                        }
                };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }

            public boolean verify(String hostname, String session) {
                return true;
            }
        });

        URLConnection conn = (HttpsURLConnection) new URL("https://uat.mepsfpx.com.my/FPXMain/RetrieveBankList").openConnection();

        conn.setDoOutput(true);
        BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        outputWriter.write(postDataStrBuilder.toString(), 0, postDataStrBuilder.toString().length());

        outputWriter.flush();
        outputWriter.close();
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String strResponse = inputReader.readLine();
        inputReader.close();
        strResponse = strResponse.trim();
        if (strResponse.equals("ERROR")) {
            System.out.println("An error occured!..Response[" + strResponse + "]");
            return respMap;
        } else {
            StringTokenizer strToknzr = new StringTokenizer(strResponse, "&");
            while (strToknzr.hasMoreElements()) {
                String temp = strToknzr.nextToken();
                if (temp.contains("=")) {
                    String nvp[] = temp.split("=");
                    String name = nvp[0];
                    String value = "";
                    if (nvp.length == 2)
                        value = URLDecoder.decode(nvp[1], "UTF-8");
                    respMap.put(name, value);
                } else {
                    System.out.println("Parsing Error!" + temp);
                }
            }
            return respMap;
        }

    }


    public Boolean customerExists(final String identifier) {
        return this.customerRepository.existsByIdentifier(identifier);
    }

    public Boolean ngoExist(final String identifier) {
        return this.customerRepository.existsNGOByIdentifier(identifier);
    }

    public Boolean masterTypeExists(final String uuid) {
        return this.documentTypeRepository.existsByIdentifier(uuid);
    }

    public Boolean masterSubTypeExists(final String uuid) {
        return this.documentSubTypeRepository.existsByIdentifier(uuid);
    }


    public Boolean identificationCardExists(final String number) {
        return this.identificationCardRepository.existsByNumber(number);
    }

    public Boolean identificationCardScanExists(final String number, final String identifier) {
        return this.identificationCardRepository.findByNumber(number)
                .map(cardEntity -> this.identificationCardScanRepository.existsByIdentifierAndIdentificationCard(identifier, cardEntity))
                .orElse(false);
    }

    public CustomerEntity getCustomerEntity(final String customerIdentifier) {
        return this.customerRepository.findByIdentifier(customerIdentifier).orElseThrow(() -> ServiceException.notFound("Customer not found"));

    }

    public Optional<Customer> findCustomer(final String identifier) {
        CustomerEntity customerEntity = customerRepository.findByIdentifier(identifier).orElseThrow(() -> ServiceException.notFound("Customer with identifier {0} not found in this system", identifier));

        Customer customer = CustomerMapper.map(customerEntity);
        customer.setAddress(AddressMapper.map(customerEntity.getAddress()));

        if (customerEntity.getReferenceCustomer() != null) {
            Optional<CustomerEntity> reffCustomer = this.customerRepository.findByRefferalCodeIdentifier(customerEntity.getReferenceCustomer());
            reffCustomer.ifPresent(reff -> customer.setRefferalUserIdentifier(reff.getIdentifier()));
        }

        customer.setSocialMatrix(getSocialMatrix(customerEntity));
        if (customer.getRefAccountNumber() != null) {
            Account account = accountingAdaptor.findAccountByIdentifier(customer.getRefAccountNumber());
            customer.setRefferalBalance(account.getBalance());
        }


        setCustomerContactDetails(customerEntity, customer);
        return Optional.of(customer);

    }

    private void setCustomerContactDetails(CustomerEntity customerEntity, Customer customer) {
        final List<ContactDetailEntity> contactDetailEntities = this.contactDetailRepository.findByCustomer(customerEntity);
        if (contactDetailEntities != null) {
            customer.setContactDetails(contactDetailEntities.stream().map(ContactDetailMapper::map).collect(Collectors.toList()));
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

    public CustomerRefPage fetchCustomerReferrals(final String refferalcode, final String searchKey, final Pageable pageable) {
        final Page<CustomerEntity> customerEntities;
        CustomerEntity customerEntity = customerRepository.findByRefferalCodeIdentifier(refferalcode).orElseThrow(() -> ServiceException.notFound("Customer with refferal code {0} not found", refferalcode));

        if (searchKey != null) {
            customerEntities = this.customerRepository.findByReferenceCustomerAndCurrentStateNotAndIdentifierContainingOrGivenNameContainingOrSurnameContaining(refferalcode, Customer.State.CLOSED.name(), searchKey, searchKey, searchKey, pageable);
        } else {
            customerEntities = this.customerRepository.findByReferenceCustomerAndIsDepositedAndCurrentStateNot(refferalcode, true, Customer.State.CLOSED.name(), pageable);
        }


        final CustomerRefPage customerRefPage = new CustomerRefPage();
        customerRefPage.setTotalPages(customerEntities.getTotalPages());
        customerRefPage.setTotalElements(customerEntities.getTotalElements());
        customerRefPage.setRefAccountNumber(customerEntity.getRefAccountNumber());
        customerRefPage.setSocialMatrix(getSocialMatrix(customerEntity));

        this.contactDetailRepository.findByCustomer(customerEntity).stream().filter(contactDetailEntity -> contactDetailEntity.getType().equals("EMAIL")).findFirst().ifPresent(contactDetailEntity -> {
            customerRefPage.setCustomerEmail(contactDetailEntity.getValue());
        });

        if (customerEntity.getRefAccountNumber() != null) {
            Account account = accountingAdaptor.findAccountByIdentifier(customerEntity.getRefAccountNumber());
            customerRefPage.setRefferalBalance(account.getBalance());

            if (customerEntities.getSize() > 0) {
                final ArrayList<Customer> customers = new ArrayList<>(customerEntities.getSize());
                customerEntities.forEach(entity -> {
                    Customer tCustomer = CustomerMapper.map(entity);
                    this.setCustomerContactDetails(entity, tCustomer);
                    if (entity.getAccountNumbers() != null) {
                        Account acc = accountingAdaptor.findAccountByIdentifier(entity.getRefAccountNumber());
                        tCustomer.setRefferalBalance(acc.getBalance());
                        tCustomer.setSocialMatrix(getSocialMatrix(entity));
                    }
                    customers.add(tCustomer);
                });
                customerRefPage.setCustomers(customers);
            }
        }
        return customerRefPage;
    }


    private SocialMatrix getSocialMatrix(CustomerEntity entity) {
        SocialMatrix socialMatrix = new SocialMatrix();
        String accountNumber = entity.getAccountNumbers();

        if (accountNumber != null) {
            List<AccountEntry> accountEntryList = accountingAdaptor.fetchAccountEntries(accountNumber);
            final LocalDateTime localDateTime = LocalDateTime.now();
            double totalBCDP = accountEntryList.stream()
                    .filter(d -> d.getTransactionType().equals("BCDP") && d.getType().equals("CREDIT"))
                    .filter(d -> {
                        LocalDateTime transactionDate = LocalDateTime.parse(d.getTransactionDate().substring(0, d.getTransactionDate().length() - 1), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        return transactionDate.getYear() == localDateTime.getYear() &&
                                transactionDate.getMonth().getValue() == localDateTime.getMonth().getValue();
                    }).mapToDouble(AccountEntry::getAmount).sum();

            double totalCHRP = accountEntryList.stream()
                    .filter(d -> d.getTransactionType().equals("CHRP") && d.getType().equals("DEBIT"))
                    .filter(d -> {
                                LocalDateTime transactionDate = LocalDateTime.parse(d.getTransactionDate().substring(0, d.getTransactionDate().length() - 1), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                                return transactionDate.getYear() == localDateTime.getYear() &&
                                        transactionDate.getMonth().getValue() == localDateTime.getMonth().getValue();
                            }
                    ).mapToDouble(AccountEntry::getAmount).sum();

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
        socialMatrix.setMyInfluence(customerRepository.findAllByRefferalCodeIdentifierActive(entity.getRefferalCodeIdentifier()));
        return socialMatrix;
    }

    public Optional<Customer> fetchCustomerByReferralcode(final String refferalCode) {

        return customerRepository.findByRefferalCodeIdentifier(refferalCode)
                .map(customerEntity -> {
                    final Customer customer = CustomerMapper.map(customerEntity);
                    customer.setAddress(AddressMapper.map(customerEntity.getAddress()));

                    setCustomerContactDetails(customerEntity, customer);

                    return customer;
                });
    }

    public BusinessCustomer findNgo(final String identifier) {
        CustomerEntity customerEntity = customerRepository.findByIdentifierAndType(identifier, "BUSINESS")
                .orElseThrow(() -> ServiceException.notFound("Invalid Username"));
        return CustomerMapper.mapBusinessCustomer(customerEntity);

    }

    public final Stream<Command> fetchCommandsByCustomer(final String identifier) {
        return customerRepository.findByIdentifier(identifier)
                .map(commandRepository::findByCustomer)
                .orElse(Stream.empty())
                .map(CommandMapper::map);
    }

    public final Optional<PortraitEntity> findPortrait(final String identifier) {
        return this.customerRepository.findByIdentifier(identifier).map(this.portraitRepository::findByCustomer);

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

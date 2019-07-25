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
import org.apache.fineract.cn.customer.catalog.api.v1.domain.Value;
import org.apache.fineract.cn.customer.catalog.internal.repository.FieldEntity;
import org.apache.fineract.cn.customer.catalog.internal.repository.FieldValueEntity;
import org.apache.fineract.cn.customer.catalog.internal.repository.FieldValueRepository;
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
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
    private final NgoProfileRepository ngoProfileRepository;
    private final FieldValueRepository fieldValueRepository;
    private final CorporateService corporateService;
    private final DocumentService documentService;

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
                           final DocumentSubTypeRepository documentSubTypeRepository,
                           final NgoProfileRepository ngoProfileRepository,
                           final FieldValueRepository fieldValueRepository,
                           final CorporateService corporateService,
                           final DocumentService documentService) {
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
        this.ngoProfileRepository = ngoProfileRepository;
        this.fieldValueRepository = fieldValueRepository;
        this.corporateService = corporateService;
        this.documentService = documentService;
    }

    public HashMap<String, String> aerequest(AERequest aeRequest) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        HashMap<String, String> respMap = new HashMap<>();
        try {
            StringBuilder postDataStrBuilder = new StringBuilder();
            postDataStrBuilder.append("fpx_msgType=").append(URLEncoder.encode(aeRequest.getFpx_msgType(), "UTF-8"));
            postDataStrBuilder.append("&fpx_msgToken=").append(URLEncoder.encode(aeRequest.getFpx_msgToken(), "UTF-8"));
            postDataStrBuilder.append("&fpx_sellerExId=").append(URLEncoder.encode(aeRequest.getFpx_sellerExId(), "UTF-8"));
            postDataStrBuilder.append("&fpx_sellerExOrderNo=").append(URLEncoder.encode(aeRequest.getFpx_sellerExOrderNo(), "UTF-8"));
            postDataStrBuilder.append("&fpx_sellerTxnTime=").append(URLEncoder.encode(aeRequest.getFpx_sellerTxnTime(), "UTF-8"));
            postDataStrBuilder.append("&fpx_sellerOrderNo=").append(URLEncoder.encode(aeRequest.getFpx_sellerOrderNo(), "UTF-8"));
            postDataStrBuilder.append("&fpx_sellerId=").append(URLEncoder.encode(aeRequest.getFpx_sellerId(), "UTF-8"));
            postDataStrBuilder.append("&fpx_sellerBankCode=").append(URLEncoder.encode(aeRequest.getFpx_sellerBankCode(), "UTF-8"));
            postDataStrBuilder.append("&fpx_txnCurrency=").append(URLEncoder.encode(aeRequest.getFpx_txnCurrency(), "UTF-8"));
            postDataStrBuilder.append("&fpx_txnAmount=").append(URLEncoder.encode(aeRequest.getFpx_txnAmount(), "UTF-8"));
            postDataStrBuilder.append("&fpx_buyerEmail=").append(URLEncoder.encode(aeRequest.getFpx_buyerEmail(), "UTF-8"));
            postDataStrBuilder.append("&fpx_buyerName=").append(URLEncoder.encode(aeRequest.getFpx_buyerName(), "UTF-8"));
            postDataStrBuilder.append("&fpx_buyerBankId=").append(URLEncoder.encode(aeRequest.getFpx_buyerBankId(), "UTF-8"));
            postDataStrBuilder.append("&fpx_buyerBankBranch=").append(URLEncoder.encode(aeRequest.getFpx_buyerBankBranch(), "UTF-8"));
            postDataStrBuilder.append("&fpx_buyerAccNo=").append(URLEncoder.encode(aeRequest.getFpx_buyerAccNo(), "UTF-8"));
            postDataStrBuilder.append("&fpx_buyerId=").append(URLEncoder.encode(aeRequest.getFpx_buyerId(), "UTF-8"));
            postDataStrBuilder.append("&fpx_makerName=").append(URLEncoder.encode(aeRequest.getFpx_makerName(), "UTF-8"));
            postDataStrBuilder.append("&fpx_buyerIban=").append(URLEncoder.encode(aeRequest.getFpx_buyerIban(), "UTF-8"));
            postDataStrBuilder.append("&fpx_productDesc=").append(URLEncoder.encode(aeRequest.getFpx_productDesc(), "UTF-8"));
            postDataStrBuilder.append("&fpx_version=").append(URLEncoder.encode(aeRequest.getFpx_version(), "UTF-8"));
            postDataStrBuilder.append("&fpx_checkSum=").append(URLEncoder.encode(aeRequest.getFpx_checkSum(), "UTF-8"));


// Create a trust manager that does not validate certificate chains only for testing environment
            createTrustManager();

            URLConnection conn = (HttpsURLConnection) new URL("https://uat.mepsfpx.com.my/FPXMain/sellerNVPTxnStatus.jsp").openConnection();

            conn.setDoOutput(true);
            BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            outputWriter.write(postDataStrBuilder.toString(), 0, postDataStrBuilder.toString().length());
            outputWriter.flush();
            outputWriter.close();
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String strResponse = null;
            while ((strResponse = inputReader.readLine()) != null) {
//                System.out.println("Response is .." + strResponse);
                if (strResponse.length() > 0)
                    break;
            }

//            System.out.println("strResponse:[" + strResponse + "] result:[" + Objects.requireNonNull(strResponse).trim() + "] " + (strResponse.trim()).equals("PROSESSING ERROR"));
            inputReader.close();
            if (strResponse.trim().equals("msgfromfpx= PROSESSING ERROR")) {
                System.out.println("An error occurred!..Response[" + strResponse + "]");
                throw ServiceException.internalError("An error occurred!..Response {0}", strResponse);
            } else {
                StringTokenizer strToknzr = new StringTokenizer(strResponse, "&");
                while (strToknzr.hasMoreElements()) {
                    String temp = strToknzr.nextToken();
                    if (temp.contains("=")) {
                        String[] nvp = temp.split("=");
                        String name = nvp[0];
                        String value = "";
                        if (nvp.length == 2)
                            value = URLDecoder.decode(nvp[1], "UTF-8");
                        respMap.put(name, value);
                    } else {
                        System.out.println("Parsing Error!" + temp);
                        throw ServiceException.internalError("Parsing Error! {0}", temp);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("<HR><H3>Error :" + e);
            throw ServiceException.internalError("Something went wrong !");
        }

        return respMap;

    }

    private void createTrustManager() throws NoSuchAlgorithmException, KeyManagementException {
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
    }


    public HashMap<String, String> fetchBankList(PaynetDetails paynetDetails) throws
            IOException, NoSuchAlgorithmException, KeyManagementException {
        HashMap<String, String> respMap = new HashMap<>();
        StringBuilder postDataStrBuilder = new StringBuilder();
        postDataStrBuilder.append("fpx_msgType=").append(URLEncoder.encode(paynetDetails.getFpx_msgType(), "UTF-8"));
        postDataStrBuilder.append("&fpx_msgToken=").append(URLEncoder.encode(paynetDetails.getFpx_msgToken(), "UTF-8"));
        postDataStrBuilder.append("&fpx_sellerExId=").append(URLEncoder.encode(paynetDetails.getFpx_sellerExId(), "UTF-8"));
        postDataStrBuilder.append("&fpx_version=").append(URLEncoder.encode(paynetDetails.getFpx_version(), "UTF-8"));
        postDataStrBuilder.append("&fpx_checkSum=").append(URLEncoder.encode(paynetDetails.getFinal_checkSum(), "UTF-8"));

//Create a trust manager that does not validate certificate chains only for testing environment
        createTrustManager();

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

    public Optional<CustomerEntity> getCustomerEntity(final String customerIdentifier) {
        return this.customerRepository.findByIdentifier(customerIdentifier);

    }

    public Customer findCustomer(final String identifier) {
        CustomerEntity customerEntity = customerRepository.findByIdentifier(identifier).orElseThrow(() -> ServiceException.notFound("Customer with identifier {0} not found in this system", identifier));
        Customer customer = CustomerMapper.map(customerEntity);
        customer.setAddress(AddressMapper.map(customerEntity.getAddress()));
        customer.setContactDetails(customerEntity.getContactDetail().stream().map(ContactDetailMapper::map).collect(Collectors.toList()));
        customer.setNgoProfileExist(this.ngoProfileRepository.existsByCustomerIdentifier(identifier));
        CustomerDocument customerDocument = this.documentService.findCustomerDocuments(customer.getIdentifier());
        customer.setCustomerDocument(customerDocument);
        customer.setKycStatus(customerDocument.getKycStatusText());
        customer.setKycVerified(customerDocument.isKycStatus());
//        this mapper should be always after kyc value is set cause in the mapper I am receiving the kycstatus to validate the user
        CustomerMapper.map(customer, this.userVerification(customerEntity));

        if (customerEntity.getReferenceCustomer() != null) {
            this.customerRepository.findByRefferalCodeIdentifier(customerEntity.getReferenceCustomer())
                    .ifPresent(reff -> customer.setRefferalUserIdentifier(reff.getIdentifier()));
        }

        customer.setSocialMatrix(getSocialMatrix(customerEntity));
        if (customer.getRefAccountNumber() != null) {
            Account account = accountingAdaptor.findAccountByIdentifier(customer.getRefAccountNumber());
            customer.setRefferalBalance(account.getBalance());
        }

        final List<FieldValueEntity> fieldValueEntities = this.fieldValueRepository.findByCustomer(customerEntity);
        if (fieldValueEntities != null) {
            customer.setCustomValues(
                    fieldValueEntities
                            .stream()
                            .map(fieldValueEntity -> {
                                final Value value = new Value();
                                value.setValue(fieldValueEntity.getValue());
                                final FieldEntity fieldEntity = fieldValueEntity.getField();
                                value.setCatalogIdentifier(fieldEntity.getCatalog().getIdentifier());
                                value.setFieldIdentifier(fieldEntity.getIdentifier());
                                return value;
                            }).collect(Collectors.toList())
            );
        }

        return customer;
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
                                Customer.UserState.CLOSED.name(), term, term, term, pageable);
            } else {
                customerEntities = this.customerRepository.findByCurrentStateNot(Customer.UserState.CLOSED.name(), pageable);
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

    public CustomerRefPage fetchCustomerReferrals(final String refferalcode, final String searchKey,
                                                  final Pageable pageable) {
        final Page<CustomerEntity> customerEntities;
        CustomerEntity customerEntity = customerRepository.findByRefferalCodeIdentifier(refferalcode).orElseThrow(() -> ServiceException.notFound("Customer with refferal code {0} not found", refferalcode));

        if (searchKey != null) {
            customerEntities = this.customerRepository.findByReferenceCustomerAndCurrentStateNotAndIdentifierContainingOrGivenNameContainingOrSurnameContaining(refferalcode, Customer.UserState.CLOSED.name(), searchKey, searchKey, searchKey, pageable);
        } else {
            customerEntities = this.customerRepository.findByReferenceCustomerAndIsDepositedAndCurrentStateNot(refferalcode, true, Customer.UserState.CLOSED.name(), pageable);
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
                    tCustomer.setContactDetails(customerEntity.getContactDetail().stream().map(ContactDetailMapper::map).collect(Collectors.toList()));
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
                    customer.setContactDetails(customerEntity.getContactDetail().stream().map(ContactDetailMapper::map).collect(Collectors.toList()));
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

    private Optional<IdentificationCardScanEntity> findIdentificationCardEntity(final String number,
                                                                                final String identifier) {
        final Optional<IdentificationCardEntity> cardEntity = this.identificationCardRepository.findByNumber(number);
        return cardEntity.flatMap(card -> this.identificationCardScanRepository.findByIdentifierAndIdentificationCard(identifier, card));
    }

    public Optional<IdentificationCardScan> findIdentificationCardScan(final String number,
                                                                       final String identifier) {
        return this.findIdentificationCardEntity(number, identifier).map(IdentificationCardScanMapper::map);
    }

    public Optional<byte[]> findIdentificationCardScanImage(final String number, final String identifier) {
        return this.findIdentificationCardEntity(number, identifier).map(IdentificationCardScanEntity::getImage);
    }

    public List<ProcessStep> getProcessSteps(final String customerIdentifier) {
        return customerRepository.findByIdentifier(customerIdentifier)
                .map(customerEntity -> {
                    final List<ProcessStep> processSteps = new ArrayList<>();

                    final Customer.UserState userState = Customer.UserState.valueOf(customerEntity.getCurrentState());
                    switch (userState) {
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


    private UserVerification userVerification(final CustomerEntity customerEntity) {
        List<ContactDetailEntity> contactDetail = customerEntity.getContactDetail();
        UserVerification userVerification = new UserVerification();
        userVerification.setEmailVerified(contactDetail.stream().anyMatch(entity -> entity.getType().equals(ContactDetail.Type.EMAIL.name()) && entity.getValid()));
        userVerification.setMobileVerified(contactDetail.stream().anyMatch(entity -> entity.getType().equals(ContactDetail.Type.MOBILE.name()) && entity.getValid()));
        userVerification.setVerifiedMobileNumber(contactDetail.stream().filter(entity -> entity.getValid() && entity.getType().equals(ContactDetail.Type.MOBILE.name())).findFirst().map(ContactDetailEntity::getValue).orElse(""));
        userVerification.setVerifiedEmailAddress(contactDetail.stream().filter(entity -> entity.getValid() && entity.getType().equals(ContactDetail.Type.EMAIL.name())).findFirst().map(ContactDetailEntity::getValue).orElse(""));
        userVerification.setProfileComplete(getProfileCompleted(customerEntity));
        return userVerification;
    }

    private boolean getProfileCompleted(CustomerEntity customerEntity) {

        if (customerEntity.getType().equals(Customer.UserType.CORPORATE.name())) {
            AddressEntity addressEntity = customerEntity.getAddress();
            List<ContactDetailEntity> detailEntityList = customerEntity.getContactDetail();
            List<FieldValueEntity> fieldValueEntities = customerEntity.getFieldValueEntities();
            return addressEntity != null &&
                    addressEntity.getCountry() != null &&
                    addressEntity.getState() != null &&
                    addressEntity.getCity() != null &&
                    addressEntity.getPostalCode() != null &&
                    fieldValueEntities.stream().anyMatch(entity -> entity.getField().getIdentifier().equals("companyName")) &&
                    fieldValueEntities.stream().anyMatch(entity -> entity.getField().getIdentifier().equals("typeOfCompany")) &&
                    detailEntityList.stream().anyMatch(entity -> entity.getType().equals(ContactDetail.Type.EMAIL.name())) &&
                    detailEntityList.stream().anyMatch(entity -> entity.getType().equals(ContactDetail.Type.MOBILE.name()));


        } else if (customerEntity.getType().equals(Customer.UserType.PERSON.name())) {
            AddressEntity addressEntity = customerEntity.getAddress();
            List<ContactDetailEntity> detailEntityList = customerEntity.getContactDetail();
            return addressEntity != null &&
                    addressEntity.getCountry() != null &&
                    customerEntity.getDateOfBirth() != null &&
                    customerEntity.getGender() != null &&
                    detailEntityList.stream().anyMatch(entity -> entity.getType().equals(ContactDetail.Type.EMAIL.name())) &&
                    detailEntityList.stream().anyMatch(entity -> entity.getType().equals(ContactDetail.Type.MOBILE.name()));

        } else if (customerEntity.getType().equals(Customer.UserType.BUSINESS.name())) {
            AddressEntity addressEntity = customerEntity.getAddress();
            List<ContactDetailEntity> detailEntityList = customerEntity.getContactDetail();
            return addressEntity != null &&
                    addressEntity.getCountry() != null &&
                    customerEntity.getRegistrationType() != null &&
                    customerEntity.getNgoName() != null &&
                    customerEntity.getGivenName() != null &&
                    customerEntity.getNgoRegistrationNumber() != null &&
                    customerEntity.getDateOfRegistration() != null &&
                    detailEntityList.stream().anyMatch(entity -> entity.getType().equals(ContactDetail.Type.EMAIL.name())) &&
                    detailEntityList.stream().anyMatch(entity -> entity.getType().equals(ContactDetail.Type.MOBILE.name()));

        } else {
            return true;
        }
    }
}

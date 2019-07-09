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
package org.apache.fineract.cn.customer.internal.command.handler;

import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.customer.api.v1.CustomerEventConstants;
import org.apache.fineract.cn.customer.api.v1.domain.CorporateUser;
import org.apache.fineract.cn.customer.api.v1.domain.Customer;
import org.apache.fineract.cn.customer.catalog.internal.repository.*;
import org.apache.fineract.cn.customer.internal.command.CreateCorporateCommand;
import org.apache.fineract.cn.customer.internal.mapper.AddressMapper;
import org.apache.fineract.cn.customer.internal.mapper.ContactDetailMapper;
import org.apache.fineract.cn.customer.internal.mapper.CustomerMapper;
import org.apache.fineract.cn.customer.internal.mapper.FieldValueMapper;
import org.apache.fineract.cn.customer.internal.repository.*;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@Aggregate
public class CorporatesAggregate {
    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final ContactDetailRepository contactDetailRepository;
    private final CommandRepository commandRepository;
    private final FieldValueRepository fieldValueRepository;
    private final CatalogRepository catalogRepository;
    private final FieldRepository fieldRepository;

    @Autowired
    public CorporatesAggregate(final AddressRepository addressRepository,
                               final CustomerRepository customerRepository,
                               final IdentificationCardRepository identificationCardRepository,
                               final IdentificationCardScanRepository identificationCardScanRepository,
                               final PortraitRepository portraitRepository,
                               final ContactDetailRepository contactDetailRepository,
                               final FieldValueRepository fieldValueRepository,
                               final CatalogRepository catalogRepository,
                               final FieldRepository fieldRepository,
                               final CommandRepository commandRepository,
                               final AmlDetailRepository amlDetailRepository,
                               final TaskAggregate taskAggregate,
                               final FieldRepository fieldRepository1) {
        super();
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
        this.contactDetailRepository = contactDetailRepository;
        this.commandRepository = commandRepository;
        this.fieldValueRepository = fieldValueRepository;
        this.catalogRepository = catalogRepository;
        this.fieldRepository = fieldRepository1;
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.POST_CUSTOMER)
    public CorporateUser createCustomer(final CreateCorporateCommand createCorporateCommand) {
        CorporateUser corporateUser = createCorporateCommand.getCorporateUser();

        final AddressEntity savedAddress = this.addressRepository.save(AddressMapper.map(corporateUser.getAddress()));

        final CustomerEntity customerEntity = CustomerMapper.map(corporateUser);
        customerEntity.setAddress(savedAddress);
        final CustomerEntity savedCustomerEntity = this.customerRepository.save(customerEntity);

        if (corporateUser.getContactDetails() != null) {
            this.contactDetailRepository.save(
                    corporateUser.getContactDetails()
                            .stream()
                            .map(contact -> {
                                final ContactDetailEntity contactDetailEntity = ContactDetailMapper.map(contact);
                                contactDetailEntity.setCustomer(savedCustomerEntity);
                                return contactDetailEntity;
                            })
                            .collect(Collectors.toList())
            );
        }


        if (corporateUser.getCustomValues() != null) {
            this.setCustomValues(corporateUser, savedCustomerEntity);
        }

        return new CorporateUser(customerEntity.getId(),
                customerEntity.getIdentifier(),
                customerEntity.getType(),
                customerEntity.getGivenName(),
                customerEntity.getSurname(),
                customerEntity.getDesignation());
    }


    private void setCustomValues(final CorporateUser corporateUser, final CustomerEntity savedCustomerEntity) {
        this.fieldValueRepository.save(
                corporateUser.getCustomValues()
                        .stream()
                        .map(value -> {
                            final Optional<CatalogEntity> catalog = this.catalogRepository.findByIdentifier(value.getCatalogIdentifier());
                            final Optional<FieldEntity> field = this.fieldRepository.findByCatalogAndIdentifier(catalog.orElseThrow(() -> ServiceException.notFound("Catalog {0} not found.", value.getCatalogIdentifier())), value.getFieldIdentifier());
                            final FieldValueEntity fieldValueEntity = FieldValueMapper.map(value);
                            fieldValueEntity.setCustomer(savedCustomerEntity);
                            fieldValueEntity.setField(field.orElseThrow(() -> ServiceException.notFound("Field {0} not found.", value.getFieldIdentifier())));
                            return fieldValueEntity;
                        }).collect(Collectors.toList())
        );
    }

}

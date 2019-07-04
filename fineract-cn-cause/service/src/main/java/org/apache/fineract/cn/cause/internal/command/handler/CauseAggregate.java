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
package org.apache.fineract.cn.cause.internal.command.handler;

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.cause.api.v1.CauseEventConstants;
import org.apache.fineract.cn.cause.api.v1.domain.*;
import org.apache.fineract.cn.cause.internal.command.*;
import org.apache.fineract.cn.cause.internal.mapper.*;
import org.apache.fineract.cn.cause.internal.repository.*;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Padma Raju Sattineni
 */

@SuppressWarnings({"unused", "UnusedReturnValue"})
@Aggregate
public class CauseAggregate {
    private final AddressRepository addressRepository;
    private final CauseRepository causeRepository;
    private final DocumentRepository documentRepository;
    private final CauseStateRepository causeStateRepository;
    private final DocumentPageRepository documentPageRepository;
    private final PortraitRepository portraitRepository;
    private final CategoryRepository categoryRepository;
    private final CommandRepository commandRepository;
    private final TaskAggregate taskAggregate;

    @Autowired
    public CauseAggregate(final AddressRepository addressRepository,
                          final CauseRepository causeRepository,
                          final DocumentRepository documentRepository,
                          final PortraitRepository portraitRepository,
                          final CauseStateRepository causeStateRepository,
                          final CategoryRepository categoryRepository,
                          final CommandRepository commandRepository,
                          final DocumentPageRepository documentPageRepository,
                          final TaskAggregate taskAggregate) {
        super();
        this.addressRepository = addressRepository;
        this.causeRepository = causeRepository;
        this.portraitRepository = portraitRepository;
        this.categoryRepository = categoryRepository;
        this.commandRepository = commandRepository;
        this.causeStateRepository = causeStateRepository;
        this.taskAggregate = taskAggregate;
        this.documentRepository = documentRepository;
        this.documentPageRepository = documentPageRepository;
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_CAUSE)
    public CreateCauseCommandResponse createCause(final CreateCauseCommand createCauseCommand) throws IOException {
        final Cause cause = createCauseCommand.getCause();
        final CategoryEntity categoryEntity;
        categoryEntity = this.categoryRepository.findByIdentifier(cause.getCauseCategories().getIdentifier())
                .orElseGet(() -> this.categoryRepository.save(CategoryMapper.map(cause.getCauseCategories())));

        final CauseEntity causeEntity = CauseMapper.map(cause);
        causeEntity.setResubmited(false);
        causeEntity.setExtended(false);
        causeEntity.setCategory(categoryEntity);
        causeEntity.setCurrentState(Cause.State.PENDING.name());
        final CauseEntity savedCauseEntity = this.causeRepository.save(causeEntity);

        AddressEntity addressEntity = AddressMapper.map(cause.getAddress());
        addressEntity.setCause(causeEntity);
        this.addressRepository.save(addressEntity);

        DocumentEntity documentEntity = documentRepository.save(DocumentMapper.map(savedCauseEntity));
        documentPageRepository.save(DocumentMapper.map(cause, documentEntity));

        this.taskAggregate.onCauseCommand(savedCauseEntity, Command.Action.ACTIVATE);
        return new CreateCauseCommandResponse(
                savedCauseEntity.getIdentifier(),
                savedCauseEntity.getTitle(),
                savedCauseEntity.getDescription(),
                savedCauseEntity.getStartDate().toString(),
                savedCauseEntity.getEndDate().toString(),
                savedCauseEntity.getCurrentState(),
                savedCauseEntity.getSoftTarget(),
                savedCauseEntity.getHardTarget(),
                savedCauseEntity.getIsTaxExamption(),
                savedCauseEntity.getAcceptedDenominationAmounts(),
                savedCauseEntity.getCreatedBy(),
                savedCauseEntity.getCreatedOn().toString(),
                savedCauseEntity.isFeeRevisionRequired(),
                savedCauseEntity.getTaxExemptionPercentage(),
                savedCauseEntity.getWebsiteUrl(),
                savedCauseEntity.getSmediaLinks(),
                savedCauseEntity.getVideoUrls(),
                savedCauseEntity.getCauseTxHash(),
                savedCauseEntity.getAccountNumber(),
                savedCauseEntity.getEthAddress(),
                savedCauseEntity.getRejectedReason());
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.PUT_CAUSE)
    public String updateCause(final UpdateCauseCommand updateCauseCommand) {
        final Cause cause = updateCauseCommand.getCause();
        final CauseEntity causeEntity = findCauseEntityOrThrow(updateCauseCommand.getIdentifier());
        final AddressEntity addressEntity = this.addressRepository.findByCause(causeEntity);
        final List<CauseFiles> causeFiles = updateCauseCommand.getCause().getCauseFiles();
        DocumentEntity documentEntity = documentRepository.findByCause(causeEntity);

        addressEntity.setStreet(cause.getAddress().getStreet());
        addressEntity.setCountryCode(cause.getAddress().getCountryCode());
        addressEntity.setState(cause.getAddress().getState());
        addressEntity.setRegion(cause.getAddress().getRegion());
        addressEntity.setPostalCode(cause.getAddress().getPostalCode());
        addressEntity.setCity(cause.getAddress().getCity());
        this.addressRepository.save(addressEntity);

        causeEntity.setTitle(cause.getTitle());
        causeEntity.setStartDate(LocalDateTime.parse(cause.getStartDate()));
        causeEntity.setEndDate(LocalDateTime.parse(cause.getEndDate()));
        causeEntity.setDescription(cause.getDescription());
        causeEntity.setWebsiteUrl(cause.getWebsiteUrl());
        causeEntity.setVideoUrls(cause.getVideoUrls());
        causeEntity.setSmediaLinks(cause.getSmediaLinks());
        causeEntity.setTaxExamption(cause.getTaxExamption());
        causeEntity.setTaxExemptionPercentage(cause.getTaxExemptionPercentage());
        causeEntity.setFinRate(cause.getFinRate());
//        causeEntity.setActualRaisedFiat(cause.getActualRaisedFiat());
//        causeEntity.setActualRaisedFin(cause.getActualRaisedFiat());
        causeEntity.setHardTarget(cause.getHardTarget());
        causeEntity.setSoftTarget(cause.getSoftTarget());
        causeEntity.setCurrentState(Cause.State.PENDING.name());
        causeEntity.setAcceptedDenominationAmounts(cause.getAcceptedDenominationAmounts());
        this.causeRepository.save(causeEntity);
        List<DocumentPageEntity> pageEntities = this.documentPageRepository.findByDocument(documentEntity);
        this.documentPageRepository.delete(pageEntities);
        List<DocumentPageEntity> documentPageEntities = DocumentMapper.map(causeFiles, documentEntity);
        this.documentPageRepository.save(documentPageEntities);

        CauseStateEntity stateEntity = CauseMapper.map(causeEntity, null, Cause.State.PENDING.name());
        this.causeStateRepository.save(stateEntity);


        return cause.getIdentifier();
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.PUBLISH_CAUSE)
    public String publishCause(final PublishCauseCommand publishCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(publishCauseCommand.getIdentifier());
        causeEntity.setCurrentState(Cause.State.ACTIVE.name());
        causeEntity.setPublishDate(LocalDateTime.now(Clock.systemUTC()));
        this.causeRepository.save(causeEntity);
        return publishCauseCommand.getIdentifier();
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.EXPIRE_CAUSE)
    public String expireCause(final ExpiredCauseCommand expiredCauseCommand) {
        List<CauseEntity> causes = causeRepository.findByEndDateAndCurrentState(Cause.State.ACTIVE.name(), Cause.State.EXTENDED.name());

        List<CauseEntity> mappedCauses = causes.stream().peek(causeEntity -> {
            causeEntity.setCurrentState(Cause.State.CLOSED.name());
            causeEntity.setClosedDate(LocalDateTime.now(Clock.systemUTC()));
        }).collect(Collectors.toList());

        causeRepository.save(mappedCauses);

        return causes.toString();
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.INACTIVE_CAUSE)
    public String expireCause(final InactiveCauseCommand inactiveCauseCommand) {
        List<CauseEntity> causes = causeRepository.findByApproveDate(LocalDateTime.now(Clock.systemUTC()).minusDays(12), Cause.State.APPROVED.name())
                .stream().peek(causeEntity -> {
                    causeEntity.setCurrentState(Cause.State.INACTIVE.name());


                    //        set the status to zero
                    Set<String> stateTypes = new HashSet<>(Arrays.asList(Cause.State.PENDING.name(), Cause.State.APPROVED.name(), Cause.State.REJECTED.name()));
                    List<CauseStateEntity> stateEntity = this.causeStateRepository.findByCauseAndTypeIn(causeEntity, stateTypes);
                    this.causeStateRepository.save(stateEntity
                            .stream()
                            .peek(entity -> entity.setType(entity.getType() + "-" + Cause.State.INACTIVE.name())).collect(Collectors.toList()));

                    //        add the status to state
                    CauseStateEntity causeStateEntity = CauseMapper.map(causeEntity, null, Cause.State.INACTIVE.name());
                    this.causeStateRepository.save(causeStateEntity);


                }).collect(Collectors.toList());
        causeRepository.save(causes);
        return causes.toString();
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.REJECT_CAUSE)
    public String RejectCause(final RejectCauseCommand rejectCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(rejectCauseCommand.getIdentifier());
        causeEntity.setCurrentState(Cause.State.REJECTED.name());
        causeEntity.setRejectedReason(rejectCauseCommand.getCauseReject().getRejectedReason());
        causeEntity.setFeeRevisionRequired(rejectCauseCommand.getCauseReject().isFeeRevisionRequired());
        causeEntity.setRejectedBy(UserContextHolder.checkedGetUser());
        this.causeRepository.save(causeEntity);

//        set the pending status to zero
        Set<String> stateTypes = new HashSet<>(Collections.singletonList(Cause.State.PENDING.name()));
        List<CauseStateEntity> stateEntity = this.causeStateRepository.findByCauseAndTypeIn(causeEntity, stateTypes);
        this.causeStateRepository.save(stateEntity.stream().peek(entity -> entity.setType(Cause.State.PENDING.name() + "-" + Cause.State.PENDING.name() + "-" + Cause.State.REJECTED.name())).collect(Collectors.toList()));

//        add the status to state
        CauseStateEntity causeStateEntity = CauseMapper.map(causeEntity, null, Cause.State.REJECTED.name());
        this.causeStateRepository.save(causeStateEntity);

        return rejectCauseCommand.getIdentifier();
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.EXTEND_CAUSE)
    public String ExtendCause(final ExtendCauseCommand extendCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(extendCauseCommand.getIdentifier());
        causeEntity.setExtended(true);
        causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));
        CauseStateEntity stateEntity = CauseMapper.map(causeEntity, extendCauseCommand.getExtendDate(), Cause.State.EXTENDED.name());
        this.causeRepository.save(causeEntity);
        this.causeStateRepository.save(stateEntity);
        return extendCauseCommand.getIdentifier();
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.UNPUBLISHED_CAUSE)
    public String ExtendCause(final UnpublishCauseCommand unpublishCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(unpublishCauseCommand.getIdentifier());
        CauseStateEntity stateEntity = CauseMapper.mapComment(causeEntity, unpublishCauseCommand.getComment(), Cause.State.UNPUBLISH.name());
        this.causeStateRepository.save(stateEntity);
        return unpublishCauseCommand.toString();
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.APPROVE_CAUSE)
    public String ApproveCause(final ApproveCauseCommand approveCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(approveCauseCommand.getIdentifier());
        causeEntity.setCurrentState(Cause.State.APPROVED.name());
        causeEntity.setFinRate(approveCauseCommand.getFinRate().toString());
//        causeEntity.setSuccessFees(approveCauseCommand.getSuccessFees());
        causeEntity.setApprovedOn(LocalDateTime.now(Clock.systemUTC()));
        causeEntity.setApprovedBy(UserContextHolder.checkedGetUser());
        this.causeRepository.save(causeEntity);

        updateCauseStateForApproval(causeEntity);

        CauseStateEntity stateEntity = CauseMapper.map(causeEntity, null, Cause.State.APPROVED.name());
        this.causeStateRepository.save(stateEntity);

        return approveCauseCommand.getIdentifier();
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.ADD_CAUSE_SUCCESS_FEE)
    public String ApproveCause(final UpdateCauseSuccessFeesCommand causeSuccessFeesCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(causeSuccessFeesCommand.getIdentifier());
        causeEntity.setManagementFee(causeSuccessFeesCommand.getSuccessFee());
        causeEntity.setLastModifiedOn(LocalDateTime.now());
        causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
        this.causeRepository.save(causeEntity);
        return causeSuccessFeesCommand.toString();
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.APPROVE_EXTENDED_CAUSE)
    public String ApproveCause(final ApproveExtendedCauseCommand approveExtendedCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(approveExtendedCauseCommand.getIdentifier());
        List<CauseStateEntity> stateEntity = causeStateRepository.findByCauseAndTypeIn(causeEntity, new HashSet<>(Collections.singletonList(Cause.State.EXTENDED.name())));

        causeEntity.setExtended(false);
        causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));
        CauseStateEntity latestStateEntity = stateEntity.stream().max(Comparator.comparing(CauseStateEntity::getCreatedOn)).get();
        causeEntity.setEndDate(latestStateEntity.getNewDate());
        this.causeRepository.save(causeEntity);

        this.causeStateRepository.save(stateEntity.stream().peek(entity -> entity.setStatus(Cause.State.APPROVED.name())).collect(Collectors.toList()));


        return approveExtendedCauseCommand.getIdentifier();
    }

    private void updateCauseStateForApproval(CauseEntity causeEntity) {
        Set<String> stateTypes = new HashSet<>(Arrays.asList(Cause.State.REJECTED.name(), Cause.State.PENDING.name()));
        List<CauseStateEntity> stateEntity = this.causeStateRepository.findByCauseAndTypeIn(causeEntity, stateTypes)
                .stream()
                .peek(entity -> {
                    entity.setType(entity.getType() + "-" + Cause.State.APPROVED.name());
                    entity.setStatus(Cause.State.APPROVED.name());
                })
                .collect(Collectors.toList());

        this.causeStateRepository.save(stateEntity);
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.DELETE_CAUSE)
    public String deleteCause(final DeleteCauseCommand deleteCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(deleteCauseCommand.getCauseIdentifier());
        setStateAndTimestamp(causeEntity, Cause.State.DELETED);
        final CauseEntity savedCauseEntity = this.causeRepository.save(causeEntity);
//        this.commandRepository.save(CommandMapper.create(savedCauseEntity, Command.Action.LOCK.name(), deleteCauseCommand.comment()));
        this.taskAggregate.onCauseCommand(savedCauseEntity, Command.Action.UNLOCK);
        return deleteCauseCommand.getCauseIdentifier();
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.ACTIVATE_CAUSE)
    public String activateCause(final ActivateCauseCommand activateCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(activateCauseCommand.identifier());

        if (this.taskAggregate.openTasksForCauseExist(causeEntity, Command.Action.ACTIVATE.name())) {
            throw ServiceException.conflict("Open Tasks for cause {0} exists.", activateCauseCommand.identifier());
        }

        setStateAndTimestamp(causeEntity, Cause.State.ACTIVE);

        final CauseEntity savedCauseEntity = this.causeRepository.save(causeEntity);

//        this.commandRepository.save(
//                CommandMapper.create(savedCauseEntity, Command.Action.ACTIVATE.name(), activateCauseCommand.comment())
//        );

        return activateCauseCommand.identifier();
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.LOCK_CAUSE)
    public String lockCause(final LockCauseCommand lockCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(lockCauseCommand.identifier());

        setStateAndTimestamp(causeEntity, Cause.State.LOCKED);

        final CauseEntity savedCauseEntity = this.causeRepository.save(causeEntity);

//        this.commandRepository.save(
//                CommandMapper.create(savedCauseEntity, Command.Action.LOCK.name(), lockCauseCommand.comment())
//        );

        this.taskAggregate.onCauseCommand(savedCauseEntity, Command.Action.UNLOCK);

        return lockCauseCommand.identifier();
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.UNLOCK_CAUSE)
    public String unlockCause(final UnlockCauseCommand unlockCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(unlockCauseCommand.identifier());

        if (this.taskAggregate.openTasksForCauseExist(causeEntity, Command.Action.UNLOCK.name())) {
            throw ServiceException.conflict("Open Tasks for cause {0} exists.", unlockCauseCommand.identifier());
        }

        setStateAndTimestamp(causeEntity, Cause.State.ACTIVE);

        final CauseEntity savedCauseEntity = this.causeRepository.save(causeEntity);

//        this.commandRepository.save(
//                CommandMapper.create(savedCauseEntity, Command.Action.UNLOCK.name(), unlockCauseCommand.comment())
//        );

        return unlockCauseCommand.identifier();
    }

    private void setStateAndTimestamp(CauseEntity causeEntity, Cause.State state) {
        causeEntity.setCurrentState(state.name());
        causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
        causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.CLOSE_CAUSE)
    public String closeCause(final CloseCauseCommand closeCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(closeCauseCommand.identifier());

        setStateAndTimestamp(causeEntity, Cause.State.CLOSED);

        final CauseEntity savedCauseEntity = this.causeRepository.save(causeEntity);

//        this.commandRepository.save(
//                CommandMapper.create(savedCauseEntity, Command.Action.CLOSE.name(), closeCauseCommand.comment())
//        );

        this.taskAggregate.onCauseCommand(savedCauseEntity, Command.Action.REOPEN);

        return closeCauseCommand.identifier();
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.REOPEN_CAUSE)
    public String reopenCause(final ReopenCauseCommand reopenCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(reopenCauseCommand.identifier());

        if (this.taskAggregate.openTasksForCauseExist(causeEntity, Command.Action.REOPEN.name())) {
            throw ServiceException.conflict("Open Tasks for cause {0} exists.", reopenCauseCommand.identifier());
        }

        setStateAndTimestamp(causeEntity, Cause.State.ACTIVE);

        final CauseEntity savedCauseEntity = this.causeRepository.save(causeEntity);

//        this.commandRepository.save(
//                CommandMapper.create(savedCauseEntity, Command.Action.REOPEN.name(), reopenCauseCommand.comment())
//        );

        return reopenCauseCommand.identifier();
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.PUT_ADDRESS)
    public String updateAddress(final UpdateAddressCommand updateAddressCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(updateAddressCommand.identifier());

        causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
        causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));
        final AddressEntity addressEntity = this.addressRepository.findByCause(causeEntity);
        addressEntity.setCity(updateAddressCommand.address().getCity());
        addressEntity.setCountry(updateAddressCommand.address().getCountry());
        addressEntity.setPostalCode(updateAddressCommand.address().getPostalCode());
        addressEntity.setRegion(updateAddressCommand.address().getRegion());
        addressEntity.setState(updateAddressCommand.address().getState());
        addressEntity.setCountryCode(updateAddressCommand.address().getCountryCode());
        addressEntity.setStreet(updateAddressCommand.address().getStreet());
        this.addressRepository.save(addressEntity);
        return updateAddressCommand.identifier();
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_PORTRAIT)
    public String createPortrait(final CreatePortraitCommand createPortraitCommand) throws IOException {
        if (createPortraitCommand.portrait() == null) {
            return null;
        }

        final CauseEntity causeEntity = findCauseEntityOrThrow(createPortraitCommand.identifier());

        final PortraitEntity portraitEntity = PortraitMapper.map(createPortraitCommand.portrait());
        portraitEntity.setCause(causeEntity);
        this.portraitRepository.save(portraitEntity);

        causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
        causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));

        this.causeRepository.save(causeEntity);

        return createPortraitCommand.identifier();
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.DELETE_PORTRAIT)
    public String deletePortrait(final DeletePortraitCommand deletePortraitCommand) throws IOException {
        final CauseEntity causeEntity = findCauseEntityOrThrow(deletePortraitCommand.identifier());

        this.portraitRepository.deleteByCause(causeEntity);

        causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
        causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));

        this.causeRepository.save(causeEntity);

        return deletePortraitCommand.identifier();
    }

    private CauseEntity findCauseEntityOrThrow(String identifier) {
        return this.causeRepository.findByIdentifier(identifier).orElseThrow(() -> ServiceException.notFound("Cause ''{0}'' not found", identifier));
    }
}


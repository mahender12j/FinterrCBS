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
import org.apache.fineract.cn.cause.api.v1.domain.Cause;
import org.apache.fineract.cn.cause.api.v1.domain.CauseDocumentPage;
import org.apache.fineract.cn.cause.api.v1.domain.Command;
import org.apache.fineract.cn.cause.internal.command.*;
import org.apache.fineract.cn.cause.internal.mapper.*;
import org.apache.fineract.cn.cause.internal.repository.*;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private final IdentificationCardRepository identificationCardRepository;
    private final IdentificationCardScanRepository identificationCardScanRepository;
    private final PortraitRepository portraitRepository;
    private final CategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;
    private final CommandRepository commandRepository;
    private final TaskAggregate taskAggregate;

    @Autowired
    public CauseAggregate(final AddressRepository addressRepository,
                          final CauseRepository causeRepository,
                          final DocumentRepository documentRepository,
                          final IdentificationCardRepository identificationCardRepository,
                          final IdentificationCardScanRepository identificationCardScanRepository,
                          final PortraitRepository portraitRepository,
                          final CauseStateRepository causeStateRepository,
                          final CategoryRepository categoryRepository,
                          final RatingRepository ratingRepository,
                          final CommandRepository commandRepository,
                          final DocumentPageRepository documentPageRepository,
                          final TaskAggregate taskAggregate) {
        super();
        this.addressRepository = addressRepository;
        this.causeRepository = causeRepository;
        this.identificationCardRepository = identificationCardRepository;
        this.identificationCardScanRepository = identificationCardScanRepository;
        this.portraitRepository = portraitRepository;
        this.categoryRepository = categoryRepository;
        this.ratingRepository = ratingRepository;
        this.commandRepository = commandRepository;
        this.causeStateRepository = causeStateRepository;
        this.taskAggregate = taskAggregate;
        this.documentRepository = documentRepository;
        this.documentPageRepository = documentPageRepository;
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_CAUSE)
    public String createCause(final CreateCauseCommand createCauseCommand) throws IOException {
        final Cause cause = createCauseCommand.getCause();
        final CategoryEntity categoryEntity;

        if (!categoryRepository.existsByIdentifier(cause.getCauseCategories().getIdentifier())) {

            categoryEntity = this.categoryRepository.save(CategoryMapper.map(cause.getCauseCategories()));
        } else {
            categoryEntity = this.categoryRepository.findByIdentifier(cause.getCauseCategories().getIdentifier()).get();
        }
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
        List<DocumentPageEntity> documentPageEntityList = new ArrayList<>();
        documentPageEntityList.add(DocumentMapper.map(createCauseCommand.getFeature(), documentEntity, "Feature"));
        documentPageEntityList.add(DocumentMapper.map(createCauseCommand.getTerms(), documentEntity, "Terms"));

        for (MultipartFile d : createCauseCommand.getGallery()) {
            documentPageEntityList.add(DocumentMapper.map(d, documentEntity, "Gallary"));
        }

        if (createCauseCommand.getOther() != null) {
            documentPageEntityList.add(DocumentMapper.map(createCauseCommand.getOther(), documentEntity, "Other"));

        }

        if (createCauseCommand.getTax() != null) {
            documentPageEntityList.add(DocumentMapper.map(createCauseCommand.getTax(), documentEntity, "Tax"));
        }


        documentPageEntityList.stream().forEach(d -> {
            d.setIsMapped(CauseDocumentPage.MappedState.ACTIVE.name());
        });

        documentPageRepository.save(documentPageEntityList);
        this.taskAggregate.onCauseCommand(savedCauseEntity, Command.Action.ACTIVATE);
        return cause.getIdentifier();
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_CAUSE)
    public String createCause(final CreateCauseDocumentCommand createCauseDocumentCommand) throws IOException {
        final CauseEntity causeEntity = this.findCauseEntityOrThrow(createCauseDocumentCommand.getCauseIdentifier());
        DocumentEntity entity = documentRepository.findByCause(causeEntity);
        DocumentPageEntity pageEntity = DocumentMapper.map(createCauseDocumentCommand.getDoc(), entity, createCauseDocumentCommand.getDocType());
        pageEntity.setIsMapped(CauseDocumentPage.MappedState.UPLOADED.name());
        documentPageRepository.save(pageEntity);
        return causeEntity.getIdentifier();
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.PUT_CAUSE)
    public String updateCause(final UpdateCauseCommand updateCauseCommand) {
        final Cause cause = updateCauseCommand.getCause();
        final CauseEntity causeEntity = findCauseEntityOrThrow(updateCauseCommand.getIdentifier());

        causeEntity.setTitle(cause.getTitle());

//        todo


        this.causeRepository.save(causeEntity);
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
        causes.forEach(causeEntity -> {
            causeEntity.setCurrentState(Cause.State.CLOSED.name());
            causeEntity.setClosedDate(LocalDateTime.now(Clock.systemUTC()));
        });
        causeRepository.save(causes);
        return causes.toString();
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.INACTIVE_CAUSE)
    public String expireCause(final InactiveCauseCommand inactiveCauseCommand) {
        List<CauseEntity> causes = causeRepository.findByApproveDate(LocalDateTime.now(Clock.systemUTC()).minusDays(12), Cause.State.APPROVED.name());
        causes.forEach(causeEntity -> {
            causeEntity.setCurrentState(Cause.State.INACTIVE.name());
        });
        causeRepository.save(causes);
        return causes.toString();
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.REJECT_CAUSE)
    public String RejectCause(final RejectCauseCommand rejectCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(rejectCauseCommand.getIdentifier());
        causeEntity.setCurrentState(Cause.State.REJECTED.name());
        causeEntity.setRejectedReason(rejectCauseCommand.getReason());
        causeEntity.setRejectedBy(UserContextHolder.checkedGetUser());
        this.causeRepository.save(causeEntity);
        return rejectCauseCommand.getIdentifier();
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.EXTEND_CAUSE)
    public String ExtendCause(final ExtendCauseCommand extendCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(extendCauseCommand.getIdentifier());
        causeEntity.setExtended(true);
        causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));
        CauseStateEntity stateEntity = CauseMapper.map(causeEntity, extendCauseCommand.getExtend_date());
        this.causeRepository.save(causeEntity);
        this.causeStateRepository.save(stateEntity);
        return extendCauseCommand.getIdentifier();
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.APPROVE_CAUSE)
    public String ApproveCause(final ApproveCauseCommand approveCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(approveCauseCommand.getIdentifier());
        causeEntity.setCurrentState(Cause.State.APPROVED.name());
        causeEntity.setFinRate(approveCauseCommand.getFinRate().toString());
        causeEntity.setManagementFee(approveCauseCommand.getSuccessFees());
        causeEntity.setApprovedOn(LocalDateTime.now(Clock.systemUTC()));
        causeEntity.setApprovedBy(UserContextHolder.checkedGetUser());
        this.causeRepository.save(causeEntity);
        return approveCauseCommand.getIdentifier();
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.DELETE_CAUSE)
    public String deleteCause(final DeleteCauseCommand deleteCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(deleteCauseCommand.getCauseIdentifier());

        System.out.println("deleteCause --- CauseIndentifier :: " + deleteCauseCommand.getCauseIdentifier());
        causeEntity.setCurrentState(Cause.State.DELETED.name());
        causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
        causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));

        final CauseEntity savedCauseEntity = this.causeRepository.save(causeEntity);
        System.out.println("deleteCause --- savedCauseEntity :: " + savedCauseEntity);


        this.commandRepository.save(
                CommandMapper.create(savedCauseEntity, Command.Action.LOCK.name(), deleteCauseCommand.comment())
        );

        this.taskAggregate.onCauseCommand(savedCauseEntity, Command.Action.UNLOCK);
        System.out.println("deleteCause --- end :: " + deleteCauseCommand.getCauseIdentifier());
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

        causeEntity.setCurrentState(Cause.State.ACTIVE.name());
        causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
        causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));

        final CauseEntity savedCauseEntity = this.causeRepository.save(causeEntity);

        this.commandRepository.save(
                CommandMapper.create(savedCauseEntity, Command.Action.ACTIVATE.name(), activateCauseCommand.comment())
        );

        return activateCauseCommand.identifier();
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.LOCK_CAUSE)
    public String lockCause(final LockCauseCommand lockCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(lockCauseCommand.identifier());

        causeEntity.setCurrentState(Cause.State.LOCKED.name());
        causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
        causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));

        final CauseEntity savedCauseEntity = this.causeRepository.save(causeEntity);

        this.commandRepository.save(
                CommandMapper.create(savedCauseEntity, Command.Action.LOCK.name(), lockCauseCommand.comment())
        );

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

        causeEntity.setCurrentState(Cause.State.ACTIVE.name());
        causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
        causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));

        final CauseEntity savedCauseEntity = this.causeRepository.save(causeEntity);

        this.commandRepository.save(
                CommandMapper.create(savedCauseEntity, Command.Action.UNLOCK.name(), unlockCauseCommand.comment())
        );

        return unlockCauseCommand.identifier();
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.CLOSE_CAUSE)
    public String closeCause(final CloseCauseCommand closeCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(closeCauseCommand.identifier());

        causeEntity.setCurrentState(Cause.State.CLOSED.name());
        causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
        causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));

        final CauseEntity savedCauseEntity = this.causeRepository.save(causeEntity);

        this.commandRepository.save(
                CommandMapper.create(savedCauseEntity, Command.Action.CLOSE.name(), closeCauseCommand.comment())
        );

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

        causeEntity.setCurrentState(Cause.State.ACTIVE.name());
        causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
        causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));

        final CauseEntity savedCauseEntity = this.causeRepository.save(causeEntity);

        this.commandRepository.save(
                CommandMapper.create(savedCauseEntity, Command.Action.REOPEN.name(), reopenCauseCommand.comment())
        );

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
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_RATING)
    public String createRating(final CreateRatingCommand createRatingCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(createRatingCommand.getCauseIdentifier());

        final RatingEntity ratingEntity = RatingMapper.map(createRatingCommand.getCauseRating());

        ratingEntity.setCause(causeEntity);
        ratingEntity.setCreatedBy(UserContextHolder.checkedGetUser());
        ratingEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));

        this.ratingRepository.save(ratingEntity);

        // causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
        // causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));
        // this.causeRepository.save(causeEntity);

        return ratingEntity.getIdentifier();
    }

    // @Transactional
    // @CommandHandler
    // @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.PUT_CONTACT_DETAILS)
    // public String updateContactDetails(final UpdateContactDetailsCommand updateContactDetailsCommand) {
    //     final CauseEntity causeEntity = findCauseEntityOrThrow(updateContactDetailsCommand.identifier());
    //     causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
    //     causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));

    //     final List<ContactDetailEntity> oldContactDetails = this.contactDetailRepository.findByCause(causeEntity);
    //     this.contactDetailRepository.delete(oldContactDetails);

    //     if (updateContactDetailsCommand.contactDetails() != null) {
    //         this.contactDetailRepository.save(
    //                 updateContactDetailsCommand.contactDetails()
    //                         .stream()
    //                         .map(contact -> {
    //                             final ContactDetailEntity newContactDetail = ContactDetailMapper.map(contact);
    //                             newContactDetail.setCause(causeEntity);
    //                             return newContactDetail;
    //                         })
    //                         .collect(Collectors.toList())
    //         );
    //     }

    //     return updateContactDetailsCommand.identifier();
    // }

  /*@Transactional
  @CommandHandler
  @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_IDENTIFICATION_CARD)
  public String createIdentificationCard(final CreateIdentificationCardCommand createIdentificationCardCommand) {
    final CauseEntity causeEntity = findCauseEntityOrThrow(createIdentificationCardCommand.identifier());

    final IdentificationCardEntity identificationCardEntity = IdentificationCardMapper.map(createIdentificationCardCommand.identificationCard());

    identificationCardEntity.setCause(causeEntity);
    identificationCardEntity.setCreatedBy(UserContextHolder.checkedGetUser());
    identificationCardEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));

    this.identificationCardRepository.save(identificationCardEntity);

    causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
    causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));

    this.causeRepository.save(causeEntity);

    return identificationCardEntity.getNumber();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.PUT_IDENTIFICATION_CARD)
  public String updateIdentificationCard(final UpdateIdentificationCardCommand updateIdentificationCardCommand) {
    final Optional<IdentificationCardEntity> optionalIdentificationCardEntity = this.identificationCardRepository.findByNumber(updateIdentificationCardCommand.number());

    final IdentificationCardEntity identificationCard = IdentificationCardMapper.map(updateIdentificationCardCommand.identificationCard());

    optionalIdentificationCardEntity.ifPresent(identificationCardEntity -> {
      identificationCardEntity.setIssuer(identificationCard.getIssuer());
      identificationCardEntity.setType(identificationCard.getType());
      identificationCardEntity.setExpirationDate(identificationCard.getExpirationDate());
      identificationCardEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
      identificationCardEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));

      this.identificationCardRepository.save(identificationCardEntity);

      final CauseEntity causeEntity = identificationCardEntity.getCause();

      causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
      causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));

      this.causeRepository.save(causeEntity);
    });

    return updateIdentificationCardCommand.number();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.DELETE_IDENTIFICATION_CARD)
  public String deleteIdentificationCard(final DeleteIdentificationCardCommand deleteIdentificationCardCommand) throws IOException {
    final Optional<IdentificationCardEntity> optionalIdentificationCardEntity = this.identificationCardRepository.findByNumber(deleteIdentificationCardCommand.number());

    optionalIdentificationCardEntity.ifPresent(identificationCardEntity -> {

      final List<IdentificationCardScanEntity> cardScanEntities = this.identificationCardScanRepository.findByIdentificationCard(identificationCardEntity);

      this.identificationCardScanRepository.delete(cardScanEntities);

      this.identificationCardRepository.delete(identificationCardEntity);

      final CauseEntity causeEntity = identificationCardEntity.getCause();

      causeEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
      causeEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));

      this.causeRepository.save(causeEntity);
    });

    return deleteIdentificationCardCommand.number();
  }

  @Transactional
  @CommandHandler(logStart = CommandLogLevel.INFO, logFinish = CommandLogLevel.INFO)
  @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_IDENTIFICATION_CARD_SCAN)
  public ScanEvent createIdentificationCardScan(final CreateIdentificationCardScanCommand command) throws Exception {
    final Optional<IdentificationCardEntity> identificationCardEntity = this.identificationCardRepository.findByNumber(command.number());

    final IdentificationCardEntity cardEntity = identificationCardEntity.orElseThrow(() -> ServiceException.notFound("Identification card {0} not found.", command.number()));

    final IdentificationCardScanEntity identificationCardScanEntity = IdentificationCardScanMapper.map(command.scan());

    final MultipartFile image = command.image();

    final LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

    identificationCardScanEntity.setImage(image.getBytes());
    identificationCardScanEntity.setContentType(image.getContentType());
    identificationCardScanEntity.setSize(image.getSize());
    identificationCardScanEntity.setIdentificationCard(cardEntity);
    identificationCardScanEntity.setCreatedBy(UserContextHolder.checkedGetUser());
    identificationCardScanEntity.setCreatedOn(now);

    identificationCardScanRepository.save(identificationCardScanEntity);

    cardEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
    cardEntity.setLastModifiedOn(now);

    identificationCardRepository.save(cardEntity);

    return new ScanEvent(command.number(), command.scan().getIdentifier());
  }

  @Transactional
  @CommandHandler(logStart = CommandLogLevel.INFO, logFinish = CommandLogLevel.INFO)
  @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.DELETE_IDENTIFICATION_CARD_SCAN)
  public ScanEvent deleteIdentificationCardScan(final DeleteIdentificationCardScanCommand command) {
    final Optional<IdentificationCardEntity> cardEntity = this.identificationCardRepository.findByNumber(command.number());
    final Optional<IdentificationCardScanEntity> scanEntity = cardEntity
            .flatMap(entity -> this.identificationCardScanRepository.findByIdentifierAndIdentificationCard(command.scanIdentifier(), entity));

    scanEntity.ifPresent(identificationCardScanEntity -> {

      this.identificationCardScanRepository.delete(identificationCardScanEntity);

      final IdentificationCardEntity identificationCard = identificationCardScanEntity.getIdentificationCard();

      identificationCard.setLastModifiedBy(UserContextHolder.checkedGetUser());
      identificationCard.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));

      this.identificationCardRepository.save(identificationCard);
    });

    return new ScanEvent(command.number(), command.scanIdentifier());
  }*/

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

/*  private void setCustomValues(final Customer customer, final CauseEntity savedCauseEntity) {
    this.fieldValueRepository.save(
        customer.getCustomValues()
            .stream()
            .map(value -> {
              final Optional<CatalogEntity> catalog =
                  this.catalogRepository.findByIdentifier(value.getCatalogIdentifier());
              final Optional<FieldEntity> field =
                  this.fieldRepository.findByCatalogAndIdentifier(
                      catalog.orElseThrow(() -> ServiceException.notFound("Catalog {0} not found.", value.getCatalogIdentifier())),
                      value.getFieldIdentifier());
              final FieldValueEntity fieldValueEntity = FieldValueMapper.map(value);
              fieldValueEntity.setCustomer(savedCauseEntity);
              fieldValueEntity.setField(
                  field.orElseThrow(() -> ServiceException.notFound("Field {0} not found.", value.getFieldIdentifier())));
              return fieldValueEntity;
            })
            .collect(Collectors.toList())
    );
  }*/

    private CauseEntity findCauseEntityOrThrow(String identifier) {
        return this.causeRepository.findByIdentifier(identifier)
                .orElseThrow(() -> ServiceException.notFound("Cause ''{0}'' not found", identifier));
    }
}


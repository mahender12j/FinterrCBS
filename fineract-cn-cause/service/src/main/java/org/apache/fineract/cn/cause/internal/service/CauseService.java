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
package org.apache.fineract.cn.cause.internal.service;

import org.apache.fineract.cn.accounting.api.v1.domain.JournalEntry;
import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.cause.api.v1.domain.*;
import org.apache.fineract.cn.cause.internal.mapper.*;
import org.apache.fineract.cn.cause.internal.repository.*;
import org.apache.fineract.cn.cause.internal.service.helper.service.AccountingAdaptor;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CauseService {

    private final CauseRepository causeRepository;
    private final DocumentRepository documentRepository;
    private final AddressRepository addressRepository;
    private final DocumentPageRepository documentPageRepository;
    private final PortraitRepository portraitRepository;
    private final CategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;
    private final CommandRepository commandRepository;
    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskInstanceRepository taskInstanceRepository;
    private final AccountingAdaptor accountingAdaptor;

    @Autowired
    public CauseService(final CauseRepository causeRepository,
                        final PortraitRepository portraitRepository,
                        final CategoryRepository categoryRepository,
                        final AddressRepository addressRepository,
                        final DocumentPageRepository documentPageRepository,
                        final RatingRepository ratingRepository,
                        final DocumentRepository documentRepository,
                        final CommandRepository commandRepository,
                        final AccountingAdaptor accountingAdaptor,
                        final TaskDefinitionRepository taskDefinitionRepository,
                        final TaskInstanceRepository taskInstanceRepository) {
        super();
        this.causeRepository = causeRepository;
        this.portraitRepository = portraitRepository;
        this.categoryRepository = categoryRepository;
        this.ratingRepository = ratingRepository;
        this.commandRepository = commandRepository;
        this.addressRepository = addressRepository;
        this.documentPageRepository = documentPageRepository;
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskInstanceRepository = taskInstanceRepository;
        this.accountingAdaptor = accountingAdaptor;
        this.documentRepository = documentRepository;
    }

    public Boolean causeExists(final String identifier) {
        return this.causeRepository.existsByIdentifier(identifier);
    }

  /*public Boolean imageExists(final String number) {
    return this.imageRepository.existsByNumber(number);
  }

  public Boolean imageScanExists(final String number, final String identifier) {
    return this.imageRepository.findByNumber(number)
            .map(cardEntity -> this.imageScanRepository.existsByIdentifierAndIdentificationCard(identifier, cardEntity))
            .orElse(false);
  }*/

    public Optional<Cause> findCause(final String identifier) {
        return causeRepository.findByIdentifier(identifier).map(causeEntity -> {
            final Cause cause = CauseMapper.map(causeEntity);
            setCauseDocuments(causeEntity, cause);
            AddressEntity addressEntity = this.addressRepository.findByCause(causeEntity);
            cause.setAddress(AddressMapper.map(addressEntity));
            cause.setCauseCategories(CategoryMapper.map(causeEntity.getCategory()));
            if (cause.getAccountNumber() != null) {
                final List<JournalEntry> journalEntry = accountingAdaptor.fetchJournalEntriesJournalEntries(cause.getAccountNumber());
                cause.setCauseStatistics(CauseStatisticsMapper.map(journalEntry));
            }
            cause.setCauseRatingList(RatingMapper.map(ratingRepository.findByCause(causeEntity)));
            final Double avgRatingValue = this.ratingRepository.findAvgRatingByCauseId(identifier);
            if (avgRatingValue != null) cause.setAvgRating(avgRatingValue.toString());
            else cause.setAvgRating("0");


            return cause;
        });
    }

    private void setCauseDocuments(CauseEntity causeEntity, Cause cause) {
        final DocumentEntity documentEntity = this.documentRepository.findByCause(causeEntity);
        final CauseDocument causeDocument = DocumentMapper.map(documentEntity);
        final List<DocumentPageEntity> pageEntity = this.documentPageRepository.findByDocumentAndIsMapped(documentEntity, CauseDocumentPage.MappedState.ACTIVE.name());
        causeDocument.setCauseDocumentPages(DocumentMapper.map(pageEntity));
        cause.setCauseDocument(causeDocument);
        cause.setCauseFiles(DocumentMapper.mapFile(pageEntity));
    }


    public List<CauseDocumentPage> causeDocumentPages(final String identifier) {
        Optional<CauseEntity> causeEntity = causeRepository.findByIdentifier(identifier);

        if (causeEntity.isPresent()) {
            DocumentEntity entity = documentRepository.findByCauseAndCreatedBy(causeEntity.get(), UserContextHolder.checkedGetUser());
            return documentPageRepository.findByDocumentAndIsMapped(entity, CauseDocumentPage.MappedState.UPLOADED.name()).stream().map(DocumentMapper::map).collect(Collectors.toList());
        } else {
            throw ServiceException.notFound("Cause not found");
        }
    }


    public Optional<CauseEntity> findCauseEntity(String identifier) {
        return causeRepository.findByIdentifier(identifier);
    }


    public CausePage fetchCause(final Boolean includeClosed, final String param, final Pageable pageable) {
        final Page<CauseEntity> causeEntities;
        CausePage causePage = new CausePage();
        if (includeClosed) {
            final String userIdentifier = UserContextHolder.checkedGetUser();
            if (param == null) {
                causeEntities = this.causeRepository.findByCreatedByAndCurrentStateNot(userIdentifier, Cause.State.ACTIVE.DELETED.name(), pageable);
                causePage.setTotalPages(causeEntities.getTotalPages());
                causePage.setTotalElements(causeEntities.getTotalElements());
                causePage.setCauses(causeArrayList(causeEntities));
            } else {
                causeEntities = this.causeRepository.findByCreatedByAndCurrentState(userIdentifier, param, pageable);
                causePage.setTotalPages(causeEntities.getTotalPages());
                causePage.setTotalElements(causeEntities.getTotalElements());
                causePage.setCauses(causeArrayList(causeEntities));
            }
        } else {
            if (param == null) {
                causeEntities = this.causeRepository.findByCurrentState(Cause.State.ACTIVE.name(), pageable);
                causePage.setTotalPages(causeEntities.getTotalPages());
                causePage.setTotalElements(causeEntities.getTotalElements());
                causePage.setCauses(causeArrayList(causeEntities));
            } else {
                causePage = fetchCauseByCategory(param, pageable);
            }
        }


        return causePage;
    }


    private CausePage fetchCauseByCategory(final String categoryIdentifier, final Pageable pageable) {
        final CausePage causePage = new CausePage();
        final Page<CauseEntity> causeEntities;
        Optional<CategoryEntity> categoryEntity;
        if (categoryIdentifier != null) {
            categoryEntity = categoryRepository.findByIdentifier(categoryIdentifier.toLowerCase());
            if (categoryEntity.isPresent()) {
                causeEntities = this.causeRepository.findByCategoryAndCurrentState(categoryEntity.get(), Cause.State.ACTIVE.name(), pageable);
                causePage.setCauses(causeArrayList(causeEntities));
                causePage.setTotalPages(causeEntities.getTotalPages());
                causePage.setTotalElements(causeEntities.getTotalElements());
            } else {
                causePage.setCauses(Collections.emptyList());
                causePage.setTotalPages(1);
                causePage.setTotalElements((long) 0);
            }
        } else {
            causeEntities = this.causeRepository.findAll(pageable);
            causePage.setCauses(causeArrayList(causeEntities));
            causePage.setTotalPages(causeEntities.getTotalPages());
            causePage.setTotalElements(causeEntities.getTotalElements());
        }

        return causePage;

    }


    private ArrayList<Cause> causeArrayList(Page<CauseEntity> causeEntities) {
        final ArrayList<Cause> causes = new ArrayList<>(causeEntities.getSize());
        for (CauseEntity causeEntity : causeEntities) {
            final Cause cause = CauseMapper.map(causeEntity);
            if (cause.getAccountNumber() != null) {
                final List<JournalEntry> journalEntry = accountingAdaptor.fetchJournalEntriesJournalEntries(cause.getAccountNumber());
                cause.setCauseStatistics(CauseStatisticsMapper.map(journalEntry));
            }

            cause.setAddress(AddressMapper.map(this.addressRepository.findByCause(causeEntity)));
            cause.setCauseCategories(CategoryMapper.map(causeEntity.getCategory()));

            setCauseDocuments(causeEntity, cause);
            final Double avgRatingValue = this.ratingRepository.findAvgRatingByCauseId(cause.getIdentifier());

            if (avgRatingValue != null) {
                cause.setAvgRating(avgRatingValue.toString());
            } else {
                cause.setAvgRating("0");
            }

            causes.add(cause);
        }

        return causes;
    }


    public NGOStatistics fetchCauseByCreatedBy(final String identifier) {
        final List<CauseEntity> causeEntities = this.causeRepository.findByCreatedByAndCurrentStateNot(identifier, Cause.State.DELETED.name());
        ArrayList<CauseStatistics> causeStatistics = new ArrayList<>(causeEntities.size());
        for (CauseEntity causeEntity : causeEntities) {
            if (causeEntity.getAccountNumber() != null) {
                final CauseStatistics causeStatistic = CauseStatisticsMapper.map(accountingAdaptor.fetchJournalEntriesJournalEntries(causeEntity.getAccountNumber()));
                causeStatistics.add(causeStatistic);
            }
        }

        final NGOStatistics ngoStatistics = new NGOStatistics();
        ngoStatistics.setTotalRaisedAmount(causeStatistics.stream().mapToDouble(CauseStatistics::getTotalRaised).sum());
        ngoStatistics.setTotalSupporter(causeStatistics.stream().mapToInt(CauseStatistics::getTotalSupporter).sum());
        ngoStatistics.setTotalCause(causeEntities.size());
        return ngoStatistics;
    }


    public Boolean causeRatingExists(final String causeIdentifier, final String createdBy) {
        return this.ratingRepository.existsByCreatedBy(causeIdentifier, createdBy);
    }

    public final Stream<CauseRating> fetchRatingsByCause(final String identifier) {
        return causeRepository.findByIdentifier(identifier)
                .map(ratingRepository::findByCause)
                .orElse(Stream.empty())
                .map(RatingMapper::map);
    }

    public final Stream<CauseRating> fetchActiveRatingsByCause(final String identifier, final Boolean active) {
        return causeRepository.findByIdentifier(identifier)
                .map(causeEntity -> this.ratingRepository.findByCauseAndActive(causeEntity, active))
                .orElse(Stream.empty())
                .map(RatingMapper::map);
    }

    public final Stream<Command> fetchCommandsByCause(final String identifier) {
        return causeRepository.findByIdentifier(identifier)
                .map(commandRepository::findByCause)
                .orElse(Stream.empty())
                .map(CommandMapper::map);
    }

    public final Optional<PortraitEntity> findPortrait(final String identifier) {
        return causeRepository.findByIdentifier(identifier)
                .map(portraitRepository::findByCause);
    }

    public static boolean isRemovableState(String val) {
        for (Cause.RemovableCauseState c : Cause.RemovableCauseState.values()) {
            if (c.name().equals(val)) {
                return true;
            }
        }
        return false;
    }

/*  public final Stream<IdentificationCard> fetchIdentificationCardsByCause(final String identifier) {
    return causeRepository.findByIdentifier(identifier)
        .map(identificationCardRepository::findByCause)
        .orElse(Stream.empty())
        .map(IdentificationCardMapper::map);
  }*/

/*
  public Optional<IdentificationCard> findIdentificationCard(final String number) {
    final Optional<IdentificationCardEntity> identificationCardEntity = this.identificationCardRepository.findByNumber(number);

    return identificationCardEntity.map(IdentificationCardMapper::map);
  }
*/

/*  public final List<IdentificationCardScan> fetchScansByIdentificationCard(final String number) {
    final Optional<IdentificationCardEntity> identificationCard = this.identificationCardRepository.findByNumber(number);

    return identificationCard.map(this.identificationCardScanRepository::findByIdentificationCard)
            .map(x -> x.stream().map(IdentificationCardScanMapper::map).collect(Collectors.toList()))
            .orElseGet(Collections::emptyList);
  }*/

/*  private Optional<IdentificationCardScanEntity> findIdentificationCardEntity(final String number, final String identifier) {
    final Optional<IdentificationCardEntity> cardEntity = this.identificationCardRepository.findByNumber(number);
    return cardEntity.flatMap(card -> this.identificationCardScanRepository.findByIdentifierAndIdentificationCard(identifier, card));
  }*/

//  public Optional<IdentificationCardScan> findIdentificationCardScan(final String number, final String identifier) {
//    return this.findIdentificationCardEntity(number, identifier).map(IdentificationCardScanMapper::map);
//  }
//
//  public Optional<byte[]> findIdentificationCardScanImage(final String number, final String identifier) {
//    return this.findIdentificationCardEntity(number, identifier).map(IdentificationCardScanEntity::getImage);
//  }

    public List<ProcessStep> getProcessSteps(final String causeIdentifier) {
        return causeRepository.findByIdentifier(causeIdentifier)
                .map(causeEntity -> {
                    final List<ProcessStep> processSteps = new ArrayList<>();

                    final Cause.State state = Cause.State.valueOf(causeEntity.getCurrentState());
                    switch (state) {
                        case PENDING:
                            processSteps.add(this.buildProcessStep(causeEntity, Command.Action.ACTIVATE));
                            processSteps.add(this.buildProcessStep(causeEntity, Command.Action.CLOSE));
                            break;
                        case ACTIVE:
                            processSteps.add(this.buildProcessStep(causeEntity, Command.Action.LOCK));
                            processSteps.add(this.buildProcessStep(causeEntity, Command.Action.CLOSE));
                            break;
                        case LOCKED:
                            processSteps.add(this.buildProcessStep(causeEntity, Command.Action.UNLOCK));
                            processSteps.add(this.buildProcessStep(causeEntity, Command.Action.CLOSE));
                            break;
                        case CLOSED:
                            processSteps.add(this.buildProcessStep(causeEntity, Command.Action.REOPEN));
                            break;
                    }

                    return processSteps;
                })
                .orElse(Collections.emptyList());
    }

    private ProcessStep buildProcessStep(final CauseEntity causeEntity, final Command.Action action) {
        final ProcessStep processStep = new ProcessStep();

        final Command command = new Command();
        command.setAction(action.name());
        processStep.setCommand(command);

        final ArrayList<TaskDefinition> taskDefinitions = new ArrayList<>();
        this.taskDefinitionRepository.findByAssignedCommandsContaining(action.name())
                .forEach(taskDefinitionEntity ->
                        this.taskInstanceRepository.findByCauseAndTaskDefinition(causeEntity, taskDefinitionEntity)
                                .forEach(taskInstanceEntity -> {
                                    if (taskInstanceEntity.getExecutedBy() == null) {
                                        taskDefinitions.add(TaskDefinitionMapper.map(taskDefinitionEntity));
                                    }
                                }));
        processStep.setTaskDefinitions(taskDefinitions);

        return processStep;
    }
}


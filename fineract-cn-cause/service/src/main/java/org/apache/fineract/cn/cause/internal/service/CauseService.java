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

import static java.util.stream.Collectors.toList;

/*import org.apache.fineract.cn.cause.catalog.internal.repository.FieldEntity;
import org.apache.fineract.cn.cause.catalog.internal.repository.FieldValueEntity;
import org.apache.fineract.cn.cause.catalog.internal.repository.FieldValueRepository;*/

//import org.apache.fineract.cn.cause.catalog.api.v1.domain.Value;

@Service
public class CauseService {

    private final CauseRepository causeRepository;
    private final PortraitRepository portraitRepository;
    private final ContactDetailRepository contactDetailRepository;
    private final CategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;
    private final CommandRepository commandRepository;
    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskInstanceRepository taskInstanceRepository;
    private final AccountingAdaptor accountingAdaptor;

    @Autowired
    public CauseService(final CauseRepository causeRepository,
                        final PortraitRepository portraitRepository,
                        final ContactDetailRepository contactDetailRepository,
                        final CategoryRepository categoryRepository,
                        final RatingRepository ratingRepository,
                        final CommandRepository commandRepository,
                        final AccountingAdaptor accountingAdaptor,
                        final TaskDefinitionRepository taskDefinitionRepository,
                        final TaskInstanceRepository taskInstanceRepository) {
        super();
        this.causeRepository = causeRepository;
        this.portraitRepository = portraitRepository;
        this.contactDetailRepository = contactDetailRepository;
        this.categoryRepository = categoryRepository;
        this.ratingRepository = ratingRepository;
        this.commandRepository = commandRepository;
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskInstanceRepository = taskInstanceRepository;
        this.accountingAdaptor = accountingAdaptor;
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
        return causeRepository.findByIdentifier(identifier)
                .map(causeEntity -> {
                    final Cause cause = CauseMapper.map(causeEntity);
                    cause.setAddress(AddressMapper.map(causeEntity.getAddress()));

                    final List<CategoryEntity> categoryEntities = this.categoryRepository.findByCause(causeEntity);
                    if (categoryEntities != null) {
                        cause.setCauseCategories(
                                categoryEntities
                                        .stream()
                                        .map(CategoryMapper::map)
                                        .collect(Collectors.toList())
                        );
                        final List<JournalEntry> journalEntry = accountingAdaptor.fetchJournalEntriesJournalEntries(cause.getAccountNumber());
                        cause.setCauseStatistics(CauseStatisticsMapper.map(journalEntry));

                    }

                    final Double avgRatingValue = this.ratingRepository.findAvgRatingByCauseId(identifier);
                    System.out.println("avgRatingValue :: " + avgRatingValue);
                    if (avgRatingValue != null) {
                        cause.setAvgRating(avgRatingValue.toString());
                    } else {
                        cause.setAvgRating("0");
                    }


                    return cause;
                });
    }

    public CausePage fetchCause(final String term, final Boolean includeClosed, final Boolean onlyActive, final Pageable pageable) {
        final Page<CauseEntity> causeEntities;
        if (includeClosed) {
            final String userIdentifier = UserContextHolder.checkedGetUser();
            System.out.println("fetchCause --- userIdentifier ::: " + userIdentifier);

            if (term != null) {
                causeEntities =
                        this.causeRepository.findByCreatedByAndIdentifierContainingOrTitleContainingOrDescriptionContainingAndCurrentStateNot(userIdentifier, term, term, term, Cause.State.ACTIVE.DELETED.name(), pageable);
            } else {
                causeEntities = this.causeRepository.findByCreatedByAndCurrentStateNot(userIdentifier, Cause.State.ACTIVE.DELETED.name(), pageable);
            }
        } else if (onlyActive) {
            if (term != null) {
                causeEntities =
                        this.causeRepository.findByCurrentStateAndIdentifierContainingOrTitleContainingOrDescriptionContaining(
                                Cause.State.ACTIVE.name(), term, term, term, pageable);
            } else {
                causeEntities = this.causeRepository.findByCurrentState(Cause.State.ACTIVE.name(), pageable);
            }
        } else {
            if (term != null) {
                causeEntities =
                        this.causeRepository.findByCurrentStateNotAndIdentifierContainingOrTitleContainingOrDescriptionContaining(
                                Cause.State.CLOSED.name(), term, term, term, pageable);
            } else {
                causeEntities = this.causeRepository.findByCurrentStateNot(Cause.State.CLOSED.name(), pageable);
            }
        }

        final CausePage causePage = new CausePage();
        causePage.setTotalPages(causeEntities.getTotalPages());
        causePage.setTotalElements(causeEntities.getTotalElements());
        if (causeEntities.getSize() > 0) {
            final ArrayList<Cause> causes = new ArrayList<>(causeEntities.getSize());
            causePage.setCauses(causes);
            for (CauseEntity causeEntity : causeEntities) {
                final Cause cause = CauseMapper.map(causeEntity);
                final List<JournalEntry> journalEntry = accountingAdaptor.fetchJournalEntriesJournalEntries(cause.getAccountNumber());
                cause.setCauseStatistics(CauseStatisticsMapper.map(journalEntry));
                cause.setAddress(AddressMapper.map(causeEntity.getAddress()));

                final List<CategoryEntity> categoryEntities = this.categoryRepository.findByCause(causeEntity);
                if (categoryEntities != null) {
                    cause.setCauseCategories(
                            categoryEntities
                                    .stream()
                                    .map(CategoryMapper::map)
                                    .collect(toList())
                    );
                }

                final Double avgRatingValue = this.ratingRepository.findAvgRatingByCauseId(cause.getIdentifier());
                if (avgRatingValue != null) {
                    cause.setAvgRating(avgRatingValue.toString());
                } else {
                    cause.setAvgRating("0");
                }

                causes.add(cause);
            }
        }

        return causePage;
    }


    public NGOStatistics fetchCauseByCreatedBy(final String identifier) {
        final List<CauseEntity> causeEntities = this.causeRepository.findByCreatedByAndCurrentStateNot(identifier, Cause.State.ACTIVE.DELETED.name());
        ArrayList<CauseStatistics> causeStatistics = new ArrayList<>(causeEntities.size());
        for (CauseEntity causeEntity : causeEntities) {
            final CauseStatistics causeStatistic = CauseStatisticsMapper.map(accountingAdaptor.fetchJournalEntriesJournalEntries(causeEntity.getAccountNumber()));
            causeStatistics.add(causeStatistic);
        }

        final NGOStatistics ngoStatistics = new NGOStatistics();
        ngoStatistics.setTotalRaisedAmount(causeStatistics.stream().mapToDouble(d -> d.getTotalRaised()).sum());
        ngoStatistics.setTotalSupporter(causeStatistics.stream().mapToInt(d -> d.getTotalSupporter()).sum());
        ngoStatistics.setTotalCause(causeEntities.size());
        return ngoStatistics;
    }


    public Boolean causeRatingExists(final String causeIdentifier, final String createdBy) {
//        System.out.println("causeRatingExists --- causeIdentifier :: " + causeIdentifier + "  createdBy :: " + createdBy);
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


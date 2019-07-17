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

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.cause.api.v1.domain.*;
import org.apache.fineract.cn.cause.internal.mapper.*;
import org.apache.fineract.cn.cause.internal.repository.*;
import org.apache.fineract.cn.cause.internal.service.helper.service.AccountingAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
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
    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskInstanceRepository taskInstanceRepository;
    private final AccountingAdaptor accountingAdaptor;
    private final CauseStateRepository causeStateRepository;
    private final CauseUpdateRepository causeUpdateRepository;

    @Autowired
    public CauseService(final CauseRepository causeRepository,
                        final PortraitRepository portraitRepository,
                        final CategoryRepository categoryRepository,
                        final AddressRepository addressRepository,
                        final DocumentPageRepository documentPageRepository,
                        final RatingRepository ratingRepository,
                        final DocumentRepository documentRepository,
                        final AccountingAdaptor accountingAdaptor,
                        final TaskDefinitionRepository taskDefinitionRepository,
                        final TaskInstanceRepository taskInstanceRepository,
                        final CauseStateRepository causeStateRepository,
                        final CauseUpdateRepository causeUpdateRepository) {
        super();
        this.causeRepository = causeRepository;
        this.portraitRepository = portraitRepository;
        this.categoryRepository = categoryRepository;
        this.ratingRepository = ratingRepository;
        this.addressRepository = addressRepository;
        this.documentPageRepository = documentPageRepository;
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskInstanceRepository = taskInstanceRepository;
        this.accountingAdaptor = accountingAdaptor;
        this.documentRepository = documentRepository;
        this.causeStateRepository = causeStateRepository;
        this.causeUpdateRepository = causeUpdateRepository;
    }

    public Boolean causeExists(final String identifier) {
        return this.causeRepository.existsByIdentifier(identifier);
    }


    public Boolean ratingExists(final Long id) {
        return this.ratingRepository.existsByid(id);
    }


    public Optional<RatingEntity> ratingExistsByCreatedBy(final Long id) {
        return this.ratingRepository.findByIdAndCreatedBy(id, UserContextHolder.checkedGetUser());
    }

    public Optional<CauseEntity> findCauseByIdentifier(final String identifier) {
        return this.causeRepository.findByIdentifier(identifier);
    }

    private void setCauseDocuments(CauseEntity causeEntity, Cause cause) {
        final DocumentEntity documentEntity = this.documentRepository.findByCause(causeEntity);
        final CauseDocument causeDocument = DocumentMapper.map(documentEntity);
        final List<DocumentPageEntity> pageEntity = this.documentPageRepository.findByDocument(documentEntity);
        causeDocument.setCauseDocumentPages(DocumentMapper.map(pageEntity));
        cause.setCauseDocument(causeDocument);
        cause.setCauseFiles(DocumentMapper.mapFile(pageEntity));
    }


    public Optional<CauseEntity> findCauseEntity(String identifier) {
        return causeRepository.findByIdentifier(identifier);
    }

                /*
            ALL = 0
            RECENT_CAUSE = 1
            Top-Funded = 2
            Most Popular = 3
            */


    private CausePage sortedCause(final int sortBy, List<Cause> causes, final Pageable pageable) {
        CausePage causePage = new CausePage();
        switch (sortBy) {
            case 1:
                causePage.setCauses(causes
                        .stream()
                        .sorted(Comparator.comparing(Cause::getCreatedOn, Comparator.reverseOrder())).collect(Collectors.toList()));
                break;
            case 2:
                causePage.setCauses(causes
                        .stream()
                        .sorted((e1, e2) -> e2.getCauseStatistics().getTotalRaised().compareTo(e1.getCauseStatistics().getTotalRaised()))
                        .collect(Collectors.toList()));
                break;
            case 3:
                causePage.setCauses(causes
                        .stream()
                        .sorted(Comparator.comparing(Cause::getAvgRating, Comparator.reverseOrder()))
                        .collect(Collectors.toList()));
                break;
            default:
                break;

        }

        int start = pageable.getOffset();
        int end = (start + pageable.getPageSize()) > causes.size() ? causes.size() : (start + pageable.getPageSize());
        Page<Cause> pages = new PageImpl<Cause>(causes.subList(start, end), pageable, causes.size());
        causePage.setTotalElements(pages.getTotalElements());
        causePage.setTotalPages(pages.getTotalPages());
        causePage.setCauses(pages.getContent());
        return causePage;

    }


    public CausePage fetchCauseForCustomer(final int sortBy, final String param, final Pageable pageable) {
        if (param == null) {
            List<CauseEntity> causeEntities = this.causeRepository.findByCurrentStateAndStartDateLessThanEqual(Cause.State.ACTIVE.name(), LocalDateTime.now(Clock.systemUTC()));
            List<Cause> causes = this.causeEntitiesToCause(causeEntities);
            return this.sortedCause(sortBy, causes, pageable);
        } else {
            return fetchCauseByCategory(sortBy, param, pageable);
        }
    }

    public CausePage fetchCauseForNGO(final String param, final Pageable pageable) {
        final Page<CauseEntity> causeEntities;
        CausePage causePage = new CausePage();
        final String userIdentifier = UserContextHolder.checkedGetUser();
        if (param == null) {
            causeEntities = this.causeRepository.findByCreatedByAndCurrentStateNot(userIdentifier, Cause.State.DELETED.name(), pageable);
            causePage.setTotalPages(causeEntities.getTotalPages());
            causePage.setTotalElements(causeEntities.getTotalElements());
            causePage.setCauses(causeEntitiesToCause(causeEntities.getContent()));
        } else {
            causeEntities = this.causeRepository.findByCreatedByAndCurrentState(userIdentifier, param, pageable);
            causePage.setTotalPages(causeEntities.getTotalPages());
            causePage.setTotalElements(causeEntities.getTotalElements());
            causePage.setCauses(causeEntitiesToCause(causeEntities.getContent()));
        }
        return causePage;
    }


    public CausePage fetchAllCause(final String param, final Pageable pageable) {
        final Page<CauseEntity> causeEntities;
        CausePage causePage = new CausePage();
        if (param == null) {
            causeEntities = this.causeRepository.findAll(pageable);
            causePage.setTotalPages(causeEntities.getTotalPages());
            causePage.setTotalElements(causeEntities.getTotalElements());
            causePage.setCauses(causeEntitiesToCause(causeEntities.getContent()));
        } else {
            causeEntities = this.causeRepository.findByCurrentState(param, pageable);
            causePage.setTotalPages(causeEntities.getTotalPages());
            causePage.setTotalElements(causeEntities.getTotalElements());
            causePage.setCauses(causeEntitiesToCause(causeEntities.getContent()));
        }
        return causePage;
    }


    /*
     ALL = 0
     RECENT_CAUSE = 1
     Top-Funded = 2
     Most Popular = 3
     */

    private CausePage fetchCauseByCategory(final int sortBy, final String categoryIdentifier, final Pageable pageable) {
        if (categoryIdentifier != null) {
            Optional<CategoryEntity> categoryEntity = this.categoryRepository.findByIdentifier(categoryIdentifier.toLowerCase());
            return categoryEntity.map(categoryEntity1 -> {
                final CausePage causePage = new CausePage();
                List<CauseEntity> allCauseEntities = this.causeRepository.findByCategoryAndCurrentStateAndStartDateLessThanEqual(categoryEntity.get(), Cause.State.ACTIVE.name(), LocalDateTime.now(Clock.systemUTC()));
                List<Cause> causes = this.causeEntitiesToCause(allCauseEntities);
                return this.sortedCause(sortBy, causes, pageable);
            }).orElseGet(() -> {
                final CausePage causePage = new CausePage();
                causePage.setCauses(Collections.emptyList());
                causePage.setTotalPages(1);
                causePage.setTotalElements((long) 0);
                return causePage;
            });
        } else {
            List<CauseEntity> allCauseEntities = this.causeRepository.findByCurrentStateAndStartDateLessThanEqual(Cause.State.ACTIVE.name(), LocalDateTime.now(Clock.systemUTC()));
            List<Cause> causes = this.causeEntitiesToCause(allCauseEntities);
            return this.sortedCause(sortBy, causes, pageable);
        }
    }

    private List<Cause> causeEntitiesToCause(List<CauseEntity> causeEntities) {
        final ArrayList<Cause> causes = new ArrayList<>(causeEntities.size());
        for (CauseEntity causeEntity : causeEntities) {
            final Cause cause = CauseMapper.map(causeEntity);

            if (cause.getAccountNumber() != null) {
                final List<CauseJournalEntry> journalEntry = this.accountingAdaptor.fetchJournalEntriesJournalEntries(cause.getAccountNumber());
                cause.setCauseStatistics(CauseStatisticsMapper.map(journalEntry));
            }

            cause.setAddress(AddressMapper.map(this.addressRepository.findByCause(causeEntity)));
            cause.setCauseCategories(CategoryMapper.map(causeEntity.getCategory()));
            setCauseExtendedAndResubmitValue(causeEntity, cause);
            setCauseDocuments(causeEntity, cause);
            setRatingsAndAverage(causeEntity, cause);
            causes.add(cause);
        }

        return causes;
    }

    public Optional<Cause> findCause(final String identifier) {
        return causeRepository.findByIdentifier(identifier).map(causeEntity -> {
            List<CauseUpdateEntity> entities = this.causeUpdateRepository.findByCauseEntity(causeEntity);
            final Cause cause = CauseMapper.map(causeEntity);
            Address address = AddressMapper.map(this.addressRepository.findByCause(causeEntity));
            cause.setAddress(address);
            cause.setCauseCategories(CategoryMapper.map(causeEntity.getCategory()));
            cause.setNumberOfUpdateProvided(entities.size());
            if (cause.getAccountNumber() != null) {
                final List<CauseJournalEntry> journalEntry = accountingAdaptor.fetchJournalEntriesJournalEntries(cause.getAccountNumber());
                cause.setCauseStatistics(CauseStatisticsMapper.map(journalEntry));
            }
            setCauseDocuments(causeEntity, cause);
            setCauseExtendedAndResubmitValue(causeEntity, cause);
            setRatingsAndAverage(causeEntity, cause);
            return cause;
        });
    }


    private void setCauseExtendedAndResubmitValue(CauseEntity causeEntity, Cause cause) {
        cause.setNumberOfExtended(this.causeStateRepository.totalStateByCauseIdentifier(causeEntity.getIdentifier(), new HashSet<>(Collections.singletonList(Cause.State.EXTENDED.name()))));
        cause.setNumberOfResubmit(this.causeStateRepository.totalStateByCauseIdentifier(causeEntity.getIdentifier(), new HashSet<>(Collections.singletonList(Cause.State.PENDING.name()))));
    }


    public CaAdminCauseData findAllCauseData() {
        CaAdminCauseData caAdminCauseData = new CaAdminCauseData();
        List<CauseEntity> causeEntities = this.causeRepository.findAll();
        final LocalDateTime startDateOfThisWeek = LocalDateTime.now(Clock.systemUTC()).minusDays(7);
        caAdminCauseData.setNoOfCause((long) causeEntities.size());
        caAdminCauseData.setActiveCause(causeEntities.stream().filter(causeEntity -> causeEntity.getCurrentState().equals(Cause.State.ACTIVE.name())).count());
        caAdminCauseData.setCausePending(causeEntities.stream().filter(causeEntity -> causeEntity.getCurrentState().equals(Cause.State.ACTIVE.name())).count());
        caAdminCauseData.setNoOfCauseThisWeek(causeEntities.stream().filter(causeEntity -> causeEntity.getCreatedOn().isAfter(startDateOfThisWeek)).count());
        caAdminCauseData.setCauseCompleted(causeEntities.stream().filter(causeEntity -> causeEntity.getCurrentState().equals(Cause.State.CLOSED.name())).count());

        caAdminCauseData.setCausePerMonth(CauseMapper.mapAll(causeEntities));
        caAdminCauseData.setActiveCausePerMonth(CauseMapper.map(causeEntities, Cause.State.ACTIVE));
        caAdminCauseData.setInactiveCausePerMonth(CauseMapper.map(causeEntities, Cause.State.INACTIVE));

        return caAdminCauseData;
    }

    public NGOStatistics fetchCauseByCreatedBy(final String identifier) {
        final List<CauseEntity> causeEntities = this.causeRepository.findByCreatedByAndCurrentStateNot(identifier, Cause.State.DELETED.name());
        ArrayList<CauseStatistics> causeStatistics = causeEntities.stream().filter(causeEntity -> causeEntity.getAccountNumber() != null).map(causeEntity -> CauseStatisticsMapper.map(accountingAdaptor.fetchJournalEntriesJournalEntries(causeEntity.getAccountNumber()))).collect(Collectors.toCollection(() -> new ArrayList<>(causeEntities.size())));
        final NGOStatistics ngoStatistics = new NGOStatistics();
        ngoStatistics.setTotalRaisedAmount(causeStatistics.stream().mapToDouble(CauseStatistics::getTotalRaised).sum());
        ngoStatistics.setTotalSupporter(causeStatistics.stream().mapToInt(CauseStatistics::getTotalSupporter).sum());
        ngoStatistics.setTotalCause(causeEntities.size());
        return ngoStatistics;
    }

    public NGOProfileStatistics findCausebyCreatedByForNgoProfile(final String identifier) {
        final List<CauseEntity> causeEntities = this.causeRepository.findByCreatedByAndCurrentState(identifier, Cause.State.ACTIVE.name());
        ArrayList<CauseStatistics> causeStatistics = causeEntities
                .stream()
                .filter(causeEntity -> causeEntity.getAccountNumber() != null)
                .map(causeEntity -> accountingAdaptor.fetchJournalEntriesJournalEntries(causeEntity.getAccountNumber()))
                .map(CauseStatisticsMapper::map)
                .collect(Collectors.toCollection(() -> new ArrayList<>(causeEntities.size())));

        final NGOStatistics ngoStatistics = new NGOStatistics();
        ngoStatistics.setTotalRaisedAmount(causeStatistics.stream().mapToDouble(CauseStatistics::getTotalRaised).sum());
        ngoStatistics.setTotalSupporter(causeStatistics.stream().mapToInt(CauseStatistics::getTotalSupporter).sum());
        ngoStatistics.setTotalCause(causeEntities.size());

        NGOProfileStatistics statistics = new NGOProfileStatistics();
        statistics.setNgoStatistics(ngoStatistics);
        statistics.setCauseList(this.causeEntitiesToCause(causeEntities));

        List<CauseJournalEntry> causeJournalEntries = causeStatistics.stream()
                .flatMap(entry -> entry.getJournalEntry().stream()).collect(Collectors.toList());
        statistics.setCauseJournalEntries(causeJournalEntries);
        return statistics;
    }


    private void setRatingsAndAverage(CauseEntity causeEntity, Cause cause) {
        List<CauseRating> causeRatings = causeRepository.findByIdentifier(causeEntity.getIdentifier())
                .map(this.ratingRepository::findAllByCauseAndActiveIsTrue)
                .map(ratingEntity -> this.fetchNestedComments(ratingEntity.collect(Collectors.toList()), (long) -1))
                .orElse(Stream.empty())
                .collect(Collectors.toList());

        cause.setCauseRatings(causeRatings);
        cause.setAvgRating(causeRatings.stream().mapToDouble(CauseRating::getRating).average().orElse(0));
    }


    public final List<CauseRating> fetchRatingsAndCommentsByCause(final String identifier) {
        return causeRepository.findByIdentifier(identifier)
                .map(this.ratingRepository::findAllByCauseAndActiveIsTrue)
                .map(ratingEntity -> this.fetchNestedComments(ratingEntity.collect(Collectors.toList()), (long) -1))
                .orElse(Stream.empty())
                .collect(Collectors.toList());

    }


    private Stream<CauseRating> fetchNestedComments(List<RatingEntity> ratingEntities, final Long ref) {
        return ratingEntities
                .stream()
                .filter(ent -> ent.getRef() == ref)  //equal equal is used cause ref value can be null value which can cause null pointer issue
                .map(entity -> {
                    CauseRating causeRating = RatingMapper.map(entity);
                    Stream<RatingEntity> ratingEntityStream = ratingEntities
                            .stream()
                            .filter(cdata -> cdata.getRef() == entity.getId()); //equal equal is used cause ref value can be null value which can cause null pointer issue
                    if (ratingEntityStream.findAny().isPresent()) {
                        causeRating.setCauseRatings(fetchNestedComments(ratingEntities, entity.getId())
                                .sorted(Comparator.comparing(CauseRating::getCreatedOn, Comparator.reverseOrder()))
                                .collect(Collectors.toList()));
                    }
                    return causeRating;
                });
    }


    public final Optional<PortraitEntity> findPortrait(final String identifier) {
        return causeRepository.findByIdentifier(identifier)
                .map(portraitRepository::findByCause);
    }

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


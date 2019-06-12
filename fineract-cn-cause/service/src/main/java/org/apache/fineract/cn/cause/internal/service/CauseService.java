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
import org.springframework.data.domain.*;
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
    private final CommandRepository commandRepository;
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
                        final CommandRepository commandRepository,
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
        this.commandRepository = commandRepository;
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


    public Optional<CauseEntity> findCauseByIdentifier(final String identifier) {
        return this.causeRepository.findByIdentifier(identifier);
    }

    public Optional<Cause> findCause(final String identifier) {
        return causeRepository.findByIdentifier(identifier).map(causeEntity -> {
            List<CauseUpdateEntity> entities = this.causeUpdateRepository.findByCauseEntity(causeEntity);
            final Cause cause = CauseMapper.map(causeEntity);
//            final int numberOfUpdateRequired = (causeEntity.getCauseImplementationDuration() * 4) / causeEntity.getFrequencyCauseImplementationUpdates();
//            cause.setTotalNumberOfUpdates(numberOfUpdateRequired);
            cause.setNumberOfUpdateProvided(entities.size());

//            LocalDateTime dateTime = causeEntity.getEndDate().plusWeeks()
//            cause.setNumberOfDaysLeftForNextUpdate(numberOfUpdateRequired - entities.size());
//            cause.setCa
            setCauseDocuments(causeEntity, cause);
            AddressEntity addressEntity = this.addressRepository.findByCause(causeEntity);
            cause.setAddress(AddressMapper.map(addressEntity));
            cause.setCauseCategories(CategoryMapper.map(causeEntity.getCategory()));
            setCauseExtendedAndResubmitValue(causeEntity, cause);
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
        final List<DocumentPageEntity> pageEntity = this.documentPageRepository.findByDocument(documentEntity);
        causeDocument.setCauseDocumentPages(DocumentMapper.map(pageEntity));
        cause.setCauseDocument(causeDocument);
        cause.setCauseFiles(DocumentMapper.mapFile(pageEntity));
    }


    public List<CauseDocumentPage> causeDocumentPages(final String identifier) {
        Optional<CauseEntity> causeEntity = causeRepository.findByIdentifier(identifier);

        if (causeEntity.isPresent()) {
            DocumentEntity entity = documentRepository.findByCauseAndCreatedBy(causeEntity.get(), UserContextHolder.checkedGetUser());
            return documentPageRepository.findByDocument(entity).stream().map(DocumentMapper::map).collect(Collectors.toList());
        } else {
            throw ServiceException.notFound("Cause not found");
        }
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

    public CausePage fetchCauseForCustomer(final String param, final int sortBy, final Pageable pageable) {

        if (param == null) {
            CausePage causePage = new CausePage();
            final List<CauseEntity> causeEntities;
            switch (sortBy) {
                case 1:
                    causeEntities = this.causeRepository.findByCurrentStateNotAndStartDateLessThanEqual(Cause.State.DELETED.name(), LocalDateTime.now(Clock.systemUTC()))
                            .stream()
                            .sorted(Comparator.comparing(CauseEntity::getCreatedOn, Comparator.nullsLast(Comparator.reverseOrder())))
                            .collect(Collectors.toList());

                    final Page<CauseEntity> page = new PageImpl<>(causeEntities, pageable, causeEntities.size());
                    causePage.setTotalPages(page.getTotalPages());
                    causePage.setTotalElements(page.getTotalElements());
                    causePage.setCauses(causeArrayList(page.getContent()));
                    break;
                case 2:

                    causeEntities = this.causeRepository.findByCurrentStateNotAndStartDateLessThanEqual(Cause.State.DELETED.name(), LocalDateTime.now(Clock.systemUTC()));
                    List<Cause> topFundedcauses = causeArrayList(causeEntities)
                            .stream()
                            .sorted(Comparator.nullsLast((e1, e2) -> e2.getCauseStatistics().getTotalRaised().compareTo(e1.getCauseStatistics().getTotalRaised())))
                            .collect(Collectors.toList());

                    final Page<Cause> topFundedcausesPage = new PageImpl<>(topFundedcauses, pageable, causeEntities.size());
                    causePage.setTotalPages(topFundedcausesPage.getTotalPages());
                    causePage.setTotalElements(topFundedcausesPage.getTotalElements());
                    causePage.setCauses(topFundedcauses);

                    break;
                case 3:
                    causeEntities = this.causeRepository.findByCurrentStateNotAndStartDateLessThanEqual(Cause.State.DELETED.name(), LocalDateTime.now(Clock.systemUTC()));
                    List<Cause> popularCauses = causeArrayList(causeEntities)
                            .stream()
                            .sorted(Comparator.nullsLast((e1, e2) -> e2.getCauseStatistics().getTotalSupporter() - (e1.getCauseStatistics().getTotalSupporter())))
                            .collect(Collectors.toList());

                    final Page<Cause> popularCausesPage = new PageImpl<>(popularCauses, pageable, causeEntities.size());
                    causePage.setTotalPages(popularCausesPage.getTotalPages());
                    causePage.setTotalElements(popularCausesPage.getTotalElements());
                    causePage.setCauses(popularCauses);

                    break;
                default:
                    final Page<CauseEntity> allPage = this.causeRepository.findByCurrentStateNotAndStartDateLessThanEqual(Cause.State.DELETED.name(), LocalDateTime.now(Clock.systemUTC()), pageable);
                    causePage.setTotalPages(allPage.getTotalPages());
                    causePage.setTotalElements(allPage.getTotalElements());
                    causePage.setCauses(causeArrayList(allPage.getContent()));
                    break;
            }

            return causePage;
        } else {
            return fetchCauseByCategory(param, sortBy, pageable);
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
            causePage.setCauses(causeArrayList(causeEntities.getContent()));
        } else {
            causeEntities = this.causeRepository.findByCreatedByAndCurrentState(userIdentifier, param, pageable);
            causePage.setTotalPages(causeEntities.getTotalPages());
            causePage.setTotalElements(causeEntities.getTotalElements());
            causePage.setCauses(causeArrayList(causeEntities.getContent()));
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
            causePage.setCauses(causeArrayList(causeEntities.getContent()));
        } else {
            causeEntities = this.causeRepository.findByCurrentState(param, pageable);
            causePage.setTotalPages(causeEntities.getTotalPages());
            causePage.setTotalElements(causeEntities.getTotalElements());
            causePage.setCauses(causeArrayList(causeEntities.getContent()));
        }
        return causePage;
    }

    private CausePage fetchCauseByCategory(final String categoryIdentifier, final int sortBy, final Pageable pageable) {
        final CausePage causePage = new CausePage();
        final List<CauseEntity> causeEntities;
        Optional<CategoryEntity> categoryEntity;
        if (categoryIdentifier != null) {
            categoryEntity = this.categoryRepository.findByIdentifier(categoryIdentifier.toLowerCase());
            if (categoryEntity.isPresent()) {

                switch (sortBy) {
                    case 1:
                        causeEntities = this.causeRepository.findByCategoryAndCurrentStateAndStartDateLessThanEqual(categoryEntity.get(), Cause.State.ACTIVE.name(), LocalDateTime.now(Clock.systemUTC()))
                                .stream()
                                .sorted(Comparator.comparing(CauseEntity::getCreatedOn, Comparator.nullsLast(Comparator.reverseOrder())))
                                .collect(Collectors.toList());

                        final Page<CauseEntity> page = new PageImpl<>(causeEntities, pageable, causeEntities.size());
                        causePage.setTotalPages(page.getTotalPages());
                        causePage.setTotalElements(page.getTotalElements());
                        causePage.setCauses(causeArrayList(page.getContent()));
                        break;
                    case 2:

                        causeEntities = this.causeRepository.findByCategoryAndCurrentStateAndStartDateLessThanEqual(categoryEntity.get(), Cause.State.ACTIVE.name(), LocalDateTime.now(Clock.systemUTC()));
                        List<Cause> topFundedcauses = causeArrayList(causeEntities)
                                .stream()
                                .sorted(Comparator.nullsLast((e1, e2) -> e2.getCauseStatistics().getTotalRaised().compareTo(e1.getCauseStatistics().getTotalRaised())))
                                .collect(Collectors.toList());

                        final Page<Cause> topFundedcausesPage = new PageImpl<>(topFundedcauses, pageable, causeEntities.size());
                        causePage.setTotalPages(topFundedcausesPage.getTotalPages());
                        causePage.setTotalElements(topFundedcausesPage.getTotalElements());
                        causePage.setCauses(topFundedcauses);

                        break;
                    case 3:
                        causeEntities = this.causeRepository.findByCategoryAndCurrentStateAndStartDateLessThanEqual(categoryEntity.get(), Cause.State.ACTIVE.name(), LocalDateTime.now(Clock.systemUTC()));
                        List<Cause> popularCauses = causeArrayList(causeEntities)
                                .stream()
                                .sorted(Comparator.nullsLast((e1, e2) -> e2.getCauseStatistics().getTotalSupporter() - (e1.getCauseStatistics().getTotalSupporter())))
                                .collect(Collectors.toList());

                        final Page<Cause> popularCausesPage = new PageImpl<>(popularCauses, pageable, causeEntities.size());
                        causePage.setTotalPages(popularCausesPage.getTotalPages());
                        causePage.setTotalElements(popularCausesPage.getTotalElements());
                        causePage.setCauses(popularCauses);

                        break;
                    default:
                        Page<CauseEntity> allCauseEntities = this.causeRepository.findByCategoryAndCurrentStateAndStartDateLessThanEqual(categoryEntity.get(), Cause.State.ACTIVE.name(), LocalDateTime.now(Clock.systemUTC()), pageable);
                        causePage.setCauses(causeArrayList(allCauseEntities.getContent()));
                        causePage.setTotalPages(allCauseEntities.getTotalPages());
                        causePage.setTotalElements(allCauseEntities.getTotalElements());
                        break;
                }

            } else {
                causePage.setCauses(Collections.emptyList());
                causePage.setTotalPages(1);
                causePage.setTotalElements((long) 0);
            }
        } else {

            switch (sortBy) {
                case 1:
                    causeEntities = this.causeRepository.findByCurrentStateAndStartDateLessThanEqual(Cause.State.ACTIVE.name(), LocalDateTime.now(Clock.systemUTC()))
                            .stream()
                            .sorted(Comparator.comparing(CauseEntity::getCreatedOn, Comparator.nullsLast(Comparator.reverseOrder())))
                            .collect(Collectors.toList());

                    final Page<CauseEntity> page = new PageImpl<>(causeEntities, pageable, causeEntities.size());
                    causePage.setTotalPages(page.getTotalPages());
                    causePage.setTotalElements(page.getTotalElements());
                    causePage.setCauses(causeArrayList(page.getContent()));
                    break;
                case 2:
                    causeEntities = this.causeRepository.findByCurrentStateAndStartDateLessThanEqual(Cause.State.ACTIVE.name(), LocalDateTime.now(Clock.systemUTC()));
                    List<Cause> topFundedcauses = causeArrayList(causeEntities)
                            .stream()
                            .sorted(Comparator.nullsLast((e1, e2) -> e2.getCauseStatistics().getTotalRaised().compareTo(e1.getCauseStatistics().getTotalRaised())))
                            .collect(Collectors.toList());

                    final Page<Cause> topFundedcausesPage = new PageImpl<>(topFundedcauses, pageable, causeEntities.size());
                    causePage.setTotalPages(topFundedcausesPage.getTotalPages());
                    causePage.setTotalElements(topFundedcausesPage.getTotalElements());
                    causePage.setCauses(topFundedcauses);

                    break;
                case 3:
                    causeEntities = this.causeRepository.findByCurrentStateAndStartDateLessThanEqual(Cause.State.ACTIVE.name(), LocalDateTime.now(Clock.systemUTC()));
                    List<Cause> popularCauses = causeArrayList(causeEntities)
                            .stream()
                            .sorted(Comparator.nullsLast((e1, e2) -> e2.getCauseStatistics().getTotalSupporter() - (e1.getCauseStatistics().getTotalSupporter())))
                            .collect(Collectors.toList());

                    final Page<Cause> popularCausesPage = new PageImpl<>(popularCauses, pageable, causeEntities.size());
                    causePage.setTotalPages(popularCausesPage.getTotalPages());
                    causePage.setTotalElements(popularCausesPage.getTotalElements());
                    causePage.setCauses(popularCauses);

                    break;
                default:
                    Page<CauseEntity> allCauseEntities = this.causeRepository.findByCurrentStateAndStartDateLessThanEqual(Cause.State.ACTIVE.name(), LocalDateTime.now(Clock.systemUTC()), pageable);
                    causePage.setCauses(causeArrayList(allCauseEntities.getContent()));
                    causePage.setTotalPages(allCauseEntities.getTotalPages());
                    causePage.setTotalElements(allCauseEntities.getTotalElements());
                    break;
            }
        }

        return causePage;

    }

    private ArrayList<Cause> causeArrayList(List<CauseEntity> causeEntities) {
        final ArrayList<Cause> causes = new ArrayList<>(causeEntities.size());
        for (CauseEntity causeEntity : causeEntities) {
            final Cause cause = CauseMapper.map(causeEntity);
            if (cause.getAccountNumber() != null) {
                final List<JournalEntry> journalEntry = this.accountingAdaptor.fetchJournalEntriesJournalEntries(cause.getAccountNumber());
                cause.setCauseStatistics(CauseStatisticsMapper.map(journalEntry));
            }

            cause.setAddress(AddressMapper.map(this.addressRepository.findByCause(causeEntity)));
            cause.setCauseCategories(CategoryMapper.map(causeEntity.getCategory()));

            setCauseExtendedAndResubmitValue(causeEntity, cause);

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

    private void setCauseExtendedAndResubmitValue(CauseEntity causeEntity, Cause cause) {
        cause.setNumberOfExtended(this.causeStateRepository.totalStateByCauseIdentifier(causeEntity.getIdentifier(), new HashSet<>(Collections.singletonList(Cause.State.EXTENDED.name()))));
        cause.setNumberOfResubmit(this.causeStateRepository.totalStateByCauseIdentifier(causeEntity.getIdentifier(), new HashSet<>(Collections.singletonList(Cause.State.PENDING.name()))));
    }


    public CaAdminCauseData findAllCauseData() {
        CaAdminCauseData caAdminCauseData = new CaAdminCauseData();
        List<CauseEntity> causeEntities = this.causeRepository.findAll();

//        final DayOfWeek firstDayOfWeek = WeekFields.ISO.getFirstDayOfWeek();
//        final LocalDateTime startDateOfThisWeek = LocalDateTime.now(Clock.systemUTC()).with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
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


    public Pageable createPageRequest(final Integer pageIndex, final Integer size, final String sortColumn, final String sortDirection) {
        final int pageIndexToUse = pageIndex != null ? pageIndex : 0;
        final int sizeToUse = size != null ? size : 20;
        final String sortColumnToUse = sortColumn != null ? sortColumn : "identifier";
        final Sort.Direction direction = sortDirection != null ? Sort.Direction.valueOf(sortDirection.toUpperCase()) : Sort.Direction.ASC;
        return new PageRequest(pageIndexToUse, sizeToUse, direction, sortColumnToUse);
    }

}


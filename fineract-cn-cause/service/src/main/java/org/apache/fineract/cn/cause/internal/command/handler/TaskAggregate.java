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

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.cause.api.v1.CauseEventConstants;
import org.apache.fineract.cn.cause.api.v1.domain.Command;
import org.apache.fineract.cn.cause.api.v1.domain.TaskDefinition;
import org.apache.fineract.cn.cause.internal.command.AddTaskDefinitionToCauseCommand;
import org.apache.fineract.cn.cause.internal.command.CreateTaskDefinitionCommand;
import org.apache.fineract.cn.cause.internal.command.ExecuteTaskForCauseCommand;
import org.apache.fineract.cn.cause.internal.command.UpdateTaskDefinitionCommand;
import org.apache.fineract.cn.cause.internal.mapper.TaskDefinitionMapper;
import org.apache.fineract.cn.cause.internal.mapper.TaskInstanceMapper;
import org.apache.fineract.cn.cause.internal.repository.*;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "unused"})
@Aggregate
public class TaskAggregate {

    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskInstanceRepository taskInstanceRepository;
    private final CauseRepository causeRepository;

    @Autowired
    public TaskAggregate(final TaskDefinitionRepository taskDefinitionRepository,
                         final TaskInstanceRepository taskInstanceRepository,
                         final CauseRepository causeRepository) {
        super();
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskInstanceRepository = taskInstanceRepository;
        this.causeRepository = causeRepository;
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_TASK)
    public String createTaskDefinition(final CreateTaskDefinitionCommand createTaskDefinitionCommand) {
        this.taskDefinitionRepository.save(TaskDefinitionMapper.map(createTaskDefinitionCommand.taskDefinition()));
        return createTaskDefinitionCommand.taskDefinition().getIdentifier();
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.PUT_TASK)
    public String updateTaskDefinition(final UpdateTaskDefinitionCommand updateTaskDefinitionCommand) {
        final TaskDefinitionEntity taskDefinitionEntity = this.taskDefinitionRepository.findByIdentifier(updateTaskDefinitionCommand.identifier());

        final TaskDefinition updatedTaskDefinition = updateTaskDefinitionCommand.taskDefinition();
        taskDefinitionEntity.setName(updatedTaskDefinition.getName());
        taskDefinitionEntity.setDescription(updatedTaskDefinition.getDescription());
        taskDefinitionEntity.setAssignedCommands(StringUtils.join(updatedTaskDefinition.getCommands(), ";"));
        taskDefinitionEntity.setMandatory(updatedTaskDefinition.getMandatory());
        taskDefinitionEntity.setPredefined(updatedTaskDefinition.getPredefined());

        this.taskDefinitionRepository.save(taskDefinitionEntity);

        return updatedTaskDefinition.getIdentifier();
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.PUT_CAUSE)
    public String addTaskToCause(final AddTaskDefinitionToCauseCommand addTaskDefinitionToCauseCommand) {
        final TaskDefinitionEntity taskDefinitionEntity =
                this.taskDefinitionRepository.findByIdentifier(addTaskDefinitionToCauseCommand.taskIdentifier());

        final CauseEntity causeEntity = findCauseEntityOrThrow(addTaskDefinitionToCauseCommand.causeIdentifier());

        this.taskInstanceRepository.save(TaskInstanceMapper.create(taskDefinitionEntity, causeEntity));

        return addTaskDefinitionToCauseCommand.causeIdentifier();
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.PUT_CAUSE)
    public String executeTaskForCause(final ExecuteTaskForCauseCommand executeTaskForCauseCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(executeTaskForCauseCommand.causeIdentifier());
        final List<TaskInstanceEntity> taskInstanceEntities = this.taskInstanceRepository.findByCause(causeEntity);
        if (taskInstanceEntities != null) {
            final Optional<TaskInstanceEntity> taskInstanceEntityOptional = taskInstanceEntities
                    .stream()
                    .filter(
                            taskInstanceEntity -> taskInstanceEntity.getTaskDefinition().getIdentifier().equals(executeTaskForCauseCommand.taskIdentifier())
                                    && taskInstanceEntity.getExecutedBy() == null
                    )
                    .findAny();

            if (taskInstanceEntityOptional.isPresent()) {
                final TaskInstanceEntity taskInstanceEntity = taskInstanceEntityOptional.get();
                taskInstanceEntity.setExecutedBy(UserContextHolder.checkedGetUser());
                taskInstanceEntity.setExecutedOn(LocalDateTime.now(Clock.systemUTC()));
                this.taskInstanceRepository.save(taskInstanceEntity);
            }
        }

        return executeTaskForCauseCommand.causeIdentifier();
    }

    @Transactional
    public void onCauseCommand(final CauseEntity causeEntity, Command.Action action) {
        final List<TaskDefinitionEntity> predefinedTasks = this.taskDefinitionRepository.findByAssignedCommandsContaining(action.name());
        if (predefinedTasks != null && predefinedTasks.size() > 0) {
            this.taskInstanceRepository.save(predefinedTasks.stream()
                    .filter(TaskDefinitionEntity::isPredefined)
                    .map(taskDefinitionEntity -> TaskInstanceMapper.create(taskDefinitionEntity, causeEntity))
                    .collect(Collectors.toList())
            );
        }
    }

    @Transactional
    public Boolean openTasksForCauseExist(final CauseEntity causeEntity, final String command) {
        final List<TaskInstanceEntity> taskInstanceEntities = this.taskInstanceRepository.findByCause(causeEntity);

        //noinspection SimplifiableIfStatement
        if (taskInstanceEntities != null) {
            return taskInstanceEntities
                    .stream()
                    .filter(taskInstanceEntity -> taskInstanceEntity.getTaskDefinition().getAssignedCommands().contains(command))
                    .filter(taskInstanceEntity -> taskInstanceEntity.getTaskDefinition().isMandatory())
                    .anyMatch(taskInstanceEntity -> taskInstanceEntity.getExecutedBy() == null);
        } else {
            return false;
        }
    }

    private CauseEntity findCauseEntityOrThrow(String identifier) {
        return this.causeRepository.findByIdentifier(identifier)
                .orElseThrow(() -> ServiceException.notFound("Cause ''{0}'' not found", identifier));
    }
}

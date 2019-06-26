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
package org.apache.fineract.cn.cause.internal.mapper;

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.cause.api.v1.domain.CauseComment;
import org.apache.fineract.cn.cause.internal.repository.CauseEntity;
import org.apache.fineract.cn.cause.internal.repository.CommentEntity;
import org.apache.fineract.cn.lang.DateConverter;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Md Robiul Hassan
 */
public class CommentMapper {
    private CommentMapper() {
        super();
    }

    public static CauseComment map(final CommentEntity entity) {
        final CauseComment ret = new CauseComment();
        ret.setId(entity.getId());
        ret.setActive(entity.getActive());
        ret.setCreatedBy(entity.getCreatedBy());
        ret.setCreatedOn(DateConverter.toIsoString(entity.getCreatedOn()));
        ret.setComment(entity.getComment());
        ret.setRef(entity.getRef());
        return ret;
    }

    public static CommentEntity map(final CauseComment comment, final CauseEntity causeEntity) {
        final CommentEntity ret = new CommentEntity();
        ret.setActive(comment.isActive());
        ret.setCreatedBy(UserContextHolder.checkedGetUser());
        ret.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        ret.setComment(comment.getComment());
        ret.setRef(comment.getRef());
        ret.setCause(causeEntity);
        return ret;
    }

//    public static CauseComment

    public static List<CauseComment> map(Stream<CommentEntity> byCause) {
        return byCause.map(CommentMapper::map).collect(Collectors.toList());
    }
}


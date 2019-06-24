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
package org.apache.fineract.cn.cause.api.v1.events;

import java.util.Objects;

/**
 * @author Padma Raju Sattineni
 */
public class RatingEvent {

    private String causeIdentifier;

    private int rating;

    public RatingEvent(String causeIdentifier, int rating) {
        this.causeIdentifier = causeIdentifier;
        this.rating = rating;
    }

    public String getCauseIdentifier() {
        return causeIdentifier;
    }

    public void setCauseIdentifier(String causeIdentifier) {
        this.causeIdentifier = causeIdentifier;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RatingEvent that = (RatingEvent) o;
        return Objects.equals(causeIdentifier, that.causeIdentifier) &&
                Objects.equals(rating, that.rating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(causeIdentifier, rating);
    }

    @Override
    public String toString() {
        return "RatingEvent{" +
                "causeIdentifier='" + causeIdentifier + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }
}


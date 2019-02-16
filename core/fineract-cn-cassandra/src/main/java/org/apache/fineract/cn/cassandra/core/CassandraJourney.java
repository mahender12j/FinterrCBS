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
package org.apache.fineract.cn.cassandra.core;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.ParseUtils;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import org.apache.fineract.cn.lang.ApplicationName;
import org.slf4j.Logger;

import java.util.Objects;

@SuppressWarnings({"WeakerAccess", "unused"})
public class CassandraJourney {

  private final Logger logger;
  private final ApplicationName applicationName;
  private final Session session;
  private final String schemaTableName;

  public CassandraJourney(final Logger logger, final ApplicationName applicationName, final Session session) {
    super();
    this.applicationName = applicationName;
    this.logger = logger;
    this.session = session;
    this.schemaTableName = this.applicationName.getServiceName() + "_cassandra_schema_table";
  }

  public void start(final CassandraJourneyRoute cassandraJourneyRoute) {
    // check for version
    final ResultSet resultSet = session.execute(
        QueryBuilder
            .select("hash_value")
            .from(this.schemaTableName)
            .where(QueryBuilder.eq("version", cassandraJourneyRoute.getVersion()))
    );

    if (!resultSet.isExhausted()) {
      final Row row = resultSet.one();
      final Integer fetchedHashValue = row.get("hash_value", Integer.class);
      if (!Objects.equals(fetchedHashValue, cassandraJourneyRoute.getHashValue())) {
        throw new IllegalStateException("Version mismatch for " + cassandraJourneyRoute.getVersion());
      }
    } else {
      cassandraJourneyRoute.getCassandraJourneyWaypoints()
          .forEach(waypoint -> this.session.execute(waypoint.getStatement()));

      this.session.execute(
          QueryBuilder
              .insertInto(this.schemaTableName)
              .value("version", cassandraJourneyRoute.getVersion())
              .value("hash_value", cassandraJourneyRoute.getHashValue())
      );
    }
  }

  void init() {
    final KeyspaceMetadata keyspaceMetadata =
        session.getCluster().getMetadata().getKeyspace(session.getLoggedKeyspace());
    final TableMetadata schemaTable = keyspaceMetadata.getTable(ParseUtils.quote(this.schemaTableName));
    if (schemaTable == null) {
      try {
        session.execute(SchemaBuilder
            .createTable(this.schemaTableName)
            .addPartitionKey("version", DataType.text())
            .addColumn("hash_value", DataType.cint())
            .buildInternal()
        );
      } catch (final Throwable th) {
        this.logger.warn("Schema table for '{}' already exists.", this.applicationName.getServiceName());
      }
    }
  }
}

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
package org.apache.fineract.cn.cause.rest.config;


import org.apache.fineract.cn.accounting.api.v1.client.JournalManager;
import org.apache.fineract.cn.anubis.config.EnableAnubis;
import org.apache.fineract.cn.async.config.EnableAsync;
import org.apache.fineract.cn.cassandra.config.EnableCassandra;
import org.apache.fineract.cn.cause.ServiceConstants;
import org.apache.fineract.cn.cause.internal.config.CauseServiceConfiguration;
import org.apache.fineract.cn.command.config.EnableCommandProcessing;
import org.apache.fineract.cn.customer.api.v1.client.CustomerManager;
import org.apache.fineract.cn.lang.ApplicationName;
import org.apache.fineract.cn.lang.config.EnableApplicationName;
import org.apache.fineract.cn.lang.config.EnableServiceException;
import org.apache.fineract.cn.lang.config.EnableTenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableAsync
@EnableTenantContext
@EnableCassandra
@EnableCommandProcessing
@EnableAnubis
@EnableServiceException
@EnableApplicationName
@EnableFeignClients(
        clients = {
                JournalManager.class,
                CustomerManager.class
        }
)
@ComponentScan({
        "org.apache.fineract.cn.cause.rest.controller"
})
@Import({
        CauseServiceConfiguration.class
})
@EnableConfigurationProperties({UploadProperties.class})
public class CauseRestConfiguration extends WebMvcConfigurerAdapter {

    public CauseRestConfiguration() {
        super();
    }

    @Bean(name = ServiceConstants.LOGGER_NAME)
    public Logger logger(final ApplicationName applicationName) {
        return LoggerFactory.getLogger(applicationName.getServiceName());
    }

    @Override
    public void configurePathMatch(final PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(Boolean.FALSE);
    }
}

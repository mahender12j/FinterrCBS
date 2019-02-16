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
package org.apache.fineract.cn.anubis;

import org.apache.fineract.cn.anubis.example.simple.Example;
import org.apache.fineract.cn.anubis.example.simple.ExampleConfiguration;
import org.apache.fineract.cn.anubis.suites.SuiteTestEnvironment;
import org.apache.fineract.cn.anubis.test.v1.TenantApplicationSecurityEnvironmentTestRule;
import org.apache.fineract.cn.anubis.suites.SuiteTestEnvironment;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Myrle Krantz
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    classes = AbstractSimpleTest.TestConfiguration.class)
public class AbstractSimpleTest extends SuiteTestEnvironment {
  private static final String LOGGER_QUALIFIER = "test-logger";

  @Configuration
  @EnableFeignClients(basePackages = {"org.apache.fineract.cn.anubis.example.simple"})
  @RibbonClient(name = APP_NAME)
  @Import({ExampleConfiguration.class})
  public static class TestConfiguration {
    public TestConfiguration() {
      super();
    }

    @Bean(name = LOGGER_QUALIFIER)
    public Logger logger() {
      return LoggerFactory.getLogger(APP_NAME + "-logger");
    }
  }

  @SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "SpringJavaAutowiringInspection"})
  @Autowired
  Example example;

  @SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "SpringJavaAutowiringInspection"})
  @Autowired
  @Qualifier(value = LOGGER_QUALIFIER)
  Logger logger;

  @Rule
  public final TenantApplicationSecurityEnvironmentTestRule tenantApplicationSecurityEnvironment
      = new TenantApplicationSecurityEnvironmentTestRule(testEnvironment);
}

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

import org.apache.fineract.cn.identity.api.v1.PermittableGroupIds;
import org.apache.fineract.cn.identity.api.v1.client.IdentityManager;
import org.apache.fineract.cn.identity.api.v1.domain.Authentication;
import org.apache.fineract.cn.identity.api.v1.domain.Password;
import org.apache.fineract.cn.identity.api.v1.domain.Permission;
import org.apache.fineract.cn.identity.api.v1.domain.Role;
import org.apache.fineract.cn.identity.api.v1.domain.UserWithPassword;
import org.apache.fineract.cn.identity.api.v1.events.ApplicationPermissionEvent;
import org.apache.fineract.cn.identity.api.v1.events.ApplicationSignatureEvent;
import org.apache.fineract.cn.identity.api.v1.events.EventConstants;

import java.util.Arrays;
import javax.annotation.PostConstruct;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.fineract.cn.anubis.api.v1.domain.AllowedOperation;
import org.apache.fineract.cn.anubis.api.v1.domain.Signature;
import org.apache.fineract.cn.anubis.test.v1.TenantApplicationSecurityEnvironmentTestRule;
import org.apache.fineract.cn.api.config.EnableApiFactory;
import org.apache.fineract.cn.api.context.AutoGuest;
import org.apache.fineract.cn.api.context.AutoUserContext;
import org.apache.fineract.cn.api.util.ApiFactory;
import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.identity.config.IdentityServiceConfig;
import org.apache.fineract.cn.lang.security.RsaKeyPairFactory;
import org.apache.fineract.cn.test.env.TestEnvironment;
import org.apache.fineract.cn.test.fixture.TenantDataStoreContextTestRule;
import org.apache.fineract.cn.test.listener.EnableEventRecording;
import org.apache.fineract.cn.test.listener.EventRecorder;
import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Myrle Krantz
 */
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = {AbstractIdentityTest.TestConfiguration.class})
@TestPropertySource(properties = {"cassandra.cl.read = LOCAL_QUORUM", "cassandra.cl.write = LOCAL_QUORUM", "cassandra.cl.delete = LOCAL_QUORUM", "identity.token.refresh.secureCookie = false", "identity.passwordExpiresInDays = 93"})
public class AbstractIdentityTest extends SuiteTestEnvironment {
  @Configuration
  @EnableApiFactory
  @EnableEventRecording
  @Import({IdentityServiceConfig.class})
  @ComponentScan("listener")
  public static class TestConfiguration {
    public TestConfiguration ( ) {
      super();
    }
  }

  static final String ADMIN_PASSWORD = "golden_osiris";
  static final String ADMIN_ROLE = "pharaoh";
  static final String ADMIN_IDENTIFIER = "antony";
  static final String AHMES_PASSWORD = "fractions";
  static final String AHMES_FRIENDS_PASSWORD = "sekhem";

  @ClassRule
  public final static TenantDataStoreContextTestRule tenantDataStoreContext = TenantDataStoreContextTestRule.forRandomTenantName(cassandraInitializer);

  //Not using this as a rule because initialize in identityManager is different.
  static final TenantApplicationSecurityEnvironmentTestRule tenantApplicationSecurityEnvironment = new TenantApplicationSecurityEnvironmentTestRule(testEnvironment);

  @Autowired
  ApiFactory apiFactory;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Autowired
  EventRecorder eventRecorder;


  private IdentityManager identityManager;

  @PostConstruct
  public void provision ( ) throws Exception {
    identityManager = apiFactory.create(IdentityManager.class, testEnvironment.serverURI());

    try (final AutoUserContext ignored
                 = tenantApplicationSecurityEnvironment.createAutoSeshatContext()) {
      identityManager.initialize(TestEnvironment.encodePassword(ADMIN_PASSWORD));
    }
  }

  @After
  public void after ( ) {
    UserContextHolder.clear();
    eventRecorder.clear();
  }

  IdentityManager getTestSubject ( ) {
    return identityManager;
  }

  AutoUserContext loginAdmin ( ) throws InterruptedException {
    final Authentication adminAuthentication =
            getTestSubject().login(ADMIN_IDENTIFIER, TestEnvironment.encodePassword(ADMIN_PASSWORD));
    Assert.assertNotNull(adminAuthentication);

    {
      final boolean found = eventRecorder
              .wait(EventConstants.OPERATION_AUTHENTICATE, ADMIN_IDENTIFIER);
      Assert.assertTrue(found);
    }

    return new AutoUserContext(ADMIN_IDENTIFIER, adminAuthentication.getAccessToken());
  }

  /**
   * In identityManager, the user is created with an expired password.  The user must change the password him- or herself
   * to access any other endpoint.
   */
  String createUserWithNonexpiredPassword (final String password, final String role) throws InterruptedException {
    final String username = testEnvironment.generateUniqueIdentifer("Ahmes");
    try (final AutoUserContext ignore = loginAdmin()) {
      getTestSubject().createUser(new UserWithPassword(username, role, TestEnvironment.encodePassword(password)));

      {
        final boolean found = eventRecorder.wait(EventConstants.OPERATION_POST_USER, username);
        Assert.assertTrue(found);
      }

      final Authentication passwordOnlyAuthentication = getTestSubject().login(username, TestEnvironment.encodePassword(password));

      try (final AutoUserContext ignore2 = new AutoUserContext(username, passwordOnlyAuthentication.getAccessToken())) {
        getTestSubject().changeUserPassword(username, new Password(TestEnvironment.encodePassword(password)));
        final boolean found = eventRecorder.wait(EventConstants.OPERATION_PUT_USER_PASSWORD, username);
        Assert.assertTrue(found);
      }
    }
    return username;
  }

  String generateRoleIdentifier ( ) {
    return testEnvironment.generateUniqueIdentifer("scribe");
  }

  Role buildRole (final String identifier, final Permission... permission) {
    final Role scribe = new Role();
    scribe.setIdentifier(identifier);
    scribe.setPermissions(Arrays.asList(permission));
    return scribe;
  }

  Permission buildRolePermission ( ) {
    final Permission permission = new Permission();
    permission.setAllowedOperations(AllowedOperation.ALL);
    permission.setPermittableEndpointGroupIdentifier(PermittableGroupIds.ROLE_MANAGEMENT);
    return permission;
  }

  Permission buildUserPermission ( ) {
    final Permission permission = new Permission();
    permission.setAllowedOperations(AllowedOperation.ALL);
    permission.setPermittableEndpointGroupIdentifier(PermittableGroupIds.IDENTITY_MANAGEMENT);
    return permission;
  }

  Permission buildSelfPermission ( ) {
    final Permission permission = new Permission();
    permission.setAllowedOperations(AllowedOperation.ALL);
    permission.setPermittableEndpointGroupIdentifier(PermittableGroupIds.SELF_MANAGEMENT);
    return permission;
  }

  Permission buildApplicationSelfPermission ( ) {
    final Permission permission = new Permission();
    permission.setAllowedOperations(AllowedOperation.ALL);
    permission.setPermittableEndpointGroupIdentifier(PermittableGroupIds.APPLICATION_SELF_MANAGEMENT);
    return permission;
  }

  String createRoleManagementRole ( ) throws InterruptedException {
    return createRole(buildRolePermission());
  }

  String createSelfManagementRole ( ) throws InterruptedException {
    return createRole(buildSelfPermission());
  }

  String createApplicationSelfManagementRole ( ) throws InterruptedException {
    return createRole(buildApplicationSelfPermission());
  }

  String createRole (final Permission... permission) throws InterruptedException {
    final String roleIdentifier = generateRoleIdentifier();
    final Role role = buildRole(roleIdentifier, permission);

    getTestSubject().createRole(role);

    eventRecorder.wait(EventConstants.OPERATION_POST_ROLE, roleIdentifier);

    return roleIdentifier;
  }

  AutoUserContext loginUser (final String userId, final String password) {
    final Authentication authentication;
    try (AutoUserContext ignored = new AutoGuest()) {
      authentication = getTestSubject().login(userId, TestEnvironment.encodePassword(password));
    }
    return new AutoUserContext(userId, authentication.getAccessToken());
  }

  private String createTestApplicationName ( ) {
    return "test" + RandomStringUtils.randomNumeric(3) + "-v1";
  }

  static class ApplicationSignatureTestData {
    private final String applicationIdentifier;
    private final RsaKeyPairFactory.KeyPairHolder keyPair;

    ApplicationSignatureTestData (final String applicationIdentifier, final RsaKeyPairFactory.KeyPairHolder keyPair) {
      this.applicationIdentifier = applicationIdentifier;
      this.keyPair = keyPair;
    }

    String getApplicationIdentifier ( ) {
      return applicationIdentifier;
    }

    RsaKeyPairFactory.KeyPairHolder getKeyPair ( ) {
      return keyPair;
    }

    String getKeyTimestamp ( ) {
      return keyPair.getTimestamp();
    }
  }

  ApplicationSignatureTestData setApplicationSignature ( ) throws InterruptedException {
    final String testApplicationName = createTestApplicationName();
    final RsaKeyPairFactory.KeyPairHolder keyPair = RsaKeyPairFactory.createKeyPair();
    final Signature signature = new Signature(keyPair.getPublicKeyMod(), keyPair.getPublicKeyExp());

    getTestSubject().setApplicationSignature(testApplicationName, keyPair.getTimestamp(), signature);

    Assert.assertTrue(eventRecorder.wait(EventConstants.OPERATION_PUT_APPLICATION_SIGNATURE, new ApplicationSignatureEvent(testApplicationName, keyPair.getTimestamp())));
    return new ApplicationSignatureTestData(testApplicationName, keyPair);
  }

  void createApplicationPermission (final String applicationIdentifier, final Permission permission) throws InterruptedException {
    getTestSubject().createApplicationPermission(applicationIdentifier, permission);
    Assert.assertTrue(eventRecorder.wait(EventConstants.OPERATION_POST_APPLICATION_PERMISSION,
            new ApplicationPermissionEvent(applicationIdentifier,
                    permission.getPermittableEndpointGroupIdentifier())));
  }
}

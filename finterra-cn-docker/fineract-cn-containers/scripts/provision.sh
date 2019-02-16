#!/bin/sh

function init-config {
    local file="$1"

    while IFS="=" read -r key value; do
        case "$key" in
            '#'*) ;;
            "cassandra.clusterName") CASSANDRA_CLUSTER_NAME="$value" ;;
            "cassandra.contactPoints") CASSANDRA_CONTACT_POINTS="$value" ;;
            "cassandra.replicationType") CASSANDRA_REPLICATION_TYPE="$value" ;;
            "cassandra.replicas") CASSANDRA_REPLICAS="$value" ;;
            "mariadb.driverClass") MARIADB_DRIVER_CLASS="$value" ;;
            "mariadb.host") MARIADB_HOST="$value" ;;
            "mariadb.port") MARIADB_PORT="$value" ;;
            "mariadb.user") MARIADB_USER="$value" ;;
            "mariadb.password") MARIADB_PWD="$value" ;;
            "provisioner.ip") PROVISIONER_IP="$value"; PROVISIONER_URL="http://${PROVISIONER_IP}:2020/provisioner-v1" ;;
            "identity-ms.name") IDENTITY_MS_NAME="$value" ;;
            "identity-ms.description") IDENTITY_MS_DESCRIPTION="$value" ;;
            "identity-ms.vendor") IDENTITY_MS_VENDOR="$value";;
            "identity.ip") IDENTITY_IP="$value"; IDENTITY_URL="http://${IDENTITY_IP}:2021/identity-v1";;
            "rhythm-ms.name") RHYTHM_MS_NAME="$value" ;;
            "rhythm-ms.description") RHYTHM_MS_DESCRIPTION="$value" ;;
            "rhythm-ms.vendor") RHYTHM_MS_VENDOR="$value";;
            "rhythm.ip") RHYTHM_IP="$value"; RHYTHM_URL="http://${RHYTHM_IP}:2022/rhythm-v1";;
            "office-ms.name") OFFICE_MS_NAME="$value" ;;
            "office-ms.description") OFFICE_MS_DESCRIPTION="$value" ;;
            "office-ms.vendor") OFFICE_MS_VENDOR="$value";;
            "office.ip") OFFICE_IP="$value"; OFFICE_URL="http://${OFFICE_IP}:2023/office-v1";;
            "customer-ms.name") CUSTOMER_MS_NAME="$value" ;;
            "customer-ms.description") CUSTOMER_MS_DESCRIPTION="$value" ;;
            "customer-ms.vendor") CUSTOMER_MS_VENDOR="$value";;
            "customer.ip") CUSTOMER_IP="$value"; CUSTOMER_URL="http://${CUSTOMER_IP}:2024/customer-v1";;
            "ledger-ms.name") LEDGER_MS_NAME="$value" ;;
            "ledger-ms.description") LEDGER_MS_DESCRIPTION="$value" ;;
            "ledger-ms.vendor") LEDGER_MS_VENDOR="$value";;
            "ledger.ip") LEDGER_IP="$value"; LEDGER_URL="http://${LEDGER_IP}:2025/accounting-v1";;
            "portfolio-ms.name") PORTFOLIO_MS_NAME="$value" ;;
            "portfolio-ms.description") PORTFOLIO_MS_DESCRIPTION="$value" ;;
            "portfolio-ms.vendor") PORTFOLIO_MS_VENDOR="$value";;
            "portfolio.ip") PORTFOLIO_IP="$value"; PORTFOLIO_URL="http://${PORTFOLIO_IP}:2026/portfolio-v1";;
            "deposit-account-management-ms.name") DEPOSIT_MS_NAME="$value" ;;
            "deposit-account-management-ms.description") DEPOSIT_MS_DESCRIPTION="$value" ;;
            "deposit-account-management-ms.vendor") DEPOSIT_MS_VENDOR="$value";;
            "deposit-account-management.ip") DEPOSIT_IP="$value"; DEPOSIT_URL="http://${DEPOSIT_IP}:2027/deposit-v1";;
            "teller-ms.name") TELLER_MS_NAME="$value" ;;
            "teller-ms.description") TELLER_MS_DESCRIPTION="$value" ;;
            "teller-ms.vendor") TELLER_MS_VENDOR="$value";;
            "teller.ip") TELLER_IP="$value"; TELLER_URL="http://${TELLER_IP}:2028/teller-v1";;
            "cause-ms.name") CAUSE_MS_NAME="$value" ;;
            "cause-ms.description") CAUSE_MS_DESCRIPTION="$value" ;;
            "cause-ms.vendor") CAUSE_MS_VENDOR="$value";;
            "cause.ip") CAUSE_IP="$value"; CAUSE_URL="http://${CAUSE_IP}:2038/cause-v1";;	    
            "report-ms.name") REPORT_MS_NAME="$value" ;;
            "report-ms.description") REPORT_MS_DESCRIPTION="$value" ;;
            "report-ms.vendor") REPORT_MS_VENDOR="$value";;
            "report.ip") REPORT_IP="$value"; REPORT_URL="http://${REPORT_IP}:2029/report-v1";;
	    "notification-ms.name") NOTIFICATION_MS_NAME="$value" ;;
            "notification-ms.description") NOTIFICATION_MS_DESCRIPTION="$value" ;;
            "notification-ms.vendor") NOTIFICATION_MS_VENDOR="$value";;
            "notification.ip") NOTIFICATION_IP="$value"; NOTIFICATION_URL="http://${NOTIFICATION_IP}:2033/notification-v1";;
            *)
                echo "Error: Unsupported key: $key"
                exit 1
                ;;
        esac
    done < "$file"
}

function auto-seshat {
    TOKEN=$( curl -s -X POST -H "Content-Type: application/json" \
        "$PROVISIONER_URL"'/auth/token?grant_type=password&client_id=service-runner&username=wepemnefret&password=oS/0IiAME/2unkN1momDrhAdNKOhGykYFH/mJN20' \
         | jq --raw-output '.token' )
}

function login {
    local tenant="$1"
    local username="$2"
    local password="$3"

    ACCESS_TOKEN=$( curl -s -X POST -H "Content-Type: application/json" -H "User: guest" -H "X-Tenant-Identifier: malaysia" \
       "${IDENTITY_URL}/token?grant_type=password&username=${username}&password=${password}" \
        | jq --raw-output '.accessToken')
    echo $ACCESS_TOKEN
}

function create-application {
    local name="$1"
    local description="$2"
    local vendor="$3"
    local homepage="$4"

    curl -# -H "Content-Type: application/json" -H "User: wepemnefret" -H "Authorization: ${TOKEN}" \
    --data '{ "name": "'"$name"'", "description": "'"$description"'", "vendor": "'"$vendor"'", "homepage": "'"$homepage"'" }' \
     ${PROVISIONER_URL}/applications
    echo "Created microservice: $name"
}

function get-application {
    echo ""
    echo "Microservices: "
    curl -s -H "Content-Type: application/json" -H "User: wepemnefret" -H "Authorization: ${TOKEN}" ${PROVISIONER_URL}/applications | jq '.'
}

function delete-application {
    local service_name="$1"

    curl -X delete -H "Content-Type: application/json" -H "User: wepemnefret" -H "Authorization: ${TOKEN}" ${PROVISIONER_URL}/applications/${service_name}
    echo "Deleted microservice: $name"
}

function create-tenant {
    local identifier="$1"
    local name="$2"
    local description="$3"
    local database_name="$4"

    curl -H "Content-Type: application/json" -H "User: wepemnefret" -H "Authorization: ${TOKEN}" \
    --data '{
	"identifier": "malaysia",
	"name": "for malaysia",
	"description": "demo test",
	"cassandraConnectionInfo": {
		"clusterName": "Test Cluster",
		"contactPoints": "206.189.45.92:9042",
		"keyspace": "malaysia",
		"repl:q!
		icationType": "Simple",
		"replicas": "1"
	},
	"databaseConnectionInfo": {
		"driverClass": "org.mariadb.jdbc.Driver",
		"databaseName": "malaysia",
		"host": "68.183.191.246",
		"port": "3306",
		"user": "root",
		"password": "mysql"
	}}' \
    ${PROVISIONER_URL}/tenants
    echo "Create tenant: $database_name"
}

function get-tenants {
    echo ""
    echo "Tenants: "
    curl -s -H "Content-Type: application/json" -H "User: wepemnefret" -H "Authorization: ${TOKEN}" ${PROVISIONER_URL}/tenants | jq '.'
}

function assign-identity-ms {
    local tenant="$1"

    ADMIN_PASSWORD=$( curl -s -H "Content-Type: application/json" -H "User: wepemnefret" -H "Authorization: ${TOKEN}" \
	--data '{ "name": "'"$IDENTITY_MS_NAME"'" }' \
	${PROVISIONER_URL}/tenants/malaysia/identityservice | jq --raw-output '.adminPassword')
    echo "Assigned identity microservice for tenant $tenant"
}

function assign-additional-ms {
    local tenant="$1"

    ADMIN_CUS_PASSWORD=$( curl -s -X PUT -H "Content-Type: application/json" -H "User: wepemnefret" -H "Authorization: ${TOKEN}" \
        --data '{ "'"$CUSTOMER_MS_NAME"'" }' \
        ${PROVISIONER_URL}/tenants/malaysia/applications | jq --raw-output '.adminPassword')
    echo "Assigned identity microservice for tenant $tenant"
    echo $ADMIN_CUS_PASSWORD
}

function get-tenant-services {
    local tenant="$1"

    echo ""
    echo "$tenant services: "
    curl -s -H "Content-Type: application/json" -H "User: wepemnefret" -H "Authorization: ${TOKEN}" -H "X-Tenant-Identifier: $tenant" ${PROVISIONER_URL}/tenants/$tenant/applications | jq '.'
}

function create-scheduler-role {
    local tenant="$1"

    curl -H "Content-Type: application/json" -H "User: antony" -H "Authorization: ${ACCESS_TOKEN}" -H "X-Tenant-Identifier: malaysia" \
        --data '{
                "identifier": "scheduler",
                "permissions": [
                        {
                                "permittableEndpointGroupIdentifier": "identity__v1__app_self",
                                "allowedOperations": ["CHANGE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "portfolio__v1__khepri",
                                "allowedOperations": ["CHANGE"]
                        }
                ]
        }' \
        ${IDENTITY_URL}/roles
    echo "Created scheduler role"
}


function create-org-admin-role {
    local tenant="$1"

    curl -H "Content-Type: application/json" -H "User: antony" -H "Authorization: ${ACCESS_TOKEN}" -H "X-Tenant-Identifier: malaysia" \
        --data '{
                "identifier": "orgadmin",
                "permissions": [
                        {
                                "permittableEndpointGroupIdentifier": "office__v1__employees",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "office__v1__offices",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "identity__v1__users",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "identity__v1__roles",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "identity__v1__self",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "accounting__v1__ledger",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "accounting__v1__account",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
			{
                                "permittableEndpointGroupIdentifier": "customer__v1__customer",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
			{
                                "permittableEndpointGroupIdentifier": "customer__v1__identifications",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
			{
                                "permittableEndpointGroupIdentifier": "customer__v1__task",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        }
                ]
        }' \
        ${IDENTITY_URL}/roles
    echo "Created organisation administrator role"
}

function create-user {
    local tenant="$1"
    local user="$2"
    local user_identifier="$3"
    local password="$4"
    local role="$5"

    curl -s -H "Content-Type: application/json" -H "User: $user" -H "Authorization: ${ACCESS_TOKEN}" -H "X-Tenant-Identifier: malaysia" \
        --data '{
                "identifier": "'"$user_identifier"'",
                "password": "'"$password"'",
                "role": "'"$role"'"
        }' \
        ${IDENTITY_URL}/users | jq '.'
    echo "Created user: $user_identifier"
}

function get-users {
    local tenant="$1"
    local user="$2"

    echo ""
    echo "Users: "
    curl -s -H "Content-Type: application/json" -H "User: $user" -H "Authorization: ${ACCESS_TOKEN}" -H "X-Tenant-Identifier: malaysia" ${IDENTITY_URL}/users | jq '.'
}

function get-customers {
    local tenant="$1"
    local user="$2"

    echo ""
    echo "Users: "
    echo  $( curl -s -H "Content-Type: application/json" -H "User: $user" -H "Authorization: ${ACCESS_TOKEN}" -H "X-Tenant-Identifier: malaysia" ${CUSTOMER_URL}/customers | jq '.')
}

function update-password {
    local tenant="$1"
    local user="$2"
    local password="$3"

    curl -s -X PUT -H "Content-Type: application/json" -H "User: $user" -H "Authorization: ${ACCESS_TOKEN}" -H "X-Tenant-Identifier: malaysia" \
        --data '{
                "password": "'"$password"'"
        }' \
        ${IDENTITY_URL}/users/${user}/password | jq '.'
    echo "Updated $user password"
}

function provision-app {
    local tenant="$1"
    local service="$2"

    curl -s -X PUT -H "Content-Type: application/json" -H "User: wepemnefret" -H "Authorization: ${TOKEN}" \
	--data '[{ "name": "'"$service"'" }]' \
	${PROVISIONER_URL}/tenants/${tenant}/applications | jq '.'
    echo "Provisioned microservice, $service for tenant, $tenant"
}

function create-user-cutomer {
    local user="$1"

    curl -s -X POST -H "Content-Type: application/json" -H "User: $user" -H "Authorization: ${ACCESS_TOKEN}" -H "X-Tenant-Identifier: malaysia" \
        --data '{
 "identifier": "hjsbfhs",
 "type": "abc",
 "givenName":"Testuyyuf",
 "middleName": "Tbvhhv",
 "surname":"rdfgdkg",
 "dateOfBirth": "2019-01-07",
 "member":true,
 "address": "Test Address",
 "contactDetails":{
        "type": "767",
        "group": "home",
        "value": "test",
        "preferenceLevel": 3,
        "validated": true
 },
 "currentState":"rytryr",
 "applicationDate":"2019-01-07",
 "createdBy": "YTftydfgh",
 "createdOn":"2019-01-07",
 "lastModifiedBy": "YTftydfgh",
 "lastModifiedOn":"2019-01-07"

}' \
        ${CUSTOMER_URL}/customers | jq '.'
    echo "created user"
}

function set-application-permission-enabled {
    local tenant="$1"
    local service="$2"
    local permission="$3"
    local user="$4"

    curl -s -H "Content-Type: application/json" -H "User: $user" -H "Authorization: ${ACCESS_TOKEN}" -H "X-Tenant-Identifier: malaysia" \
        --data ' "permission": [
                        {
                                "permittableEndpointGroupIdentifier": "identity__v1__app_self",
                                "allowedOperations":  ["READ", "CHANGE", "DELETE"]
                        }' \
        ${IDENTITY_URL}/applications/${service}/permissions | jq '.'
    echo "Enabled permission, $permission for service $service"
}

function get-application-permission-enabled {
    local tenant="$1"
    local service="$2"
    local permission="$3"
    local user="$4"

    echo $(curl -s -H "Content-Type: application/json" -H "User: $user" -H "Authorization: ${ACCESS_TOKEN}" -H "X-Tenant-Identifier: malaysia" \
	${IDENTITY_URL}/applications/${service}/permissions | jq '.')
    #echo "Enabled permission, $permission for service $service"
}

function set-application-permission-enabled-for-user {
    local tenant="$1"
    local service="$2"
    local permission="$3"
    local user="$4"

    curl -s -X PUT -H "Content-Type: application/json" -H "User: $user" -H "Authorization: ${ACCESS_TOKEN}" -H "X-Tenant-Identifier: malaysia" \
	--data 'true' \
	${IDENTITY_URL}/applications/${service}/permissions/${permission}/users/${user}/enabled | jq '.'
    echo "Enabled permission, $permission for service $service"
}

function create-org-new-role {
    local tenant="$1"

    curl -H "Content-Type: application/json" -H "User: antony" -H "Authorization: ${ACCESS_TOKEN}" -H "X-Tenant-Identifier: malaysia" \
        --data '{
                "identifier": "admin",
                "permissions": [
                        {
                                "permittableEndpointGroupIdentifier": "identity__v1__app_self",
                                "allowedOperations":  ["READ", "CHANGE", "DELETE"]
                        },
			{
                                "permittableEndpointGroupIdentifier": "office__v1__employees",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "office__v1__offices",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "identity__v1__users",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "identity__v1__roles",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "identity__v1__self",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "accounting__v1__ledger",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "accounting__v1__account",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "customer__v1__customer",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "customer__v1__identifications",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        },
                        {
                                "permittableEndpointGroupIdentifier": "customer__v1__task",
                                "allowedOperations": ["READ", "CHANGE", "DELETE"]
                        }
                ]
        }' \
        ${IDENTITY_URL}/roles
    echo "Created organisation administrator role"
}

init-config $1
TENANT="malaysia"
auto-seshat
#assign-additional-ms "malaysia"
#create-application $IDENTITY_MS_NAME $IDENTITY_MS_DESCRIPTION $IDENTITY_MS_VENDOR $IDENTITY_URL
#create-application $RHYTHM_MS_NAME $RHYTHM_MS_DESCRIPTION $RHYTHM_MS_VENDOR $RHYTHM_URL
#create-application $OFFICE_MS_NAME $OFFICE_MS_DESCRIPTION $OFFICE_MS_VENDOR $OFFICE_URL
#create-application $CUSTOMER_MS_NAME $CUSTOMER_MS_DESCRIPTION $CUSTOMER_MS_VENDOR $CUSTOMER_URL
#create-application $LEDGER_MS_NAME $LEDGER_MS_DESCRIPTION $LEDGER_MS_VENDOR $LEDGER_URL
#create-application $PORTFOLIO_MS_NAME $PORTFOLIO_MS_DESCRIPTION $PORTFOLIO_MS_VENDOR $PORTFOLIO_URL
#create-application $DEPOSIT_MS_NAME $DEPOSIT_MS_DESCRIPTION $DEPOSIT_MS_VENDOR $DEPOSIT_URL
#create-application $TELLER_MS_NAME $TELLER_MS_DESCRIPTION $TELLER_MS_VENDOR $TELLER_URL
#create-application $REPORT_MS_NAME $REPORT_MS_DESCRIPTION $REPORT_MS_VENDOR $REPORT_URL
create-application $CAUSE_MS_NAME $CAUSE_MS_DESCRIPTION $CAUSE_MS_VENDOR $CAUSE_URL
#get-tenants
#for TENANT in "${TENANTS[1]}"; do
#    echo
#    echo "Provisioning applications for tenant, ${TENANT}."
#    echo
#    read -p "Enter a name for the tenant, ${TENANT}: " -r name
#    create-tenant ${TENANT} "${name}" "All in one Demo Server" ${TENANT}
#    assign-identity-ms ${TENANT} 
#    login "malaysia" "antony" "i9P0meP2V%2BjIe72vNZS7Z82ltMP10SDcs3MCLKFvLCQ="
 #   update-scheduler-role "malaysia"	
    #   echo "created antony"
#    echo $ADMIN_PASSWORD
#    create-scheduler-role "malaysia"
#     create-user "malaysia" "antony" "imhotep" "cDRzc3cwcmQ=" "scheduler"
#    login "malaysia" "imhotep" "cDRzc3cwcmQ="
#    update-password "malaysia" "imhotep" "cDRzc3cwcmQ="
#    login "malaysia" "imhotep" "cDRzc3cwcmQ="
#    provision-app "malaysia" $RHYTHM_MS_NAME
#    login "malaysia" "satprem" "cDRzc3cwcmQ="
#    get-application-permission-enabled "malaysia" $RHYTHM_MS_NAME "identity__v1__app_self" "satprem"
#    set-application-permission-enabled "malaysia" $RHYTHM_MS_NAME "identity__v1__app_self" "imhotep"
  #  set-application-permission-enabled-for-user "malaysia" $RHYTHM_MS_NAME "identity__v1__app_self" "imhotep"
#     provision-app "malaysia" $IDENTITY_MS_NAME	
#    provision-app "malaysia" $OFFICE_MS_NAME
#    provision-app "malaysia" $LEDGER_MS_NAME
  #  provision-app "malaysia" $PORTFOLIO_MS_NAME
#    set-application-permission-enabled-for-user "malaysia" $RHYTHM_MS_NAME "portfolio__v1__khepri" "malaysia"
  #  provision-app "malaysia" $CUSTOMER_MS_NAME
  #  provision-app "malaysia" $DEPOSIT_MS_NAME
  #  provision-app "malaysia" $TELLER_MS_NAME
  #  provision-app "malaysia" $REPORT_MS_NAME
  #   provision-app "malaysia" $PROVISION_MS_NAME
   #   provision-app "malaysia" $CAUSE_MS_NAME
   get-tenant-services "malaysia"
  #  login "malaysia" "antony" "i9P0meP2V%2BjIe72vNZS7Z82ltMP10SDcs3MCLKFvLCQ="
  #  create-org-new-role "malaysia"
    #  create-org-admin-role "malaysia"
  #  create-user "malaysia" "antony" "charansree4" "cDRzc3cwcmQ=" "customerclientnew"
 #  login "malaysia" "charansree4" "cDRzc3cwcmQ="
 #   create-user "operator"
  #  get-customers "malaysia" "charansree4"
  #  get-users "malaysia" "operator"



echo "COMPLETED PROCESS SUCCESSFULLY."

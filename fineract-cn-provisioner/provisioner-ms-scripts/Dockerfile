FROM openjdk:8-jdk-alpine

ARG provisioner_port=2020

ENV server.max-http-header-size=26384 \
    bonecp.partitionCount=1 \
    bonecp.maxConnectionsPerPartition=4 \
    bonecp.minConnectionsPerPartition=1 \
    bonecp.acquireIncrement=1 \
    server.port=9090 \
    cassandra.clusterName="Test Cluster" \
    cassandra.contactPoints=157.230.47.17:9042 \
    cassandra.keyspace=seshat \
    cassandra.cl.read=ONE \
    cassandra.cl.write=ONE \
    cassandra.cl.delete=ONE \
    mariadb.driverClass=org.mariadb.jdbc.Driver \
    mariadb.database=seshat \
    mariadb.host=157.230.46.54 \
    mariadb.port=3306 \
    mariadb.user=${MARIA_DB_USER} \
    mariadb.password=${MARIA_DB_PASSWORD} \
    spring.cloud.discovery.enabled=false \
    spring.cloud.config.enabled=false \
    flyway.enabled=false \
    feign.hystrix.enabled=false \
    ribbon.eureka.enabled=false \
    ribbon.listOfServers=${RIBBON_LIST_OF_SERVERS} \
    eureka.client.serviceUrl.defaultZone=${EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE} \
    spring.cloud.discovery.enabled=true \
    eureka.instance.prefer-ip-address=true \
    eureka.instance.hostname=${EUREKA_INSTANCE_HOSTNAME} \
    eureka.client.fetchRegistry=true \
    eureka.registration.enabled=false \
    eureka.instance.leaseRenewalIntervalInSeconds=1 \
    eureka.client.instanceInfoReplicationIntervalSeconds=1 \
    activemq.brokerUrl=${ACTIVEMQ_BROKER_URL} \
    ribbon.eureka.enabled=true \
    system.initialclientid=service-runner \
    spring.profiles.active=production

WORKDIR /tmp
COPY provisioner-service-boot-0.1.0-BUILD-SNAPSHOT.jar .
ENV spring.application.name="provisioner-v1" \
    server.port=$provisioner_port \
    server.contextPath="/provisioner-v1"

CMD ["java", "-jar", "provisioner-service-boot-0.1.0-BUILD-SNAPSHOT.jar"]


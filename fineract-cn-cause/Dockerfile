FROM openjdk:8-jdk-alpine

ARG cause_port=2038

ENV server.max-http-header-size=26384 \
    bonecp.partitionCount=1 \
    bonecp.maxConnectionsPerPartition=4 \
    bonecp.minConnectionsPerPartition=1 \
    bonecp.acquireIncrement=1 \
    server.port=9090 \
    cassandra.clusterName="Test Cluster" \
    cassandra.contactPoints=${CASSANDRA_CONTACT_POINTS} \
    cassandra.keyspace=seshat \
    cassandra.cl.read=ONE \
    cassandra.cl.write=ONE \
    cassandra.cl.delete=ONE \
    mariadb.driverClass=org.mariadb.jdbc.Driver \
    mariadb.database=seshat \
    mariadb.host=${MARIA_DB_HOST} \
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
    eureka.instance.hostname=${EUREKA_INSTANCE_HOSTNAME} \
    eureka.client.fetchRegistry=true \
    eureka.registration.enabled=false \
    eureka.instance.leaseRenewalIntervalInSeconds=1 \
    eureka.client.instanceInfoReplicationIntervalSeconds=1 \
    activemq.brokerUrl=${ACTIVEMQ_BROKER_URL} \
    ribbon.eureka.enabled=true \
    system.initialclientid=service-runner \
    system.publicKey.timestamp=2018-09-25T03_31_48 \
    system.publicKey.modulus=17318007762202276915667781209840284241720502707521873078897505356138539383363050049667785138353485988207634647603188455336356425002849760281049240791476283942497160184775797971723519829903627967481361379054447959291649289057756603579576792654752639713561637781944789557030934756137133197066405169971699156642941128638988408730741608813413507008146153853427551648252129508313718401750574588730639392661484581481667612606514906545379914299119012278873059875167157788525230442044206536668591517665262097071112361699974092567361081634436230322305603628346761177821595272443570075328903137181224797586174954037775119011659 \
    system.publicKey.exponent=65537

WORKDIR /tmp
COPY service/build/libs/service-0.1.0-BUILD-SNAPSHOT-boot.jar .
ENV spring.application.name="cause-v1" \
    server.port=$cause_port \
    server.contextPath="/cause-v1"

CMD ["java", "-jar", "service-0.1.0-BUILD-SNAPSHOT-boot.jar"]

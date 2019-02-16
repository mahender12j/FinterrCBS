#!/bin/bash
# Run a command in the background.
_evalBg() {
    eval "$@" &>/tmp/run_log.out &disown;
}

cmd="java -Ddemoserver.persistent=true -Dcustom.cassandra.contactPoints=206.189.45.92:9042 -Dcassandra.cluster.user=cassandra -Dcassandra.cluster.pwd=cassandra -Dcustom.mariadb.host=68.183.191.246 -Dcustom.mariadb.user=root -Dcustom.mariadb.password=mysql -jar /usr/local/integration-tests/fineract-cn-demo-server/build/libs/demo-server-0.1.0-BUILD-SNAPSHOT.jar";

_evalBg "${cmd}";


#!/bin/bash

cd core;
echo "building fineract-cn-api"
cd fineract-cn-lang; ./gradlew publishToMavenLocal;  cd ..;
echo "building fineract-cn-api"
cd fineract-cn-mariadb; ./gradlew publishToMavenLocal;  cd ..;
echo "building fineract-cn-api"
cd fineract-cn-cassandra; ./gradlew publishToMavenLocal;  cd ..;

cd fineract-cn-api; ./gradlew publishToMavenLocal;  cd ..;

cd fineract-cn-async; ./gradlew publishToMavenLocal;  cd ..;

cd fineract-cn-data-jpa; ./gradlew publishToMavenLocal;  cd ..;

cd fineract-cn-command; ./gradlew publishToMavenLocal;  cd ..;

cd fineract-cn-test; ./gradlew publishToMavenLocal;  cd ..;

cd fineract-cn-api; ./gradlew publishToMavenLocal;  cd ..;

cd ..

cd fineract-cn-anubis;  ./gradlew publishToMavenLocal;  cd ..;

cd tools;

cd fineract-cn-crypto; ./gradlew publishToMavenLocal;  cd ..;


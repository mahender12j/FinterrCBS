#!/bin/bash
echo "===============BUILDING CORE LIBRARIES==============="
cd core;

echo "===============BUILDING fineract-cn-api==============="
cd fineract-cn-lang; ./gradlew publishToMavenLocal;  cd ..;

echo "===============BUILDING fineract-cn-mariadb==============="
cd fineract-cn-mariadb; ./gradlew publishToMavenLocal;  cd ..;

echo "===============BUILDING fineract-cn-cassandra==============="
cd fineract-cn-cassandra; ./gradlew publishToMavenLocal;  cd ..;

echo "===============BUILDING fineract-cn-api==============="
cd fineract-cn-api; ./gradlew publishToMavenLocal;  cd ..;

echo "===============BUILDING fineract-cn-async==============="
cd fineract-cn-async; ./gradlew publishToMavenLocal;  cd ..;

echo "===============BUILDING fineract-cn-data-jpa==============="
cd fineract-cn-data-jpa; ./gradlew publishToMavenLocal;  cd ..;

echo "===============BUILDING fineract-cn-command==============="
cd fineract-cn-command; ./gradlew publishToMavenLocal;  cd ..;

echo "===============BUILDING fineract-cn-test==============="
cd fineract-cn-test; ./gradlew publishToMavenLocal;  cd ..;

echo "===============BUILDING fineract-cn-api==============="
cd fineract-cn-api; ./gradlew publishToMavenLocal;  cd ..;

cd ..

echo "===============BUILDING fineract-cn-anubis==============="
cd fineract-cn-anubis;  ./gradlew publishToMavenLocal;  cd ..;

echo "===============BUILDING fineract-cn-crypto==============="
cd tools;
cd fineract-cn-crypto; ./gradlew publishToMavenLocal;  cd ..;


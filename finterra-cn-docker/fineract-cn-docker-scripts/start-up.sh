#!/bin/sh

cd external-tools/
docker-compose up -d
while ! nc -z 206.189.45.92 9042; do
  echo "Cassandra is unavailable - sleeping"
  sleep 1
done
echo "Cassandra is up - executing command"
cd ..
docker-compose up -d --build

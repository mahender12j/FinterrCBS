cd api
sudo ./../gradlew clean
sudo ./../gradlew classes
sudo ./../gradlew jar
sudo ./../gradlew build
sudo ./../gradlew publishToMavenLocal
cd ..
sudo ./gradlew publishToMavenLocal

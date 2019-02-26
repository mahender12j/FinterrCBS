cd api
sudo ./../gradlew clean
sudo ./../gradlew classes
sudo ./../gradlew jar
sudo ./../gradlew build
sudo ./../gradlew publishToMavenLoca
cd ../service
sudo ./../gradlew clean
sudo ./../gradlew classes
sudo ./../gradlew jar
sudo ./../gradlew build
sudo ./../gradlew publishToMavenLoca
cd ..


sudo ./gradlew publishToMavenLoca

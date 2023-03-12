JAR_FILE="hw11-0.0.1-SNAPSHOT.jar"

./gradlew bootJar
cp build/libs/$JAR_FILE .
docker build -f Dockerfile -t stock-exchange-application:latest .
rm $JAR_FILE

FROM amazoncorretto:21.0.1 AS build

ARG NEXUS_USERNAME
ARG NEXUS_PASSWORD

WORKDIR /var/ip-worker
ADD . .
RUN ./gradlew clean build -x test -Pcentral-nexus-username=$NEXUS_USERNAME -Pcentral-nexus-password=$NEXUS_PASSWORD

# Run stage
FROM amazoncorretto:21.0.1
WORKDIR /var/ip-worker
COPY --from=build /var/ip-worker/build/libs/BambooHR-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

#ENTRYPOINT ["java", "-jar", "app.jar"]
ENTRYPOINT java -jar -DtaskToDomain=$taskToDomain app.jar

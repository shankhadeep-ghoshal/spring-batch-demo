#syntax=docker/dockerfile:1.4
FROM gradle:7.6.0-jdk19 as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build -x spotlessJava --no-daemon

FROM eclipse-temurin:17-jre-alpine
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/batch-app.jar
ENTRYPOINT ["java", "-jar","/app/batch-app.jar"]

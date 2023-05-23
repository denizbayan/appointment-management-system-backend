FROM openjdk:11
MAINTAINER denizbayan
COPY target/ams-0.0.1-SNAPSHOT.jar ams.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker","-jar","/ams.jar"]
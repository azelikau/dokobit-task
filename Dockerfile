FROM maven:3.6.3-jdk-11-slim as builder
ARG WORKING_DIRECTORY=/workspace

COPY src ${WORKING_DIRECTORY}/src
COPY pom.xml ${WORKING_DIRECTORY}

WORKDIR ${WORKING_DIRECTORY}
RUN mvn clean package

FROM openjdk:11.0.8-jre-slim
ARG WORKING_DIRECTORY=/workspace
COPY --from=builder ${WORKING_DIRECTORY}/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
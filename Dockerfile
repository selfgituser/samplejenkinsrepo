FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} samplejenkinsmicro.jar
ENTRYPOINT ["java", "-jar", "/samplejenkinsmicro.jar"]
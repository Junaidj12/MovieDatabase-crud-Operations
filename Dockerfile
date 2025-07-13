FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/crud-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 7890

ENTRYPOINT ["java", "-jar", "app.jar"]

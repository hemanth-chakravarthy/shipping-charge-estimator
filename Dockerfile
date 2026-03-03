FROM eclipse-temurin:22-jdk-alpine
WORKDIR /app
COPY target/estimator-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

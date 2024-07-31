FROM openjdk:17
WORKDIR /app
COPY target/*.jar payment-service.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "payment-service.jar"]
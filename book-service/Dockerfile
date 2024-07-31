FROM openjdk:17
WORKDIR /app
COPY target/*.jar book-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "book-service.jar"]
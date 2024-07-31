FROM openjdk:17
WORKDIR /app
COPY target/*.jar search-service.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "search-service.jar"]
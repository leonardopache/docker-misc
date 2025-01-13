# Use OpenJDK as the base image
FROM openjdk:20-jdk-slim

# Copy the jar file to the container
ARG JAR_FILE=target/stock-price-mockAPI-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} stock-price-mockAPI.jar

# Expose the port the app runs on
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "/stock-price-mockAPI.jar"]

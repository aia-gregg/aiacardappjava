# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built jar file into the container. Adjust the jar name if necessary.
COPY target/decryption-microservice-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 (or the port your Spring Boot app uses)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]

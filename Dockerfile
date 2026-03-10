# Use a lightweight Java runtime
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /ma_visualization_be

# Copy the jar file into the container
COPY target/ma_visualization_be-0.0.1-SNAPSHOT.jar ma_visualization_be-0.0.1-SNAPSHOT.jar

# Expose port (nếu app bạn chạy ở 9999)
EXPOSE 9999

# Command to run the application
ENTRYPOINT ["java", "-jar", "ma_visualization_be-0.0.1-SNAPSHOT.jar"]

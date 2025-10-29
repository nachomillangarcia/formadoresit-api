# Dockerfile
# ===== Stage 1: Build the application =====
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven descriptor and download dependencies first (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Now copy the source code
COPY src ./src

# Package the app (fat jar with dependencies)
RUN mvn package

# ===== Stage 2: Run the application =====
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=build /app/target/java-server-*-jar-with-dependencies.jar app.jar

# Run it
ENTRYPOINT ["java", "-jar", "app.jar"]

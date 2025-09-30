# ---------- Build Stage ----------
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy pom.xml and download dependencies (caching helps speed up rebuilds)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy source code and build jar
COPY src ./src
RUN mvn -B package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy only the built JAR into runtime image
COPY --from=build /workspace/target/*.jar app.jar

# Render provides PORT as env variable (default 10000)
EXPOSE 10000

# Run Spring Boot application
ENTRYPOINT ["java","-jar","/app/app.jar"]

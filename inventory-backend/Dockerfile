# Stage 1: Build the Spring Boot app with Java 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the app with Java 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/inventory-service-1.0.0.jar app.jar

# Environment variables for configuration
# Note: On Railway, these can be overridden by setting env vars in the Railway dashboard
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -Djava.net.preferIPv4Stack=true"

# Database configuration - defaults to localhost for local development
# On Railway, set these via environment variables:
# MYSQL_URL=jdbc:mysql://[host]:[port]/dbfms?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=false
# MYSQL_USER=[user from Railway MySQL service]
# MYSQL_PASSWORD=[password from Railway MySQL service]
ENV MYSQL_URL=""
ENV MYSQL_USER=""
ENV MYSQL_PASSWORD=""

# JWT and other config
ENV JWT_SECRET=""
ENV JWT_EXPIRATION="86400000"
ENV LOGGING_LEVEL_ROOT="INFO"

EXPOSE 8082

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

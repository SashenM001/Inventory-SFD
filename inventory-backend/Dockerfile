# Stage 1: Build the Spring Boot app with Java 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY inventory-backend/pom.xml .
RUN mvn dependency:resolve
COPY inventory-backend/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the app with Java 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/inventory-service.jar app.jar

# Environment variables for configuration
# Note: On Railway, these can be overridden by setting env vars in the Railway dashboard
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -Djava.net.preferIPv4Stack=true"

# Database configuration - Railway auto-injects MYSQLHOST, MYSQLPORT, etc.
# These are empty defaults; Railway overrides them at runtime
ENV MYSQLHOST=""
ENV MYSQLPORT="3306"
ENV MYSQLDATABASE="railway"
ENV MYSQLUSER=""
ENV MYSQLPASSWORD=""

# JWT and other config
ENV JWT_SECRET=""
ENV JWT_EXPIRATION="86400000"
ENV LOGGING_LEVEL_ROOT="INFO"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8080}"]

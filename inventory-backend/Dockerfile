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
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -Djava.net.preferIPv4Stack=true"
ENV MYSQL_URL="jdbc:mysql://localhost:3306/dbfms?useSSL=true&serverTimezone=Asia/Colombo&allowPublicKeyRetrieval=false"
ENV MYSQL_USER="root"
ENV MYSQL_PASSWORD=""
ENV JWT_SECRET=""
ENV JWT_EXPIRATION="86400000"
ENV LOGGING_LEVEL_ROOT="INFO"

EXPOSE 8082
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

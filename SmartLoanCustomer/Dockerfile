# Build stage
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw -q -B dependency:go-offline

COPY src src
RUN ./mvnw -q -B package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

COPY --from=build /workspace/target/SmartLoanCustomer.war app.war
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.war"]

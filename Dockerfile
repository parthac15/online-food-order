# Build Stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# Build all modules
RUN mvn clean install -DskipTests

# Run Stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app /app/

# The command will be overridden by render.yaml's dockerCommand
CMD ["java", "-jar", "order-service/target/order-service-1.0.0.jar"]

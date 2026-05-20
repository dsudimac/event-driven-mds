# syntax=docker/dockerfile:1.6

# ---------- BUILD STAGE ----------
FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

# 1. COPY ONLY MAVEN FILES FIRST (cache layer)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# 2. copy source
COPY order-api-service/ order-api-service/
COPY inventory-processing-service/ inventory-processing-service/

# 3. cache dependencies
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -N dependency:go-offline

# 4. build WITHOUT forcing full clean every time (faster rebuilds)
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -pl order-api-service/order-api-service-app -am package -DskipTests && \
    ./mvnw -pl inventory-processing-service -am package -DskipTests

# ---------- RUNTIME: ORDER SERVICE ----------
FROM eclipse-temurin:21-jre AS order-api-service

WORKDIR /app

COPY --from=build /workspace/order-api-service/order-api-service-app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]


# ---------- RUNTIME: INVENTORY SERVICE ----------
FROM eclipse-temurin:21-jre AS inventory-processing-service

WORKDIR /app

COPY --from=build /workspace/inventory-processing-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]

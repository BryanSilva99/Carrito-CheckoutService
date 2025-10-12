# ---- Etapa 1: build ----
FROM gradle:8.9-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# ---- Etapa 2: runtime ----
FROM eclipse-temurin:24-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

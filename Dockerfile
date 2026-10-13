# ---------- Stage 1: Build fat JAR ----------
FROM gradle:8.10-jdk17-alpine AS build
WORKDIR /app

# Cache dependency dulu supaya build ulang lebih cepat
COPY build.gradle.kts* settings.gradle.kts* gradle.properties* ./
RUN gradle dependencies --no-daemon

# Copy source lalu build
COPY src ./src
RUN gradle build --no-daemon -x test

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Jalankan sebagai non-root
RUN addgroup -S app && adduser -S app -G app
COPY --from=build --chown=app:app /app/build/libs/travelwise.jar /app/travelwise.jar
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/travelwise.jar"]
# ---------- Build Stage ----------
FROM maven:3.9.9-eclipse-temurin-11 AS builder

WORKDIR /app
# Copy pom.xml first
COPY pom.xml .
# Download dependencies (cached)
RUN mvn dependency:go-offline -B
# Copy source code
COPY src ./src
# Build application
RUN mvn clean package -DskipTests -B

# ---------- Runtime Stage ----------
FROM eclipse-temurin:11-jre

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app
# Create non-root user
RUN addgroup --system spring && adduser --system --ingroup spring spring

# Copy jar and give ownership to spring user
COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

# Create log directory and give permission
RUN mkdir -p /logs && chown -R spring:spring /logs

# Switch to non-root user
USER spring

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
CMD curl -f http://localhost:8082/journal/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

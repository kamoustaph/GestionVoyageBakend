# ---------- Construction ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

# ---------- Exécution ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S yoon && adduser -S yoon -G yoon
COPY --from=build /app/target/gestion-voyage.jar app.jar
USER yoon
EXPOSE 8093
ENV TZ=Africa/Dakar \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -Dfile.encoding=UTF-8"
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8093/actuator/health/liveness || exit 1
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

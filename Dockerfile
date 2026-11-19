# Vercel Container runtime: build Spring Boot, then run as a lightweight JVM image.
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN mvn --batch-mode -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/plateable-eats-0.1.0.jar /app/app.jar
# Optimise JVM interpretation/compilation for a short-lived, low-traffic demo container.
# TieredStopAtLevel=1 prioritises faster startup over peak throughput.
ENV JAVA_TOOL_OPTIONS="-XX:TieredStopAtLevel=1 -XX:MaxRAMPercentage=75.0"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

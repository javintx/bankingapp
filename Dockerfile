# Build stage
FROM maven:3-amazoncorretto-21-alpine AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Running stage
FROM gcr.io/distroless/java21-debian12

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 3000

CMD ["app.jar"]

HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:3000/health || exit 1

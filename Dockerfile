FROM maven:3-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .
RUN mvn package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 3000

CMD ["java", "-jar", "app.jar"]

HEALTHCHECK --interval=60s --retries=5 --start-period=5s --timeout=10s \
  CMD wget --no-verbose --tries=1 --spider localhost:3000/health || exit 1

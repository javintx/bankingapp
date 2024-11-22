FROM maven:3-amazoncorretto-21-alpine

WORKDIR /app

COPY pom.xml ./
COPY src ./src

ENTRYPOINT ["mvn", "spring-boot:run"]
EXPOSE 3000

HEALTHCHECK --interval=60s --retries=5 --start-period=5s --timeout=10s \
  CMD wget --no-verbose --tries=1 --spider localhost:3000/health || exit 1

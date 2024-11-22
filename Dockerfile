FROM maven:3-amazoncorretto-21-alpine

RUN apk update --no-cache --no-check-certificate && apk add --no-cache curl ca-certificates
WORKDIR /app

COPY pom.xml ./
COPY src ./src
EXPOSE 3000

CMD ["mvn", "spring-boot:run"]

HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:3000/health || exit 1


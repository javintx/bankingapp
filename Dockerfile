FROM maven:3-amazoncorretto-21-alpine

RUN apk update --no-cache --no-check-certificate && apk add --no-cache curl ca-certificates
WORKDIR /app

COPY pom.xml ./
COPY src ./src
EXPOSE 3000

CMD ["mvn", "spring-boot:run"]

HEALTHCHECK --interval=5s --timeout=5s --retries=5 --start-period=15s \
  CMD curl -f http://127.0.0.1:3000/health || exit 1


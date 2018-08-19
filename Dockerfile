FROM openjdk:8-jre-alpine

RUN apk add dumb-init libpurple libpurple-oscar
COPY target/teleicq*.jar app.jar

ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["java", "-jar", "/app.jar"]

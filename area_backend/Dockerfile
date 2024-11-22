FROM eclipse-temurin:17-jdk-focal

WORKDIR /app

ARG PORTS_SERVER

ENV PORTS_SERVER=${PORTS_SERVER}

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean

EXPOSE $PORTS_SERVER

CMD ["./mvnw", "spring-boot:run"]

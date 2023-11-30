FROM maven:3.8.1-jdk-11 AS build

ARG MAVEN_OPTS
ENV MAVEN_OPTS=${MAVEN_OPTS}

WORKDIR /api

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src/ /api/src/
RUN mvn clean package -DskipTests

FROM openjdk:11-jdk-slim

RUN mkdir -p /etc/apt/apt.conf.d/ && \
    echo 'Acquire::http::Proxy "http://192.168.4.8:3128";' > /etc/apt/apt.conf.d/proxy

WORKDIR /api

COPY --from=build /api/target/api-0.0.1-SNAPSHOT.jar /api/api.jar
COPY start.sh /api/start.sh

EXPOSE 4444

RUN chmod +x /api/start.sh

CMD ["/bin/bash", "/api/start.sh"]

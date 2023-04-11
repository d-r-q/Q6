FROM openjdk:17-alpine
EXPOSE 8080

ADD /build/distributions/Q6-1.0-SNAPSHOT.zip /tmp/
WORKDIR /tmp
RUN unzip Q6-1.0-SNAPSHOT.zip
RUN mkdir /app
RUN mv Q6-1.0-SNAPSHOT/* /app
WORKDIR /app
ENTRYPOINT "/app/bin/Q6"
FROM openjdk:alpine
EXPOSE 8080
RUN apk add libc6-compat
COPY target/scala-2.12/enrich.jar /usr/src/app/
WORKDIR /usr/src/app
CMD java -XX:+PrintFlagsFinal $JAVA_OPTIONS -jar enrich.jar
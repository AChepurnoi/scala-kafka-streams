FROM openjdk:alpine
EXPOSE 8080
COPY target/scala-2.12/enrich.jar /usr/src/app/
WORKDIR /usr/src/app
CMD java -XX:+PrintFlagsFinal $JAVA_OPTIONS -jar enrich.jar
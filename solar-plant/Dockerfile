FROM openjdk:alpine
EXPOSE 8080
COPY target/scala-2.12/solar.jar /usr/src/app/
WORKDIR /usr/src/app
CMD java -XX:+PrintFlagsFinal $JAVA_OPTIONS -jar solar.jar
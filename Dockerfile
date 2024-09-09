FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /app.jar
RUN mkdir -p /usr/local/newrelic
ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml
#ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
ENTRYPOINT ["java","-javaagent:/usr/local/newrelic/newrelic.jar", "-Dspring.profiles.active=docker","-jar","app.jar"]

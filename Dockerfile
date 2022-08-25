FROM openjdk:11-jdk
RUN apt-get update
RUN apt-get install -y maven
COPY pom.xml /usr/local/service/pom.xml
COPY src /usr/local/service/src
WORKDIR /usr/local/service
RUN mvn clean package
EXPOSE 8093
ENTRYPOINT ["java","-jar","target/subscriptions-service.jar"]
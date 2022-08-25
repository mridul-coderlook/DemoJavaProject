FROM openjdk:11-jdk
RUN apt-get update
RUN apt-get install -y maven
COPY pom.xml /usr/local/service/pom.xml
COPY src /usr/local/service/src
WORKDIR /usr/local/service
RUN mvn -Dmaven.test.skip package
EXPOSE 8093
ENTRYPOINT ["java","-jar","/subscriptions-service.jar"]
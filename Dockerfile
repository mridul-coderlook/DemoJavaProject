FROM openjdk:11-jdk
EXPOSE 8093
ADD target/subscriptions-service.jar subscriptions-service.jar
ENTRYPOINT ["java","-jar","/subscriptions-service.jar"]
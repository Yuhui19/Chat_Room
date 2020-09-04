FROM openjdk:12
WORKDIR /
ADD build/libs/ChatServer.jar ChatServer.jar
ADD resources/ resources/
EXPOSE 8080
CMD java -jar ChatServer.jar
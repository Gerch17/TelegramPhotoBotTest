FROM openjdk:11-jre-slim

COPY photo-bot-mvn.jar /chroot/

EXPOSE 8080
ENTRYPOINT ["java","-jar","/chroot/photo-bot-mvn.jar"]
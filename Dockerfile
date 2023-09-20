FROM openjdk:latest

WORKDIR /app

# Copy the JAR file from the build/libs directory to the /app directory in the image
COPY build/libs/trafileaTestProject-0.0.1-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
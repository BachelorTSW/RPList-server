# AS <NAME> to name this stage as maven
FROM maven:3-amazoncorretto-17 AS builder

WORKDIR /app
COPY . .
# Compile and package the application to an executable JAR
RUN mvn package

# For Java 17
FROM amazoncorretto:17-alpine

WORKDIR /app

# Copy the spring-boot-api-tutorial.jar from the maven stage to the /opt/app directory of the current stage.
COPY --from=builder /app/target/rplist-server.jar /app

ENTRYPOINT ["java","-jar","rplist-server.jar"]
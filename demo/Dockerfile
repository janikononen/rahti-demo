# Stage 1: build
FROM docker.io/maven:3.9.12-eclipse-temurin-25-noble AS builder

WORKDIR /tmp

# copy pom download dependencies from cache 
COPY ./pom.xml .
RUN mvn dependency:go-offline -B

# copy sourcecode and compile
COPY ./src ./src
RUN mvn clean package -DskipTests


# Stage 2: Production image
FROM docker.io/eclipse-temurin:25-jre-noble

WORKDIR /app
COPY --from=builder /tmp/target/*.jar /app.jar

EXPOSE 8080

# Start application with prod -profile
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]
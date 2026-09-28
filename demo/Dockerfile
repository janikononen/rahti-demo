# Stage 1: build
FROM docker.io/maven:3.9.12-eclipse-temurin-25-noble AS builder

WORKDIR /tmp

# copy pom download dependencies from cache 
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline

# copy sourcecode and compile
COPY ./src ./src
RUN ./mvnw clean install -DskipTests 


# Stage 2: Production image
FROM docker.io/eclipse-temurin:25-jre-noble

WORKDIR /app
COPY --from=builder /tmp/target/*.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
FROM maven:3.8.5-openjdk-17 as builder
WORKDIR /app
COPY pom.xml .
COPY src /app/src
# RUN mvn dependency:go-offline -B
RUN mvn package -Pproduction -DskipTests

FROM eclipse-temurin:17.0.12_7-jre
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY --from=builder /app/target/performancerinhabackend-1.0-SNAPSHOT.jar app.jar

ENV JAVA_OPTS="-XX:+UseG1GC -Xms128m -Xmx640m"
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar

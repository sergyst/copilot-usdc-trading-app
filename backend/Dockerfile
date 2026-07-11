FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn

RUN apk add --no-cache maven && mvn dependency:go-offline

COPY src src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=0 /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

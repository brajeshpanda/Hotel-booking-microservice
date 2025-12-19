# -------- BUILD STAGE --------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent POM and module POM
COPY pom.xml .
COPY admin-server/pom.xml admin-server/pom.xml

# Download deps
RUN mvn -B -q -e -DskipTests dependency:go-offline

# Copy full source
COPY . .

# Build only admin-server module
RUN mvn -pl admin-server -am clean package -DskipTests

# -------- RUNTIME STAGE --------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/admin-server/target/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","app.jar"]

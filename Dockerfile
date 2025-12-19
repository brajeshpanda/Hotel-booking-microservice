# -------- BUILD STAGE --------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy everything at once (important)
COPY . .

# Build only admin-server and required parents
RUN mvn -pl admin-server -am clean package -DskipTests

# -------- RUNTIME STAGE --------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/admin-server/target/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","app.jar"]

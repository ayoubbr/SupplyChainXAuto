# Étape 1 : Build du projet
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copier uniquement le pom.xml et télécharger les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source ensuite
COPY src ./src

# Compiler le projet
RUN mvn clean package -DskipTests

# Étape 2 : Exécution de l'application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/supplyChainX-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
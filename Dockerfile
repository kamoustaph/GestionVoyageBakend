# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copier les fichiers
COPY pom.xml .
COPY src ./src

# CORRECTION : Créer application.properties avec configuration DOCKER
RUN rm -f /app/src/main/resources/application.properties && \
    cat > /app/src/main/resources/application.properties << 'EOF'
spring.application.name=gestionVoyage

# Configuration base de données POUR DOCKER (mysql-db = nom du service)
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://mysql-db:3306/gestionVoyage
spring.datasource.username=root
spring.datasource.password=771577089

# Configuration JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

# Configuration mail POUR DOCKER (mailhog = nom du service)
spring.mail.host=mailhog
spring.mail.port=1025
spring.mail.username=
spring.mail.password=
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false

logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.moustapha.teach=DEBUG

# Server
server.port=8093

spring.web.cors.allowed-origins=http://localhost:4200,http://127.0.0.1:4200
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
spring.main.allow-circular-references=true

# ===== Google OAuth2 Configuration =====
spring.security.oauth2.client.registration.google.client-id=568182573172-b1kh9c7tpbg0j20bkv92l6k15sblj98h.apps.googleusercontent.com
spring.security.oauth2.client.registration.google.client-secret=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
spring.security.oauth2.client.registration.google.scope=openid,profile,email
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8093/login/oauth2/code/google

# Variable personnalisée
frontend.url=http://localhost:4200
EOF

# CORRECTION : Forcer l'encodage UTF-8 pour Maven
ENV MAVEN_OPTS="-Dfile.encoding=UTF-8"

# Télécharger les dépendances
RUN mvn dependency:go-offline -B

# Build avec encodage explicite
RUN mvn clean package -DskipTests -Dfile.encoding=UTF-8

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copier le JAR
COPY --from=build /app/target/*.jar app.jar

# Port exposé
EXPOSE 8093

# Health check (simplifié sans wget)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD [ -e /tmp/spring-health ] || exit 1

# Démarrer avec encodage UTF-8
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copier les fichiers
COPY pom.xml .
COPY src ./src

# CORRECTION : Créer application.properties avec configuration DOCKER
RUN rm -f /app/src/main/resources/application.properties && \
    cat > /app/src/main/resources/application.properties << 'EOF'
spring.application.name=gestionVoyage

# Configuration base de données POUR DOCKER (mysql-db = nom du service)
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://mysql-db:3306/gestionVoyage
spring.datasource.username=root
spring.datasource.password=771577089

# Configuration JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

# Configuration mail POUR DOCKER (mailhog = nom du service)
spring.mail.host=mailhog
spring.mail.port=1025
spring.mail.username=
spring.mail.password=
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false

logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.moustapha.teach=DEBUG

# Server
server.port=8093

spring.web.cors.allowed-origins=http://localhost:4200,http://127.0.0.1:4200
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
spring.main.allow-circular-references=true

# ===== Google OAuth2 Configuration =====
spring.security.oauth2.client.registration.google.client-id=568182573172-b1kh9c7tpbg0j20bkv92l6k15sblj98h.apps.googleusercontent.com
spring.security.oauth2.client.registration.google.client-secret=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
spring.security.oauth2.client.registration.google.scope=openid,profile,email
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8093/login/oauth2/code/google

# Variable personnalisée
frontend.url=http://localhost:4200
EOF

# CORRECTION : Forcer l'encodage UTF-8 pour Maven
ENV MAVEN_OPTS="-Dfile.encoding=UTF-8"

# Télécharger les dépendances
RUN mvn dependency:go-offline -B

# Build avec encodage explicite
RUN mvn clean package -DskipTests -Dfile.encoding=UTF-8

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copier le JAR
COPY --from=build /app/target/*.jar app.jar

# Port exposé
EXPOSE 8093

# Health check (simplifié sans wget)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD [ -e /tmp/spring-health ] || exit 1

# Démarrer avec encodage UTF-8
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
-- Création de la base de données
CREATE DATABASE IF NOT EXISTS gestionVoyage;
USE gestionVoyage;

-- Création de l'utilisateur (identique à celui dans application.properties)
CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY '771577089';
GRANT ALL PRIVILEGES ON gestionVoyage.* TO 'root'@'%';

-- Alternative : Créer un utilisateur dédié (plus sécurisé)
CREATE USER IF NOT EXISTS 'gestion_user'@'%' IDENTIFIED BY 'StrongPassword123!';
GRANT ALL PRIVILEGES ON gestionVoyage.* TO 'gestion_user'@'%';

FLUSH PRIVILEGES;
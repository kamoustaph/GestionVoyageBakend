
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    password VARCHAR(255) NOT NULL,
    actif BOOLEAN DEFAULT true
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS voyages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    lieu_depart VARCHAR(255) NOT NULL,
    lieu_arrivee VARCHAR(255) NOT NULL,
    date_depart TIMESTAMP NOT NULL,
    date_retour TIMESTAMP NOT NULL,
    prix DECIMAL(10,2) NOT NULL,
    places_disponibles INT NOT NULL,
    places_total INT NOT NULL,
    statut VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    voyage_id BIGINT NOT NULL,
    statut VARCHAR(20) NOT NULL,
    date_reservation TIMESTAMP NOT NULL,
    date_expiration TIMESTAMP,
    nombre_places INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (voyage_id) REFERENCES voyages(id)
);

CREATE TABLE IF NOT EXISTS paiements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    montant DECIMAL(10,2) NOT NULL,
    methode VARCHAR(20) NOT NULL,
    statut VARCHAR(20) NOT NULL,
    date_paiement TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);

CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    qr_code VARCHAR(255) NOT NULL UNIQUE,
    statut VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);


SET REFERENTIAL_INTEGRITY FALSE;

DELETE FROM tickets;
DELETE FROM paiements;
DELETE FROM reservations;
DELETE FROM voyages;
DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM roles;

INSERT INTO roles (id, name) VALUES
(1, 'ROLE_USER'),
(2, 'ROLE_ADMIN');

INSERT INTO users (id, username, name, password, actif) VALUES
(1, 'john.doe@example.com', 'Doe John', 'password123', true),
(2, 'jane.smith@example.com', 'Smith Jane', 'password123', true),
(3, 'pierre.martin@example.com', 'Martin Pierre', 'password123', true),
(4, 'marie.dupont@example.com', 'Dupont Marie', 'password123', true);

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), (2, 1), (3, 1), (4, 1), (1, 2);

INSERT INTO voyages (id, lieu_depart, lieu_arrivee, date_depart, date_retour, prix, places_disponibles, places_total, statut) VALUES
(1, 'Paris', 'Lyon', '2024-12-01 08:00:00', '2024-12-01 12:00:00', 50.00, 48, 50, 'ACTIF'),
(2, 'Marseille', 'Nice', '2024-12-02 09:00:00', '2024-12-02 13:00:00', 40.00, 49, 50, 'ACTIF'),
(3, 'Toulouse', 'Bordeaux', '2024-12-03 10:00:00', '2024-12-03 14:00:00', 35.00, 50, 50, 'ACTIF'),
(4, 'Lille', 'Strasbourg', '2024-12-04 07:30:00', '2024-12-04 11:30:00', 60.00, 45, 50, 'ACTIF'),
(5, 'Nantes', 'Rennes', '2024-12-05 14:00:00', '2024-12-05 16:00:00', 25.00, 50, 50, 'ACTIF');

INSERT INTO reservations (id, user_id, voyage_id, statut, date_reservation, date_expiration, nombre_places) VALUES
(1, 1, 1, 'CONFIRMED', '2024-11-25 10:00:00', '2024-11-30 23:59:59', 2),
(2, 2, 2, 'PENDING', '2024-11-26 11:00:00', '2024-12-01 23:59:59', 1),
(3, 3, 1, 'CONFIRMED', '2024-11-27 14:30:00', '2024-12-01 23:59:59', 1),
(4, 4, 3, 'CANCELLED', '2024-11-28 16:00:00', '2024-12-02 23:59:59', 3),
(5, 1, 4, 'PENDING', '2024-11-29 09:15:00', '2024-12-03 23:59:59', 2),
(6, 2, 5, 'COMPLETED', '2024-11-20 12:00:00', '2024-11-25 23:59:59', 1);

INSERT INTO paiements (id, reservation_id, montant, methode, statut, date_paiement) VALUES
(1, 1, 100.00, 'CREDIT_CARD', 'VALIDE', '2024-11-25 10:05:00'),
(2, 2, 40.00, 'PAYPAL', 'EN_ATTENTE', '2024-11-26 11:05:00'),
(3, 3, 50.00, 'ORANGE_MONEY', 'VALIDE', '2024-11-27 14:35:00'),
(4, 5, 120.00, 'WAVE', 'EN_ATTENTE', '2024-11-29 09:20:00');

INSERT INTO tickets (id, reservation_id, qr_code, statut, created_at) VALUES
(1, 1, 'QR123456ABCDEF', 'VALIDE', '2024-11-25 10:10:00'),
(2, 3, 'QR789012GHIJKL', 'VALIDE', '2024-11-27 14:40:00');

SET REFERENTIAL_INTEGRITY TRUE;
# Parcial-web-2

# El código se encuentra en la rama master

# Queries para la creacion de la db

CREATE DATABASE IF NOT EXISTS parcial_2
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE parcial_2;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre_completo VARCHAR(80) NOT NULL,
    username VARCHAR(40) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMINISTRADOR', 'MEDICO', 'PACIENTE') NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuarios_username (username)
);

CREATE TABLE IF NOT EXISTS consultas_medicas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre_paciente VARCHAR(40) NOT NULL,
    motivo_consulta VARCHAR(100) NOT NULL,
    numero_consultorio INT NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    medico_id BIGINT NOT NULL,
    paciente_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_consultas_medico (medico_id),
    KEY idx_consultas_paciente (paciente_id),
    CONSTRAINT fk_consultas_medico
        FOREIGN KEY (medico_id) REFERENCES usuarios(id),
    CONSTRAINT fk_consultas_paciente
        FOREIGN KEY (paciente_id) REFERENCES usuarios(id)
);

INSERT INTO usuarios (nombre_completo, username, password, rol) VALUES
('Administrador General', 'admin', '$2a$10$Qos7CiNS0TmyydKBvUFAROLCSHI7NkIPInQK/lI6xo8DzCTOYYt9.', 'ADMINISTRADOR'),
('Dra. Laura Medina', 'medico1', '$2a$10$n9gLRK4tSwqyhQdXoNmJ3.HfbHb.sFKGvUE6Gc4Nz7SfDJ6mbJC22', 'MEDICO'),
('Dr. Carlos Rojas', 'medico2', '$2a$10$5xT3.IcfLdO9wG.MuZw7auRqqqDQsQuOVeLbvqst9Eu7ScLNy9yJ.', 'MEDICO'),
('Ana Torres', 'paciente1', '$2a$10$qt/z.AcF0xd8srdN7QeFnuDUuTB/xxIzwYnuThVVrY4vMOyl2PgfC', 'PACIENTE'),
('Miguel Castro', 'paciente2', '$2a$10$JIx99ZheES4LAXY6ujhj9uBBIGBijuZc7F7zCTHkT6y4sw.zQeGL2', 'PACIENTE');

INSERT INTO consultas_medicas (nombre_paciente, motivo_consulta, numero_consultorio, hora_inicio, hora_fin, medico_id, paciente_id) VALUES
('Ana Torres', 'Control general y revision de signos vitales.', 101, '07:00:00', '07:30:00',
 (SELECT id FROM usuarios WHERE username = 'medico1'),
 (SELECT id FROM usuarios WHERE username = 'paciente1')),
('Miguel Castro', 'Seguimiento posterior a tratamiento.', 102, '08:00:00', '08:30:00',
 (SELECT id FROM usuarios WHERE username = 'medico2'),
 (SELECT id FROM usuarios WHERE username = 'paciente2'));

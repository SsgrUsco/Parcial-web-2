CREATE DATABASE IF NOT EXISTS parcial2
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE parcial2;

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

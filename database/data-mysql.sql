USE parcial2;

INSERT INTO usuarios (nombre_completo, username, password, rol) VALUES
('Administrador General', 'admin', '$2a$10$3qYqICboFy93CoJPcd1a/OHaT.2ogX9/XFiy2fa3xkxxwwvEQ4pau', 'ADMINISTRADOR'),
('Dra. Laura Medina', 'medico1', '$2a$10$bz4UOoSfyPHCVIy7QNL70uHpAp1CPjXuHyjRXfARkVbG1QNWkRhAW', 'MEDICO'),
('Dr. Carlos Rojas', 'medico2', '$2a$10$bz4UOoSfyPHCVIy7QNL70uHpAp1CPjXuHyjRXfARkVbG1QNWkRhAW', 'MEDICO'),
('Ana Torres', 'paciente1', '$2a$10$QBHcln7I.Ubm6kQ7VMKwUuX93Wjdw5A.sTGVgQExZZULK9b0jlYdC', 'PACIENTE'),
('Miguel Castro', 'paciente2', '$2a$10$QBHcln7I.Ubm6kQ7VMKwUuX93Wjdw5A.sTGVgQExZZULK9b0jlYdC', 'PACIENTE');

INSERT INTO consultas_medicas (nombre_paciente, motivo_consulta, numero_consultorio, hora_inicio, hora_fin, medico_id, paciente_id) VALUES
('Ana Torres', 'Control general y revision de signos vitales.', 101, '07:00:00', '07:30:00',
 (SELECT id FROM usuarios WHERE username = 'medico1'),
 (SELECT id FROM usuarios WHERE username = 'paciente1')),
('Miguel Castro', 'Seguimiento posterior a tratamiento.', 102, '08:00:00', '08:30:00',
 (SELECT id FROM usuarios WHERE username = 'medico2'),
 (SELECT id FROM usuarios WHERE username = 'paciente2'));

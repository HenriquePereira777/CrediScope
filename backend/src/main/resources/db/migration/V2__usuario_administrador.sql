-- Usuário administrador inicial
--   e-mail: admin@crediscope.local
--   senha:  admin123   (TROQUE após o primeiro acesso)
INSERT INTO usuario (nome, email, senha_hash, perfil, dois_fatores, ativo)
VALUES ('Administrador', 'admin@crediscope.local',
        '$2a$10$bppIFR0/kiDOWXqdw6yWWOO1hZee.m.N8Zhj13tdz5J.O.9QkLWlC',
        'ADMINISTRADOR', FALSE, TRUE);

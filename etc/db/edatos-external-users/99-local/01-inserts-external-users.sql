INSERT INTO tb_external_users (id, opt_lock, email, password, name, surname1, surname2, treatment, language, phone_number, organism, created_by, created_date, email_notifications_enabled)
VALUES (1, 1, 'arte-consultores-elizabeth@test.com', 'password', 'Elizabeth', 'Rodríguez', 'Elvira', 'LADY', 'SPANISH', '612345678', 'Agencia Tributaria', 'system', '2021-04-15 08:19:33.000000', true);

INSERT INTO tb_external_users (id, opt_lock, email, password, name, surname1, surname2, language, created_by, created_date, email_notifications_enabled)
VALUES (2, 1, 'arte-consultores-leo@test.com', 'password', 'Leo', 'Carballo', 'Peral', 'CATALAN', 'system', '2021-04-15 08:19:33.000000', true);

INSERT INTO tb_external_users (id, opt_lock, email, password, name, surname1, surname2, language, created_by, created_date, email_notifications_enabled)
VALUES (3, 1, 'arte-consultores-maria@test.com', 'password', 'Maria Julia', 'Chacón', 'Reyes', 'CATALAN', 'system', '2021-04-15 08:19:33.000000', true);

INSERT INTO tb_external_users (id, opt_lock, email, password, name, surname1, surname2, treatment, language, created_by, created_date, email_notifications_enabled)
VALUES (4, 1, 'arte-consultores-luis@test.com', 'password', 'Luis  Marco', 'Antón', 'Ochoa', 'MISTER', 'SPANISH', 'system', '2021-04-15 08:19:33.000000', true);

ALTER SEQUENCE seq_tb_external_users RESTART WITH 5;

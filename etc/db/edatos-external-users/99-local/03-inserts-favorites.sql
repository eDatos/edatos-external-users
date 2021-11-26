INSERT INTO tb_favorites (id, created_by, created_date, last_modified_by, last_modified_date, opt_lock, category_fk, external_operation_fk, external_user_fk)
VALUES (1, 'admin', '2021-04-16 14:27:46.038000', 'admin', '2021-04-16 14:27:46.038000', 0, 14, null, 1),
       (2, 'admin', '2021-04-16 14:27:46.542000', 'admin', '2021-04-16 14:27:46.542000', 0, 17, null, 1),
       (3, 'admin', '2021-04-16 14:27:46.929000', 'admin', '2021-04-16 14:27:46.929000', 0, 12, null, 1),
       (4, 'admin', '2021-04-16 14:27:47.924000', 'admin', '2021-04-16 14:27:47.924000', 0, 5, null, 1),
       (5, 'admin', '2021-04-16 14:27:48.915000', 'admin', '2021-04-16 14:27:48.915000', 0, 33, null, 1),
       (6, 'admin', '2021-04-16 14:27:49.274000', 'admin', '2021-04-16 14:27:49.274000', 0, 40, null, 1),
       (7, 'admin', '2021-04-16 14:27:49.543000', 'admin', '2021-04-16 14:27:49.543000', 0, 24, null, 1),
       (8, 'admin', '2021-04-16 14:28:16.517000', 'admin', '2021-04-16 14:28:16.517000', 0, 1, null, 1);

ALTER SEQUENCE seq_tb_favorites RESTART WITH 9;

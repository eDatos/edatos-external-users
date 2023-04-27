------------------------------------------------------------------
--------------------------- CATEGORIES ---------------------------
------------------------------------------------------------------

INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (1, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Estadísticas demográficas y sociales",
      "locale": "es"
    }
  ]
}', 1, null);


INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (2, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Población y migración",
      "locale": "es"
    }
  ]
}', 1, 1);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (3, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Trabajo",
      "locale": "es"
    }
  ]
}', 2, 1);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (4, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Educación",
      "locale": "es"
    }
  ]
}', 3, 1);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (5, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Salud",
      "locale": "es"
    }
  ]
}', 4, 1);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (6, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Ingresos y consumo",
      "locale": "es"
    }
  ]
}', 5, 1);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (7, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Protección social",
      "locale": "es"
    }
  ]
}', 6, 1);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (8, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Asentamientos y vivienda",
      "locale": "es"
    }
  ]
}', 7, 1);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (9, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Justicia y crímenes",
      "locale": "es"
    }
  ]
}', 8, 1);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (10, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Cultura",
      "locale": "es"
    }
  ]
}', 9, 1);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (11, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Política y otras actividades de la comunidad",
      "locale": "es"
    }
  ]
}', 10, 1);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (12, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Ocio",
      "locale": "es"
    }
  ]
}', 11, 1);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (13, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Estadísticas económicas",
      "locale": "es"
    }
  ]
}', 2, null);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (14, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Estadísticas macroeconómicas",
      "locale": "es"
    }
  ]
}', 1, 13);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (15, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Cuentas económicas",
      "locale": "es"
    }
  ]
}', 2, 13);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (16, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Estadísticas empresariales",
      "locale": "es"
    }
  ]
}', 3, 13);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (17, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Estadísticas sectoriales",
      "locale": "es"
    }
  ]
}', 4, 13);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (18, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Agricultura, silvicultura y pesca",
      "locale": "es"
    }
  ]
}', 1, 17);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (19, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Energía",
      "locale": "es"
    }
  ]
}', 2, 17);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (20, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Minería, manufactura y construcción",
      "locale": "es"
    }
  ]
}', 3, 17);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (21, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Transporte",
      "locale": "es"
    }
  ]
}', 4, 17);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (22, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Turismo",
      "locale": "es"
    }
  ]
}', 5, 17);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (23, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Banca, seguros y estadísticas financieras",
      "locale": "es"
    }
  ]
}', 6, 17);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (24, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Finanzas públicas, fiscales y de estadísticas del sector público",
      "locale": "es"
    }
  ]
}', 5, 13);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (25, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Comercio internacional y balanza de pagos",
      "locale": "es"
    }
  ]
}', 6, 13);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (26, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Precios",
      "locale": "es"
    }
  ]
}', 7, 13);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (27, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Coste laboral",
      "locale": "es"
    }
  ]
}', 8, 13);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (28, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Ciencia, tecnología e innovación",
      "locale": "es"
    }
  ]
}', 9, 13);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (29, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Medio Ambiente y estadísticas multi-dominio",
      "locale": "es"
    }
  ]
}', 3, null);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (30, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Medio Ambiente",
      "locale": "es"
    }
  ]
}', 1, 29);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (31, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Estadísticas regionales y en áreas pequeñas",
      "locale": "es"
    }
  ]
}', 2, 29);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (32, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Estadísticas e indicadores multi-dominio",
      "locale": "es"
    }
  ]
}', 3, 29);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (33, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Condiciones de vida, pobreza y cuestiones sociales transversales",
      "locale": "es"
    }
  ]
}', 1, 32);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (34, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Género y grupos especiales de población",
      "locale": "es"
    }
  ]
}', 2, 32);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (35, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Sociedad de la información",
      "locale": "es"
    }
  ]
}', 3, 32);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (36, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Globalización",
      "locale": "es"
    }
  ]
}', 4, 32);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (37, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Indicadores relacionados con los Objetivos de Desarrollo del Milenio",
      "locale": "es"
    }
  ]
}', 5, 32);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (38, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Desarrollo sostenible",
      "locale": "es"
    }
  ]
}', 6, 32);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (39, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Espíritu empresarial",
      "locale": "es"
    }
  ]
}', 7, 32);



INSERT INTO tb_categories (id, created_by, created_date, opt_lock, name, index, parent_fk)
VALUES (40, 'system', now(), 1, '{
  "texts": [
    {
      "label": "Anuarios y recopilaciones similares",
      "locale": "es"
    }
  ]
}', 4, 29);


ALTER SEQUENCE seq_tb_categories RESTART WITH 41;

------------------------------------------------------------------
----------------------- EXTERNAL OPERATIONS ----------------------
------------------------------------------------------------------

INSERT INTO tb_external_items (id, opt_lock, code, name, urn, type)
VALUES (41, 1, 'op1', '{
  "texts": [
    {
      "label": "Nacimientos",
      "locale": "es"
    }
  ]
}', 'urn:sdmx:org.sdmx.infomodel.categoryscheme.Category=ISTAC:SDMXStatSubMatDomainsWD1(01.000).op1',
        'statisticalOperations#operation');

INSERT INTO tb_external_operations (external_category_urn, enabled, notifications_enabled, publication_date,
                                    external_item_fk)
VALUES ('urn:sdmx:org.sdmx.infomodel.categoryscheme.Category=ISTAC:SDMXStatSubMatDomainsWD1(01.000).1.1', true, true,
        now(), 41);

------------------------------------------------------------------

INSERT INTO tb_external_items (id, opt_lock, code, name, urn, type)
VALUES (42, 1, 'op2', '{
  "texts": [
    {
      "label": "Fallecimientos",
      "locale": "es"
    }
  ]
}', 'urn:sdmx:org.sdmx.infomodel.categoryscheme.Category=ISTAC:SDMXStatSubMatDomainsWD1(01.000).op2',
        'statisticalOperations#operation');

INSERT INTO tb_external_operations (external_category_urn, enabled, notifications_enabled, publication_date,
                                    external_item_fk)
VALUES ('urn:sdmx:org.sdmx.infomodel.categoryscheme.Category=ISTAC:SDMXStatSubMatDomainsWD1(01.000).1.1', true, true,
        now(), 42);

------------------------------------------------------------------

INSERT INTO tb_external_items (id, opt_lock, code, name, urn, type)
VALUES (43, 1, 'op3', '{
  "texts": [
    {
      "label": "Producción de energía",
      "locale": "es"
    }
  ]
}', 'urn:sdmx:org.sdmx.infomodel.categoryscheme.Category=ISTAC:SDMXStatSubMatDomainsWD1(01.000).op3',
        'statisticalOperations#operation');

INSERT INTO tb_external_operations (external_category_urn, enabled, notifications_enabled, publication_date,
                                    external_item_fk)
VALUES ('urn:sdmx:org.sdmx.infomodel.categoryscheme.Category=ISTAC:SDMXStatSubMatDomainsWD1(01.000).2.4.2', true, true,
        now(), 43);

------------------------------------------------------------------

INSERT INTO tb_external_items (id, opt_lock, code, name, urn, type)
VALUES (44, 1, 'op4', '{
  "texts": [
    {
      "label": "Consumo de energía",
      "locale": "es"
    }
  ]
}', 'urn:sdmx:org.sdmx.infomodel.categoryscheme.Category=ISTAC:SDMXStatSubMatDomainsWD1(01.000).op4',
        'statisticalOperations#operation');

INSERT INTO tb_external_operations (external_category_urn, enabled, notifications_enabled, publication_date,
                                    external_item_fk)
VALUES ('urn:sdmx:org.sdmx.infomodel.categoryscheme.Category=ISTAC:SDMXStatSubMatDomainsWD1(01.000).2.4.2', true, true,
        now(), 44);


------------------------------------------------------------------

INSERT INTO tb_external_items (id, opt_lock, code, name, urn, type)
VALUES (45, 1, 'dt1', '{
  "texts": [
    {
      "label": "Consumo de energía",
      "locale": "es"
    }
  ]
}', 'urn:sdmx:org.sdmx.infomodel.categoryscheme.Category=ISTAC:SDMXStatSubMatDomainsWD1(01.000).dt1',
        'statisticalResources#dataset');


INSERT INTO public.tb_external_datasets (recived_date, external_operation_urn, external_item_fk)
VALUES ('2021-07-01 11:31:51.000000',
        'urn:sdmx:org.sdmx.infomodel.categoryscheme.Category=ISTAC:SDMXStatSubMatDomainsWD1(01.000).op3', 45);

------------------------------------------------------------------

INSERT INTO tb_external_items (id, opt_lock, code, name, urn, type)
VALUES (46, 1, 'dt2', '{
  "texts": [
    {
      "label": "Producción de energía",
      "locale": "es"
    }
  ]
}', 'urn:sdmx:org.sdmx.infomodel.categoryscheme.Category=ISTAC:SDMXStatSubMatDomainsWD1(01.000).dt2',
        'statisticalResources#dataset');


INSERT INTO public.tb_external_datasets (recived_date, external_operation_urn, external_item_fk)
VALUES ('2021-07-01 11:31:51.000000',
        'urn:sdmx:org.sdmx.infomodel.categoryscheme.Category=ISTAC:SDMXStatSubMatDomainsWD1(01.000).op4', 46);


ALTER SEQUENCE seq_tb_external_items RESTART WITH 5;


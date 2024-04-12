-- Función que genera una cadena aleatoria del tamaño proporcionado por parámetro
CREATE OR REPLACE FUNCTION RANDOM_STRING(numeric)
RETURNS text
AS $$
SELECT array_to_string(
           ARRAY(
               SELECT substring('0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ' FROM (random() * 26 + 1)::int FOR 1) -- + 1 en random porque a veces generaba substring de 1 vacío
      FROM generate_series(1, $1) ), '')
           $$ LANGUAGE sql;

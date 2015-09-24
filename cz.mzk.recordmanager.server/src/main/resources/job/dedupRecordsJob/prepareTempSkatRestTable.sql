DROP TABLE IF EXISTS tmp_skat_keys_rest;

CREATE TABLE tmp_skat_keys_rest AS
SELECT 
  nextval('tmp_table_id_seq') as row_id,
  skat_record_id,
  array_to_string(array_agg(hr.id), ',')  id_array
FROM skat_keys sk 
  INNER JOIN sigla s ON sk.sigla = s.sigla
  INNER JOIN harvested_record hr ON hr.import_conf_id = s.import_conf_id AND hr.raw_001_id = sk.local_record_id
GROUP BY sk.skat_record_id
HAVING max(hr.updated) > ANY(SELECT time from last_dedup_time) AND bool_or(sk.manually_merged) IS NOT true AND count(hr.id) > 1;

CREATE INDEX tmp_skat_keys_rest_idx ON tmp_skat_keys_rest(row_id);
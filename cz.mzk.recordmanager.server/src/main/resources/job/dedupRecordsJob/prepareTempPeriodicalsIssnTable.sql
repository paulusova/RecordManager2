DROP TABLE IF EXISTS tmp_simmilar_periodicals_issn;

CREATE TABLE tmp_simmilar_periodicals_issn AS
SELECT
  nextval('tmp_table_id_seq') as row_id,
  i.issn,
  t.title,
  array_to_string(array_agg(hr.id), ',')  id_array
FROM harvested_record hr 
  INNER JOIN issn i ON hr.id = i.harvested_record_id 
  INNER JOIN title t ON hr.id = t.harvested_record_id
  INNER JOIN harvested_record_format_link hrl on hr.id = hrl.harvested_record_id
WHERE hr.id IN (
    SELECT hrfl.harvested_record_id FROM harvested_record_format_link hrfl 
    INNER JOIN harvested_record_format hrf ON hrf.id = hrfl.harvested_record_format_id
    WHERE hrf.name = 'PERIODICALS')
GROUP BY i.issn,t.title
HAVING COUNT(DISTINCT hr.id) > 1 
  AND count(DISTINCT dedup_record_id) + sum(case when dedup_record_id is null then 1 else 0 end) != 1
  AND max(hr.updated) > ALL(SELECT time FROM last_dedup_time);
  
CREATE INDEX tmp_simmilar_periodicals_issn_idx ON tmp_simmilar_periodicals_issn(row_id);
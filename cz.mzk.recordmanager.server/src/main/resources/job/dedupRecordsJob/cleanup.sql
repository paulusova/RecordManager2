DELETE FROM dedup_record WHERE id IN (SELECT id FROM dedup_record EXCEPT (SELECT dedup_record_id FROM harvested_record));
EXPLAIN ANALYZE
SELECT id, concert_id, concert_date
FROM concert_schedule IGNORE INDEX (uk_concert_id_date)
WHERE concert_id = 1000 AND concert_date > '2024-03-01'

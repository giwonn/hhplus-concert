DELIMITER $$

TRUNCATE concert_schedule;
DROP PROCEDURE if exists insert_concert_data;
CREATE PROCEDURE insert_concert_data()
BEGIN
  DECLARE concert_id INT DEFAULT 1;
  DECLARE concert_date INT DEFAULT 0;

  START TRANSACTION;

  -- 약 3650만개
  WHILE concert_id <= 100000 DO
      SET concert_date = 0;

      WHILE concert_date < 365 DO
          INSERT INTO concert_schedule (concert_id, concert_date)
          VALUES (concert_id, DATE_ADD('2025-01-01', INTERVAL concert_date DAY));
      END WHILE;

      SET concert_id = concert_id + 1;

      -- 10만 건마다 커밋
      IF concert_id % 1000 = 0 THEN
        COMMIT;
        START TRANSACTION;
      END IF;
    END WHILE;

  COMMIT;
END$$

DELIMITER ;

-- 프로시저 실행
CALL insert_concert_data();



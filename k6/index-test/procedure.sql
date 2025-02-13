DELIMITER $$

TRUNCATE concert_schedule;
DROP PROCEDURE if exists insert_concert_schedule_data;
CREATE PROCEDURE insert_concert_schedule_data()
BEGIN
  DECLARE concert_id INT DEFAULT 1;
  DECLARE concert_date INT DEFAULT 0;

  START TRANSACTION;

  -- 약 80만개

  -- 일정이 많은 콘서트 (30만건)
  WHILE concert_id <= 30000 DO
      SET concert_date = 0;

      WHILE concert_date < 100 DO
          INSERT INTO concert_schedule (concert_id, concert_date)
          VALUES (concert_id, DATE_ADD('2025-01-01', INTERVAL concert_date DAY));
          SET concert_date = concert_date + 1;
        END WHILE;

      SET concert_id = concert_id + 1;

      -- 10만 건마다 커밋
      IF concert_id % 1000 = 0 THEN
        COMMIT;
        START TRANSACTION;
      END IF;
    END WHILE;

  -- 일정이 적은 콘서트 (50만건)
  WHILE concert_id <= 80000 DO
      SET concert_date = 50;

      WHILE concert_date < 150 DO
          INSERT INTO concert_schedule (concert_id, concert_date)
          VALUES (concert_id, DATE_ADD('2025-01-01', INTERVAL concert_date DAY));
          SET concert_date = concert_date + 1;
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
CALL insert_concert_schedule_data();

ANALYZE TABLE concert_schedule;



DELIMITER $$

TRUNCATE concert_seat;
DROP PROCEDURE if exists insert_concert_seat_data;
CREATE PROCEDURE insert_concert_seat_data()
BEGIN
  DECLARE concert_schedule_id INT DEFAULT 1;
  DECLARE seat_num INT DEFAULT 1;

  START TRANSACTION;

  -- 약 80만개

  -- 일정이 많은 콘서트 (30만건)
  WHILE concert_schedule_id <= 30000 DO
      SET seat_num = 1;

      WHILE seat_num <= 50 DO
          INSERT INTO concert_seat (concert_schedule_id, seat_num, amount, is_reserved)
          VALUES (concert_schedule_id, seat_num, 1000, RAND() < 0.8);
          SET seat_num = seat_num + 1;
        END WHILE;

      SET concert_schedule_id = concert_schedule_id + 1;

      -- 10만 건마다 커밋
      IF concert_schedule_id % 1000 = 0 THEN
        COMMIT;
        START TRANSACTION;
      END IF;
    END WHILE;

  COMMIT;
END$$

DELIMITER ;

-- 프로시저 실행
CALL insert_concert_seat_data();

ANALYZE TABLE concert_seat;



DELIMITER $$

TRUNCATE reservation;
DROP PROCEDURE IF EXISTS insert_reservation_data;
CREATE PROCEDURE insert_reservation_data()
BEGIN
  DECLARE concert_seat_id INT DEFAULT 1;
  DECLARE v_status VARCHAR(20);
  DECLARE extra_probability DOUBLE DEFAULT 0.7;  -- 추가 예약 삽입 확률 (예: 70%)

  START TRANSACTION;

  -- 약 80만 건
  WHILE concert_seat_id <= 1000000 DO
      -- 80% 확률로 'CONFIRMED', 20% 확률로 'EXPIRED'
      SET v_status = IF(RAND() < 0.8, 'CONFIRMED', 'EXPIRED');

      INSERT INTO reservation (concert_seat_id, user_id, amount, status, created_at)
      VALUES (
               concert_seat_id,
               FLOOR(RAND() * 6000) + 1,
               1000,
               v_status,
               NOW()
             );

      -- 만약 status가 'EXPIRED'이면, extra_probability 확률로 'CONFIRMED' 상태의 추가 예약을 삽입
      IF v_status = 'EXPIRED' AND RAND() < extra_probability THEN
        INSERT INTO reservation (concert_seat_id, user_id, amount, status, created_at)
        VALUES (
                 concert_seat_id,
                 FLOOR(RAND() * 6000) + 1,
                 1000,
                 'CONFIRMED',
                 NOW()
               );
      END IF;

      SET concert_seat_id = concert_seat_id + 1;

      -- 1,000건마다 커밋
      IF concert_seat_id % 1000 = 0 THEN
        COMMIT;
        START TRANSACTION;
      END IF;
    END WHILE;

  COMMIT;
END$$

DELIMITER ;

-- 프로시저 실행
CALL insert_reservation_data();

ANALYZE TABLE reservation;

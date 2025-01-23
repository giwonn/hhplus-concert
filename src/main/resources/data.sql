
INSERT INTO user (point) VALUES (0);
INSERT INTO user (point) VALUES (0);
INSERT INTO user (point) VALUES (0);
INSERT INTO user (point) VALUES (0);
INSERT INTO user (point) VALUES (0);
INSERT INTO user (point) VALUES (0);
INSERT INTO user (point) VALUES (0);
INSERT INTO user (point) VALUES (0);
INSERT INTO user (point) VALUES (0);
INSERT INTO user (point) VALUES (0);

INSERT INTO concert (id) VALUES (null);

INSERT INTO concert_schedule (concert_id, concert_date) VALUES (1, DATE"2025-01-01");

INSERT INTO concert_seat (concert_schedule_id, seat_num, amount, is_reserved)
VALUES (1, 1, 1000, false),
       (1, 2, 1000, false),
       (1, 3, 1000, false),
       (1, 4, 1000, false),
       (1, 5, 1000, false),
       (1, 6, 1000, false),
       (1, 7, 1000, false),
       (1, 8, 1000, false),
       (1, 9, 1000, false),
       (1, 10, 1000, false);

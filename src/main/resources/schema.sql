CREATE TABLE IF NOT EXISTS user (
  id bigint PRIMARY KEY,
  point bigint
);

CREATE TABLE IF NOT EXISTS user_point_history (
  id bigint PRIMARY KEY,
  user_id bigint,
  action varchar(30),
  amount bigint,
  transaction_at timestamp
);

CREATE TABLE IF NOT EXISTS token (
  id bigint PRIMARY KEY,
  concert_id bigint,
  user_id bigint,
  is_activated boolean,
  expired_at timestamp
);

CREATE TABLE IF NOT EXISTS concert (
  id bigint PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS concert_schedule (
  id bigint PRIMARY KEY,
  concert_id bigint,
  concert_date date,
  is_sold_out boolean,
  INDEX idx_concert_id_date (concert_id, concert_date) -- 복합 인덱스
);

CREATE TABLE IF NOT EXISTS concert_seat (
  id bigint PRIMARY KEY,
  concert_schedule_id bigint,
  amount bigint,
  is_reserved boolean
);

CREATE TABLE IF NOT EXISTS reservation (
  id bigint PRIMARY KEY,
  concert_seat_id bigint,
  user_id bigint,
  amount bigint,
  status varchar(30),
  expired_at timestamp,
  paid_at timestamp
);

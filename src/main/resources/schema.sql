CREATE TABLE IF NOT EXISTS user (
  id bigint PRIMARY KEY AUTO_INCREMENT,
  point bigint not null,
  version bigint null
);

CREATE TABLE IF NOT EXISTS user_point_history (
  id bigint PRIMARY KEY AUTO_INCREMENT,
  user_id bigint not null,
  action varchar(30) not null,
  amount bigint not null,
  transaction_at timestamp,
  CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS token (
  id bigint PRIMARY KEY AUTO_INCREMENT,
  user_id bigint not null,
  is_queue_passed boolean not null default false,
  expired_at timestamp
);

CREATE TABLE IF NOT EXISTS concert (
  id bigint PRIMARY KEY AUTO_INCREMENT
);

CREATE TABLE IF NOT EXISTS concert_schedule (
  id bigint PRIMARY KEY AUTO_INCREMENT,
  concert_id bigint not null,
  concert_date date not null,
  INDEX idx_concert_id (concert_id)
);

CREATE TABLE IF NOT EXISTS concert_seat (
  id bigint PRIMARY KEY AUTO_INCREMENT,
  concert_schedule_id bigint not null,
  seat_num int not null,
  amount bigint not null,
  is_reserved boolean default false,
  version bigint null
);

CREATE TABLE IF NOT EXISTS reservation (
  id bigint PRIMARY KEY AUTO_INCREMENT,
  concert_seat_id bigint not null,
  user_id bigint not null,
  amount bigint not null,
  status varchar(30) not null,
  created_at timestamp not null,
  paid_at timestamp,
  version bigint null
);

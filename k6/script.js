
import { signToken } from './api/signToken.js';
import { seatReservation } from './api/seatReservation.js';
import { Counter } from 'k6/metrics';
import { sleep } from 'k6';
import { findAvailableDates } from './api/findAvailableDates.js';
import { findAvailableSeats } from './api/findAvailableSeats.js';
import { seatPayment } from './api/seatPayment.js';

export const options = {
  scenarios: {
    burst_traffic: {
      executor: 'shared-iterations',
      vus: 200, // 동시에 실행할 사용자 수
      iterations: 200, // 총 200개의 요청을 한 번에 실행
      maxDuration: '10s', // 최대 10초 동안 실행
    },
  },
};

const failFindScheduleCount = new Counter('Z_fail_find_schedule');
const failFindSeatCount = new Counter('Z_fail_find_seat');
const failReservationCount = new Counter('Z_fail_reservation');
const failPaymentCount = new Counter('Z_fail_payment');
const successCount = new Counter('Z_success');

const getRandomItem = (arr) => arr[Math.floor(Math.random() * arr.length)];

export default async () => {
  const userId = Math.floor(Math.random() * 50) + 1; // 최대 50의 랜덤 숫자
  const concertId = Math.floor(Math.random() * 300) + 1; // 최대 300의 랜덤 숫자

  // 1. 대기열 토큰 발급
  const queueToken = signToken(userId);
  sleep(1);

  // 2. 콘서트 날짜 조회
  const schedules = await findAvailableDates(concertId, queueToken);
  if (!schedules) {
    failFindScheduleCount.add(1);
    return;
  }
  if (schedules.reason) {
    console.log(schedules.reason)
    failFindScheduleCount.add(1);
    return;
  }
  if (schedules.length === 0) {
    failFindScheduleCount.add(1);
    return;
  }
  const schedule = getRandomItem(schedules);
  sleep(1);

  // 3. 콘서트 좌석 조회
  const seats = await findAvailableSeats(schedule.concertId, schedule.concertScheduleId, queueToken);
  if (!seats) {
    failFindSeatCount.add(1);
    return;
  }
  if (seats.length === 0) {
    console.error(seats.reason)
    failFindSeatCount.add(1);
    return;
  }
  const seat = getRandomItem(seats);
  sleep(1);

  // 4. 콘서트 좌석 선점
  const reservation = await seatReservation(userId, seat.id, seat.amount, queueToken);
  if (reservation.code) {
    console.log(reservation.reason)
    failReservationCount.add(1);
    return;
  }
  sleep(1);

  // 5. 좌석 결제
  const paymentResp = await seatPayment(userId, reservation.reservationId, queueToken);
  if (paymentResp.code) {
    console.log(paymentResp.reason)
    failPaymentCount.add(1);
    return;
  }
  successCount.add(1);

}


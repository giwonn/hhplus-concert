
import { signToken } from './api/signToken.js';
import { seatReservation } from './api/seatReservation.js';
import { Counter } from 'k6/metrics';

export const options = {
  scenarios: {
    generate_new_vus: {
      executor: 'constant-arrival-rate',  // VU가 한 번 실행 후 종료
      rate: 50, // 초당 50개의 VU 생성
      timeUnit: '1s',
      duration: '10s',
      preAllocatedVUs: 50,
    },
  },
};

const successCount = new Counter('Z_success_seat_reservation');
const failureCount = new Counter('Z_fail_seat_reservation');

export default async () => {
  const userId = __VU;  // VU 번호를 userId로 사용
  const queueToken = signToken(userId);

  const resp = await seatReservation(userId, queueToken);
  if (resp.status === 200) {
    successCount.add(1);
  } else {
    failureCount.add(1);
  }
}

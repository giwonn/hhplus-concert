import http from "k6/http";
import { check } from "k6";

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

export default () => {
  const userId = __VU;  // VU 번호를 userId로 사용
  signToken(userId);
  concertSeat(userId);
}

const signToken = (userId) => {
  const url = "http://host.docker.internal:8080/queue/tokens";
  const payload = JSON.stringify({ userId });
  const params = {
    headers: {
      "Content-Type": "application/json",
    }
  };
  const response = http.post(url, payload, params);
  check(response, { "status is 200": (r) => r.status === 200 });
}

const concertSeat = (userId) => {
  const url = "http://host.docker.internal:8080/reservation/concerts";
  const payload = JSON.stringify({
    concertId: 1,
    seatId: Math.floor(Math.random() * 20) + 1,
    userId,
    amount: 1000,
    date: "2025-01-01"
  });
  const params = {
    headers: {
      "Content-Type": "application/json",
      "X-Waiting-Token": JSON.stringify({ userId })
    }
  };
  const response = http.post(url, payload, params);
  check(response, { "status is 200 or 400": (r) => r.status === 200 || r.status === 400 });
}

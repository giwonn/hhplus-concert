import http from "k6/http";
import { check } from "k6";
import { Counter } from "k6/metrics";

const vus = 400;

export const options = {
  scenarios: {
    scenarios_example: {
      executor: "per-vu-iterations", // 시나리오 실행 방식 (per-vu-iterations, constant-vus, ramping-vus)
      vus: vus, // 가상 사용자 수
      iterations: 1, // 사용자당 몇번 요청을 보낼지 (총 요청횟수 = vus * iterations)
      maxDuration: "30s", // 타임아웃 (30초 초과하면 나머지 작업은 drop
    },
  },
};

const successCount = new Counter("success_count");
const failCount = new Counter("fail_count");

export default () => {
  const url = "http://host.docker.internal:8080/reservation/concerts";
  const payload = JSON.stringify({
    concertId: 1,
    seatId: 2,
    userId: 1,
    amount: 1000,
    date: "2025-01-01"
  });
  const params = {
    headers: {
      "Content-Type": "application/json",
    }
  };
  const response = http.post(url, payload, params);
  check(response, { "status is 200 or 400": (r) => r.status === 200 || r.status === 400 });

  if (response.status === 200) {
    successCount.add(1);
  } else if (response.status === 400) {
    failCount.add(1);
  }
}


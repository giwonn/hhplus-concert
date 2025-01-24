import http from "k6/http";
import { check } from "k6";
import { Counter } from "k6/metrics";

export const options = {
  stages: [
    { duration: "3s", target: 100 },
    { duration: "3s", target: 50 },
    { duration: "3s", target: 10 },
  ],
};

const successCount = new Counter("success_count");
const failCount = new Counter("fail_count");

export default () => {
  const url = "http://host.docker.internal:8080/reservation/concerts";
  const payload = JSON.stringify({
    concertId: 1,
    seatId: Math.floor(Math.random() * 20) + 1,
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


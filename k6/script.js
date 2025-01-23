import http from "k6/http";
import { check } from "k6";
import { Counter } from "k6/metrics";

const vus = 300;

export const options = {
  stages: [
    { duration: "1s", target: 1000 },
    { duration: "1s", target: 900 },
    { duration: "1s", target: 800 },
    { duration: "1s", target: 700 },
    { duration: "1s", target: 500 },
    { duration: "1s", target: 400 },
  ],
};

const successCount = new Counter("success_count");
const failCount = new Counter("fail_count");

export default () => {
  const url = "http://host.docker.internal:8080/reservation/concerts";
  const payload = JSON.stringify({
    concertId: 1,
    seatId: Math.floor(Math.random() * 10) + 1,
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


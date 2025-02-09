import http from "k6/http";
import { check } from "k6";
import { Counter } from "k6/metrics";

export const options = {
  stages: [
    { duration: "5s", target: 100 },
    { duration: "5s", target: 50 },
    { duration: "5s", target: 10 },
    { duration: "5s", target: 5 },
  ],
};

export default () => {
  const url = "http://host.docker.internal:8080/queue/tokens";
  const payload = JSON.stringify({ userId: 1 });
  const params = {
    headers: {
      "Content-Type": "application/json",
    }
  };
  const response = http.post(url, payload, params);
  check(response, { "status is 200": (r) => r.status === 200 });

  if (response.status === 200) {
    successCount.add(1);
  } else {
    failCount.add(1);
  }
}


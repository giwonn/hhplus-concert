import http from "k6/http";

export const signToken = (userId) => {
  const url = "http://host.docker.internal:8080/queue/tokens";
  const payload = JSON.stringify({ userId });
  const params = {
    headers: {
      "Content-Type": "application/json",
    },
    tags: { name: `POST /queue/tokens`}
  };
  const response = http.post(url, payload, params);
  return JSON.parse(response.body);
}

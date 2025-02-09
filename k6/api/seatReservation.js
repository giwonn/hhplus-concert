import { callApi } from './callApi.js';

export const seatReservation = async (userId, token) => {
  const headers = {
    "X-Waiting-Token": JSON.stringify(token)
  };
  const path = "/reservation/concerts";
  const body = {
    seatId: Math.floor(Math.random() * 20) + 1,
    userId,
    date: "2025-01-01"
  };

  return await callApi({method: "POST", headers, path, body })
}

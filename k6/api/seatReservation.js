import { callApi } from './callApi.js';

export const seatReservation = async (userId, seatId, amount, token) => {
  const headers = {
    "X-Waiting-Token": JSON.stringify(token)
  };
  const path = "/reservation/concerts";
  const body = {
    userId,
    seatId,
    amount,
  };

  return await callApi({method: "POST", headers, path, body, tag: '좌석 선점' })
}

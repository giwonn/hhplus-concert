import { callApi } from './callApi.js';

export const seatPayment = async (userId, reservationId, token) => {
  const headers = {
    "X-Waiting-Token": JSON.stringify(token)
  };
  const path = "/reservation/payments";
  const body = {
    userId,
    reservationId,
  };

  return await callApi({method: "POST", headers, path, body, tag: '좌석 결제' });
}

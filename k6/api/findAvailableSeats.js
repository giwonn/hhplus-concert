import { callApi } from './callApi.js';

export const findAvailableSeats = async (concertId, concertScheduleId, token) => {
  const headers = {
    "X-Waiting-Token": JSON.stringify(token)
  };
  const path = `/concerts/${concertId}/schedules/${concertScheduleId}/available-seats`;

  return await callApi({method: "GET", headers, path, tag: '콘서트 좌석 조회' }).then(resp => resp.seats)
}

import { callApi } from './callApi.js';

export const findAvailableDates = async (concertId, token) => {
  const headers = {
    "X-Waiting-Token": JSON.stringify(token)
  };
  const path = `/concerts/${concertId}/available-dates`;

  return await callApi({method: "GET", headers, path, tag: '콘서트 날짜 조회' }).then(resp => resp.schedules || resp);
}

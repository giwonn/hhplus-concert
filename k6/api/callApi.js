import http from "k6/http";

/**
 * @ts-check
 * @param { 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE' } method
 * @param {Object} headers
 * @param {string} path - 요청 path
 * @param {Object=} body
 * @param {string=} tag
 */
export const callApi = async ({method, headers, path, body, tag }) => {
  while (true) {
    if (!['GET', 'POST', 'PUT', 'PATCH', 'DELETE'].includes(method)) {
      throw new Error('HTTP 메서드 잘못됨');
    }

    const requestBody = method !== 'GET' ? JSON.stringify(body) : undefined
    const resp = await http.asyncRequest(method, `http://host.docker.internal:8080${path}`, requestBody, {
      headers: { 'Content-Type': 'application/json', ...headers },
      tags: { name: tag ?? `${method} ${path}`}
    });

    if (resp.status === 202) {
      await new Promise((resolve) => setTimeout(resolve, 3000));
      continue
    }

    return JSON.parse(resp.body);
  }
}

/**
 * K6 lookup test for GET /keys/{key}
 * Tests both cache-warm (same key) and cache-cold (unique key per VU) scenarios.
 *
 * Run: k6 run lookup_key.js
 * Run cold only: k6 run -e SCENARIO=cold lookup_key.js
 */
import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const SCENARIO = __ENV.SCENARIO || 'warm';

export const options = {
  stages: [
    { duration: '10s', target: 20 },
    { duration: '30s', target: 20 },
    { duration: '10s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(99)<200'],
    errors:            ['rate<0.05'],
  },
};

// Register a single CPF key used for cache-warm lookups.
export function setup() {
  const body = JSON.stringify({
    key:           '55544433321',
    keyType:       'CPF',
    ownerId:       'k6-owner',
    accountHolder: 'K6 User',
    accountBranch: '0001',
    accountNumber: '99999-9',
  });
  http.post(`${BASE_URL}/keys`, body, { headers: { 'Content-Type': 'application/json' } });
  return { warmKey: '55544433321' };
}

export default function (data) {
  if (SCENARIO === 'cold') {
    group('cache cold — unique key miss', () => {
      const key = `nonexistent-${__VU}-${__ITER}`;
      const res = http.get(`${BASE_URL}/keys/${key}`);
      const ok = check(res, { 'status 404': (r) => r.status === 404 });
      errorRate.add(!ok);
    });
  } else {
    group('cache warm — registered key hit', () => {
      const res = http.get(`${BASE_URL}/keys/${data.warmKey}`);
      const ok = check(res, {
        'status 200': (r) => r.status === 200,
        'key match':  (r) => JSON.parse(r.body).key === data.warmKey,
      });
      errorRate.add(!ok);
    });
  }

  sleep(0.05);
}

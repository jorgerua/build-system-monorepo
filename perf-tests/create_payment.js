/**
 * K6 throughput test for POST /payments
 * Target: baseline TPS at p99 < 200ms
 *
 * Run: k6 run create_payment.js
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  stages: [
    { duration: '10s', target: 10 },   // ramp up to 10 VUs
    { duration: '30s', target: 10 },   // hold for 30s
    { duration: '10s', target: 0 },    // ramp down
  ],
  thresholds: {
    http_req_duration: ['p(99)<200'],   // p99 must be < 200ms
    errors:            ['rate<0.01'],   // error rate < 1%
  },
};

let counter = 0;

export default function () {
  const idempotencyKey = `k6-create-${__VU}-${__ITER}-${counter++}`;

  const payload = JSON.stringify({
    payerKey:       '12345678901',
    payeeKey:       'alice@example.com',
    amountCentavos: 100,
    idempotencyKey,
  });

  const params = { headers: { 'Content-Type': 'application/json' } };
  const res = http.post(`${BASE_URL}/payments`, payload, params);

  const ok = check(res, {
    'status 200': (r) => r.status === 200,
    'has id':     (r) => JSON.parse(r.body).id !== undefined,
  });

  errorRate.add(!ok);
  sleep(0.1);
}

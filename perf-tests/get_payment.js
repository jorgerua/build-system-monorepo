/**
 * K6 latency test for GET /payments/{id}
 * Measures read latency on an already-created payment.
 *
 * Run: k6 run get_payment.js
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  stages: [
    { duration: '10s', target: 20 },
    { duration: '30s', target: 20 },
    { duration: '10s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(99)<200', 'p(50)<50'],
    errors:            ['rate<0.01'],
  },
};

// Seed: create one payment and capture its ID for all read VUs.
let seedPaymentId;

export function setup() {
  const payload = JSON.stringify({
    payerKey:       '12345678901',
    payeeKey:       'alice@example.com',
    amountCentavos: 200,
    idempotencyKey: 'k6-get-seed-001',
  });
  const res = http.post(`${BASE_URL}/payments`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });
  return { paymentId: JSON.parse(res.body).id };
}

export default function (data) {
  const res = http.get(`${BASE_URL}/payments/${data.paymentId}`);

  const ok = check(res, {
    'status 200':  (r) => r.status === 200,
    'correct id':  (r) => JSON.parse(r.body).id === data.paymentId,
  });

  errorRate.add(!ok);
  sleep(0.05);
}

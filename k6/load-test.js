import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const failRate = new Rate('failed_requests');
const lockFailRate = new Rate('lock_failed_requests');
const issuanceDuration = new Trend('issuance_duration');

// 테스트 설정: 점점 VU 늘려서 락 경합 증가 확인
export const options = {
  stages: [
    { duration: '10s', target: 10 },   // 워밍업
    { duration: '20s', target: 50 },   // 경합 시작
    { duration: '10s', target: 30 },  // 핫키 구간
    { duration: '5s', target: 40 },  // 폭발 구간
    { duration: '10s', target: 0 },    // 마무리
  ],
  thresholds: {
    http_req_duration: ['p(99)<3000'],  // p99 3초 이내
    failed_requests: ['rate<0.1'],      // 실패율 10% 이내
  },
};

const BASE_URL = 'http://192.168.192.1:8080';
const COUPON_ID = 1; // setup에서 생성한 쿠폰 ID

export default function (data) {
  const couponId = 3

  // 모든 VU가 같은 couponId로 요청 → 핫키 + 락 경합 발생
  const payload = JSON.stringify({
    couponId: couponId,
    userName: `user_${__VU}_${__ITER}`,
  });

  const start = Date.now();
  const res = http.post(`${BASE_URL}/coupon/inssuance/redis`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });
  issuanceDuration.add(Date.now() - start);

  const success = check(res, {
    'status 200': (r) => r.status === 200,
    'isSuccess true': (r) => {
      try {
        return JSON.parse(r.body).isSuccess === true;
      } catch {
        return false;
      }
    },
  });

  failRate.add(!success);

  // 락 획득 실패 체크
  if (res.status !== 200) {
    lockFailRate.add(1);
    console.log(`락 실패 - VU: ${__VU}, 응답: ${res.body}`);
  } else {
    lockFailRate.add(0);
  }
}

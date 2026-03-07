import http from 'k6/http';
import { check, sleep } from 'k6';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

// Конфігурація STRESS тесту
export const options = {
    // Сходинки навантаження
    stages: [
        { duration: '30s', target: 50 },
        { duration: '30s', target: 100 },
        { duration: '30s', target: 500 },
        { duration: '2m', target: 2000 },
        { duration: '2m', target: 10000 },
    ],
    // ТРИГЕР (Threshold): Автоматично зупинити тест, якщо сервер "впав"
    thresholds: {
        // Якщо кількість невдалих запитів перевищить 5% (0.05) - примусово зупинити тест!
        http_req_failed: [{ threshold: 'rate<0.05', abortOnFail: true, delayAbortEval: '10s' }],
    }
};

const BASE_URL = 'http://192.168.0.222:8080/api';

export default function () {
    // Авторизація
    const payload = JSON.stringify({ login: 'Fox', password: '09125689' });
    const params = { headers: { 'Content-Type': 'application/json' } };
    http.post(`${BASE_URL}/auth/login`, payload, params);

    // 1. Вибираємо ВИПАДКОВУ сторінку стрічки (від 0 до 50)
    const randomPage = Math.floor(Math.random() * 50);
    let res = http.get(`${BASE_URL}/blog/posts?page=${randomPage}&size=20`);
    check(res, { 'status is 200 (Feed)': (r) => r.status === 200 });

    // 2. Вибираємо ВИПАДКОВИЙ допис (від 1 до 10000) для перегляду коментарів
    const randomPostId = Math.floor(Math.random() * 10000) + 1;
    let postRes = http.get(`${BASE_URL}/blog/comments/${randomPostId}`);
    check(postRes, { 'status is 200 (Post)': (r) => r.status === 200 });

    sleep(1);
}

export function handleSummary(data) {
    return { "summary.html": htmlReport(data) };
}
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 5,
    duration: '30s',
};

const BASE_URL = 'http://localhost:8080/api';

function login() {
    const payload = JSON.stringify({ login: 'Fox', password: '09125689' });
    const params = { headers: { 'Content-Type': 'application/json' } };
    const res = http.post(`${BASE_URL}/auth/login`, payload, params);
    check(res, { 'logged in successfully': (r) => r.status === 200 });
}

function logout() {
    const res = http.post(`${BASE_URL}/auth/logout`);
    check(res, { 'logged out successfully': (r) => r.status === 200 });
}

export default function () {
    login();
    sleep(1);

    const scenario = Math.floor(Math.random() * 4) + 1;

    if (scenario === 1) {
        // Сценарій 1: Читання стрічки дописів
        let res = http.get(`${BASE_URL}/blog/posts`);
        check(res, { 'fetched posts': (r) => r.status === 200 });
        sleep(2);
    }
    else if (scenario === 2) {
        // Сценарій 2: Залишення коментаря
        http.get(`${BASE_URL}/blog/posts`);
        sleep(2);

        http.get(`${BASE_URL}/blog/comments/1`);
        sleep(2);

        const commentPayload = JSON.stringify({
            postId: 1,
            content: "Навантажувальний коментар від k6",
            authorUsername: "Fox"
        });
        const params = { headers: { 'Content-Type': 'application/json' } };
        let res = http.post(`${BASE_URL}/blog/comments`, commentPayload, params);

        check(res, { 'comment added': (r) => r.status === 200 || r.status === 201 });
        sleep(1);
    }
    else if (scenario === 3) {
        // Сценарій 3: Створення нового допису
        const postPayload = JSON.stringify({
            content: "Новий пост від k6",
            authorUsername: "Fox"
        });
        const params = { headers: { 'Content-Type': 'application/json' } };
        let res = http.post(`${BASE_URL}/blog/posts`, postPayload, params);

        check(res, { 'post created': (r) => r.status === 200 || r.status === 201 });
        sleep(2);
    }
    else if (scenario === 4) {
        // Сценарій 4: Перегляд та оновлення профілю
        http.get(`${BASE_URL}/profile/Fox`);
        sleep(2);

        const profilePayload = JSON.stringify({
            currentUsername: "Fox",
            bio: "Оновлено автоматично під час навантажувального тесту"
        });
        const params = { headers: { 'Content-Type': 'application/json' } };
        let res = http.put(`${BASE_URL}/profile/update`, profilePayload, params);

        check(res, { 'profile updated': (r) => r.status === 200 });
        sleep(1);
    }

    logout();
    sleep(1);
}
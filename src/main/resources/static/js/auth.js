const API_BASE = '/api/auth';

function getCsrfToken() {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; XSRF-TOKEN=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return '';
}

function showSection(sectionId) {
    document.querySelectorAll('.form-section').forEach(el => el.classList.remove('active'));
    document.getElementById(sectionId).classList.add('active');
}

async function login() {
    const user = document.getElementById('loginUsername').value;
    const pass = document.getElementById('loginPassword').value;

    try {
        const response = await fetch(`${API_BASE}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'X-XSRF-TOKEN': getCsrfToken() },
            credentials: 'include',
            body: JSON.stringify({login: user, password: pass})
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('isAuthenticated', 'true');
            localStorage.setItem('currentUser', data.username);
            window.location.href = '/index.html';
        } else {
            document.getElementById('loginError').style.display = 'block';
        }
    } catch (error) { alert('Server connection error'); }
}

async function sendVerificationCode() {
    const email = document.getElementById('regEmail').value;
    const pass = document.getElementById('regPassword').value;
    const confirm = document.getElementById('regPasswordConfirm').value;

    if (pass !== confirm) return alert('Passwords do not match!');
    if (!email) return alert('Please enter your email!');

    try {
        const response = await fetch(`${API_BASE}/send-code`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'X-XSRF-TOKEN': getCsrfToken() },
            credentials: 'include',
            body: JSON.stringify({ email: email })
        });
        if (response.ok) {
            alert('Verification code sent! Please check your email.');
            showSection('verifyForm');
        } else {
            alert('Error: ' + await response.text());
        }
    } catch (error) { alert('Connection error'); }
}

async function completeRegistration() {
    const payload = {
        username: document.getElementById('regNickname').value,
        email: document.getElementById('regEmail').value,
        password: document.getElementById('regPassword').value,
        code: document.getElementById('regCode').value
    };

    try {
        const response = await fetch(`${API_BASE}/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'X-XSRF-TOKEN': getCsrfToken() },
            credentials: 'include',
            body: JSON.stringify(payload)
        });
        if (response.ok) {
            alert('Registration successful! You can now log in.');
            showSection('loginForm');
        } else { alert('Error: ' + await response.text()); }
    } catch (error) { alert('Connection error'); }
}

async function requestPasswordReset() {
    const email = document.getElementById('forgotEmail').value;
    if (!email) return alert('Please enter your email!');

    try {
        const response = await fetch(`${API_BASE}/forgot-password`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'X-XSRF-TOKEN': getCsrfToken() },
            credentials: 'include',
            body: JSON.stringify({email: email})
        });
        alert(await response.text());
    } catch (error) { alert('Connection error'); }
}

// Прив'язка подій після завантаження DOM
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('loginBtn').addEventListener('click', login);
    document.getElementById('getRegCodeBtn').addEventListener('click', sendVerificationCode);
    document.getElementById('completeRegBtn').addEventListener('click', completeRegistration);
    document.getElementById('sendResetLinkBtn').addEventListener('click', requestPasswordReset);

    document.getElementById('linkToReg').addEventListener('click', () => showSection('registerForm'));
    document.getElementById('linkToForgot').addEventListener('click', () => showSection('forgotForm'));
    document.getElementById('linkToLoginFromReg').addEventListener('click', () => showSection('loginForm'));
    document.getElementById('linkToRegFromVerify').addEventListener('click', () => showSection('registerForm'));
    document.getElementById('linkToLoginFromForgot').addEventListener('click', () => showSection('loginForm'));
});
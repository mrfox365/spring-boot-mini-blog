function getCsrfToken() {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; XSRF-TOKEN=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return '';
}

async function resetPassword() {
    const pass = document.getElementById('newPassword').value;
    const confirm = document.getElementById('confirmPassword').value;

    if (!pass.trim()) { alert('Password cannot be empty!'); return; }
    if (pass !== confirm) { alert('Passwords do not match!'); return; }

    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');

    if (!token) { alert('Error: Security token is missing from the link.'); return; }

    try {
        const response = await fetch('/api/auth/reset-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'X-XSRF-TOKEN': getCsrfToken() },
            credentials: 'include',
            body: JSON.stringify({token: token, newPassword: pass})
        });

        if (response.ok) {
            alert('Password has been changed successfully!');
            window.location.href = '/auth.html';
        } else {
            alert('Error: ' + await response.text());
        }
    } catch (error) { alert('Connection error'); }
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('savePasswordBtn').addEventListener('click', resetPassword);
});
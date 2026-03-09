const API_BASE = '/api';
let currentPage = 0;

if (!localStorage.getItem('isAuthenticated')) { window.location.replace('/auth.html'); }
const currentUser = localStorage.getItem('currentUser');

function getCsrfToken() {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; XSRF-TOKEN=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return '';
}

function getAuthHeaders() {
    return { 'Content-Type': 'application/json', 'X-XSRF-TOKEN': getCsrfToken() };
}

function formatDate(dateString) {
    const options = {year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'};
    return new Date(dateString).toLocaleDateString('en-US', options);
}

// Завантаження постів
async function loadPosts(page = 0) {
    currentPage = page;
    try {
        const response = await fetch(`${API_BASE}/blog/posts?page=${page}&size=20`, { credentials: 'include' });
        const posts = await response.json();
        const container = document.getElementById('feedContainer');
        container.innerHTML = '';

        if (posts.length === 0 && page === 0) {
            container.innerHTML = '<p class="empty-feed">No posts yet. Be the first!</p>';
            return;
        }

        for (const post of posts) {
            const postEl = document.createElement('div');
            postEl.className = 'post-card';

            const deletePostBtn = (post.authorUsername === currentUser)
                ? `<button title="Delete post" class="delete-icon-btn large delete-post-action" data-post-id="${post.id}">✖</button>`
                : '';

            postEl.innerHTML = `
                <div class="post-header">
                    <div class="avatar avatar-small view-profile-action" data-username="${post.authorUsername}">${post.authorUsername.charAt(0).toUpperCase()}</div>
                    <span class="post-author view-profile-action" data-username="${post.authorUsername}">${post.authorUsername}</span>
                    <span class="post-date">${formatDate(post.createdAt)}</span>
                    ${deletePostBtn}
                </div>
                <div class="post-content">${post.content}</div>
                <div class="comments-section" id="comments-${post.id}">
                    <div id="comment-list-${post.id}"></div>
                    <div class="comment-input-box">
                        <input type="text" id="comment-input-${post.id}" placeholder="Write a comment...">
                        <button class="btn submit-comment-action" data-post-id="${post.id}">Send</button>
                    </div>
                </div>
            `;
            container.appendChild(postEl);
            loadComments(post.id);
        }
        renderPaginationControls(posts.length);
    } catch (error) { console.error('Error loading posts:', error); }
}

function renderPaginationControls(loadedPostsCount) {
    const container = document.getElementById('feedContainer');
    const paginationDiv = document.createElement('div');
    paginationDiv.className = 'pagination-container';

    const prevBtn = document.createElement('button');
    prevBtn.className = 'btn btn-secondary';
    prevBtn.innerText = '← Previous Page';
    prevBtn.disabled = currentPage === 0;
    prevBtn.addEventListener('click', () => loadPosts(currentPage - 1));

    const nextBtn = document.createElement('button');
    nextBtn.className = 'btn btn-secondary';
    nextBtn.innerText = 'Next Page →';
    nextBtn.disabled = loadedPostsCount < 20;
    nextBtn.addEventListener('click', () => loadPosts(currentPage + 1));

    paginationDiv.appendChild(prevBtn);
    paginationDiv.appendChild(nextBtn);
    container.appendChild(paginationDiv);
}

// Завантаження коментарів
async function loadComments(postId) {
    const response = await fetch(`${API_BASE}/blog/comments/${postId}`, { credentials: 'include' });
    const comments = await response.json();
    const listElement = document.getElementById(`comment-list-${postId}`);
    listElement.innerHTML = '';

    comments.forEach(comment => {
        const cEl = document.createElement('div');
        cEl.className = 'comment';
        const deleteCommentBtn = (comment.authorUsername === currentUser)
            ? `<button title="Delete comment" class="delete-icon-btn small delete-comment-action" data-comment-id="${comment.id}" data-post-id="${postId}">✖</button>`
            : '';

        cEl.innerHTML = `
            <div class="avatar avatar-tiny view-profile-action" data-username="${comment.authorUsername}">${comment.authorUsername.charAt(0).toUpperCase()}</div>
            <div class="comment-body">
                <div class="comment-header">
                    <strong class="view-profile-action" data-username="${comment.authorUsername}">${comment.authorUsername}</strong>
                    <div class="comment-time-container">
                        <span>${formatDate(comment.createdAt)}</span>
                        ${deleteCommentBtn}
                    </div>
                </div>
                <div>${comment.content}</div>
            </div>
        `;
        listElement.appendChild(cEl);
    });
}

// API Дії
async function submitPost() {
    const content = document.getElementById('newPostContent').value;
    if (!content.trim()) return alert('Post cannot be empty!');
    const response = await fetch(`${API_BASE}/blog/posts`, {
        method: 'POST', headers: getAuthHeaders(), credentials: 'include', body: JSON.stringify({content: content, authorUsername: currentUser})
    });
    if (response.ok) { document.getElementById('newPostContent').value = ''; loadPosts(0); }
}

async function deletePost(postId) {
    if (!confirm('Are you sure you want to delete this post?')) return;
    const response = await fetch(`${API_BASE}/blog/posts/${postId}`, { method: 'DELETE', headers: getAuthHeaders(), credentials: 'include' });
    if (response.ok) loadPosts(0);
}

async function submitComment(postId) {
    const input = document.getElementById(`comment-input-${postId}`);
    const content = input.value;
    if (!content.trim()) return;
    const response = await fetch(`${API_BASE}/blog/comments`, {
        method: 'POST', headers: getAuthHeaders(), credentials: 'include', body: JSON.stringify({postId: postId, content: content, authorUsername: currentUser})
    });
    if (response.ok) { input.value = ''; loadComments(postId); }
}

async function deleteComment(commentId, postId) {
    if (!confirm('Delete this comment?')) return;
    const response = await fetch(`${API_BASE}/blog/comments/${commentId}`, { method: 'DELETE', headers: getAuthHeaders(), credentials: 'include' });
    if (response.ok) loadComments(postId);
}

// Профіль та UI
function toggleDropdown() { document.getElementById('profileMenu').classList.toggle('show'); }
function closeModal(modalId) { document.getElementById(modalId).classList.remove('show'); }

async function viewProfile(username) {
    const response = await fetch(`${API_BASE}/profile/${username}`, { credentials: 'include' });
    if (response.ok) {
        const profile = await response.json();
        document.getElementById('viewUsername').innerText = profile.username;
        document.getElementById('viewEmail').innerText = profile.email;
        document.getElementById('viewBio').innerText = profile.bio || 'No information provided';
        document.getElementById('viewBirthDate').innerText = profile.birthDate || 'Not specified';
        document.getElementById('viewProfileModal').classList.add('show');
    }
}

async function openSettingsModal() {
    const response = await fetch(`${API_BASE}/profile/${currentUser}`, { credentials: 'include' });
    if (response.ok) {
        const profile = await response.json();
        document.getElementById('editUsername').value = profile.username;
        document.getElementById('editBio').value = profile.bio || '';
        document.getElementById('editBirthDate').value = profile.birthDate || '';
        document.getElementById('editPassword').value = '';
        document.getElementById('settingsModal').classList.add('show');
    }
}

async function saveProfileSettings() {
    const newUsername = document.getElementById('editUsername').value;
    const payload = {
        newUsername: newUsername, newPassword: document.getElementById('editPassword').value,
        bio: document.getElementById('editBio').value, birthDate: document.getElementById('editBirthDate').value || null
    };
    const response = await fetch(`${API_BASE}/profile/update`, {
        method: 'PUT', headers: getAuthHeaders(), credentials: 'include', body: JSON.stringify(payload)
    });
    if (response.ok) {
        alert('Profile updated successfully!');
        if (newUsername !== currentUser) { localStorage.setItem('currentUser', newUsername); window.location.reload(); }
        else { closeModal('settingsModal'); }
    } else { alert('Error: ' + await response.text()); }
}

async function logout() {
    try { await fetch('/api/auth/logout', { method: 'POST', credentials: 'include', headers: getAuthHeaders() }); } catch (e) {}
    localStorage.clear(); window.location.href = '/auth.html';
}

// Прив'язка подій після завантаження
document.addEventListener('DOMContentLoaded', () => {
    if (currentUser) { document.getElementById('headerAvatar').innerText = currentUser.charAt(0).toUpperCase(); loadPosts(0); }

    // Статичні кнопки
    document.getElementById('headerAvatar').addEventListener('click', toggleDropdown);
    document.getElementById('openSettingsBtn').addEventListener('click', openSettingsModal);
    document.getElementById('logoutBtn').addEventListener('click', logout);
    document.getElementById('publishBtn').addEventListener('click', submitPost);
    document.getElementById('saveProfileBtn').addEventListener('click', saveProfileSettings);

    // Закриття модальних вікон
    document.getElementById('closeViewProfileBtn').addEventListener('click', () => closeModal('viewProfileModal'));
    document.getElementById('closeSettingsBtn').addEventListener('click', () => closeModal('settingsModal'));
    document.getElementById('cancelSettingsBtn').addEventListener('click', () => closeModal('settingsModal'));

    window.onclick = function (event) {
        if (!event.target.matches('.avatar')) {
            document.querySelectorAll(".dropdown-menu.show").forEach(menu => menu.classList.remove('show'));
        }
    }

    // ДЕЛЕГУВАННЯ ПОДІЙ ДЛЯ ДИНАМІЧНИХ ЕЛЕМЕНТІВ
    document.getElementById('feedContainer').addEventListener('click', (e) => {
        const target = e.target;
        if (target.classList.contains('delete-post-action')) {
            deletePost(target.getAttribute('data-post-id'));
        } else if (target.classList.contains('view-profile-action')) {
            viewProfile(target.getAttribute('data-username'));
        } else if (target.classList.contains('submit-comment-action')) {
            submitComment(target.getAttribute('data-post-id'));
        } else if (target.classList.contains('delete-comment-action')) {
            deleteComment(target.getAttribute('data-comment-id'), target.getAttribute('data-post-id'));
        }
    });
});
/**
 * STREAMFLIX API Client & UI utilities
 */
const StreamFlix = (() => {
    const TOKEN_KEY = 'token';
    const USER_KEY = 'user';

    function getToken() {
        return localStorage.getItem(TOKEN_KEY);
    }

    function setAuth(token, user) {
        localStorage.setItem(TOKEN_KEY, token);
        if (user) localStorage.setItem(USER_KEY, JSON.stringify(user));
    }

    function clearAuth() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
    }

    function getUser() {
        try { return JSON.parse(localStorage.getItem(USER_KEY)); }
        catch { return null; }
    }

    function requireAuth(redirect = '/login') {
        if (!getToken()) {
            window.location.href = redirect;
            return false;
        }
        return true;
    }

    function requireGuest(redirect = '/') {
        if (getToken()) {
            window.location.href = redirect;
            return false;
        }
        return true;
    }

    async function api(path, options = {}) {
        const headers = { 'Content-Type': 'application/json', ...options.headers };
        const token = getToken();
        if (token) headers['Authorization'] = 'Bearer ' + token;

        const res = await fetch(path, { ...options, headers });
        if (res.status === 401) {
            clearAuth();
            window.location.href = '/login';
            throw new Error('Unauthorized');
        }
        const text = await res.text();
        let data = null;
        if (text) {
            try { data = JSON.parse(text); }
            catch { data = text; }
        }
        if (!res.ok) {
            const msg = data?.error || data?.message || res.statusText;
            throw new Error(msg);
        }
        return data;
    }

    const auth = {
        login: (email, password) => api('/api/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }),
        register: (name, email, password) => api('/api/auth/register', { method: 'POST', body: JSON.stringify({ name, email, password }) }),
        forgotPassword: (email) => api('/api/auth/forgot-password', { method: 'POST', body: JSON.stringify({ email }) }),
        resetPassword: (token, newPassword) => api('/api/auth/reset-password', { method: 'POST', body: JSON.stringify({ token, newPassword }) }),
    };

    const videos = {
        list: () => api('/api/videos'),
        get: (id) => api('/api/videos/' + id),
        categories: () => api('/api/videos/categories'),
        stream: (id, quality = 'HD') => api('/api/videos/' + id + '/stream?quality=' + quality),
        play: () => api('/api/videos/player/play'),
        pause: () => api('/api/videos/player/pause'),
    };

    const watchlist = {
        list: () => api('/api/watchlist'),
        add: (videoId) => api('/api/watchlist/' + videoId, { method: 'POST' }),
        remove: (videoId) => api('/api/watchlist/' + videoId, { method: 'DELETE' }),
    };

    const subscriptions = {
        plans: () => api('/api/subscriptions/plans'),
        current: () => api('/api/subscriptions/me'),
        subscribe: (plan, paymentMethod) => api('/api/subscriptions', { method: 'POST', body: JSON.stringify({ plan, paymentMethod }) }),
        cancel: () => api('/api/subscriptions/me', { method: 'DELETE' }),
        payments: () => api('/api/subscriptions/payments'),
    };

    const users = {
        me: () => api('/api/users/me'),
        update: (data) => api('/api/users/me', { method: 'PUT', body: JSON.stringify(data) }),
        history: () => api('/api/users/me/history'),
        updateHistory: (videoId, data) => api('/api/users/me/history/' + videoId, { method: 'PUT', body: JSON.stringify(data) }),
    };

    const admin = {
        analytics: () => api('/api/admin/analytics'),
        users: () => api('/api/admin/users'),
        createVideo: (data) => api('/api/admin/videos', { method: 'POST', body: JSON.stringify(data) }),
        updateVideo: (id, data) => api('/api/admin/videos/' + id, { method: 'PUT', body: JSON.stringify(data) }),
        deleteVideo: (id) => api('/api/admin/videos/' + id, { method: 'DELETE' }),
    };

    function toast(message, duration = 3000) {
        const el = document.createElement('div');
        el.className = 'toast';
        el.textContent = message;
        document.body.appendChild(el);
        setTimeout(() => el.remove(), duration);
    }

    function formatDuration(seconds) {
        if (!seconds) return '—';
        const h = Math.floor(seconds / 3600);
        const m = Math.floor((seconds % 3600) / 60);
        return h > 0 ? `${h}h ${m}m` : `${m}m`;
    }

    function formatGenre(genre) {
        return genre ? genre.replace(/_/g, ' ') : '';
    }

    function renderVideoCard(video, onClick) {
        const card = document.createElement('article');
        card.className = 'video-card';
        card.innerHTML = `
            <div class="video-card-thumb">
                <img src="${video.thumbnailUrl}" alt="${video.title}" onerror="this.style.display='none'">
            </div>
            <div class="video-card-body">
                <h3 class="video-card-title">${video.title}</h3>
                <div class="video-card-meta">
                    <span>${formatGenre(video.genre)}</span>
                    <span>${video.releaseYear || ''}</span>
                    ${video.isPremium ? '<span class="badge badge-premium">Premium</span>' : ''}
                </div>
            </div>`;
        card.addEventListener('click', () => {
            if (onClick) onClick(video);
            else window.location.href = '/videos/' + video.videoId;
        });
        return card;
    }

    function initNav() {
        const path = window.location.pathname;
        document.querySelectorAll('.nav-links a').forEach(a => {
            const href = a.getAttribute('href');
            if (href === path || (href !== '/' && path.startsWith(href))) {
                a.classList.add('active');
            }
        });

        const user = getUser();
        const logoutBtn = document.getElementById('navLogout');
        const loginBtn = document.getElementById('navLogin');
        const adminLink = document.getElementById('navAdmin');

        if (user) {
            if (logoutBtn) logoutBtn.classList.remove('hidden');
            if (loginBtn) loginBtn.classList.add('hidden');
            if (adminLink && user.role === 'ADMIN') adminLink.classList.remove('hidden');
        } else {
            if (logoutBtn) logoutBtn.classList.add('hidden');
            if (loginBtn) loginBtn.classList.remove('hidden');
            if (adminLink) adminLink.classList.add('hidden');
        }

        if (logoutBtn) {
            logoutBtn.addEventListener('click', (e) => {
                e.preventDefault();
                clearAuth();
                window.location.href = '/login';
            });
        }

        const toggle = document.getElementById('navToggle');
        const links = document.querySelector('.nav-links');
        if (toggle && links) {
            toggle.addEventListener('click', () => links.classList.toggle('open'));
        }
    }

    function showError(el, message) {
        if (!el) return;
        el.textContent = message;
        el.classList.remove('hidden');
    }

    function initFeaturedSlider(videos) {
        if (!videos || !videos.length) return;

        const track = document.getElementById('sliderTrack');
        const dots = document.getElementById('sliderDots');
        const titleEl = document.getElementById('sliderTitle');
        const descEl = document.getElementById('sliderDesc');
        const metaEl = document.getElementById('sliderMeta');
        const playBtn = document.getElementById('sliderPlay');
        const infoBtn = document.getElementById('sliderInfoBtn');
        const eyebrow = document.getElementById('sliderEyebrow');
        let current = 0;
        let timer = null;

        videos.forEach((v, i) => {
            const slide = document.createElement('div');
            slide.className = 'slider-slide' + (i === 0 ? ' active' : '');
            slide.style.backgroundImage = `url('${v.thumbnailUrl}')`;
            track.appendChild(slide);

            const dot = document.createElement('button');
            dot.className = 'slider-dot' + (i === 0 ? ' active' : '');
            dot.setAttribute('aria-label', 'Slide ' + (i + 1));
            dot.addEventListener('click', () => goTo(i));
            dots.appendChild(dot);
        });

        function updateInfo(video) {
            titleEl.textContent = video.title;
            descEl.textContent = (video.description || '').slice(0, 180) + ((video.description || '').length > 180 ? '…' : '');
            metaEl.textContent = [
                formatGenre(video.genre),
                video.releaseYear,
                video.rating ? video.rating + ' ★' : null,
                video.isPremium ? 'Premium' : null
            ].filter(Boolean).join('  ·  ');
            eyebrow.textContent = video.isPremium ? 'Premium Feature' : 'Now Streaming';
            const url = '/videos/' + video.videoId;
            playBtn.href = url;
            infoBtn.href = url;
        }

        function goTo(index) {
            current = (index + videos.length) % videos.length;
            track.querySelectorAll('.slider-slide').forEach((s, i) => s.classList.toggle('active', i === current));
            dots.querySelectorAll('.slider-dot').forEach((d, i) => d.classList.toggle('active', i === current));
            updateInfo(videos[current]);
            resetTimer();
        }

        function resetTimer() {
            clearInterval(timer);
            timer = setInterval(() => goTo(current + 1), 6000);
        }

        document.getElementById('sliderPrev').addEventListener('click', () => goTo(current - 1));
        document.getElementById('sliderNext').addEventListener('click', () => goTo(current + 1));

        updateInfo(videos[0]);
        resetTimer();
    }

    document.addEventListener('DOMContentLoaded', initNav);

    return {
        getToken, setAuth, clearAuth, getUser, requireAuth, requireGuest,
        api, auth, videos, watchlist, subscriptions, users, admin,
        toast, formatDuration, formatGenre, renderVideoCard, showError,
        initFeaturedSlider,
    };
})();

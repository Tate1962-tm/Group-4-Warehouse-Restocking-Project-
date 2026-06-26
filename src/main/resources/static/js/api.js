const API = {
  token: localStorage.getItem('awrs_token'),
  user: JSON.parse(localStorage.getItem('awrs_user') || 'null'),

  async request(method, path, body) {
    const headers = { 'Content-Type': 'application/json' };
    if (this.token) headers['Authorization'] = `Bearer ${this.token}`;
    const opts = { method, headers };
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch(path, opts);
    if (res.status === 204) return null;
    const data = await res.json().catch(() => null);
    if (!res.ok) throw new Error(data?.message || `Request failed (${res.status})`);
    return data;
  },

  get(path) { return this.request('GET', path); },
  post(path, body) { return this.request('POST', path, body); },
  del(path) { return this.request('DELETE', path); },

  setSession(token, user) {
    this.token = token;
    this.user = user;
    localStorage.setItem('awrs_token', token);
    localStorage.setItem('awrs_user', JSON.stringify(user));
  },

  clearSession() {
    this.token = null;
    this.user = null;
    localStorage.removeItem('awrs_token');
    localStorage.removeItem('awrs_user');
  },

  isAdmin() { return this.user?.role === 'ADMIN'; },
  isManager() { return this.user?.role === 'MANAGER' || this.isAdmin(); },
};

function toast(msg, type = 'success') {
  const el = document.getElementById('toast');
  el.textContent = msg;
  el.className = `toast ${type} show`;
  setTimeout(() => el.classList.remove('show'), 3000);
}

function badge(text, cls) {
  return `<span class="badge badge-${cls}">${text}</span>`;
}

function roleBadge(role) {
  return badge(role, role.toLowerCase());
}

function statusBadge(status) {
  return badge(status, status.toLowerCase().replace('_', '-'));
}

function riskBadge(level) {
  return badge(level, level.toLowerCase());
}

function formatDate(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleString();
}

function esc(str) {
  const d = document.createElement('div');
  d.textContent = str ?? '';
  return d.innerHTML;
}

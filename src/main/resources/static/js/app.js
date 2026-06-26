// ── Page loaders ──

async function loadDashboard() {
  const d = await API.get('/api/dashboard');
  document.getElementById('dash-stats').innerHTML = `
    <div class="stat-card accent"><div class="label">Items</div><div class="value">${d.totalItems}</div></div>
    <div class="stat-card"><div class="label">Locations</div><div class="value">${d.totalLocations}</div></div>
    <div class="stat-card"><div class="label">Inventory Records</div><div class="value">${d.totalInventoryRecords}</div></div>
    <div class="stat-card danger"><div class="label">Critical Low Stock</div><div class="value">${d.lowStockCount}</div></div>
    <div class="stat-card warning"><div class="label">Pending Tasks</div><div class="value">${d.pendingRestockTasks}</div></div>
    <div class="stat-card success"><div class="label">Completed Tasks</div><div class="value">${d.completedRestockTasks}</div></div>`;

  const alerts = d.lowStockAlerts;
  document.getElementById('dash-alerts').innerHTML = alerts.length ? `<table>
    <thead><tr><th>SKU</th><th>Location</th><th>Qty</th><th>Min</th><th>Risk</th></tr></thead>
    <tbody>${alerts.map(a => `<tr>
      <td><strong>${esc(a.itemSku)}</strong><br><small>${esc(a.description)}</small></td>
      <td>${esc(a.locationId)}</td><td>${a.quantity}</td><td>${a.minimumThreshold}</td>
      <td>${riskBadge(a.riskLevel)}</td></tr>`).join('')}</tbody></table>`
    : '<div class="empty-state">✅ No low stock alerts</div>';

  const tasks = d.urgentTasks;
  document.getElementById('dash-tasks').innerHTML = tasks.length ? `<table>
    <thead><tr><th>Item</th><th>Location</th><th>Qty</th><th>Priority</th><th>Status</th></tr></thead>
    <tbody>${tasks.map(t => `<tr>
      <td>${esc(t.itemSku)}</td><td>${esc(t.locationId)}</td>
      <td>${t.requestedQuantity}</td><td>${t.priority}</td>
      <td>${statusBadge(t.status)}</td></tr>`).join('')}</tbody></table>`
    : '<div class="empty-state">No pending restock tasks</div>';

  const activity = d.recentActivity;
  document.getElementById('dash-activity').innerHTML = activity.length ? `<table>
    <thead><tr><th>Time</th><th>Action</th><th>Item</th><th>Location</th><th>Change</th><th>By</th></tr></thead>
    <tbody>${activity.map(a => `<tr>
      <td>${formatDate(a.timestamp)}</td><td>${esc(a.action)}</td>
      <td>${esc(a.itemSku)}</td><td>${esc(a.locationId)}</td>
      <td>${a.quantityChange > 0 ? '+' : ''}${a.quantityChange}</td>
      <td>${esc(a.performedBy)}</td></tr>`).join('')}</tbody></table>`
    : '<div class="empty-state">No activity yet</div>';
}

async function loadItems() {
  const items = await API.get('/api/items');
  document.getElementById('items-list').innerHTML = items.length ? `<table>
    <thead><tr><th>SKU</th><th>Description</th><th>Supplier</th><th>UOM</th><th>Min</th><th>Reorder</th></tr></thead>
    <tbody>${items.map(i => `<tr>
      <td><strong>${esc(i.sku)}</strong></td><td>${esc(i.description)}</td>
      <td>${esc(i.supplier)}</td><td>${esc(i.unitOfMeasure)}</td>
      <td>${i.minimumThreshold}</td><td>${i.reorderPoint}</td></tr>`).join('')}</tbody></table>`
    : '<div class="empty-state">No items yet</div>';
  return items;
}

async function loadLocations() {
  const locs = await API.get('/api/locations');
  document.getElementById('locations-list').innerHTML = locs.length ? `<table>
    <thead><tr><th>ID</th><th>Name</th><th>Type</th><th>Path</th></tr></thead>
    <tbody>${locs.map(l => `<tr>
      <td><strong>${esc(l.id)}</strong></td><td>${esc(l.name)}</td>
      <td>${esc(l.type)}</td><td>${esc(l.fullPath)}</td></tr>`).join('')}</tbody></table>`
    : '<div class="empty-state">No locations yet</div>';
  return locs;
}

async function populateSelects() {
  const [items, locs] = await Promise.all([API.get('/api/items'), API.get('/api/locations')]);
  const itemOpts = items.map(i => `<option value="${esc(i.sku)}">${esc(i.sku)} — ${esc(i.description)}</option>`).join('');
  const locOpts = locs.map(l => `<option value="${esc(l.id)}">${esc(l.fullPath)}</option>`).join('');
  ['receive', 'fulfill', 'adjust'].forEach(prefix => {
    const itemEl = document.getElementById(`${prefix}-item`);
    const locEl = document.getElementById(`${prefix}-location`);
    if (itemEl) itemEl.innerHTML = itemOpts;
    if (locEl) locEl.innerHTML = locOpts;
  });
}

async function loadRestock() {
  const tasks = await API.get('/api/restock/tasks');
  document.getElementById('restock-count').textContent = `${tasks.length} tasks`;
  document.getElementById('restock-list').innerHTML = tasks.length ? `<table>
    <thead><tr><th>Item</th><th>Location</th><th>Request Qty</th><th>Priority</th><th>Status</th><th>Actions</th></tr></thead>
    <tbody>${tasks.map(t => `<tr>
      <td>${esc(t.itemSku)}</td><td>${esc(t.locationId)}</td>
      <td>${t.requestedQuantity}</td><td>${t.priority}</td>
      <td>${statusBadge(t.status)}</td>
      <td>${t.status !== 'COMPLETED' ? `
        <button class="btn btn-sm btn-secondary" onclick="assignTask('${t.id}')">Assign</button>
        <button class="btn btn-sm btn-primary" onclick="completeTask('${t.id}')">Complete</button>` : '—'}</td>
    </tr>`).join('')}</tbody></table>`
    : '<div class="empty-state">No restock tasks. Run batch evaluate to generate tasks.</div>';
}

async function loadAudit() {
  const logs = await API.get('/api/inventory/audit-logs');
  document.getElementById('audit-list').innerHTML = logs.length ? `<table>
    <thead><tr><th>Time</th><th>Action</th><th>Item</th><th>Location</th><th>Change</th><th>By</th><th>Reason</th></tr></thead>
    <tbody>${logs.map(a => `<tr>
      <td>${formatDate(a.timestamp)}</td><td>${esc(a.action)}</td>
      <td>${esc(a.itemSku)}</td><td>${esc(a.locationId)}</td>
      <td>${a.quantityChange > 0 ? '+' : ''}${a.quantityChange}</td>
      <td>${esc(a.performedBy)}</td><td>${esc(a.reason || '—')}</td></tr>`).join('')}</tbody></table>`
    : '<div class="empty-state">No audit entries yet</div>';
}

async function loadReports() {
  const [d, logs, inventory] = await Promise.all([
    API.get('/api/dashboard'), API.get('/api/inventory/audit-logs'), API.get('/api/inventory')]);
  document.getElementById('report-stats').innerHTML = `
    <div class="stat-card accent"><div class="label">Total SKUs</div><div class="value">${d.totalItems}</div></div>
    <div class="stat-card danger"><div class="label">Stockouts Avoided</div><div class="value">${d.completedRestockTasks}</div></div>
    <div class="stat-card warning"><div class="label">Active Alerts</div><div class="value">${d.lowStockAlerts.length}</div></div>
    <div class="stat-card"><div class="label">Transactions</div><div class="value">${logs.length}</div></div>`;

  const receives = logs.filter(l => l.action === 'RECEIVE').length;
  const fulfills = logs.filter(l => l.action === 'FULFILL').length;
  const adjusts = logs.filter(l => l.action === 'ADJUST').length;
  const totalUnits = inventory.reduce((s, r) => s + r.quantity, 0);

  document.getElementById('report-movement').innerHTML = `<table>
    <thead><tr><th>Metric</th><th>Value</th></tr></thead>
    <tbody>
      <tr><td>Total units in stock</td><td><strong>${totalUnits}</strong></td></tr>
      <tr><td>Receive transactions</td><td>${receives}</td></tr>
      <tr><td>Fulfill transactions</td><td>${fulfills}</td></tr>
      <tr><td>Adjustment transactions</td><td>${adjusts}</td></tr>
      <tr><td>Inventory locations tracked</td><td>${inventory.length}</td></tr>
    </tbody></table>`;
}

async function loadUsers() {
  const users = await API.get('/api/users');
  document.getElementById('users-list').innerHTML = `<table>
    <thead><tr><th>Username</th><th>Role</th><th>Actions</th></tr></thead>
    <tbody>${users.map(u => `<tr>
      <td><strong>${esc(u.username)}</strong></td>
      <td>${roleBadge(u.role)}</td>
      <td>${u.username !== API.user.username ? `<button class="btn btn-sm btn-danger" onclick="deleteUser('${esc(u.username)}')">Delete</button>` : '—'}</td>
    </tr>`).join('')}</tbody></table>`;
}

// ── Actions ──

async function assignTask(id) {
  try { await API.post(`/api/restock/tasks/${id}/assign`, {}); toast('Task assigned'); loadRestock(); }
  catch (e) { toast(e.message, 'error'); }
}

async function completeTask(id) {
  try { await API.post(`/api/restock/tasks/${id}/complete`, {}); toast('Restock completed — inventory updated'); loadRestock(); }
  catch (e) { toast(e.message, 'error'); }
}

async function deleteUser(username) {
  if (!confirm(`Delete user ${username}?`)) return;
  try { await API.del(`/api/users/${username}`); toast('User deleted'); loadUsers(); }
  catch (e) { toast(e.message, 'error'); }
}

// ── Navigation ──

const pageLoaders = {
  dashboard: loadDashboard,
  items: loadItems,
  locations: loadLocations,
  receive: populateSelects,
  fulfill: populateSelects,
  adjust: populateSelects,
  restock: loadRestock,
  audit: loadAudit,
  reports: loadReports,
  users: loadUsers,
};

function navigate(page) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
  document.getElementById(`page-${page}`)?.classList.add('active');
  document.querySelector(`[data-page="${page}"]`)?.classList.add('active');
  pageLoaders[page]?.();
}

// ── Auth ──

function showApp() {
  document.getElementById('login-page').style.display = 'none';
  document.getElementById('app-shell').classList.add('active');
  const u = API.user;
  document.getElementById('user-name').textContent = u.username;
  document.getElementById('user-role').textContent = u.role;
  document.getElementById('user-avatar').textContent = u.username[0].toUpperCase();
  document.querySelectorAll('.admin-only').forEach(el => el.classList.toggle('hidden', !API.isAdmin()));
  document.querySelectorAll('.manager-only').forEach(el => el.classList.toggle('hidden', !API.isManager()));
  navigate('dashboard');
}

function showLogin() {
  API.clearSession();
  document.getElementById('login-page').style.display = 'flex';
  document.getElementById('app-shell').classList.remove('active');
}

// ── Forms ──

document.getElementById('login-form').addEventListener('submit', async e => {
  e.preventDefault();
  const errEl = document.getElementById('login-error');
  errEl.classList.remove('show');
  try {
    const data = await API.post('/api/auth/login', {
      username: document.getElementById('username').value,
      password: document.getElementById('password').value,
    });
    API.setSession(data.token, { username: data.username, role: data.role });
    showApp();
  } catch (err) {
    errEl.textContent = err.message;
    errEl.classList.add('show');
  }
});

document.getElementById('logout-btn').addEventListener('click', async () => {
  try { await API.post('/api/auth/logout', {}); } catch (_) {}
  showLogin();
});

document.getElementById('nav-menu').addEventListener('click', e => {
  const btn = e.target.closest('.nav-item');
  if (btn) navigate(btn.dataset.page);
});

document.getElementById('item-form')?.addEventListener('submit', async e => {
  e.preventDefault();
  const fd = new FormData(e.target);
  try {
    await API.post('/api/items', {
      sku: fd.get('sku'), description: fd.get('description'),
      supplier: fd.get('supplier'), unitOfMeasure: fd.get('unitOfMeasure'),
      minimumThreshold: +fd.get('minimumThreshold'), reorderPoint: +fd.get('reorderPoint'),
    });
    toast('Item created'); e.target.reset(); loadItems();
  } catch (err) { toast(err.message, 'error'); }
});

document.getElementById('location-form')?.addEventListener('submit', async e => {
  e.preventDefault();
  const fd = new FormData(e.target);
  try {
    await API.post('/api/locations', {
      id: fd.get('id'), name: fd.get('name'), type: fd.get('type'),
      parentId: fd.get('parentId') || null,
    });
    toast('Location created'); e.target.reset(); loadLocations();
  } catch (err) { toast(err.message, 'error'); }
});

document.getElementById('receive-form').addEventListener('submit', async e => {
  e.preventDefault();
  const fd = new FormData(e.target);
  try {
    const r = await API.post('/api/inventory/receive', {
      itemSku: fd.get('itemSku'), locationId: fd.get('locationId'), quantity: +fd.get('quantity'),
    });
    toast(`Received — new quantity: ${r.quantity}`);
  } catch (err) { toast(err.message, 'error'); }
});

document.getElementById('fulfill-form').addEventListener('submit', async e => {
  e.preventDefault();
  const fd = new FormData(e.target);
  try {
    const r = await API.post('/api/inventory/fulfill', {
      itemSku: fd.get('itemSku'), locationId: fd.get('locationId'), quantity: +fd.get('quantity'),
    });
    toast(`Fulfilled — remaining: ${r.quantity}`);
  } catch (err) { toast(err.message, 'error'); }
});

document.getElementById('adjust-form')?.addEventListener('submit', async e => {
  e.preventDefault();
  const fd = new FormData(e.target);
  try {
    const r = await API.post('/api/inventory/adjust', {
      itemSku: fd.get('itemSku'), locationId: fd.get('locationId'),
      newQuantity: +fd.get('newQuantity'), reason: fd.get('reason'),
    });
    toast(`Adjusted — new quantity: ${r.quantity}`);
  } catch (err) { toast(err.message, 'error'); }
});

document.getElementById('batch-evaluate-btn').addEventListener('click', async () => {
  try {
    const tasks = await API.post('/api/restock/evaluate/batch', {});
    toast(`Generated ${tasks.length} restock task(s)`);
    loadRestock();
  } catch (err) { toast(err.message, 'error'); }
});

document.getElementById('user-form')?.addEventListener('submit', async e => {
  e.preventDefault();
  const fd = new FormData(e.target);
  try {
    await API.post('/api/users', {
      username: fd.get('username'), password: fd.get('password'), role: fd.get('role'),
    });
    toast('User created'); e.target.reset(); loadUsers();
  } catch (err) { toast(err.message, 'error'); }
});

// ── Init ──
window.assignTask = assignTask;
window.completeTask = completeTask;
window.deleteUser = deleteUser;

if (API.token && API.user) {
  showApp();
} else {
  showLogin();
}

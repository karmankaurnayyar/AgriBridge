/**
 * AgriBridge frontend — shared API helper.
 *
 * Week 2 note: the backend enforces authenticated endpoints with HTTP Basic
 * (see docs/architecture.md, "Security Approach"), so this helper stores the
 * base64-encoded "email:password" pair returned at login and attaches it as
 * an Authorization: Basic header on every subsequent request. A JWT is also
 * returned by /api/auth/login and stored for forward compatibility, but it
 * is not yet what the backend checks — that is planned for Week 3 once
 * JwtAuthenticationFilter is wired in. Storing Basic-Auth credentials in
 * localStorage is a simplification appropriate for local Week 2 development
 * only; see README "Known Limitations" before using this pattern anywhere
 * that matters.
 */

const API_BASE_URL = "http://localhost:8080";

const AgriBridgeAuth = {
  save(email, password, profile, token) {
    localStorage.setItem("ab_basic", btoa(`${email}:${password}`));
    localStorage.setItem("ab_profile", JSON.stringify(profile));
    if (token) localStorage.setItem("ab_token", token);
  },
  profile() {
    const raw = localStorage.getItem("ab_profile");
    return raw ? JSON.parse(raw) : null;
  },
  isLoggedIn() {
    return !!localStorage.getItem("ab_basic");
  },
  logout() {
    localStorage.removeItem("ab_basic");
    localStorage.removeItem("ab_profile");
    localStorage.removeItem("ab_token");
    window.location.href = "login.html";
  },
  requireLogin() {
    if (!this.isLoggedIn()) {
      window.location.href = "login.html";
    }
  },
};

async function apiFetch(path, { method = "GET", body, auth = false } = {}) {
  const headers = { "Content-Type": "application/json" };
  if (auth) {
    const basic = localStorage.getItem("ab_basic");
    if (basic) headers["Authorization"] = `Basic ${basic}`;
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  let data = null;
  try {
    data = await response.json();
  } catch (e) {
    // No JSON body (e.g., 204 No Content) — that's fine.
  }

  if (!response.ok) {
    const message = (data && (data.message || data.error)) || `Request failed (${response.status})`;
    const error = new Error(message);
    error.status = response.status;
    error.details = data && data.details;
    throw error;
  }

  return data;
}

function showAlert(containerEl, message, type = "error") {
  containerEl.innerHTML = `<div class="alert ${type}">${message}</div>`;
}

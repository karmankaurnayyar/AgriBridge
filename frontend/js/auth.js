/**
 * Handles the register.html and login.html forms. Expects api.js to be
 * loaded first.
 */

function initRegisterForm() {
  const form = document.getElementById("register-form");
  const alertBox = document.getElementById("form-alert");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    alertBox.innerHTML = "";

    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const role = document.getElementById("role").value;

    try {
      const data = await apiFetch("/api/auth/register", {
        method: "POST",
        body: { name, email, password, role },
      });
      AgriBridgeAuth.save(email, password, { userId: data.userId, name: data.name, email: data.email, role: data.role }, data.token);
      showAlert(alertBox, "Account created successfully. Redirecting to your dashboard...", "success");
      setTimeout(() => (window.location.href = "dashboard.html"), 900);
    } catch (err) {
      const detail = err.details && err.details.length ? ` (${err.details.join("; ")})` : "";
      showAlert(alertBox, `${err.message}${detail}`);
    }
  });
}

function initLoginForm() {
  const form = document.getElementById("login-form");
  const alertBox = document.getElementById("form-alert");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    alertBox.innerHTML = "";

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    try {
      const data = await apiFetch("/api/auth/login", {
        method: "POST",
        body: { email, password },
      });
      AgriBridgeAuth.save(email, password, { userId: data.userId, name: data.name, email: data.email, role: data.role }, data.token);
      window.location.href = "dashboard.html";
    } catch (err) {
      showAlert(alertBox, err.message);
    }
  });
}

document.addEventListener("DOMContentLoaded", () => {
  initRegisterForm();
  initLoginForm();
});

/**
 * Drives dashboard.html. /api/dashboard/summary is planned for Week 4 (see
 * docs/roadmap.md) and does not exist yet, so this page computes the one
 * real metric it can (farm count, from the implemented /api/farms endpoint)
 * and clearly marks the rest as placeholders for future modules.
 */

document.addEventListener("DOMContentLoaded", async () => {
  AgriBridgeAuth.requireLogin();

  const profile = AgriBridgeAuth.profile();
  const greeting = document.getElementById("dashboard-greeting");
  if (profile && greeting) {
    greeting.textContent = `Welcome back, ${profile.name} (${profile.role})`;
  }

  const farmCountEl = document.getElementById("stat-farms");
  try {
    const farms = await apiFetch("/api/farms", { auth: true });
    farmCountEl.textContent = farms.length;
  } catch (err) {
    farmCountEl.textContent = "—";
    showAlert(document.getElementById("dashboard-alert"), err.message);
  }
});

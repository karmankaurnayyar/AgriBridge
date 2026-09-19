/**
 * Drives farms.html: lists the logged-in user's farms and supports
 * create/edit/delete against the implemented /api/farms endpoints.
 */

let editingFarmId = null;

async function loadFarms() {
  const tbody = document.getElementById("farms-tbody");
  const emptyState = document.getElementById("farms-empty");
  try {
    const farms = await apiFetch("/api/farms", { auth: true });
    tbody.innerHTML = "";
    emptyState.style.display = farms.length ? "none" : "block";

    farms.forEach((farm) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${escapeHtml(farm.farmName)}</td>
        <td>${escapeHtml(farm.location)}</td>
        <td>${farm.landArea} ha</td>
        <td>${escapeHtml(farm.soilType || "—")}</td>
        <td class="row-actions">
          <button type="button" onclick="startEditFarm(${farm.id}, '${escapeAttr(farm.farmName)}', '${escapeAttr(farm.location)}', ${farm.landArea}, '${escapeAttr(farm.soilType || "")}')">Edit</button>
          <button type="button" class="danger" onclick="deleteFarm(${farm.id})">Delete</button>
        </td>`;
      tbody.appendChild(tr);
    });
  } catch (err) {
    showAlert(document.getElementById("farms-alert"), err.message);
  }
}

function startEditFarm(id, farmName, location, landArea, soilType) {
  editingFarmId = id;
  document.getElementById("farmName").value = farmName;
  document.getElementById("location").value = location;
  document.getElementById("landArea").value = landArea;
  document.getElementById("soilType").value = soilType;
  document.getElementById("farm-form-title").textContent = "Edit Farm";
  document.getElementById("farm-submit-btn").textContent = "Update Farm";
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function resetFarmForm() {
  editingFarmId = null;
  document.getElementById("farm-form").reset();
  document.getElementById("farm-form-title").textContent = "Add a Farm";
  document.getElementById("farm-submit-btn").textContent = "Add Farm";
}

async function deleteFarm(id) {
  if (!confirm("Delete this farm? This cannot be undone.")) return;
  try {
    await apiFetch(`/api/farms/${id}`, { method: "DELETE", auth: true });
    await loadFarms();
  } catch (err) {
    showAlert(document.getElementById("farms-alert"), err.message);
  }
}

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str;
  return div.innerHTML;
}
function escapeAttr(str) {
  return String(str).replace(/'/g, "\\'");
}

document.addEventListener("DOMContentLoaded", () => {
  AgriBridgeAuth.requireLogin();
  loadFarms();

  const form = document.getElementById("farm-form");
  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const alertBox = document.getElementById("farms-alert");
    alertBox.innerHTML = "";

    const body = {
      farmName: document.getElementById("farmName").value.trim(),
      location: document.getElementById("location").value.trim(),
      landArea: parseFloat(document.getElementById("landArea").value),
      soilType: document.getElementById("soilType").value.trim() || null,
    };

    try {
      if (editingFarmId) {
        await apiFetch(`/api/farms/${editingFarmId}`, { method: "PUT", body, auth: true });
      } else {
        await apiFetch("/api/farms", { method: "POST", body, auth: true });
      }
      resetFarmForm();
      await loadFarms();
    } catch (err) {
      const detail = err.details && err.details.length ? ` (${err.details.join("; ")})` : "";
      showAlert(alertBox, `${err.message}${detail}`);
    }
  });

  document.getElementById("farm-cancel-btn").addEventListener("click", resetFarmForm);
});

const adminMessage = document.getElementById("dashboardMessage");
const tenantTableBody = document.getElementById("tenantTableBody");

async function loadAdminDashboard() {
  try {
    const identityResponse = await apiRequest("/api/auth/me");
    const identity = await identityResponse.json();
    if (identity.role !== "ADMIN") {
      window.location.replace("index.html");
      return;
    }

    const response = await apiRequest("/api/admin/tenants");
    const tenants = await response.json();
    tenantTableBody.replaceChildren();
    tenants.forEach(tenant => {
      const row = document.createElement("tr");
      [tenant.fullName, tenant.email, tenant.flatNumber || ""].forEach(value => {
        const cell = document.createElement("td");
        cell.textContent = value;
        row.appendChild(cell);
      });
      tenantTableBody.appendChild(row);
    });
  } catch (error) {
    if (error.status === 401 || error.status === 403) {
      window.location.replace("index.html");
      return;
    }
    adminMessage.textContent = error.message;
  }
}

loadAdminDashboard();
window.addEventListener("pageshow", event => {
  if (event.persisted) {
    loadAdminDashboard();
  }
});

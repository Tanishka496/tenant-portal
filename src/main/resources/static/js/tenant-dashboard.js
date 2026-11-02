function markAsSolved(button) {
  const listItem = button.parentElement;
  const statusSpan = listItem.querySelector("span");
  statusSpan.textContent = "Resolved";
  statusSpan.className = "solved";
  button.remove();
  alert("Complaint marked as solved!");
}

const complaintList = document.getElementById("complaintList");
complaintList.addEventListener("click", function(event) {
  if (event.target.matches("button")) {
    markAsSolved(event.target);
  }
});

document.getElementById("complaintForm").addEventListener("submit", function(event) {
  event.preventDefault();
  const complaintText = document.getElementById("complaintText").value.trim();
  if (complaintText !== "") {
    const li = document.createElement("li");
    li.innerHTML = `${complaintText} - <span class="pending">Pending</span> 
                    <button>Mark as Solved</button>`;
    document.getElementById("complaintList").appendChild(li);
    document.getElementById("complaintText").value = "";
  }
});

async function loadTenantDashboard() {
  const dashboardMessage = document.getElementById("dashboardMessage");
  try {
    const identityResponse = await apiRequest("/api/auth/me");
    const identity = await identityResponse.json();
    if (identity.role !== "TENANT") {
      window.location.replace("index.html");
      return;
    }

    const response = await apiRequest("/api/tenant/me");
    const tenant = await response.json();
    document.getElementById("tenantName").textContent = tenant.fullName;
    document.getElementById("tenantFlatNumber").textContent = tenant.flatNumber || "Not assigned";
    document.getElementById("tenantPhone").textContent = tenant.phone;
  } catch (error) {
    if (error.status === 401 || error.status === 403) {
      window.location.replace("index.html");
      return;
    }
    dashboardMessage.textContent = error.message;
  }
}

loadTenantDashboard();
window.addEventListener("pageshow", event => {
  if (event.persisted) {
    loadTenantDashboard();
  }
});

async function logout() {
  try {
    await apiRequest("/api/auth/logout", { method: "POST" });
    window.location.replace("index.html");
  } catch (error) {
    const message = document.getElementById("dashboardMessage");
    message.textContent = `Logout failed: ${error.message}`;
  }
}

document.getElementById("logoutButton").addEventListener("click", logout);

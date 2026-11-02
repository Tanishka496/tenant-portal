const loginForm = document.getElementById("loginForm");
const loginButton = document.getElementById("loginButton");
const loginMessage = document.getElementById("loginMessage");
const tenantLoginMode = document.getElementById("tenantLoginMode");
const adminLoginMode = document.getElementById("adminLoginMode");
const loginModeMessage = document.getElementById("loginModeMessage");
let selectedRole = "TENANT";

function selectLoginMode(role) {
  selectedRole = role;
  const isAdmin = role === "ADMIN";
  tenantLoginMode.classList.toggle("active", !isAdmin);
  adminLoginMode.classList.toggle("active", isAdmin);
  tenantLoginMode.setAttribute("aria-pressed", String(!isAdmin));
  adminLoginMode.setAttribute("aria-pressed", String(isAdmin));
  loginModeMessage.textContent = isAdmin
    ? "Sign in with an administrator account."
    : "Sign in as a tenant.";
  loginMessage.textContent = "";
}

tenantLoginMode.addEventListener("click", () => selectLoginMode("TENANT"));
adminLoginMode.addEventListener("click", () => selectLoginMode("ADMIN"));

const registered = new URLSearchParams(window.location.search).get("registered");
if (registered === "true") {
  loginMessage.textContent = "Registration successful. Please log in.";
}

loginForm.addEventListener("submit", async function(event) {
  event.preventDefault();
  loginMessage.textContent = "";
  if (!loginForm.reportValidity()) {
    return;
  }

  loginButton.disabled = true;
  loginButton.textContent = "Signing in...";
  try {
    await apiRequest("/api/auth/login", {
      method: "POST",
      body: JSON.stringify({
        username: document.getElementById("username").value.trim(),
        password: document.getElementById("password").value
      })
    });

    const response = await apiRequest("/api/auth/me");
    const user = await response.json();
    if (user.role !== selectedRole) {
      await apiRequest("/api/auth/logout", { method: "POST" });
      throw new Error(
        selectedRole === "ADMIN"
          ? "This account is not authorized for Admin Login."
          : "Please use Admin Login for this account."
      );
    }

    if (user.role === "ADMIN") {
      window.location.replace("admin final.html");
    } else if (user.role === "TENANT") {
      window.location.replace("tenantdashboard.html");
    } else {
      throw new Error("The authenticated account has no supported portal role.");
    }
  } catch (error) {
    loginMessage.textContent = error.status === 401
      ? "Invalid username or password."
      : error.message;
  } finally {
    loginButton.disabled = false;
    loginButton.textContent = "Login";
  }
});

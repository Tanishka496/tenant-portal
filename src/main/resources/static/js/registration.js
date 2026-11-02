const registrationForm = document.getElementById("registrationForm");
const registrationButton = document.getElementById("registrationButton");
const registrationMessage = document.getElementById("registrationMessage");

registrationForm.addEventListener("submit", async function(event) {
  event.preventDefault();
  registrationMessage.textContent = "";
  if (!registrationForm.reportValidity()) {
    return;
  }

  const password = document.getElementById("password").value;
  const confirmPassword = document.getElementById("confirmPassword").value;
  if (password !== confirmPassword) {
    registrationMessage.textContent = "Passwords do not match.";
    return;
  }

  registrationButton.disabled = true;
  registrationButton.textContent = "Registering...";
  try {
    const response = await apiRequest("/api/auth/register", {
      method: "POST",
      body: JSON.stringify({
        username: document.getElementById("username").value.trim(),
        password,
        fullName: document.getElementById("fullName").value.trim(),
        email: document.getElementById("email").value.trim(),
        phone: document.getElementById("phone").value.trim(),
        flatNumber: document.getElementById("flatNumber").value.trim() || null
      })
    });
    if (response.status === 201) {
      window.location.replace("index.html?registered=true");
    }
  } catch (error) {
    registrationMessage.textContent = error.message;
  } finally {
    registrationButton.disabled = false;
    registrationButton.textContent = "Register";
  }
});

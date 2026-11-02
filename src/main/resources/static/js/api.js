function readCookie(name) {
  const prefix = `${name}=`;
  const cookie = document.cookie.split("; ").find(value => value.startsWith(prefix));
  return cookie ? decodeURIComponent(cookie.substring(prefix.length)) : null;
}

async function ensureCsrfToken() {
  const response = await fetch("/api/auth/csrf", {
    credentials: "same-origin"
  });
  if (!response.ok) {
    throw new Error("Unable to initialize secure session protection.");
  }
  await response.json();
}

async function apiRequest(url, options = {}) {
  const request = {
    credentials: "same-origin",
    ...options,
    headers: {
      ...(options.headers || {})
    }
  };

  if (request.body && !request.headers["Content-Type"]) {
    request.headers["Content-Type"] = "application/json";
  }

  if (["POST", "PUT", "PATCH", "DELETE"].includes(request.method || "GET")) {
    await ensureCsrfToken();
    const token = readCookie("XSRF-TOKEN");
    if (token) {
      request.headers["X-XSRF-TOKEN"] = token;
    }
  }

  const response = await fetch(url, request);
  if (!response.ok) {
    let message = `Request failed with status ${response.status}.`;
    try {
      const error = await response.json();
      message = error.message || message;
    } catch (ignored) {
      // Use the status-based message when the server does not return JSON.
    }
    const requestError = new Error(message);
    requestError.status = response.status;
    throw requestError;
  }
  return response;
}

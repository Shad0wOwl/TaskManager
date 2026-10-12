const loginForm = document.getElementById("login-form");
const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");

loginForm.addEventListener("submit", async function(event) {
    event.preventDefault();

    const email = emailInput.value;
    const password = passwordInput.value;
    const loginData = {
        email: email,
        password: password
    };

    try {
        const csrfResponse = await fetch("http://127.0.0.1:8080/api/auth/csrf", {
            credentials: "include"
        });

        if (!csrfResponse.ok) {
            throw new Error("Failed to fetch CSRF token");
        }

        const csrf = await csrfResponse.json();

        const response = await fetch("http://127.0.0.1:8080/api/auth/login", {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
            [csrf.headerName]: csrf.token
        },
        body: JSON.stringify(loginData)
    });

    if (response.ok) {
            alert("Credentials verified successfully!");
            loginForm.reset();
        } else if (response.status === 401) {
            alert("Invalid email or password.");
        } else {
            alert("Something went wrong. Please try again later.");
        }

    } catch (error) {
        console.error("Login error:", error);
        alert("Cannot connect to the server.");
    }
})
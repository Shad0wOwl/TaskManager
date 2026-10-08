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
        const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(loginData)
    });

    if (response.ok) {
            alert("Credentials verified successfully!");
            registerForm.reset();
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
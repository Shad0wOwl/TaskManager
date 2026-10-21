const registerForm = document.getElementById("register-form");
const passwordInput = document.getElementById("password");
const passwordError = document.getElementById("password-error");
const confirmPasswordInput = document.getElementById("con-password");
const nameInput = document.getElementById("name");
const emailInput = document.getElementById("email");



registerForm.addEventListener("submit", async function(event) {
    event.preventDefault();

    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;


    if (password.length < 8) {
        passwordError.textContent = "Password must be at least 8 characters long";
        return;
    }
    if (password !== confirmPassword) {
        passwordError.textContent = "Passwords do not match!";
        return;
    }

    passwordError.textContent = "";

    const userData = {
        name: nameInput.value.trim(),
        email: emailInput.value.trim(),
        password: password
    };

    try {
        const csrfResponse = await fetch(`${API_URL}/api/auth/csrf`, {
            credentials: "include"
        });

        if (!csrfResponse.ok) {
            throw new Error("Failed to fetch CSRF token");
        }

        const csrf = await csrfResponse.json();
        
        const response = await fetch(`${API_URL}/api/auth/register`, {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
                [csrf.headerName]: csrf.token
            },
            body: JSON.stringify(userData)
        });

        if (response.ok) {
            const loginResponse = await fetch(
                `${API_URL}/api/auth/login`,
                {
                    method: "POST",
                    credentials: "include",
                    headers: {
                        "Content-Type": "application/json",
                        [csrf.headerName]: csrf.token
                    },
                    body: JSON.stringify({
                        email: userData.email,
                        password:userData.password
                    })
                }
            );

            if (!loginResponse.ok) {
                alert("Account created, but automatic login failed. Please log in");
                window.location.href = "login.html";
                return;
            }

            window.location = "../index.html";
            
        } else if (response.status === 409) {
            alert("This email is already registered.");
        } else if (response.status === 400) {
            alert("Please check your registration details.");
        } else {
            alert("Something went wrong. Please try again later.");
        }
    } catch (error) {
        console.error("Registration error: ", error);
        alert("Cannot connect to the server. Please try again later.");
    }
});



confirmPasswordInput.addEventListener("input", function() {
    passwordError.textContent = "";
});
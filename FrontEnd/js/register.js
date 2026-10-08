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
        const response = await fetch("http://localhost:8080/api/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        });

        if (response.ok) {
            alert("Account created successfully!");
            registerForm.reset();
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
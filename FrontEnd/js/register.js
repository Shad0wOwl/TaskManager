const registerForm = document.getElementById("register-form");
const passwordInput = document.getElementById("password");
const passwordError = document.getElementById("password-error");
const confirmPasswordInput = document.getElementById("con-password");



registerForm.addEventListener("submit", function(event) {
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
});



confirmPasswordInput.addEventListener("input", function() {
    passwordError.textContent = "";
});
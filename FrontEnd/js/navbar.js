const guestLinks = document.querySelectorAll(".guest-only");
const userLinks = document.querySelectorAll(".user-only");
const userName = document.getElementById("userName");
const userManuBtn = document.getElementById("userMenuBtn");
const userDropdown = document.getElementById("userDropdown");
const navLogoutBtn = document.getElementById("navLogoutBtn");
const heroGuestActions = document.querySelector(".hero-guest-actions");
const heroUserActions = document.querySelector(".hero-user-actions");

async function checkUserSession() {
    try {
        const response = await fetch(
            "http://127.0.0.1:8080/api/users/me",
            {
                method: "GET",
                credentials: "include"
            }
        );

        if (!response.ok) {
            return;
        }

        const user = await response.json();

        guestLinks.forEach(link => {
            link.hidden = true;
        });

        userLinks.forEach(link => {
            link.hidden = false;
        });

        userName.textContent = user.name;

        heroGuestActions.hidden = true;
        heroUserActions.hidden = false;

    } catch (error) {
        console.error("Could not check user session:", error);
    } 
}

checkUserSession();

userMenuBtn.addEventListener("click", () => {
    userDropdown.hidden = !userDropdown.hidden;

    userMenuBtn.setAttribute(
        "aria-expanded",
        String(!userDropdown.hidden)
    );
});

navLogoutBtn.addEventListener("click", async () => {
    try {
        const csrfResponse = await fetch(
            "http://127.0.0.1:8080/api/auth/csrf",
            {
                credentials: "include"
            }
        );

        if (!csrfResponse.ok) {
            throw new Error("Failed to fetch CSRF token");
        }

        const csrf = await csrfResponse.json();

        const response = await fetch(
            "http://127.0.0.1:8080/api/auth/logout",
            {
                method: "POST",
                credentials: "include",
                headers: {
                    [csrf.headerName]: csrf.token
                }
            }
        );

        if (!response.ok) {
            throw new Error("Logout failed");
        }

        userLinks.forEach(link => {
            link.hidden = true;
        });

        guestLinks.forEach(link => {
            link.hidden = false;
        });

        heroGuestActions.hidden = false;
        heroUserActions.hidden = true;

        userDropdown.hidden = true;
        userMenuBtn.setAttribute("aria-expanded", "false");

    } catch (error) {
        console.error("Logout error:", error);
        alert("Could not log out");
    }
});

document.addEventListener("click", (event) => {
    if (!event.target.closest(".user-menu")) {
        userDropdown.hidden = true;
        userMenuBtn.setAttribute("aria-expanded", "false");
    }
});
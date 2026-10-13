const taskList = document.getElementById("taskList");
const createTaskForm = document.getElementById("createTaskForm");
const taskTitle = document.getElementById("taskTitle");
const taskDescription = document.getElementById("taskDescription");
const logoutBtn = document.getElementById("logoutBtn");

async function loadTasks() {
    try {
        const response = await fetch("http://127.0.0.1:8080/api/tasks", {
            method: "GET",
            credentials: "include"
        });

        if (response.status === 403 || response.status === 401) {
            window.location.href = "login.html";
            return;
        }

        if (!response.ok) {
            throw new Error("Failed to load tasks");
        }

        const tasks = await response.json();

        console.log("Loaded tasks:", tasks);

        taskList.innerHTML = "";

        if (tasks.length === 0) {
            taskList.textContent = "You don't have any tasks yet.";
            return;
        }

        tasks.forEach(task => {
            const taskElement = document.createElement("div");
            taskElement.classList.add("task-item");
            if (task.completed) {
                taskElement.classList.add("completed")
            }

            const title = document.createElement("h3");
            title.textContent = task.title;

            const checkbox = document.createElement("input");
            checkbox.type = "checkbox";
            checkbox.checked = task.completed;
            checkbox.classList.add("task-checkbox");

            checkbox.addEventListener("change", async () => {
                checkbox.disabled = true;

                try {
                    const csrfResponse = await fetch("http://127.0.0.1:8080/api/auth/csrf", {
                        credentials: "include"
                    });

                    if (!csrfResponse.ok) {
                        throw new Error("Failed to fetch CSRF token");
                    }

                    const csrf = await csrfResponse.json();

                    const response = await fetch(
                        `http://127.0.0.1:8080/api/tasks/${task.id}/status`,
                        {
                            method: "PATCH",
                            credentials: "include",
                            headers: {
                                "Content-Type": "application/json",
                                [csrf.headerName]: csrf.token
                            },
                            body: JSON.stringify({
                                completed: checkbox.checked
                            })
                        }
                    );

                    if (!response.ok) {
                        throw new Error("Failed to update task status");
                    }

                    taskElement.classList.toggle("completed", checkbox.checked);

                } catch (error) {
                    console.error("Error updating tasks:", error);
                    checkbox.checked = !checkbox.checked;
                    alert("Could not update task.");
                } finally {
                    checkbox.disabled = false
                }
            });

            const description = document.createElement("p");
            description.textContent = task.description ?? "";

            const taskContent = document.createElement("div");
            taskContent.classList.add("task-content");

            taskContent.append(title, description);
            taskElement.append(checkbox, taskContent);

            taskList.appendChild(taskElement);
        });

    } catch (error) {
        console.error("Error loading tasks:", error);
        taskList.textContent = "Could not load tasks.";
    }
}
loadTasks();

createTaskForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const title = taskTitle.value.trim();
    const description = taskDescription.value.trim();

    if (!title) {
        alert("Task title cannot be empty.");
        return;
    }

    try {
        const csrfResponse = await fetch("http://127.0.0.1:8080/api/auth/csrf", {
            credentials: "include"
        });

        if(!csrfResponse.ok) {
            throw new Error("Failed to fetch CSRF token");
        }

        const csrf = await csrfResponse.json();

        const response = await fetch("http://127.0.0.1:8080/api/tasks", {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
                [csrf.headerName]: csrf.token
            },
            body: JSON.stringify({
                title: title,
                description: description
            })
        });

        if (!response.ok) {
            throw new Error("Failed to create task");
        }

        createTaskForm.reset();
        await loadTasks();
    } catch (error) {
        console.error("Error creating task:", error);
        alert("Could not create task.");
    }
});

logoutBtn.addEventListener("click", async () => {
    try {
        const csrfResponse = await fetch("http://127.0.0.1:8080/api/auth/csrf", {
            credentials: "include"
        });

        if (!csrfResponse.ok) {
            throw new Error("Failed to fetch CSRF token");
        }

        const csrf = await csrfResponse.json();

        const response = await fetch("http://127.0.0.1:8080/api/auth/logout", {
            method: "POST",
            credentials: "include",
            headers: {
                [csrf.headerName]: csrf.token
            }
        });

        if (!response.ok) {
            throw new Error("Logout failed");
        }

        window.location.href = "login.html";
    } catch (error) {
        console.error("Logout error:", error);
        alert("Could not log out.");
    }
});
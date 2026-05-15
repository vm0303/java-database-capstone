function renderHeader() {
    const headerDiv = document.getElementById("header");

    if (!headerDiv) {
        return;
    }

    if (window.location.pathname.endsWith("/")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");

        headerDiv.innerHTML = `
            <header class="header">
                <div class="logo-section">
                    <img src="../assets/images/logo/logo.png" alt="Smart Clinic Logo" class="logo-img">
                    <span class="logo-title">Smart Clinic</span>
                </div>
            </header>
        `;
        return;
    }

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    let headerContent = `
        <header class="header">
            <div class="logo-section">
                <img src="../assets/images/logo/logo.png" alt="Smart Clinic Logo" class="logo-img">
                <span class="logo-title">Smart Clinic</span>
            </div>
            <nav>
    `;

    if (role === "admin") {
        headerContent += `
            <button id="addDocBtn" class="adminBtn">Add Doctor</button>
            <a href="#" id="logoutBtn">Logout</a>
        `;
    } else if (role === "doctor") {
        headerContent += `
            <button id="doctorHomeBtn" class="adminBtn">Home</button>
            <a href="#" id="logoutBtn">Logout</a>
        `;
    } else if (role === "patient") {
        headerContent += `
            <button id="patientLogin" class="adminBtn">Login</button>
            <button id="patientSignup" class="adminBtn">Sign Up</button>
        `;
    } else if (role === "loggedPatient") {
        headerContent += `
            <button id="patientHomeBtn" class="adminBtn">Home</button>
            <button id="patientAppointments" class="adminBtn">Appointments</button>
            <a href="#" id="logoutPatientBtn">Logout</a>
        `;
    }

    headerContent += `
            </nav>
        </header>
    `;

    headerDiv.innerHTML = headerContent;
    attachHeaderButtonListeners();
}

function attachHeaderButtonListeners() {
    const addDocBtn = document.getElementById("addDocBtn");
    const logoutBtn = document.getElementById("logoutBtn");
    const logoutPatientBtn = document.getElementById("logoutPatientBtn");
    const doctorHomeBtn = document.getElementById("doctorHomeBtn");
    const patientLogin = document.getElementById("patientLogin");
    const patientSignup = document.getElementById("patientSignup");
    const patientHomeBtn = document.getElementById("patientHomeBtn");
    const patientAppointments = document.getElementById("patientAppointments");

    if (addDocBtn) {
        addDocBtn.addEventListener("click", function () {
            openModal("addDoctor");
        });
    }

    if (logoutBtn) {
        logoutBtn.addEventListener("click", logout);
    }

    if (logoutPatientBtn) {
        logoutPatientBtn.addEventListener("click", logoutPatient);
    }

    if (doctorHomeBtn) {
        doctorHomeBtn.addEventListener("click", function () {
            window.location.href = "/pages/doctorDashboard.html";
        });
    }

    if (patientLogin) {
        patientLogin.addEventListener("click", function () {
            openModal("patientLogin");
        });
    }

    if (patientSignup) {
        patientSignup.addEventListener("click", function () {
            openModal("patientSignup");
        });
    }

    if (patientHomeBtn) {
        patientHomeBtn.addEventListener("click", function () {
            window.location.href = "/pages/loggedPatientDashboard.html";
        });
    }

    if (patientAppointments) {
        patientAppointments.addEventListener("click", function () {
            window.location.href = "/pages/patientAppointments.html";
        });
    }
}

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
}

renderHeader();
// patientDashboard.js

import { createDoctorCard } from "../components/doctorCard.js";
import { openModal } from "../components/modals.js";
import { getDoctors, filterDoctors } from "../services/doctorServices.js";
import { patientSignup, patientLogin } from "../services/patientServices.js";

document.addEventListener("DOMContentLoaded", function () {
    loadDoctorCards();

    const signupBtn = document.getElementById("patientSignup");
    const loginBtn = document.getElementById("patientLogin");
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    if (signupBtn) {
        signupBtn.addEventListener("click", function () {
            openModal("patientSignup");
        });
    }

    if (loginBtn) {
        loginBtn.addEventListener("click", function () {
            openModal("patientLogin");
        });
    }

    if (searchBar) {
        searchBar.addEventListener("input", filterDoctorsOnChange);
    }

    if (filterTime) {
        filterTime.addEventListener("change", filterDoctorsOnChange);
    }

    if (filterSpecialty) {
        filterSpecialty.addEventListener("change", filterDoctorsOnChange);
    }
});

async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Failed to load doctors:", error);
        showMessage("Failed to load doctors.");
    }
}

async function filterDoctorsOnChange() {
    const searchBar = document.getElementById("searchBar")?.value.trim() || "";
    const filterTime = document.getElementById("filterTime")?.value || "";
    const filterSpecialty = document.getElementById("filterSpecialty")?.value || "";

    const name = searchBar || "";
    const time = filterTime || "";
    const specialty = filterSpecialty || "";

    try {
        const response = await filterDoctors(name, time, specialty);
        const doctors = response.doctors || response || [];

        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Failed to filter doctors:", error);
        alert("An error occurred while filtering doctors.");
    }
}

function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");

    if (!contentDiv) {
        return;
    }

    contentDiv.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
        return;
    }

    doctors.forEach(function (doctor) {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

function showMessage(message) {
    const contentDiv = document.getElementById("content");

    if (contentDiv) {
        contentDiv.innerHTML = `<p>${message}</p>`;
    }
}

window.signupPatient = async function () {
    try {
        const name = document.getElementById("name")?.value;
        const email = document.getElementById("email")?.value;
        const password = document.getElementById("password")?.value;
        const phone = document.getElementById("phone")?.value;
        const address = document.getElementById("address")?.value;

        const patient = {
            name,
            email,
            password,
            phone,
            address
        };

        const result = await patientSignup(patient);

        if (result.success) {
            alert(result.message || "Signup successful.");
            document.getElementById("modal").style.display = "none";
            window.location.reload();
        } else {
            alert(result.message || "Signup failed.");
        }
    } catch (error) {
        console.error("Signup failed:", error);
        alert("An error occurred while signing up.");
    }
};

window.loginPatient = async function () {
    try {
        const email = document.getElementById("email")?.value;
        const password = document.getElementById("password")?.value;

        const patient = {
            email,
            password
        };

        const response = await patientLogin(patient);

        if (response.ok) {
            const result = await response.json();

            selectRole("loggedPatient");
            localStorage.setItem("token", result.token);

            window.location.href = "/pages/loggedPatientDashboard.html";
        } else {
            alert("Invalid credentials!");
        }
    } catch (error) {
        console.error("Login failed:", error);
        alert("Failed to login.");
    }
};
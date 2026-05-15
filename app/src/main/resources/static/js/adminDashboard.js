import { openModal } from "../components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "../services/doctorServices.js";
import { createDoctorCard } from "../components/doctorCard.js";

document.addEventListener("DOMContentLoaded", function () {
    const addDocBtn = document.getElementById("addDocBtn");
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    if (addDocBtn) {
        addDocBtn.addEventListener("click", function () {
            openModal("addDoctor");
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

    loadDoctorCards();
});

async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error loading doctors:", error);
        alert("Could not load doctors.");
    }
}

async function filterDoctorsOnChange() {
    const name = document.getElementById("searchBar")?.value || "";
    const time = document.getElementById("filterTime")?.value || "";
    const specialty = document.getElementById("filterSpecialty")?.value || "";

    try {
        const doctors = await filterDoctors(name, time, specialty);
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error filtering doctors:", error);
        alert("Could not filter doctors.");
    }
}

function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");

    if (!contentDiv) {
        return;
    }

    contentDiv.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found.</p>";
        return;
    }

    doctors.forEach(function (doctor) {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

window.adminAddDoctor = async function () {
    const token = localStorage.getItem("token");

    if (!token) {
        alert("Admin session expired. Please log in again.");
        window.location.href = "/";
        return;
    }

    const name = document.getElementById("doctorName")?.value;
    const specialty = document.getElementById("doctorSpecialty")?.value;
    const email = document.getElementById("doctorEmail")?.value;
    const password = document.getElementById("doctorPassword")?.value;
    const phone = document.getElementById("doctorPhone")?.value;

    const checkedTimes = document.querySelectorAll("input[name='availableTimes']:checked");
    const availableTimes = Array.from(checkedTimes).map(function (checkbox) {
        return checkbox.value;
    });

    const doctor = {
        name,
        specialty,
        email,
        password,
        phone,
        availableTimes
    };

    try {
        const result = await saveDoctor(doctor, token);

        if (result.success) {
            alert(result.message || "Doctor added successfully.");
            const modal = document.getElementById("modal");
            if (modal) {
                modal.classList.remove("show");
                modal.classList.add("hidden");
            }
            loadDoctorCards();
        } else {
            alert(result.message || "Could not add doctor.");
        }
    } catch (error) {
        console.error("Error adding doctor:", error);
        alert("Something went wrong while adding the doctor.");
    }
};
import { showBookingOverlay } from "../services/loggedPatient.js";
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";

export function createDoctorCard(doctor) {
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    const role = localStorage.getItem("userRole");

    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name;

    const specialization = document.createElement("p");
    specialization.textContent = `Specialty: ${doctor.specialty}`;

    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email}`;

    const availability = document.createElement("p");
    availability.textContent = `Available Times: ${
        doctor.availableTimes && doctor.availableTimes.length > 0
            ? doctor.availableTimes.join(", ")
            : "No available times listed"
    }`;

    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    if (role === "admin") {
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";

        removeBtn.addEventListener("click", async () => {
            const confirmDelete = confirm(`Delete Dr. ${doctor.name}?`);

            if (!confirmDelete) {
                return;
            }

            const token = localStorage.getItem("token");

            if (!token) {
                alert("Session expired. Please log in again.");
                window.location.href = "/";
                return;
            }

            try {
                await deleteDoctor(doctor.id, token);
                alert("Doctor deleted successfully.");
                card.remove();
            } catch (error) {
                console.error("Delete doctor failed:", error);
                alert("Failed to delete doctor.");
            }
        });

        actionsDiv.appendChild(removeBtn);
    } else if (role === "patient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";

        bookNow.addEventListener("click", () => {
            alert("Patient needs to login first.");
        });

        actionsDiv.appendChild(bookNow);
    } else if (role === "loggedPatient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";

        bookNow.addEventListener("click", async (event) => {
            const token = localStorage.getItem("token");

            if (!token) {
                alert("Please log in first.");
                window.location.href = "/";
                return;
            }

            try {
                const patientData = await getPatientData(token);
                showBookingOverlay(event, doctor, patientData);
            } catch (error) {
                console.error("Unable to load patient data:", error);
                alert("Could not load patient information.");
            }
        });

        actionsDiv.appendChild(bookNow);
    }

    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;
}
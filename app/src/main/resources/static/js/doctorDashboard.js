import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";
let tableBody;
let selectedDate = new Date().toISOString().split("T")[0];
let token = localStorage.getItem("token");
let patientName = "null";

document.addEventListener("DOMContentLoaded", function () {
    if (typeof renderContent === "function") {
        renderContent();
    }

    tableBody = document.getElementById("patientTableBody");

    const searchBar = document.getElementById("searchBar");
    const todayButton = document.getElementById("todayButton");
    const datePicker = document.getElementById("datePicker");

    if (datePicker) {
        datePicker.value = selectedDate;
    }

    if (searchBar) {
        searchBar.addEventListener("input", function () {
            const value = searchBar.value.trim();
            patientName = value === "" ? "null" : value;
            loadAppointments();
        });
    }

    if (todayButton) {
        todayButton.addEventListener("click", function () {
            selectedDate = new Date().toISOString().split("T")[0];

            if (datePicker) {
                datePicker.value = selectedDate;
            }

            loadAppointments();
        });
    }

    if (datePicker) {
        datePicker.addEventListener("change", function () {
            selectedDate = datePicker.value;
            loadAppointments();
        });
    }

    loadAppointments();
});

async function loadAppointments() {
    if (!tableBody) {
        return;
    }

    tableBody.innerHTML = "";

    try {
        token = localStorage.getItem("token");

        if (!token) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5" class="noPatientRecord">
                        Session expired. Please log in again.
                    </td>
                </tr>
            `;
            return;
        }

        const appointments = await getAllAppointments(selectedDate, patientName, token);

        if (!appointments || appointments.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5" class="noPatientRecord">
                        No appointments found for this date.
                    </td>
                </tr>
            `;
            return;
        }

        appointments.forEach(function (appointment) {
            const patient = appointment.patient || {
                id: appointment.patientId,
                name: appointment.patientName,
                phone: appointment.patientPhone,
                email: appointment.patientEmail
            };

            const row = createPatientRow(patient, appointment.id, appointment.doctorId);
            tableBody.appendChild(row);
        });

    } catch (error) {
        console.error("Error loading appointments:", error);

        tableBody.innerHTML = `
            <tr>
                <td colspan="5" class="noPatientRecord">
                    Error loading appointments. Try again later.
                </td>
            </tr>
        `;
    }
}
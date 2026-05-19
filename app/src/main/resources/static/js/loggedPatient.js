// loggedPatient.js

import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';
import { bookAppointment } from './services/appointmentRecordService.js';

document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

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

function loadDoctorCards() {
  getDoctors()
    .then(doctors => {
      renderDoctorCards(doctors);
    })
    .catch(error => {
      console.error("Failed to load doctors:", error);
    });
}

export function showBookingOverlay(e, doctor, patient) {
  const button = e.target;
  const rect = button.getBoundingClientRect();

  console.log(patient.name);
  console.log(patient);

  const ripple = document.createElement("div");
  ripple.classList.add("ripple-overlay");
  ripple.style.left = `${e.clientX}px`;
  ripple.style.top = `${e.clientY}px`;

  document.body.appendChild(ripple);

  setTimeout(() => ripple.classList.add("active"), 50);

  const modalApp = document.createElement("div");
  modalApp.classList.add("modalApp");

  modalApp.innerHTML = `
    <h2>Book Appointment</h2>

    <input class="input-field" type="text" value="${patient.name}" disabled />
    <input class="input-field" type="text" value="${doctor.name}" disabled />
    <input class="input-field" type="text" value="${doctor.specialty}" disabled />
    <input class="input-field" type="email" value="${doctor.email}" disabled />

    <input class="input-field" type="date" id="appointment-date" />

    <select class="input-field" id="appointment-time">
      <option value="">Select time</option>
      ${doctor.availableTimes.map(t =>
        `<option value="${t}">${t}</option>`
      ).join('')}
    </select>

    <button class="confirm-booking">Confirm Booking</button>
  `;

  document.body.appendChild(modalApp);

  setTimeout(() => modalApp.classList.add("active"), 600);

  modalApp.querySelector(".confirm-booking").addEventListener("click", async () => {

    const date = modalApp.querySelector("#appointment-date").value;
    const time = modalApp.querySelector("#appointment-time").value;

    const token = localStorage.getItem("token");

    if (!date || !time) {
      alert("Please select date and time.");
      return;
    }

    const startTime = time.split("-")[0].trim();

    const appointment = {
      doctor: {
        id: doctor.id
      },

      patient: {
        id: patient.id
      },

      appointmentTime: `${date}T${startTime}:00`,
      status: 0
    };

    try {

      const { success, message } =
        await bookAppointment(appointment, token);

      if (success) {
        alert("Appointment booked successfully.");

        ripple.remove();
        modalApp.remove();

      } else {
        alert("❌ Failed to book appointment: " + message);
      }

    } catch (error) {
      console.error("Booking error:", error);
      alert("❌ Something went wrong while booking.");
    }
  });
}

function filterDoctorsOnChange() {

  const name =
    document.getElementById("searchBar")?.value.trim() || "null";

  const time =
    document.getElementById("filterTime")?.value || "null";

  const specialty =
    document.getElementById("filterSpecialty")?.value || "null";

  filterDoctors(name, time, specialty)

    .then(doctors => {

      if (doctors.doctors) {
        renderDoctorCards(doctors.doctors);
      } else {
        renderDoctorCards(doctors);
      }

    })

    .catch(error => {
      console.error("Failed to filter doctors:", error);
      alert("❌ An error occurred while filtering doctors.");
    });
}

export function renderDoctorCards(doctors) {

  const contentDiv = document.getElementById("content");

  if (!contentDiv) {
    return;
  }

  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML =
      "<p>No doctors found with the given filters.</p>";
    return;
  }

  doctors.forEach(doctor => {

    const card = createDoctorCard(doctor);

    contentDiv.appendChild(card);

  });
}
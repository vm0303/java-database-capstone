import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + "/doctor";

export async function getDoctors() {
    try {
        const response = await fetch(DOCTOR_API);

        if (!response.ok) {
            throw new Error("Failed to fetch doctors");
        }

        const data = await response.json();

        return data.doctors || data || [];
    } catch (error) {
        console.error("Error fetching doctors:", error);
        return [];
    }
}

export async function deleteDoctor(id, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
            method: "DELETE"
        });

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message || "Doctor delete request completed."
        };
    } catch (error) {
        console.error("Error deleting doctor:", error);

        return {
            success: false,
            message: "Could not delete doctor."
        };
    }
}

export async function saveDoctor(doctor, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${token}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(doctor)
        });

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message || "Doctor save request completed."
        };
    } catch (error) {
        console.error("Error saving doctor:", error);

        return {
            success: false,
            message: "Could not save doctor."
        };
    }
}

export async function filterDoctors(name = "", time = "", specialty = "") {
    try {
        const encodedName = encodeURIComponent(name || "null");
        const encodedTime = encodeURIComponent(time || "null");
        const encodedSpecialty = encodeURIComponent(specialty || "null");

        const response = await fetch(
            `${DOCTOR_API}/filter/${encodedName}/${encodedTime}/${encodedSpecialty}`
        );

        if (!response.ok) {
            console.error("Failed to filter doctors");
            return [];
        }

        const data = await response.json();

        return data.doctors || data || [];
    } catch (error) {
        console.error("Error filtering doctors:", error);
        alert("Could not filter doctors.");
        return [];
    }
}
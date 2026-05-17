package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(
            TokenService tokenService,
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorService doctorService,
            PatientService patientService
    ) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();

        boolean valid = tokenService.validateToken(token, user);

        if (!valid) {
            response.put("message", "Invalid or expired token.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        response.put("message", "Token is valid.");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();

        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

            if (admin == null || !admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("message", "Invalid admin credentials.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(admin.getId(), "admin");

            response.put("message", "Admin login successful.");
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "An error occurred during admin login.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        boolean hasName = name != null && !name.isBlank() && !name.equalsIgnoreCase("null");
        boolean hasSpecialty = specialty != null && !specialty.isBlank() && !specialty.equalsIgnoreCase("null");
        boolean hasTime = time != null && !time.isBlank() && !time.equalsIgnoreCase("null");

        if (hasName && hasSpecialty && hasTime) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        }

        if (hasName && hasSpecialty) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        }

        if (hasName && hasTime) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        }

        if (hasSpecialty && hasTime) {
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        }

        if (hasName) {
            return doctorService.findDoctorByName(name);
        }

        if (hasSpecialty) {
            return doctorService.filterDoctorBySpecility(specialty);
        }

        if (hasTime) {
            return doctorService.filterDoctorsByTime(time);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());
        return response;
    }

    public int validateAppointment(Appointment appointment) {
        if (appointment == null || appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            return -1;
        }

        Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId()).orElse(null);

        if (doctor == null) {
            return -1;
        }

        if (appointment.getAppointmentTime() == null) {
            return 0;
        }

        LocalDate appointmentDate = appointment.getAppointmentTime().toLocalDate();
        LocalTime requestedTime = appointment.getAppointmentTime().toLocalTime();

        List<String> availableSlots = doctorService.getDoctorAvailability(doctor.getId(), appointmentDate);

        for (String slot : availableSlots) {
            if (slot.startsWith(requestedTime.toString())) {
                return 1;
            }
        }

        return 0;
    }

    public boolean validatePatient(Patient patient) {
        Patient existingPatient = patientRepository.findByEmailOrPhone(
                patient.getEmail(),
                patient.getPhone()
        );

        return existingPatient == null;
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();

        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());

            if (patient == null || !patient.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid patient credentials.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(patient.getId(), "patient");

            response.put("message", "Patient login successful.");
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "An error occurred during patient login.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(
            String condition,
            String name,
            String token
    ) {
        Long patientId = tokenService.getIdFromToken(token);

        boolean hasCondition = condition != null && !condition.isBlank() && !condition.equalsIgnoreCase("null");
        boolean hasName = name != null && !name.isBlank() && !name.equalsIgnoreCase("null");

        if (hasCondition && hasName) {
            return patientService.filterByDoctorAndCondition(condition, name, patientId);
        }

        if (hasCondition) {
            return patientService.filterByCondition(condition, patientId);
        }

        if (hasName) {
            return patientService.filterByDoctor(name, patientId);
        }

        return patientService.getPatientAppointment(patientId, token);
    }
}
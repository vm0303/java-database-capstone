package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PatientService {
    @Autowired
    private final PatientRepository patientRepository;
    @Autowired
    private final AppointmentRepository appointmentRepository;
    @Autowired
    private final TokenService tokenService;

    public PatientService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService
    ) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();

        String email = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null || !patient.getId().equals(id)) {
            response.put("message", "Unauthorized access.");
            return ResponseEntity.status(401).body(response);
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(id);
        response.put("appointments", convertToDTOList(appointments));

        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();

        int status;

        if (condition.equalsIgnoreCase("future")) {
            status = 0;
        } else if (condition.equalsIgnoreCase("past")) {
            status = 1;
        } else {
            response.put("message", "Invalid condition. Use 'past' or 'future'.");
            return ResponseEntity.badRequest().body(response);
        }

        List<Appointment> appointments =
                appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status);

        response.put("appointments", convertToDTOList(appointments));
        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();

        List<Appointment> appointments =
                appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);

        response.put("appointments", convertToDTOList(appointments));
        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(
            String condition,
            String name,
            long patientId
    ) {
        Map<String, Object> response = new HashMap<>();

        int status;

        if (condition.equalsIgnoreCase("future")) {
            status = 0;
        } else if (condition.equalsIgnoreCase("past")) {
            status = 1;
        } else {
            response.put("message", "Invalid condition. Use 'past' or 'future'.");
            return ResponseEntity.badRequest().body(response);
        }

        List<Appointment> appointments =
                appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);

        response.put("appointments", convertToDTOList(appointments));
        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();

        String email = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) {
            response.put("message", "Patient not found.");
            return ResponseEntity.status(404).body(response);
        }

        response.put("id", patient.getId());
        response.put("name", patient.getName());
        response.put("email", patient.getEmail());
        response.put("phone", patient.getPhone());
        response.put("address", patient.getAddress());

        return ResponseEntity.ok(response);
    }

    private List<AppointmentDTO> convertToDTOList(List<Appointment> appointments) {
        List<AppointmentDTO> appointmentDTOs = new ArrayList<>();

        for (Appointment appointment : appointments) {
            appointmentDTOs.add(convertToDTO(appointment));
        }

        return appointmentDTOs;
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        return new AppointmentDTO(
                appointment.getId(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getPatient().getEmail(),
                appointment.getPatient().getPhone(),
                appointment.getPatient().getAddress(),
                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }
}
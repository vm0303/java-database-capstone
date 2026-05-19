package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final PatientRepository patientRepository;

    private final DoctorRepository doctorRepository;

    private final TokenService tokenService;

    private final com.project.back_end.services.Service service;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            TokenService tokenService,
            com.project.back_end.services.Service service) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.service = service;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {

        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existingAppointment =
                appointmentRepository.findById(appointment.getId());

        if (existingAppointment.isEmpty()) {
            response.put("message", "Appointment not found.");
            return ResponseEntity.badRequest().body(response);
        }

        int validation = service.validateAppointment(appointment);

        if (validation == -1) {
            response.put("message", "Doctor not found.");
            return ResponseEntity.badRequest().body(response);
        }

        if (validation == 0) {
            response.put("message", "Appointment time is not available.");
            return ResponseEntity.badRequest().body(response);
        }

        appointmentRepository.save(appointment);

        response.put("message", "Appointment updated successfully.");

        return ResponseEntity.ok(response);
    }

    @Transactional
public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
    Map<String, String> response = new HashMap<>();

    Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);

    if (appointmentOptional.isEmpty()) {
        response.put("message", "Appointment not found.");
        return ResponseEntity.badRequest().body(response);
    }

    Appointment appointment = appointmentOptional.get();

    Long patientId = tokenService.getIdFromToken(token);

    if (!appointment.getPatient().getId().equals(patientId)) {
        response.put("message", "You are not allowed to cancel this appointment.");
        return ResponseEntity.status(403).body(response);
    }

    appointmentRepository.delete(appointment);

    response.put("message", "Appointment cancelled successfully.");
    return ResponseEntity.ok(response);
}

    @Transactional
    public String changeStatus(Long appointmentId, int status) {

        Optional<Appointment> appointmentOptional =
                appointmentRepository.findById(appointmentId);

        if (appointmentOptional.isEmpty()) {
            return "Appointment not found.";
        }

        Appointment appointment = appointmentOptional.get();

        appointment.setStatus(status);

        appointmentRepository.save(appointment);

        return "Appointment status updated successfully.";
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();

        Long doctorId = tokenService.getIdFromToken(token);

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments;

        if (pname == null || pname.isBlank() || pname.equalsIgnoreCase("null")) {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                    doctorId,
                    start,
                    end);
        } else {
            appointments = appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                            doctorId,
                            pname,
                            start,
                            end);
        }

        List<AppointmentDTO> appointmentDTOs = new ArrayList<>();

        for (Appointment appointment : appointments) {
            appointmentDTOs.add(convertToDTO(appointment));
        }

        response.put("appointments", appointmentDTOs);
        return response;
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
                appointment.getStatus());
    }
}
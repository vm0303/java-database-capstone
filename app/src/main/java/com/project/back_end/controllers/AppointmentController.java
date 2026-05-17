package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    public AppointmentController(
            AppointmentService appointmentService,
            Service service
    ) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable LocalDate date,
            @PathVariable String patientName,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "doctor");

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", tokenResponse.getBody().get("message"));
            return ResponseEntity.status(tokenResponse.getStatusCode()).body(response);
        }

        Map<String, Object> appointments =
                appointmentService.getAppointment(patientName, date, token);

        return ResponseEntity.ok(appointments);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @PathVariable String token,
            @Valid @RequestBody Appointment appointment
    ) {
        ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "patient");

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            return tokenResponse;
        }

        int validation = service.validateAppointment(appointment);
        Map<String, String> response = new HashMap<>();

        if (validation == -1) {
            response.put("message", "Doctor not found.");
            return ResponseEntity.badRequest().body(response);
        }

        if (validation == 0) {
            response.put("message", "Appointment time is not available.");
            return ResponseEntity.badRequest().body(response);
        }

        int result = appointmentService.bookAppointment(appointment);

        if (result == 1) {
            response.put("message", "Appointment booked successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        response.put("message", "Could not book appointment.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @PathVariable String token,
            @Valid @RequestBody Appointment appointment
    ) {
        ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "patient");

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            return tokenResponse;
        }

        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable long id,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "patient");

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            return tokenResponse;
        }

        return appointmentService.cancelAppointment(id, token);
    }
}
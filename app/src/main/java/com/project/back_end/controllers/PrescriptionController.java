package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;
    private final AppointmentService appointmentService;

    public PrescriptionController(
            PrescriptionService prescriptionService,
            Service service,
            AppointmentService appointmentService
    ) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @Valid @RequestBody Prescription prescription
    ) {
        ResponseEntity<Map<String, String>> tokenResponse =
                service.validateToken(token, "doctor");

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            return tokenResponse;
        }

        ResponseEntity<Map<String, String>> saveResponse =
                prescriptionService.savePrescription(prescription);

        if (saveResponse.getStatusCode().is2xxSuccessful()) {
            appointmentService.changeStatus(
                    prescription.getAppointmentId(),
                    1
            );
        }

        return saveResponse;
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenResponse =
                service.validateToken(token, "doctor");

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", tokenResponse.getBody().get("message"));

            return ResponseEntity
                    .status(tokenResponse.getStatusCode())
                    .body(response);
        }

        return prescriptionService.getPrescription(appointmentId);
    }
}
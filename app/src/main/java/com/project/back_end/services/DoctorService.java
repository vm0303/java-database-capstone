package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService
    ) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOptional = doctorRepository.findById(doctorId);

        if (doctorOptional.isEmpty()) {
            return new ArrayList<>();
        }

        Doctor doctor = doctorOptional.get();

        if (doctor.getAvailableTimes() == null) {
            return new ArrayList<>();
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> bookedAppointments =
                appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        List<String> availableTimes = new ArrayList<>(doctor.getAvailableTimes());

        for (Appointment appointment : bookedAppointments) {
            String bookedTime = appointment.getAppointmentTime().toLocalTime().toString();

            availableTimes.removeIf(slot -> slot.startsWith(bookedTime));
        }

        return availableTimes;
    }

    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            Doctor existingDoctor = doctorRepository.findByEmail(doctor.getEmail());

            if (existingDoctor != null) {
                return -1;
            }

            doctorRepository.save(doctor);
            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existingDoctor = doctorRepository.findById(doctor.getId());

            if (existingDoctor.isEmpty()) {
                return -1;
            }

            doctorRepository.save(doctor);
            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> doctor = doctorRepository.findById(id);

            if (doctor.isEmpty()) {
                return -1;
            }

            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);

            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();

        Doctor doctor = doctorRepository.findByEmail(login.getEmail());

        if (doctor == null) {
            response.put("message", "Invalid email.");
            return ResponseEntity.badRequest().body(response);
        }

        if (!doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid password.");
            return ResponseEntity.badRequest().body(response);
        }

        String token = tokenService.generateToken(doctor.getId(), "doctor");

        response.put("message", "Doctor login successful.");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findDoctorByName(String name) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        return doctorMap(doctors);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(
            String name,
            String specialty,
            String amOrPm
    ) {
        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);

        return doctorMap(filterDoctorByTime(doctors, amOrPm));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);

        return doctorMap(filterDoctorByTime(doctors, amOrPm));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specilty) {
        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specilty);

        return doctorMap(doctors);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specilty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);

        return doctorMap(filterDoctorByTime(doctors, amOrPm));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorBySpecility(String specilty) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);

        return doctorMap(doctors);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();

        return doctorMap(filterDoctorByTime(doctors, amOrPm));
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null || amOrPm.isBlank() || amOrPm.equalsIgnoreCase("null")) {
            return doctors;
        }

        List<Doctor> filteredDoctors = new ArrayList<>();

        for (Doctor doctor : doctors) {
            if (doctor.getAvailableTimes() == null) {
                continue;
            }

            for (String slot : doctor.getAvailableTimes()) {
                if (slot.toUpperCase().contains(amOrPm.toUpperCase())) {
                    filteredDoctors.add(doctor);
                    break;
                }
            }
        }

        return filteredDoctors;
    }

    private Map<String, Object> doctorMap(List<Doctor> doctors) {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }
}
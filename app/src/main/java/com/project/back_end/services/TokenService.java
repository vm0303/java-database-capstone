package com.project.back_end.services;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public TokenService(
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository
    ) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String identifier) {
        long sevenDays = 1000L * 60 * 60 * 24 * 7;

        return Jwts.builder()
                .subject(identifier)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + sevenDays))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractIdentifier(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token, String user) {
        try {
            String identifier = extractIdentifier(token);

            if (user.equalsIgnoreCase("admin")) {
                return adminRepository.findByUsername(identifier) != null;
            }

            if (user.equalsIgnoreCase("doctor")) {
                return doctorRepository.findByEmail(identifier) != null;
            }

            if (user.equalsIgnoreCase("patient")) {
                return patientRepository.findByEmail(identifier) != null;
            }

            return false;

        } catch (Exception e) {
            return false;
        }
    }
}
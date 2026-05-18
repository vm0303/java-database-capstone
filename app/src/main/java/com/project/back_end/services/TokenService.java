package com.project.back_end.services;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.Claims;
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

    @Value("${jwt.expiration:604800000}")
    private long jwtExpiration;

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

    public String generateToken(Long id, String role) {
        return Jwts.builder()
                .subject(String.valueOf(id))
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Long getIdFromToken(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    public boolean validateToken(String token, String user) {
        try {
            Claims claims = getClaims(token);

            String role = claims.get("role", String.class);
            Long id = Long.parseLong(claims.getSubject());

            if (role == null || !role.equalsIgnoreCase(user)) {
                return false;
            }

            if (user.equalsIgnoreCase("admin")) {
                return adminRepository.existsById(id);
            }

            if (user.equalsIgnoreCase("doctor")) {
                return doctorRepository.existsById(id);
            }

            if (user.equalsIgnoreCase("patient")) {
                return patientRepository.existsById(id);
            }

            return false;

        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
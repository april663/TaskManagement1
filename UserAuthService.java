package com.TaskManagement1.Service;

import com.TaskManagement1.DTO.*;
import com.TaskManagement1.Email.EmailService;
import com.TaskManagement1.Entity.UserAuth;
import com.TaskManagement1.Repository.UserAuthRepository;
import com.TaskManagement1.Security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class UserAuthService {

    private final UserAuthRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public UserAuthService(UserAuthRepository userRepo,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService,
                           JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    // ================= REGISTER =================
    public String register(RegisterRequestDTO request) {

        userRepo.findByUserOfficialEmail(request.getUserOfficialEmail())
                .ifPresent(u -> {
                    throw new RuntimeException("User already exists");
                });

        UserAuth user = new UserAuth();
        user.setUsername(request.getUsername());
        user.setUserOfficialEmail(request.getUserOfficialEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepo.save(user);
        return "Registered Successfully";
    }

    // ================= LOGIN =================
    public AuthResponseDTO login(LoginRequestDTO request) {

        UserAuth user = userRepo.findByUserOfficialEmail(request.getUserOfficialEmail())
                .orElseThrow(() -> new RuntimeException("Invalid Email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Password");
        }

        String token = jwtUtil.generateToken(user.getUserOfficialEmail());
        return new AuthResponseDTO(token, "Login Successful", user.getId(), user.getRole().name());
    }

    // ================= FORGOT PASSWORD =================
    public void forgotPassword(ForgotPasswordRequestDTO request) {

        UserAuth user = userRepo.findByUserOfficialEmail(request.getUserOfficialEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setResetTokenExpire(Instant.now().plus(10, ChronoUnit.MINUTES));
        userRepo.save(user);

        emailService.passwordMail(
                user.getUserOfficialEmail(),
                "http://localhost:8081/api/auth/reset-password?token=" + token
        );
    }

    // ================= RESET PASSWORD =================
    public void resetPassword(ResetPasswordRequestDTO request) {

        UserAuth user = userRepo.findByResetToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getResetTokenExpire().isBefore(Instant.now())) {
            throw new RuntimeException("Token expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpire(null);

        userRepo.save(user);
    }
}

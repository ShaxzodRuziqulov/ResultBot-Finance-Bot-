package com.example.resultbot.service;

import com.example.resultbot.entity.User;
import com.example.resultbot.entity.enumirated.Status;
import com.example.resultbot.repository.RoleRepository;
import com.example.resultbot.repository.UserRepository;
import com.example.resultbot.service.dto.LoginUserDto;
import com.example.resultbot.service.dto.RefreshTokenDto;
import com.example.resultbot.service.dto.RegisterUserDto;
import com.example.resultbot.service.dto.UserDto;
import com.example.resultbot.service.mapper.UserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            AuthenticationManager authenticationManager, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }


    public UserDto signup(RegisterUserDto input) {
        if (input.getEmail() == null || input.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email bo‘sh bo‘lmasligi kerak.");
        }

        Optional<User> existingUser = userRepository.findByChatId(input.getChatId());
        if (existingUser.isPresent()) {
            throw new IllegalStateException("Foydalanuvchi allaqachon ro‘yxatdan o‘tgan.");
        }
        User user = userMapper.toUser(input);
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setStatus(Status.PENDING);

        if (input.getRoleId() == null) {
            user.setRole(roleRepository.findByName("ROLE_USER"));
        }
        String verificationCode = generateVerificationCode();
        user.setVerificationCode(verificationCode);

        userRepository.save(user);
        sendVerificationEmail(user.getEmail(), verificationCode);

        return userMapper.toDto(user);
    }

    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void sendVerificationEmail(String email, String verificationCode) {
        String subject = "Please verify your email";
        String body = "Your verification code is: " + verificationCode;
        emailService.sendEmail(email, subject, body);
    }

    public boolean verifyCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Foydalanuvchi topilmadi: " + email));

        if (user.getVerificationCode().equals(code)) {
            user.setStatus(Status.ACTIVE);
            user.setVerificationCode(null);
            userRepository.save(user);
            return true;
        } else {
            throw new IllegalArgumentException("Tasdiqlash kodi noto‘g‘ri.");
        }
    }

    public User authenticate(LoginUserDto input) {
        if (input.getEmail() == null || input.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email kiritilishi shart");
        }
        if (input.getPassword() == null || input.getPassword().isEmpty()) {
            throw new IllegalArgumentException(("Parol kiritilishi shart"));
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Email yoki parol noto'g'ri");
        }
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Foydalanuvchi topilmadi " + input.getEmail()));
    }

    public User authenticateRefresh(RefreshTokenDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}

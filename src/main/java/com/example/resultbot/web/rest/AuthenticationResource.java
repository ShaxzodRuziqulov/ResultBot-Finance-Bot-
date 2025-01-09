package com.example.resultbot.web.rest;

import com.example.resultbot.entity.User;
import com.example.resultbot.repository.UserRepository;
import com.example.resultbot.security.JwtService;
import com.example.resultbot.service.AuthenticationService;
import com.example.resultbot.service.dto.LoginUserDto;
import com.example.resultbot.service.dto.RefreshTokenDto;
import com.example.resultbot.service.dto.RegisterUserDto;
import com.example.resultbot.service.dto.UserDto;
import com.example.resultbot.service.request.LoginResponse;
import com.example.resultbot.service.request.RefreshTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthenticationResource {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public AuthenticationResource(JwtService jwtService, AuthenticationService authenticationService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> register(@RequestBody RegisterUserDto registerUserDto) {
        UserDto registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String code) {
        User user = userRepository.findByVerificationCode(code);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code.");
        }

        user.setVerificationCode(null);
        userRepository.save(user);

        return ResponseEntity.ok("User verified successfully.");
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = LoginResponse.builder().token(jwtToken).expiresIn(jwtService.getExpirationTime()).build();
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {

        User refreshTokenUser = authenticationService.authenticateRefresh(refreshTokenDto);

        String rwtToken = jwtService.generateRefreshToken(refreshTokenUser);

        RefreshTokenResponse refreshTokenResponse = RefreshTokenResponse.builder().token(rwtToken).expiresIn(jwtService.getExpirationTimeRefresh()).build();

        return ResponseEntity.ok(refreshTokenResponse);

    }

}
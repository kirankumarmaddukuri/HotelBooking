package com.demo.HotelBooking.controller;

import com.demo.HotelBooking.dto.JwtResponse;
import com.demo.HotelBooking.dto.LoginRequest;
import com.demo.HotelBooking.dto.MessageResponse;
import com.demo.HotelBooking.dto.SignupRequest;
import com.demo.HotelBooking.model.Role;
import com.demo.HotelBooking.model.User;
import com.demo.HotelBooking.repository.UserRepository;
import com.demo.HotelBooking.security.JwtUtils;
import com.demo.HotelBooking.security.UserDetailsImpl;
import com.demo.HotelBooking.service.EmailService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    EmailService emailService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("User signed in: {}", userDetails.getEmail());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getName(),
                userDetails.getAuthorities().iterator().next().getAuthority()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setRole(Role.USER); // Defaulting to USER role

        userRepository.save(user);
        logger.info("User registered: {}", user.getEmail());

        String emailError = emailService.sendRegistrationEmailWithError(user.getEmail(), user.getName());

        if (emailError != null) {
            return ResponseEntity.ok(new MessageResponse("User registered successfully, but the confirmation email could not be sent. Reason: " + emailError));
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}

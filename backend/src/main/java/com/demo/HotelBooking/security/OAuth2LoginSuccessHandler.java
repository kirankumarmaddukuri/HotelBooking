package com.demo.HotelBooking.security;

import com.demo.HotelBooking.model.AuthProvider;
import com.demo.HotelBooking.model.Role;
import com.demo.HotelBooking.model.User;
import com.demo.HotelBooking.repository.UserRepository;
import com.demo.HotelBooking.service.EmailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // If user exists but is local, we might want to update provider, or just let them login
            if (user.getAuthProvider() == AuthProvider.LOCAL) {
                user.setAuthProvider(AuthProvider.GOOGLE);
                userRepository.save(user);
            }
        } else {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setRole(Role.USER);
            user.setAuthProvider(AuthProvider.GOOGLE);
            userRepository.save(user);
            
            // Send welcome email for new OAuth users
            emailService.sendRegistrationEmail(user.getEmail(), user.getName());
        }

        String jwt = jwtUtils.generateTokenFromUsername(user.getEmail());
        
        // Redirect to React frontend with JWT token in URL
        String targetUrl = "http://localhost:5173/oauth2/redirect?token=" + jwt +
                "&email=" + user.getEmail() + "&name=" + user.getName() + "&role=" + user.getRole().name() + "&id=" + user.getId();
        
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

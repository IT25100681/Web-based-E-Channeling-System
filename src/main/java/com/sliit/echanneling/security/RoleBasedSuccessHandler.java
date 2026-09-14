package com.sliit.echanneling.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleBasedSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/dashboard");
                return;
            } else if (role.equals("ROLE_DOCTOR")) {
                response.sendRedirect("/doctor/dashboard");
                return;
            } else if (role.equals("ROLE_PATIENT")) {
                response.sendRedirect("/patient/dashboard");
                return;
            } else if (role.equals("ROLE_RECEPTIONIST") || role.equals("ROLE_NURSE") || role.equals("ROLE_PHARMACIST")) {
                response.sendRedirect("/staff/dashboard");
                return;
            }
        }
        response.sendRedirect("/");
    }
}

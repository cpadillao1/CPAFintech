package com.fintech.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userLogin;

        // 1. Validación del encabezado Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            // 2. Extraer el login (subject) del token
            userLogin = jwtService.validateTokenAndGetSubject(jwt);

            if (userLogin != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 3. Extraer los permisos.
                // IMPORTANTE: Asegúrate de que jwtService.getAuthoritiesFromToken(jwt)
                // busque la llave "roles" que es la que viene en tu token.
                List<String> codes = jwtService.getAuthoritiesFromToken(jwt);

                if (codes != null && !codes.isEmpty()) {
                    // 4. Convertimos a SimpleGrantedAuthority
                    List<SimpleGrantedAuthority> authorities = codes.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    // 5. Creamos el token de autenticación de Spring
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userLogin,
                            null,
                            authorities
                    );

                    // 6. Adjuntamos detalles de la solicitud (IP, sesión, etc.)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 7. Establecemos la autenticación en el contexto
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Si el token es inválido o expiró, limpiamos el contexto para asegurar el 403/401
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
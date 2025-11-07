package springcrud.securityimp.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import springcrud.securityimp.Services.JwtService;
import springcrud.securityimp.Services.UserDetailsServiceImp;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsServiceImp userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        System.out.println(">>> JwtFilter: incoming request = " + req.getRequestURI());

        final String authorizationHeader = req.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.out.println(">>> JwtFilter: No token found, continuing filter chain");
            chain.doFilter(req, res);
            return;
        }

        final String token = authorizationHeader.substring(7);
        final String username = jwtService.extractUsername(token);
        System.out.println(">>> JwtFilter: Token found, username = " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println(">>> JwtFilter: Token is valid, authentication set");
            }
        }

        chain.doFilter(req, res);
    }


}

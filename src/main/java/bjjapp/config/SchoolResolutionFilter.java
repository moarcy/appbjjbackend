package bjjapp.config;

import bjjapp.context.SchoolContext;
import bjjapp.entity.School;
import bjjapp.entity.User;
import bjjapp.enums.Role;
import bjjapp.repository.SchoolRepository;
import bjjapp.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SchoolResolutionFilter extends OncePerRequestFilter {

    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        // Whitelist para rotas públicas
        if (path.startsWith("/api/auth/") || path.startsWith("/auth/") || path.startsWith("/schools/") || path.startsWith("/admin/global/") || path.startsWith("/swagger/") ||
            path.startsWith("/actuator/") || path.startsWith("/css/") || path.startsWith("/js/") ||
            path.startsWith("/images/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (path.startsWith("/s/")) {
                String slug = path.split("/")[2];
                School school = schoolRepository.findBySlugAndDeletedAtIsNull(slug)
                    .orElseThrow(() -> new IllegalArgumentException("Escola não encontrada: " + slug));
                SchoolContext.set(school.getId());

                // Validação de consistência User × School
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getPrincipal() instanceof User user) {
                    if (!user.getRole().equals(Role.SUPER_ADMIN) &&
                        !user.getSchool().getId().equals(school.getId())) {
                        throw new AccessDeniedException("Acesso negado: escola incompatível");
                    }
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            SchoolContext.clear();
        }
    }
}

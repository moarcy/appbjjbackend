package bjjapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/login",
                    "/auth/register"
                ).permitAll()
                .requestMatchers("/api/users/me").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers("/api/users/findById/**").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers("/api/users/status/**").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers("/api/users/graduacao/**").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers("/api/users/historico/**").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers("/api/users/save").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers("/api/users/update/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers("/api/users/delete/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers("/api/chamadas/presencas-ausencias/**").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers("/api/chamadas/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers("/api/turmas/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers("/api/professores/**").hasAnyRole("ADMIN", "PROFESSOR")
                .anyRequest().authenticated()
            );
            // Temporariamente removido o filtro JWT para testar
            // .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

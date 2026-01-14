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
import org.springframework.http.HttpMethod;

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
                .requestMatchers("/users/me").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers("/users/findById/**").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers("/users/status/**").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers("/users/graduacao/**").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers("/users/historico/**").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers("/users/save").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers("/users/update/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers("/users/delete/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers("/professores/deactivate/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers("/turmas/deactivate/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.PUT, "/users/deactivate/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/turmas/deactivate/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/professores/deactivate/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/users/graduacao/**").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers("/users/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers("/chamadas/presencas-ausencias/**").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers("/chamadas/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers("/turmas/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers("/professores/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers("/requisitos-graduacao/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

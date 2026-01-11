package bjjapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/register"
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
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:5173",
            "http://localhost:4200",
            "https://www.appbjj.com.br",
            "https://appbjj.com.br"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

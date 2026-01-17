package bjjapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SchoolResolutionFilter schoolResolutionFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, SchoolResolutionFilter schoolResolutionFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.schoolResolutionFilter = schoolResolutionFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Forma mais moderna
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS configurado
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // 🔥 ADICIONE ESTAS LINHAS (PÚBLICAS) - PRIMEIRO!
                .requestMatchers("/api/robots.txt").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/").permitAll() // Raiz também

                // SWAGGER/OPENAPI (se tiver)
                .requestMatchers("/api/swagger-ui/**", "/api/v3/api-docs/**", "/api/swagger-ui.html").permitAll()

                // AUTH PÚBLICO
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                .requestMatchers("/auth/login", "/auth/register").permitAll() // Suporte sem /api

                // OPTIONS para CORS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // SCHOOLS para SUPER_ADMIN
                .requestMatchers("/schools/**").hasRole("SUPER_ADMIN")

                // PUT ESPECÍFICOS
                .requestMatchers(HttpMethod.PUT, "/api/s/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN")

                // USERS
                .requestMatchers("/api/s/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT")

                // PROFESSORES
                .requestMatchers("/api/s/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER")

                // TURMAS
                .requestMatchers("/api/s/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER")

                // CHAMADAS
                .requestMatchers("/api/s/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT")

                // OUTROS
                .requestMatchers("/api/requisitos-graduacao/**").permitAll()

                // SUPER_ADMIN global
                .requestMatchers("/api/admin/global/**").hasRole("SUPER_ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(schoolResolutionFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🔥 ADICIONE ESTE MÉTODO PARA CONFIGURAR CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Domínios permitidos (Vercel + local)
        configuration.setAllowedOrigins(Arrays.asList(
            "https://appbjjfront-hvhk.vercel.app", // Domínio real do frontend Vercel
            "http://localhost:3000",               // Dev local
            "http://localhost:5173",               // Vite dev
            "https://localhost:3000"
        ));

        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-School-Id" // Se usar header para escola
        ));

        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Disposition"
        ));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hora cache CORS

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

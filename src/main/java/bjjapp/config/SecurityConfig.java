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
                .requestMatchers("/robots.txt").permitAll()
                .requestMatchers("/health").permitAll()
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/").permitAll() // Raiz também

                // SWAGGER/OPENAPI (se tiver)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                // AUTH PÚBLICO
                .requestMatchers("/auth/login", "/auth/register").permitAll()

                // PUT ESPECÍFICOS
                .requestMatchers(HttpMethod.PUT, "/s/**/users/deactivate/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/s/**/turmas/deactivate/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/s/**/professores/deactivate/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN")

                // USERS
                .requestMatchers("/s/**/users/me").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT")
                .requestMatchers("/s/**/users/findById/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT")
                .requestMatchers("/s/**/users/status/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT")
                .requestMatchers("/s/**/users/graduacao/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT")
                .requestMatchers("/s/**/users/historico/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT")
                .requestMatchers("/s/**/users/save").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER")
                .requestMatchers("/s/**/users/update/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER")
                .requestMatchers("/s/**/users/delete/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER")

                // PROFESSORES
                .requestMatchers("/s/**/professores/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER")

                // TURMAS
                .requestMatchers("/s/**/turmas/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER")

                // CHAMADAS
                .requestMatchers("/s/**/chamadas/presencas-ausencias/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER", "STUDENT")
                .requestMatchers("/s/**/chamadas/**").hasAnyRole("SUPER_ADMIN", "SCHOOL_ADMIN", "TEACHER")

                // OUTROS
                .requestMatchers("/s/**/requisitos-graduacao/**").permitAll()

                // SUPER_ADMIN global
                .requestMatchers("/admin/global/**").hasRole("SUPER_ADMIN")

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
            "https://seu-frontend.vercel.app",     // Substitua pelo seu domínio Vercel
            "https://*.vercel.app",                // Todos subdomínios Vercel
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

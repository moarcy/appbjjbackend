package bjjapp.service;

import bjjapp.entity.School;
import bjjapp.entity.User;
import bjjapp.enums.Role;
import bjjapp.repository.SchoolRepository;
import bjjapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public School createSchool(String name, String slug, String phone, String adminEmail, String adminPassword) {
        // Validate slug uniqueness
        if (schoolRepository.existsBySlugAndDeletedAtIsNull(slug)) {
            throw new IllegalArgumentException("Slug já existe: " + slug);
        }

        School school = School.builder()
            .name(name)
            .slug(slug)
            .status(School.SchoolStatus.ACTIVE)
            .phone(phone)
            .build();
        school = schoolRepository.save(school);

        User admin = User.builder()
            .nome("Admin")  // Or derive from email
            .username(adminEmail)
            .password(passwordEncoder.encode(adminPassword))
            .role(Role.SCHOOL_ADMIN)
            .school(school)
            .ativo(true)
            .build();
        userRepository.save(admin);

        return school;
    }

    public Optional<School> findBySlug(String slug) {
        return schoolRepository.findBySlugAndDeletedAtIsNull(slug);
    }
}

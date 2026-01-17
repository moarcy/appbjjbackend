package bjjapp.service;

import bjjapp.entity.School;
import bjjapp.entity.School.SchoolStatus;
import bjjapp.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class SchoolService {

    private final SchoolRepository schoolRepository;

    @Transactional(readOnly = true)
    public List<School> findAll() {
        return schoolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public School findById(Long id) {
        return schoolRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Escola não encontrada: " + id));
    }

    public School create(School school) {
        // Validações
        if (school.getName() == null || school.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da escola é obrigatório");
        }
        if (school.getSlug() == null || school.getSlug().trim().isEmpty()) {
            throw new IllegalArgumentException("Slug da escola é obrigatório");
        }
        if (schoolRepository.existsBySlugAndDeletedAtIsNull(school.getSlug())) {
            throw new IllegalArgumentException("Slug já existe: " + school.getSlug());
        }

        school.setStatus(SchoolStatus.ACTIVE);
        return schoolRepository.save(school);
    }

    public School update(Long id, School school) {
        School existing = findById(id);
        existing.setName(school.getName());
        existing.setPhone(school.getPhone());
        // Slug não pode ser alterado
        return schoolRepository.save(existing);
    }

    public School activate(Long id) {
        School school = findById(id);
        school.setStatus(SchoolStatus.ACTIVE);
        return schoolRepository.save(school);
    }

    public School deactivate(Long id) {
        School school = findById(id);
        school.setStatus(SchoolStatus.INACTIVE);
        return schoolRepository.save(school);
    }

    public void delete(Long id) {
        School school = findById(id);
        school.setDeletedAt(LocalDateTime.now());
        schoolRepository.save(school);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getSummary() {
        List<School> allSchools = schoolRepository.findAll();
        long active = 0;
        long trial = 0;
        long inactive = 0;

        LocalDateTime now = LocalDateTime.now();
        for (School school : allSchools) {
            if (school.getDeletedAt() != null) continue; // Skip soft deleted

            if (school.getStatus() == SchoolStatus.INACTIVE) {
                inactive++;
            } else if (school.getStatus() == SchoolStatus.ACTIVE) {
                if (school.getCreatedAt().plusDays(30).isAfter(now)) {
                    trial++;
                } else {
                    active++;
                }
            }
        }

        return Map.of(
            "active", active,
            "trial", trial,
            "inactive", inactive
        );
    }
}

package bjjapp.service;

import bjjapp.entity.School;
import bjjapp.entity.SchoolOwner;
import bjjapp.entity.Subscription;
import bjjapp.entity.Invoice;
import bjjapp.enums.SchoolStatus;
import bjjapp.enums.BillingCycle;
import bjjapp.enums.SubscriptionStatus;
import bjjapp.repository.SchoolRepository;
import bjjapp.repository.SchoolOwnerRepository;
import bjjapp.repository.SubscriptionRepository;
import bjjapp.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service responsável pela gestão de Escolas (Tenants), Proprietários e
 * Assinaturas.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final SchoolOwnerRepository schoolOwnerRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository;

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

    @Transactional
    public School createWithOwnerAndSubscription(School school, SchoolOwner owner, BigDecimal amount, int trialDays) {
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
        if (owner.getFullName() == null || owner.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do responsável é obrigatório");
        }
        if (owner.getEmail() == null || owner.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email do responsável é obrigatório");
        }

        // Criar owner
        SchoolOwner savedOwner = schoolOwnerRepository.save(owner);

        // Criar subscription
        LocalDateTime now = LocalDateTime.now();
        Subscription subscription = Subscription.builder()
                .amount(amount)
                .billingCycle(BillingCycle.MONTHLY)
                .status(SubscriptionStatus.TRIAL)
                .startDate(now)
                .trialEndDate(now.plusDays(trialDays))
                .build();
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // Criar school
        school.setStatus(SchoolStatus.ACTIVE);
        school.setTrialEndDate(now.plusDays(trialDays));
        school.setOwner(savedOwner);
        school.setSubscription(savedSubscription);

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
            if (school.getDeletedAt() != null)
                continue; // Skip soft deleted

            if (school.getStatus() == SchoolStatus.INACTIVE) {
                inactive++;
            } else if (school.getStatus() == SchoolStatus.ACTIVE) {
                if (school.getTrialEndDate() != null && school.getTrialEndDate().isAfter(now)) {
                    trial++;
                } else {
                    active++;
                }
            }
        }

        return Map.of(
                "active", active,
                "trial", trial,
                "inactive", inactive);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getBillingInfo(Long schoolId) {
        School school = findById(schoolId);
        List<Invoice> invoices = invoiceRepository.findBySchoolId(schoolId);

        return Map.of(
                "school", school,
                "subscription", school.getSubscription(),
                "invoices", invoices);
    }
}

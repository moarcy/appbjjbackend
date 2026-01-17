package bjjapp.service;

import bjjapp.entity.Invoice;
import bjjapp.entity.School;
import bjjapp.entity.Subscription;
import bjjapp.enums.InvoiceStatus;
import bjjapp.enums.PaymentMethod;
import bjjapp.enums.SubscriptionStatus;
import bjjapp.repository.InvoiceRepository;
import bjjapp.repository.SchoolRepository;
import bjjapp.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final SchoolRepository schoolRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional(readOnly = true)
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Invoice> findBySchoolId(Long schoolId) {
        return invoiceRepository.findBySchoolId(schoolId);
    }

    @Transactional(readOnly = true)
    public Invoice findById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fatura não encontrada: " + id));
    }

    @Transactional
    public Invoice generateNextInvoice(Long schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("Escola não encontrada: " + schoolId));

        if (school.getSubscription() == null) {
            throw new IllegalArgumentException("Escola não possui assinatura");
        }

        Subscription subscription = school.getSubscription();
        LocalDateTime now = LocalDateTime.now();

        // Próximo mês
        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        LocalDate dueDate = nextMonth.atDay(10); // Exemplo: dia 10 do mês

        Invoice invoice = Invoice.builder()
                .school(school)
                .subscription(subscription)
                .referenceMonth(nextMonth)
                .dueDate(dueDate)
                .amount(subscription.getAmount())
                .status(InvoiceStatus.PENDING)
                .build();

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice markAsPaid(Long invoiceId, PaymentMethod paymentMethod, String notes) {
        Invoice invoice = findById(invoiceId);
        LocalDateTime now = LocalDateTime.now();

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(now);
        invoice.setPaymentMethod(paymentMethod);
        invoice.setNotes(notes);

        // Avançar nextBillingDate da subscription
        Subscription subscription = invoice.getSubscription();
        if (subscription.getNextBillingDate() != null) {
            subscription.setNextBillingDate(subscription.getNextBillingDate().plusMonths(1));
        } else {
            // Se não tem, definir para o próximo mês
            subscription.setNextBillingDate(now.plusMonths(1));
        }

        // Se estava PAST_DUE, voltar para ACTIVE
        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }

        subscriptionRepository.save(subscription);
        return invoiceRepository.save(invoice);
    }

    // Método para atualizar status baseado em datas (chamado periodicamente ou
    // manualmente)
    @Transactional
    public void updateOverdueInvoices() {
        LocalDate today = LocalDate.now();
        List<Invoice> pendingInvoices = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.PENDING && inv.getDueDate().isBefore(today))
                .toList();

        for (Invoice invoice : pendingInvoices) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
            invoiceRepository.save(invoice);

            // Se houver OVERDUE, marcar subscription como PAST_DUE
            Subscription subscription = invoice.getSubscription();
            if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
                subscription.setStatus(SubscriptionStatus.PAST_DUE);
                subscriptionRepository.save(subscription);
            }
        }
    }
}

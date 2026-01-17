package bjjapp.controller;

import bjjapp.entity.School;
import bjjapp.entity.SchoolOwner;
import bjjapp.entity.Subscription;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class SchoolCreationRequest {

    @NotBlank(message = "Nome da escola é obrigatório")
    private String schoolName;

    @NotBlank(message = "Slug da escola é obrigatório")
    private String schoolSlug;

    private String schoolPhone;

    @NotBlank(message = "Nome do responsável é obrigatório")
    private String ownerFullName;

    @NotBlank(message = "Email do responsável é obrigatório")
    private String ownerEmail;

    private String ownerDocument;

    private String ownerPhone;

    @NotNull(message = "Valor da mensalidade é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal subscriptionAmount;

    private int trialDays = 30;

    // Getters and setters

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolSlug() {
        return schoolSlug;
    }

    public void setSchoolSlug(String schoolSlug) {
        this.schoolSlug = schoolSlug;
    }

    public String getSchoolPhone() {
        return schoolPhone;
    }

    public void setSchoolPhone(String schoolPhone) {
        this.schoolPhone = schoolPhone;
    }

    public String getOwnerFullName() {
        return ownerFullName;
    }

    public void setOwnerFullName(String ownerFullName) {
        this.ownerFullName = ownerFullName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerDocument() {
        return ownerDocument;
    }

    public void setOwnerDocument(String ownerDocument) {
        this.ownerDocument = ownerDocument;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public BigDecimal getSubscriptionAmount() {
        return subscriptionAmount;
    }

    public void setSubscriptionAmount(BigDecimal subscriptionAmount) {
        this.subscriptionAmount = subscriptionAmount;
    }

    public int getTrialDays() {
        return trialDays;
    }

    public void setTrialDays(int trialDays) {
        this.trialDays = trialDays;
    }
}

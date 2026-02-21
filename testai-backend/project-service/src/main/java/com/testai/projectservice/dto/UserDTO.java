package com.testai.projectservice.dto;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private UserRole role;
    private String keycloakId;
    private String avatar;
    private String company;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLogin;
    private Boolean emailVerified;
    private String emailVerificationToken;
    private Instant verificationTokenExpiresAt;
    private String tempPassword;
    private String phoneNumber; // Format international : +33612345678
    private Boolean phoneVerified;
    private String phoneVerificationCode;
    private Instant phoneVerificationCodeExpiresAt;
    private Integer phoneVerificationAttempts;
    private Instant phoneVerificationSentAt;
    private String passwordResetToken;
    private Instant passwordResetTokenExpiresAt;
    private Integer passwordResetAttempts;
    private Instant passwordResetRequestedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Instant lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    public Instant getVerificationTokenExpiresAt() {
        return verificationTokenExpiresAt;
    }

    public void setVerificationTokenExpiresAt(Instant verificationTokenExpiresAt) {
        this.verificationTokenExpiresAt = verificationTokenExpiresAt;
    }

    public String getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public String getPhoneVerificationCode() {
        return phoneVerificationCode;
    }

    public void setPhoneVerificationCode(String phoneVerificationCode) {
        this.phoneVerificationCode = phoneVerificationCode;
    }

    public Instant getPhoneVerificationCodeExpiresAt() {
        return phoneVerificationCodeExpiresAt;
    }

    public void setPhoneVerificationCodeExpiresAt(Instant phoneVerificationCodeExpiresAt) {
        this.phoneVerificationCodeExpiresAt = phoneVerificationCodeExpiresAt;
    }

    public Integer getPhoneVerificationAttempts() {
        return phoneVerificationAttempts;
    }

    public void setPhoneVerificationAttempts(Integer phoneVerificationAttempts) {
        this.phoneVerificationAttempts = phoneVerificationAttempts;
    }

    public Instant getPhoneVerificationSentAt() {
        return phoneVerificationSentAt;
    }

    public void setPhoneVerificationSentAt(Instant phoneVerificationSentAt) {
        this.phoneVerificationSentAt = phoneVerificationSentAt;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public Instant getPasswordResetTokenExpiresAt() {
        return passwordResetTokenExpiresAt;
    }

    public void setPasswordResetTokenExpiresAt(Instant passwordResetTokenExpiresAt) {
        this.passwordResetTokenExpiresAt = passwordResetTokenExpiresAt;
    }

    public Integer getPasswordResetAttempts() {
        return passwordResetAttempts;
    }

    public void setPasswordResetAttempts(Integer passwordResetAttempts) {
        this.passwordResetAttempts = passwordResetAttempts;
    }

    public Instant getPasswordResetRequestedAt() {
        return passwordResetRequestedAt;
    }

    public void setPasswordResetRequestedAt(Instant passwordResetRequestedAt) {
        this.passwordResetRequestedAt = passwordResetRequestedAt;
    }

    public enum UserRole {
        ADMIN,
        MANAGER,
        DEVELOPER
    }
}

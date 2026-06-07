package com.suuka.cleaning.auth.entity;

import com.suuka.cleaning.auth.enums.CleanerApplicationStatus;
import com.suuka.cleaning.common.entity.AuditableEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "cleaner_applications")
public class CleanerApplication extends AuditableEntity {
    @Column(nullable = false)
    private UUID userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CleanerApplicationStatus applicationStatus = CleanerApplicationStatus.PENDING_REVIEW;
    private String phoneNumber;
    private String nationalId;
    private String location;
    private String experienceLevel;
    private String availability;
    private String idDocumentName;
    private String profilePhotoName;
    @Column(length = 2000)
    private String reviewNotes;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public CleanerApplicationStatus getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(CleanerApplicationStatus applicationStatus) { this.applicationStatus = applicationStatus; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }
    public String getIdDocumentName() { return idDocumentName; }
    public void setIdDocumentName(String idDocumentName) { this.idDocumentName = idDocumentName; }
    public String getProfilePhotoName() { return profilePhotoName; }
    public void setProfilePhotoName(String profilePhotoName) { this.profilePhotoName = profilePhotoName; }
    public String getReviewNotes() { return reviewNotes; }
    public void setReviewNotes(String reviewNotes) { this.reviewNotes = reviewNotes; }
}

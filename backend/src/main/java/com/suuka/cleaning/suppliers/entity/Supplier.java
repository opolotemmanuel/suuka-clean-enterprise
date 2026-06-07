package com.suuka.cleaning.suppliers.entity;

import com.suuka.cleaning.common.entity.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "suppliers")
public class Supplier extends AuditableEntity {
    @Column(nullable = false)
    private String name;
    private String contactEmail;
    private String phone;
    private int rating;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
}

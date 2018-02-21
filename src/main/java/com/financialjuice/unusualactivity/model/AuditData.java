package com.financialjuice.unusualactivity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Temporal annotation is used with java.util.Date and java.util.Calendar classes. It converts the date and time values from Java Object to compatible database type and vice versa.
 *
 * @JsonIgnoreProperties annotation is a Jackson annotation. Spring Boot uses Jackson for Serializing and Deserializing Java objects to and from JSON.
 *
 * @EntityListeners(AuditingEntityListener.class) we have annotated createdAt and updatedAt fields with @CreatedDate and @LastModifiedDate annotations respectively.
 *
 * Now, what we want is that these fields should automatically get populated whenever we create or update an entity.
 *
 * To achieve this, we need to do two things -
 *
 * 1. Add Spring Data JPA’s AuditingEntityListener to the domain model.
 *
 * We have already done this in our Note model with the annotation @EntityListeners(AuditingEntityListener.class).
 *
 */

@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
abstract class AuditData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt = new Date();

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "AuditData{" +
                "createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
package com.example.trafileatestproject.model.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import java.time.Instant;

@Data
@MappedSuperclass
public class AuditableEntity {

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }
}

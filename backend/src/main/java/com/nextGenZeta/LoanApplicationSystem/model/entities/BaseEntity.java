package com.nextGenZeta.LoanApplicationSystem.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.Map;

@MappedSuperclass
@JsonIgnoreProperties(value = {"createdAt", "updatedAt", "createdBy", "updatedBy"}, allowGetters = true)
public abstract class BaseEntity {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attrs", columnDefinition = "jsonb")
    private Map<String, Object> attrs;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;
}


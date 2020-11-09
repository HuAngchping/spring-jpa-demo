package com.example.demo.dto;

import lombok.ToString;

import javax.persistence.*;

/**
 * @author huang
 */
@Entity
@ToString
@Table(name = "country")
@EntityListeners(value = {TenantEntityListener.class})
public class Country {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(name = "tenant_id")
    private String tenantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}

package com.intranet.kch.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class CompanyEntity extends Base {
    @Id @GeneratedValue
    private Long id;
    private String companyTitle;
    private String companyName;
    private String companyAddr;
    private String companyInvoiceAcronym;
}

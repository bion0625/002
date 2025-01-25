package com.intranet.kch.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class IVExcelEntity extends Base {
    @Id @GeneratedValue
    private Long id;
    private Long bcId;
    private String name;
    private Long nights;
    private String invoiceDate;
    private String title;
    private String startDate;
    private String endDate;
    private Long totalPrice;
}

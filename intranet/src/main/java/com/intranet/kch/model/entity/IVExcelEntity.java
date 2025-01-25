package com.intranet.kch.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class IVExcelEntity extends Base {
    @Id @GeneratedValue
    private Long id;
    private Long bcId;
    private String name;
    private Long nights;
    private LocalDate invoiceDate;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long totalPrice;
}

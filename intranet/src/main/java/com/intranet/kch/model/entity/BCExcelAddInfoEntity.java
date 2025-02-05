package com.intranet.kch.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class BCExcelAddInfoEntity extends Base{
    @Id
    @GeneratedValue
    private Long id;
    private Long bcExcelId;
    private String houseAddr;
    private String startDate;
    private String endDate;
    private String deposit;
    private String monthlyRent;
    private String isTax;
    private String accountInfo;
    private String info1;
    private String info2;
    private String info3;
}

package com.intranet.kch.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class BCExcelEntity extends Base {
    @Id @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String title;
    private Long companyId;
    /**
     * Booking Confirmation
     * */
    private String propertyName;
    private String guestName;
    private String checkIn;
    private String checkOut;
    private String apartmentType;
    private String apartmentAddress;
    private String koreanAddress;
    private String totalRent;
    private Long price;
    private Long totalNights;
    private String ofGuests;
    private String bookedBy;
    private String bookingRequestCompany;
    private String extensionOfLease;
    private String notice;
    private String signedDate;

    /**
     * Invoice
     * */
    private String service;
    private String remarks01;
    private String remarks02;
}

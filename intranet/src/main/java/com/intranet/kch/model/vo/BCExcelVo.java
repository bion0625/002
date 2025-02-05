package com.intranet.kch.model.vo;

import com.intranet.kch.model.entity.BCExcelAddInfoEntity;
import com.intranet.kch.model.entity.BCExcelEntity;
import com.intranet.kch.util.DateFormatUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BCExcelVo {
    private Long id;
    private String createdAt;
    private String updatedAt;
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
    private Double totalRent;
    private Double price;
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

    private List<IVExcelVo> ivExcelVos;

    /**
     * MEMO
     * */
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

    public BCExcelEntity toEntity(String loginId) {
        BCExcelEntity entity = new BCExcelEntity();
        entity.setTitle(this.title);
        entity.setCompanyId(this.companyId);
        entity.setPropertyName(this.propertyName);
        entity.setGuestName(this.guestName);
        entity.setCheckIn(this.checkIn);
        entity.setCheckOut(this.checkOut);
        entity.setApartmentType(this.apartmentType);
        entity.setApartmentAddress(this.apartmentAddress);
        entity.setKoreanAddress(this.koreanAddress);
        entity.setTotalRent(this.totalRent);
        entity.setPrice(this.price);
        entity.setTotalNights(this.totalNights);
        entity.setOfGuests(this.ofGuests);
        entity.setBookedBy(this.bookedBy);
        entity.setBookingRequestCompany(this.bookingRequestCompany);
        entity.setExtensionOfLease(this.extensionOfLease);
        entity.setNotice(this.notice);
        entity.setSignedDate(this.signedDate);
        entity.setService(this.service);
        entity.setRemarks01(this.remarks01);
        entity.setRemarks02(this.remarks02);

        entity.setCreateUser(loginId);
        return entity;
    }

    public BCExcelAddInfoEntity toAddInfoEntity(Long bcId) {
        BCExcelAddInfoEntity entity = new BCExcelAddInfoEntity();
        entity.setBcExcelId(bcId);
        entity.setHouseAddr(this.houseAddr);
        entity.setStartDate(this.startDate);
        entity.setEndDate(this.endDate);
        entity.setDeposit(this.deposit);
        entity.setMonthlyRent(this.monthlyRent);
        entity.setIsTax(this.isTax);
        entity.setAccountInfo(this.accountInfo);
        entity.setInfo1(this.info1);
        entity.setInfo2(this.info2);
        entity.setInfo3(this.info3);
        return entity;
    }

    public static BCExcelVo fromEntity(BCExcelEntity entity) {
        return new BCExcelVo(
                entity.getId(),
                DateFormatUtil.localDateTimeToString(entity.getCreatedAt(), "yyyy-MM-dd"),
                DateFormatUtil.localDateTimeToString(entity.getUpdatedAt(), "yyyy-MM-dd"),
                entity.getTitle(),
                entity.getCompanyId(),
                entity.getPropertyName(),
                entity.getGuestName(),
                entity.getCheckIn(),
                entity.getCheckOut(),
                entity.getApartmentType(),
                entity.getApartmentAddress(),
                entity.getKoreanAddress(),
                entity.getTotalRent(),
                entity.getPrice(),
                entity.getTotalNights(),
                entity.getOfGuests(),
                entity.getBookedBy(),
                entity.getBookingRequestCompany(),
                entity.getExtensionOfLease(),
                entity.getNotice(),
                entity.getSignedDate(),
                entity.getService(),
                entity.getRemarks01(),
                entity.getRemarks02(),
                new ArrayList<>(),
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "");
    }

    public BCExcelVo setAddInfoFromEntity(BCExcelAddInfoEntity entity) {
        this.houseAddr = entity.getHouseAddr();
        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();
        this.deposit = entity.getDeposit();
        this.monthlyRent = entity.getMonthlyRent();
        this.isTax = entity.getIsTax();
        this.accountInfo = entity.getAccountInfo();
        this.info1 = entity.getInfo1();
        this.info2 = entity.getInfo2();
        this.info3 = entity.getInfo3();
        return this;
    }
}

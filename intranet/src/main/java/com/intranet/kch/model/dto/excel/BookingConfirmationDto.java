package com.intranet.kch.model.dto.excel;

import com.intranet.kch.model.entity.BCExcelEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class BookingConfirmationDto {
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

    List<InVoiceDto> inVoiceDtos;

    public static BookingConfirmationDto fromEntity(BCExcelEntity entity) {
        return new BookingConfirmationDto(
                entity.getPropertyName(),
                entity.getGuestName(),
                entity.getCheckIn().toString(),//TODO
                entity.getCheckOut().toString(),//TODO
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
                new ArrayList<>()
        );
    }
}

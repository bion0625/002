package com.intranet.kch.model.dto.excel;

import com.intranet.kch.model.entity.BCExcelEntity;
import com.intranet.kch.util.DateFormatUtil;
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
    private Double price;
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
                DateFormatUtil.stringToLocalDateTimeString(entity.getCheckIn(), "MMMM dd, yyyy hh:mma"),
                DateFormatUtil.stringToLocalDateTimeString(entity.getCheckOut(), "MMMM dd, yyyy hh:mma"),
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
                DateFormatUtil.stringToLocalDateString(entity.getSignedDate(), "MMMM dd, yyyy"),
                new ArrayList<>()
        );
    }
}

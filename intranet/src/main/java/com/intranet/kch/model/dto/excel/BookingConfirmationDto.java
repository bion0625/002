package com.intranet.kch.model.dto.excel;

import com.intranet.kch.model.entity.BCExcelEntity;
import com.intranet.kch.util.DateFormatUtil;
import com.intranet.kch.util.ExcelPoiUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.ss.usermodel.Sheet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class BookingConfirmationDto implements ExcelDto {
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

    @Override
    public void setSheet(Sheet sheet) {
        ExcelPoiUtil excelPoiUtil = new ExcelPoiUtil(sheet);
        excelPoiUtil.setCellStringValue(14, 3, this.getPropertyName());
        excelPoiUtil.setCellStringValue(17, 3, this.getGuestName());
        excelPoiUtil.setCellStringValue(18, 3, this.getCheckIn());
        excelPoiUtil.setCellStringValue(19, 3, this.getCheckOut());
        excelPoiUtil.setCellStringValue(20, 3, this.getApartmentType());
        excelPoiUtil.setCellStringValue(20, 3, this.getApartmentType());
        excelPoiUtil.setCellStringValue(21, 3, this.getApartmentAddress());
        excelPoiUtil.setCellStringValue(22, 3, this.getKoreanAddress());
        excelPoiUtil.setCellStringValue(23, 3, "USD " + new DecimalFormat("#,###.00").format(this.totalRent) + " including tax for given term");
        excelPoiUtil.setCellStringValue(24, 3,  "(USD" + new DecimalFormat("#,###.00").format(this.price) + " x " + this.getTotalNights() + "night)");
        excelPoiUtil.setCellStringValue(26, 3,  this.getOfGuests());
        excelPoiUtil.setCellStringValue(27, 3,  this.getBookedBy());
        excelPoiUtil.setCellStringValue(28, 3,  this.getBookingRequestCompany());
        excelPoiUtil.setCellStringValue(32, 3,  this.getExtensionOfLease());

        String[] notices = this.getNotice().split("\n");
        int[] lines = {33, 35, 37, 38, 39, 40, 41};
        for (int i = 0; i < Math.min(notices.length, 7); i++) {
            if (i == 6) {
                // 7번째 줄은 거기서부터 끝까지 연결
                StringBuilder line = new StringBuilder(notices[i]);
                for (int j = 7; j < notices.length; j++) {
                    line.append("\n").append(notices[j]);
                }
                excelPoiUtil.setCellStringValue(lines[i], 3, line.toString());
            } else excelPoiUtil.setCellStringValue(lines[i], 3,  notices[i]);
        }

        excelPoiUtil.setCellStringValue(48, 3,  this.getPropertyName());
        excelPoiUtil.setCellStringValue(56, 3,  "Signing Date: " + this.getSignedDate());
    }
}

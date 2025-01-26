package com.intranet.kch.model.dto.excel;

import com.intranet.kch.model.entity.IVExcelEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InVoiceDto {

    private String service;
    private String remarks01;
    private String remarks02;

    private String name;
    private String nights;
    private String invoiceDate;
    private String startDate;
    private String endDate;
    private String totalPrice;

    private String companyName;
    private String companyAddr;

    public InVoiceDto(String name, String nights, String invoiceDate, String startDate, String endDate, String totalPrice) {
        this.name = name;
        this.nights = nights;
        this.invoiceDate = invoiceDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
    }

    public static InVoiceDto fromEntity(IVExcelEntity entity) {
        return new InVoiceDto(
                entity.getName(),
                entity.getNights().toString(),
                entity.getInvoiceDate().toString(),//TODO
                entity.getStartDate().toString(),//TODO
                entity.getEndDate().toString(),//TODO
                entity.getTotalPrice().toString()
        );
    }
}

package com.intranet.kch.model.dto.excel;

import com.intranet.kch.model.entity.IVExcelEntity;
import com.intranet.kch.util.DateFormatUtil;
import com.intranet.kch.util.ExcelPoiUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.ss.usermodel.Sheet;

@Data
@AllArgsConstructor
public class InVoiceDto implements ExcelDto {
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
                DateFormatUtil.localDateToString(entity.getInvoiceDate(), "dd MMMM yyyy"),
                DateFormatUtil.localDateToString(entity.getStartDate().toLocalDate(), "MMMM dd, yyyy"),
                DateFormatUtil.localDateToString(entity.getEndDate().toLocalDate(), "MMMM dd, yyyy"),
                entity.getTotalPrice().toString()
        );
    }

    @Override
    public void setSheet(Sheet sheet) {
        ExcelPoiUtil excelPoiUtil = new ExcelPoiUtil(sheet);
        excelPoiUtil.setCellStringValue(4, 3, this.getInvoiceDate());
        excelPoiUtil.setCellStringValue(6, 3, this.getName());
        excelPoiUtil.setCellStringValue(9, 7, this.getCompanyName());
        excelPoiUtil.setCellStringValue(10, 7, this.getCompanyAddr());
        excelPoiUtil.setCellStringValue(14, 1, this.getService());
        excelPoiUtil.setCellStringValue(14, 4, this.getStartDate());
        excelPoiUtil.setCellStringValue(14, 6, this.getEndDate());
        excelPoiUtil.setCellStringValue(14, 8, this.getNights());
        excelPoiUtil.setCellDoubleValue(14, 9, Double.parseDouble(this.getTotalPrice()));
        excelPoiUtil.setCellStringValue(35, 1, this.getRemarks01());
        excelPoiUtil.setCellStringValue(36, 1, this.getRemarks02());
    }
}

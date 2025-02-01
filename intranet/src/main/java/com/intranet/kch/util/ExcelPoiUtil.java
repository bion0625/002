package com.intranet.kch.util;

import com.intranet.kch.model.dto.excel.ExcelDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExcelPoiUtil {

    private final Sheet sheet;

    public ExcelPoiUtil(Sheet sheet) {
        this.sheet = sheet;
    }
    private Cell getCell(int r, int c) {
        Row row = sheet.getRow(r);
        return row.getCell(c);
    }

    public void setCellStringValue(int r, int c, String value) {
        Cell cell = getCell(r, c);
        cell.setCellValue(value);
    }

    public void setCellDoubleValue(int r, int c, double value) {
        Cell cell = getCell(r, c);
        cell.setCellValue(value);
    }

    public static byte[] generateExcel(ExcelDto excelDto, String templateName, boolean hasFunction) {
        try (InputStream templateStream = new ClassPathResource("templates/excelFile/" + templateName + ".xlsx").getInputStream();
             Workbook workbook = new XSSFWorkbook(templateStream);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 템플릿의 첫 번째 시트를 가져오기
            Sheet sheet = workbook.getSheetAt(0);

            // 특정 셀에 값 설정
            excelDto.setSheet(sheet);

            // 미리 계산식 적용
            if (hasFunction) workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file from template", e);
        }
    }
}

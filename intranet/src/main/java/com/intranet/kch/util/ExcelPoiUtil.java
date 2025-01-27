package com.intranet.kch.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

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
}

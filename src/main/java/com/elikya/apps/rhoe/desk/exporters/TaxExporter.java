/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.exporters;

import com.elikya.apps.rhoe.desk.entity.Tax;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import com.elikya.apps.rhoe.desk.util.NumbersFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Properties;

/**
 *
 * @author Mafole Loemelah
 */
public class TaxExporter {
    
    private static final Properties LANG = ControlsHandler.getLanguage();

    public static void export(List<Tax> items) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(LANG.getProperty("taxes"));
        createHeaders(sheet);
        spreadData(items, sheet);
        DataExporter.writeStream(workbook);
    }

    private static void spreadData(List<Tax> items, XSSFSheet sheet) {
        XSSFRow row;
        int index = 1;
        for (Tax tax : items) {
            row = sheet.createRow(index);
            row.createCell(0).setCellValue(tax.getName());
            row.createCell(1).setCellValue(tax.getCost().doubleValue() > 0
                    ? NumbersFormatter.getFormattedString(tax.getCost()) : "-");
            row.createCell(2).setCellValue(tax.getPercent().doubleValue() > 0
                    ? String.valueOf(tax.getPercent()) : "-");
            row.createCell(3).setCellValue(tax.getDescription());
            index++;
        }
    }

    private static void createHeaders(XSSFSheet sheet) {
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue(LANG.getProperty("name").toUpperCase());
        header.createCell(1).setCellValue(LANG.getProperty("cost").toUpperCase()
                + " (" + ApplicationCurrency.getActualCurrency() + ")");
        header.createCell(2).setCellValue(LANG.getProperty("percent").toUpperCase() + " (%)");
        header.createCell(3).setCellValue(LANG.getProperty("description").toUpperCase());
    }
}

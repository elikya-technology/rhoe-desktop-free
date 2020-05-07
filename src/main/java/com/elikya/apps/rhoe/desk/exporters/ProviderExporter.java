/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.exporters;

import com.elikya.apps.rhoe.desk.entity.Provider;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import java.util.List;
import java.util.Properties;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Mafole Loemelah
 */
public class ProviderExporter {
    
    private static final Properties LANG = ControlsHandler.getLanguage();

    public static void export(List<Provider> providers) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(LANG.getProperty("providers").toUpperCase());
        createHeader(sheet);
        spreadData(providers, sheet);
        DataExporter.writeStream(workbook);
    }

    private static void spreadData(List<Provider> providers, XSSFSheet sheet) {
        int index = 1;
        XSSFRow row;
        for (Provider provider : providers) {
            row = sheet.createRow(index);
            row.createCell(0).setCellValue(provider.getLabel());
            row.createCell(1).setCellValue(provider.getAddress());
            row.createCell(2).setCellValue(provider.getPhone());
            row.createCell(3).setCellValue(provider.getEmail());
            row.createCell(4).setCellValue(provider.getDescription());
            index++;
        }
    }

    private static void createHeader(XSSFSheet sheet) {
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue(LANG.getProperty("name").toUpperCase());
        header.createCell(1).setCellValue(LANG.getProperty("address").toUpperCase());
        header.createCell(2).setCellValue(LANG.getProperty("phone").toUpperCase());
        header.createCell(3).setCellValue(LANG.getProperty("email").toUpperCase());
        header.createCell(4).setCellValue(LANG.getProperty("description").toUpperCase());
    }
}

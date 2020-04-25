/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.exporters;

import com.elikya.apps.rhoe.desk.entity.Sale;
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
public class SaleExporter {
    
    private static Properties LANG;
    private static String CURRENCY;

    public static void export(List<Sale> sales) {
        LANG = ControlsHandler.getLanguage();
        CURRENCY = ApplicationCurrency.getActualCurrency();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(LANG.getProperty("sales").toUpperCase());
        createHeader(sheet);
        spreadData(sales, sheet);
        DataExporter.writeStream(workbook);
    }

    private static void spreadData(List<Sale> sales, XSSFSheet sheet) {
        int index = 1;
        XSSFRow row;
        for (Sale sale : sales) {
            row = sheet.createRow(index);
            row.createCell(0).setCellValue(sale.getNumber());
            row.createCell(1).setCellValue(sale.getSaleDate().toString());
            row.createCell(2).setCellValue(sale.getSaleTime().toString());
            row.createCell(3).setCellValue(NumbersFormatter.getFormattedString(sale.getTotalPrice()));
            row.createCell(4).setCellValue(NumbersFormatter.getFormattedString(sale.getTaxedPrice()));
            index++;
        }
    }

    private static void createHeader(XSSFSheet sheet) {
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue(LANG.getProperty("sale_number").toUpperCase());
        header.createCell(1).setCellValue(LANG.getProperty("date").toUpperCase());
        header.createCell(2).setCellValue(LANG.getProperty("time").toUpperCase());
        header.createCell(3).setCellValue(LANG.getProperty("total_price")
                .toUpperCase() + " (" + CURRENCY + ")");
        header.createCell(4).setCellValue(LANG.getProperty("total_price_tax")
                .toUpperCase() + " (" + CURRENCY + ")");
    }

}

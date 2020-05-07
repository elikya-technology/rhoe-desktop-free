/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.exporters;

import com.elikya.apps.rhoe.desk.entity.ProductLog;
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
public class ProductLogExporter {

    private static Properties LANG;

    public static void export(List<ProductLog> logs) {
        LANG = ControlsHandler.getLanguage();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(LANG.getProperty("product_moves").toUpperCase());
        createHeaders(sheet);
        spreadData(logs, sheet);
        DataExporter.writeStream(workbook);
    }

    private static void createHeaders(XSSFSheet sheet) {
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue(LANG.getProperty("date").toUpperCase());
        header.createCell(1).setCellValue(LANG.getProperty("time").toUpperCase());
        header.createCell(2).setCellValue(LANG.getProperty("name").toUpperCase());
        header.createCell(3).setCellValue(LANG.getProperty("serial_number").toUpperCase());
        header.createCell(4).setCellValue(LANG.getProperty("product_number").toUpperCase());
        header.createCell(5).setCellValue(LANG.getProperty("action").toUpperCase());
        header.createCell(6).setCellValue(LANG.getProperty("quantity").toUpperCase());
        header.createCell(7).setCellValue(LANG.getProperty("unit_price_tax")
                .toUpperCase() + " (" + ApplicationCurrency.getActualCurrency() + ")");
        header.createCell(8).setCellValue(LANG.getProperty("total_price_tax")
                .toUpperCase() + " (" + ApplicationCurrency.getActualCurrency() + ")");
        header.createCell(9).setCellValue(LANG.getProperty("move_currency").toUpperCase());
        header.createCell(10).setCellValue(LANG.getProperty("move_rate").toUpperCase());
        header.createCell(11).setCellValue(LANG.getProperty("stock_quantity").toUpperCase());
        header.createCell(12).setCellValue(LANG.getProperty("reason").toUpperCase());
    }

    private static void spreadData(List<ProductLog> logs, XSSFSheet sheet) {
        int index = 1;
        XSSFRow row;
        for (ProductLog log : logs) {
            row = sheet.createRow(index);
            row.createCell(0).setCellValue(log.getLogDate().toString());
            row.createCell(1).setCellValue(log.getLogTime().toString());
            row.createCell(2).setCellValue(log.getProduct().getLabel());
            row.createCell(3).setCellValue(log.getProduct().getSerialNumber());
            row.createCell(4).setCellValue(log.getProduct().getNumber());
            row.createCell(5).setCellValue(LANG.getProperty(log.getLogAction()));
            row.createCell(6).setCellValue(log.getActionQty());
            row.createCell(7).setCellValue(NumbersFormatter.getFormattedString(log.getUnitPrice()));
            row.createCell(8).setCellValue(NumbersFormatter.getFormattedString(log.getTotalPrice()));
            row.createCell(9).setCellValue(log.getActualCurrency());
            row.createCell(10).setCellValue(log.getCurrencyRate());
            row.createCell(11).setCellValue(log.getStockQty());
            row.createCell(12).setCellValue(log.getReason());
            index++;
        }
    }
    
}

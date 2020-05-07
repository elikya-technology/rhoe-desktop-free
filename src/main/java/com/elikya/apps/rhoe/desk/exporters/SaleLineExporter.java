/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.exporters;

import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.entity.SaleLine;
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
public class SaleLineExporter {

    private static Properties LANG;
    private static String CURRENCY;

    public static void export(List<SaleLine> lines) {
        CURRENCY = ApplicationCurrency.getActualCurrency();
        LANG = ControlsHandler.getLanguage();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(LANG.getProperty("sale_details").toUpperCase());
        createHeader(sheet);
        spreadData(lines, sheet);
        DataExporter.writeStream(workbook);
    }

    private static void createHeader(XSSFSheet sheet) {
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue(LANG.getProperty("sale_number").toUpperCase());
        header.createCell(1).setCellValue(LANG.getProperty("name").toUpperCase());
        header.createCell(2).setCellValue(LANG.getProperty("serial_number").toUpperCase());
        header.createCell(3).setCellValue(LANG.getProperty("product_number").toUpperCase());
        header.createCell(4).setCellValue(LANG.getProperty("category").toUpperCase());
        header.createCell(5).setCellValue(LANG.getProperty("quantity").toUpperCase());
        header.createCell(6).setCellValue(LANG.getProperty("unit_price_tax")
                .toUpperCase() + " (" + CURRENCY + ")");
        header.createCell(7).setCellValue(LANG.getProperty("price")
                .toUpperCase() + " (" + CURRENCY + ")");
    }

    private static void spreadData(List<SaleLine> lines, XSSFSheet sheet) {
        int index = 1;
        XSSFRow row;
        for (SaleLine line : lines) {
            row = sheet.createRow(index);
            row.createCell(0).setCellValue(line.getSale().getNumber());
            Product product = line.getProduct();
            row.createCell(1).setCellValue(product.getLabel());
            row.createCell(2).setCellValue(product.getSerialNumber());
            row.createCell(3).setCellValue(product.getNumber());
            row.createCell(4).setCellValue(product.getCategory().getLabel());
            row.createCell(5).setCellValue(line.getQuantity());
            row.createCell(6).setCellValue(NumbersFormatter
                    .getFormattedString(line.getUnitPrice()));
            row.createCell(7).setCellValue(NumbersFormatter
                    .getFormattedString(line.getPrice()));
            index++;
        }
    }

}

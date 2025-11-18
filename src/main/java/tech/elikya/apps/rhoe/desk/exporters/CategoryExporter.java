/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.exporters;

import tech.elikya.apps.rhoe.desk.entity.Category;
import tech.elikya.apps.rhoe.desk.ui.ControlsHandler;
import java.util.List;
import java.util.Properties;

import tech.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import tech.elikya.apps.rhoe.desk.util.NumbersFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Mafole Loemelah
 */
public class CategoryExporter {

    private static Properties LANG;
    
    public static void export(List<Category> categories) {
        LANG = ControlsHandler.getLanguage();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(LANG.getProperty("categories").toUpperCase());
        createHeaders(sheet);
        spreadData(categories, sheet);
        DataExporter.writeStream(workbook);
    }

    private static void spreadData(List<Category> categories, XSSFSheet sheet) {
        int index = 1;
        XSSFRow row;
        for (Category category : categories) {
            row = sheet.createRow(index);
            row.createCell(0).setCellValue(category.getLabel());
            row.createCell(1).setCellValue(category.getNumber());
            row.createCell(2).setCellValue(category.getDescription());
            row.createCell(3).setCellValue(category.getProductsNumber());
            row.createCell(4).setCellValue(category.getProductsQty());
            row.createCell(5).setCellValue(NumbersFormatter
                    .getFormattedString(category.getProductsStockPrice()));
            index++;
        }
    }

    private static void createHeaders(XSSFSheet sheet) {
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue(LANG.getProperty("name").toUpperCase());
        header.createCell(1).setCellValue(LANG.getProperty("category_number").toUpperCase());
        header.createCell(2).setCellValue(LANG.getProperty("description").toUpperCase());
        header.createCell(3).setCellValue(LANG.getProperty("products_number").toUpperCase());
        header.createCell(4).setCellValue(LANG.getProperty("products_quantity").toUpperCase());
        header.createCell(5).setCellValue(LANG.getProperty("price").toUpperCase()
                + " (" + ApplicationCurrency.getActualCurrency() + ")");
    }
}

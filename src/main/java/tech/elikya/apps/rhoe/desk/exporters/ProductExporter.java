/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.exporters;

import tech.elikya.apps.rhoe.desk.entity.Product;
import tech.elikya.apps.rhoe.desk.ui.ControlsHandler;
import tech.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import tech.elikya.apps.rhoe.desk.util.NumbersFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Mafole Loemelah
 */
public class ProductExporter {

    private static Properties LANG;
    private static String CURRENCY;
    
    public static void export(List<Product> products, UnitPriceContext context) {
        LANG = ControlsHandler.getLanguage();
        CURRENCY = ApplicationCurrency.getActualCurrency();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(LANG.getProperty("products").toUpperCase());
        createHeader(sheet);
        spreadData(products, sheet, context);
        DataExporter.writeStream(workbook);
    }

    private static void spreadData(List<Product> products, XSSFSheet sheet, UnitPriceContext context) {
        boolean unitPriceWithTax = context.equals(UnitPriceContext.WITH_TAX);
        BigDecimal concernedUnitPrice;
        int index = 1;
        XSSFRow row;
        for (Product product : products) {
            row = sheet.createRow(index);
            row.createCell(0).setCellValue(product.getLabel());
            row.createCell(1).setCellValue(product.getSerialNumber());
            row.createCell(2).setCellValue(product.getNumber());
            row.createCell(3).setCellValue(product.getBarCode().isEmpty()
                    ? "-" : product.getBarCode());
            concernedUnitPrice = computeConcernedUnitPrice(unitPriceWithTax, product);
            row.createCell(4).setCellValue(NumbersFormatter.getFormattedString(concernedUnitPrice));
            row.createCell(5).setCellValue(product.getStockQuantity());
            row.createCell(6).setCellValue(NumbersFormatter.getFormattedString
                    (concernedUnitPrice.multiply(BigDecimal.valueOf(product.getStockQuantity()))));
            row.createCell(7).setCellValue(product.getMaximumQuantity());
            row.createCell(8).setCellValue(product.getMinimumQuantity());
            row.createCell(9).setCellValue(product.getCategory().getLabel());
            row.createCell(10).setCellValue(product.getProvider().getLabel());
            index++;
        }
    }

    private static BigDecimal computeConcernedUnitPrice(boolean unitPriceWithTax, Product product) {
        return unitPriceWithTax ? product.getConvertedUnitPriceTax()
                : product.getConvertedUnitPrice();
    }

    private static void createHeader(XSSFSheet sheet) {
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue(LANG.getProperty("name").toUpperCase());
        header.createCell(1).setCellValue(LANG.getProperty("serial_number").toUpperCase());
        header.createCell(2).setCellValue(LANG.getProperty("product_number").toUpperCase());
        header.createCell(3).setCellValue(LANG.getProperty("barcode").toUpperCase());
        header.createCell(4).setCellValue(LANG.getProperty("unit_price_tax")
                .toUpperCase() + " (" + CURRENCY + ")");
        header.createCell(5).setCellValue(LANG.getProperty("stock_quantity").toUpperCase());
        header.createCell(6).setCellValue(LANG.getProperty("price")
                .toUpperCase() + " (" + CURRENCY + ")");
        header.createCell(7).setCellValue(LANG.getProperty("maximum_quantity").toUpperCase());
        header.createCell(8).setCellValue(LANG.getProperty("minimum_quantity").toUpperCase());
        header.createCell(9).setCellValue(LANG.getProperty("category").toUpperCase());
        header.createCell(10).setCellValue(LANG.getProperty("provider").toUpperCase());
    }

    public enum UnitPriceContext {WITH_TAX, WITHOUT_TAX}
    
}

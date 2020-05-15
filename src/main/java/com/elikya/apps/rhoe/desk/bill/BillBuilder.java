/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.bill;

import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.entity.Sale;
import com.elikya.apps.rhoe.desk.entity.SaleLine;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import com.elikya.apps.rhoe.desk.configs.RhoeConfig;
import com.elikya.apps.rhoe.desk.util.NumbersFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.JobSettings;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 *
 * @author Mafole Loemelah
 */
public class BillBuilder {

    public static final int BOX_WIDTH = 400;
    public static final int LIGNS_BOX_WITH = 90;
    private static Properties options = RhoeConfig.get();
    private static Properties lang;

    private static VBox getEnterpriseInfos() {
        String style = "-fx-font-weight: bold; -fx-font-size: 14;";
        Label enterprise = new Label(options.getProperty("enterprise"));
        enterprise.setStyle(style);
        Label slogan = new Label(options.getProperty("business_words"));
        slogan.setStyle(style);
        Label address = new Label(options.getProperty("address"));
        address.setStyle(style);
        VBox box = new VBox();
        box.getChildren().addAll(enterprise, slogan, address, separate());
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(BOX_WIDTH);
        return box;
    }

    private static VBox getSaleInfos(Sale sale) {
        Label number = new Label(lang.getProperty("sale_number") + "\t\t:\t" + sale.getNumber());
        Label date = new Label(lang.getProperty("date") + "\t\t\t:\t" + sale.getSaleDate());
        Label time = new Label(lang.getProperty("time") + "\t\t:\t" + sale.getSaleTime()
                .format(DateTimeFormatter.ofPattern("hh:mm:ss")));
        return new VBox(number, date, time, separate());
    }

    private static void getOrderHeader(VBox prodBox, VBox upBox, VBox qtyBox, VBox priceBox) {
        Label product = new Label(lang.getProperty("product"));
        String style = "-fx-underline: true; -fx-font-size: 14;";
        product.setStyle(style);
        Label up = new Label(lang.getProperty("unit_price_tax"));
        up.setStyle(style);
        Label qty = new Label(lang.getProperty("qty"));
        qty.setStyle(style);
        Label price = new Label(lang.getProperty("price"));
        price.setStyle(style);
        prodBox.getChildren().addAll(product);
        upBox.getChildren().addAll(up);
        qtyBox.getChildren().add(qty);
        priceBox.getChildren().add(price);
    }

    private static VBox getLigns(List<SaleLine> ligns) {
        VBox prodBox = new VBox();
        prodBox.setPrefWidth(LIGNS_BOX_WITH);
        prodBox.setSpacing(10);
        VBox upBox = new VBox();
        upBox.setPrefWidth(LIGNS_BOX_WITH);
        upBox.setSpacing(10);
        upBox.setAlignment(Pos.CENTER);
        VBox qtyBox = new VBox();
        qtyBox.setSpacing(10);
        qtyBox.setPrefWidth(LIGNS_BOX_WITH);
        qtyBox.setAlignment(Pos.CENTER);
        VBox priceBox = new VBox();
        priceBox.setPrefWidth(LIGNS_BOX_WITH);
        priceBox.setSpacing(10);
        priceBox.setAlignment(Pos.TOP_RIGHT);
        getOrderHeader(prodBox, upBox, qtyBox, priceBox);
        processLigns(ligns, prodBox, upBox, qtyBox, priceBox);
        HBox box = new HBox();
        box.setPrefWidth(BOX_WIDTH);
        box.setSpacing(10);
        box.getChildren().addAll(prodBox, upBox, qtyBox, priceBox);
        box.setAlignment(Pos.CENTER);
        VBox parent = new VBox();
        parent.getChildren().addAll(box, separate());
        return parent;
    }

    private static void processLigns(List<SaleLine> ligns, VBox prodBox, VBox upBox, VBox qtyBox, VBox priceBox) {
        ligns.forEach(it -> {
            Product product = it.getProduct();
            Text prod = new Text(product.getLabel() + " " + product.getSerialNumber());
            prodBox.getChildren().add(prod);
            BigDecimal convertedPrice = it.getPrice().multiply(BigDecimal
                    .valueOf(ApplicationCurrency.getActualRate()));
            BigDecimal upValue = convertedPrice.divide(BigDecimal.valueOf(it.getQuantity()), 3);
            Label up = new Label(NumbersFormatter.getFormattedString(upValue));
            upBox.getChildren().add(up);
            Label qty = new Label(String.valueOf(it.getQuantity()));
            qtyBox.getChildren().add(qty);
            Label price = new Label(NumbersFormatter.getFormattedString(convertedPrice));
            priceBox.getChildren().add(price);
        });
    }

    private static VBox separate() {
        Label separator = new Label("-----------------------------" +
                "--------------------------------------------------\n");
        VBox box = new VBox();
        box.setPrefWidth(BOX_WIDTH);
        box.getChildren().add(separator);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private static VBox resumePrices(Sale sale) {
        VBox box = new VBox();
        String currency = ApplicationCurrency.getActualCurrency();
        BigDecimal actualRate = BigDecimal.valueOf(ApplicationCurrency.getActualRate());
        BigDecimal total = sale.getTotalPrice().multiply(actualRate);
        Label totalET = new Label(lang.getProperty("total_price") + " ("
                + currency + ")\t:\t\t" + NumbersFormatter.getFormattedString(total));
        box.getChildren().add(totalET);
        BigDecimal taxedPriceValue = sale.getTaxedPrice().multiply(actualRate);
        Label taxedPrice = new Label(lang.getProperty("total_price_tax") + " ("
                + currency + ")\t\t:\t\t" + NumbersFormatter.getFormattedString(taxedPriceValue));
        BigDecimal moneyReceivedValue = sale.getMoneyReceived();
        Label moneyReceived = new Label(lang.getProperty("money_received") + " ("
                + currency + ")\t\t:\t\t" + NumbersFormatter.getFormattedString(moneyReceivedValue));
        BigDecimal diff = moneyReceivedValue.subtract(taxedPriceValue);
        Label difference = new Label(lang.getProperty("rest") + " ("
                + currency + ")\t\t:\t\t" + NumbersFormatter.getFormattedString(diff));
        box.getChildren().addAll(taxedPrice, moneyReceived, difference, separate());
        box.setSpacing(5);
        box.setAlignment(Pos.BOTTOM_RIGHT);
        return box;
    }

    private static VBox getFooter() {
        Label text = new Label(lang.getProperty("thank_you"));
        text.setStyle("-fx-font-weight: bold;");
        VBox box = new VBox();
        box.setPrefWidth(BOX_WIDTH);
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(text);
        return box;
    }

    public static void buildAndPrint(Sale sale) {
        lang = ControlsHandler.getLanguage();
        VBox box = new VBox();
        box.getChildren().addAll(getEnterpriseInfos(), getSaleInfos(sale)
                , getLigns(sale.getLines()), resumePrices(sale), getFooter());
        box.setStyle("-fx-font-family: Arial;");
        box.setPadding(new Insets(10, 10, 10, 10));
        print(box);
    }

    private static void print(VBox box) {
        Optional<PrinterJob> printerJob = Optional.ofNullable(PrinterJob.createPrinterJob());
        printerJob.ifPresent(it -> {
            resizeBox(box, it);
            boolean printed = it.printPage(box);
            if (printed) it.endJob();
            else Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("not_printed"));
        });
    }

    private static void resizeBox(VBox box, PrinterJob it) {
        JobSettings jobSettings = it.getJobSettings();
        PageLayout pageLayout = jobSettings.getPageLayout();
        double pageWidth = pageLayout.getPrintableWidth();
        double pageHeight = pageLayout.getPrintableHeight();
        box.setPrefSize(pageWidth, pageHeight);
    }
}

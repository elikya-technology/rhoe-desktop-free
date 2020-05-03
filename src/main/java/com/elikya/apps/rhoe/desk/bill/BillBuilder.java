/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.bill;

import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.entity.Sale;
import com.elikya.apps.rhoe.desk.entity.SaleLine;
import com.elikya.apps.rhoe.desk.entity.Tax;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import com.elikya.apps.rhoe.desk.util.NumbersFormatter;
import com.elikya.apps.rhoe.desk.util.Configs;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

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

    private static Properties options = Configs.get();
    private static Properties lang = ControlsHandler.getLanguage();

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
        box.setPrefWidth(400);
        return box;
    }

    private static VBox getSaleInfos(Sale sale) {
        Label number = new Label(lang.getProperty("sale_number") + "\t:\t" + sale.getNumber());
        Label date = new Label(lang.getProperty("date") + "\t\t:\t" + sale.getSaleDate());
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
        prodBox.setPrefWidth(90);
        prodBox.setSpacing(10);
        VBox upBox = new VBox();
        upBox.setPrefWidth(90);
        upBox.setSpacing(10);
        upBox.setAlignment(Pos.CENTER);
        VBox qtyBox = new VBox();
        qtyBox.setSpacing(10);
        qtyBox.setPrefWidth(90);
        qtyBox.setAlignment(Pos.CENTER);
        VBox priceBox = new VBox();
        priceBox.setPrefWidth(90);
        priceBox.setSpacing(10);
        priceBox.setAlignment(Pos.TOP_RIGHT);
        getOrderHeader(prodBox, upBox, qtyBox, priceBox);
        processLigns(ligns, prodBox, upBox, qtyBox, priceBox);
        HBox box = new HBox();
        box.setPrefWidth(400);
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
            BigDecimal upValue = it.getPrice().divide(BigDecimal.valueOf(it.getQuantity()), 3);
            Label up = new Label(String.valueOf(upValue));
            upBox.getChildren().add(up);
            Label qty = new Label(String.valueOf(it.getQuantity()));
            qtyBox.getChildren().add(qty);
            Label price = new Label(String.valueOf(it.getPrice()));
            priceBox.getChildren().add(price);
        });
    }

    private static VBox separate() {
        Label separator = new Label("-----------------------------" +
                "--------------------------------------------------");
        VBox box = new VBox();
        box.setPrefWidth(400);
        box.getChildren().add(separator);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private static VBox computePrices(Sale sale) {
        VBox box = new VBox();
        BigDecimal totalPrice = sale.getTotalPrice();
        String currency = ApplicationCurrency.getActualCurrency();
        Label totalET = new Label(lang.getProperty("total_price") + " ("
                + currency + ")\t:\t\t" + totalPrice.toString());
        box.getChildren().add(totalET);
        computeTax(box, totalPrice);
        BigDecimal taxedPriceValue = sale.getTaxedPrice();
        Label taxedPrice = new Label(lang.getProperty("total_price_tax") + " ("
                + currency + ")\t:\t\t" + taxedPriceValue);
        BigDecimal moneyReceivedValue = sale.getMoneyReceived();
        Label moneyReceived = new Label(lang.getProperty("money_received") + " ("
                + currency + ")\t:\t\t" + moneyReceivedValue.toString());
        BigDecimal diff = moneyReceivedValue.subtract(taxedPriceValue);
        Label difference = new Label(lang.getProperty("rest") + " ("
                + currency + ")\t:\t\t" + NumbersFormatter.getFormattedString(diff));
        box.getChildren().addAll(taxedPrice, moneyReceived, difference, separate());
        box.setSpacing(5);
        box.setAlignment(Pos.BOTTOM_RIGHT);
        return box;
    }

    private static void computeTax(VBox box, BigDecimal totalPrice) {
//        taxList.forEach(it -> {
//            Label text = new Label();
//            BigDecimal cost = it.getCost();
//            if (cost.doubleValue() > 0) {
//                text.setText(it.getName() + " (" + ApplicationCurrency.getActualCurrency() + ")\t:\t\t" + cost);
//            } else {
//                BigDecimal value = (totalPrice.multiply(it.getPercent()))
//                        .divide(BigDecimal.valueOf(100), 3);
//                text.setText(it.getName() + " (" + it.getPercent() + "%)\t:\t\t" + value);
//            }
//            box.getChildren().add(text);
//        });
    }

    private static VBox getFooter() {
        Label text = new Label(lang.getProperty("thank_you"));
        text.setStyle("-fx-font-weight: bold;");
        VBox box = new VBox();
        box.setPrefWidth(400);
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(text);
        return box;
    }

    public static void buildAndPrint(Sale sale) {
        VBox box = new VBox();
        box.setPrefWidth(400);
        box.getChildren().addAll(getEnterpriseInfos(), getSaleInfos(sale), getFooter());
        box.setStyle("-fx-font-family: Arial;");
        box.setPadding(new Insets(10, 10, 10, 10));
//        Scene scene = new Scene(box);
//        Stage stage = new Stage();
//        stage.setScene(scene);
//        stage.show();
        print(box);
    }

    private static void print(VBox box) {
//        Printer printer = Printer.getDefaultPrinter();
//        PageLayout pageLayout = printer.createPageLayout(Paper.NA_LETTER, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
//        double scaleX = pageLayout.getPrintableWidth() / box.getBoundsInParent().getWidth();
//        double scaleY = pageLayout.getPrintableHeight() / box.getBoundsInParent().getHeight();
//        box.getTransforms().add(new Scale(scaleX, scaleY));
//
//        System.out.println(printer.getName());

        Optional<PrinterJob> printerJob = Optional.ofNullable(PrinterJob.createPrinterJob());
        printerJob.ifPresent(it -> {
            boolean printed = it.printPage(box);
            if (!printed) {
                Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("not_printed"));
            }
        });
    }
}

/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.ui;

/**
 *
 * @author Mafole Loemelah
 */
public enum StagesPaths {
    
    ABOUT("/fxml/About.fxml"),
    DELETION_DIALOG("/fxml/DeletionDialog.fxml"),
    ERROR_NOTIF("/fxml/ErrorNotif.fxml"),
    ENTERPRISE("/fxml/Enterprise.fxml"),
    EXIT_APPLICATION("/fxml/ExitApplication.fxml"),
    FEEDBACK("/fxml/Feedback.fxml"),
    INFO_NOTIF("/fxml/InfoNotif.fxml"),
    ITEMS_PRODUCT("/fxml/ItemsProducts.fxml"),
    LANGUAGE("/fxml/Language.fxml"),
    LOGIN("/fxml/Login.fxml"),
    LOGS_RESUME("/fxml/LogsResume.fxml"),
    MAIN("/fxml/Main.fxml"),
    MENU("/fxml/Menu.fxml"),
    MORE("/fxml/More.fxml"),
    PRODUCT_HANDLER("/fxml/ProductHandler.fxml"),
    NEW_SALE("/fxml/NewSale.fxml"),
    OPTIONS("/fxml/Options.fxml"),
    PRELOADER("/fxml/Preloader.fxml"),
    PRODUCT_QUANTITY("/fxml/ProductQuantity.fxml"),
    PRODUCT_LOGS("/fxml/ProductStockDetails.fxml"),
    PRODUCTS("/fxml/Products.fxml"),
    REPORTS("/fxml/Reports.fxml"),
    SALE_DETAILS("/fxml/SaleDetails.fxml"),
    SALES("/fxml/Sales.fxml"),
    STOCK_UP("/fxml/StockUp.fxml"),
    STARTER_MORE("/fxml/StarterMore.fxml"),
    STARTER_PRODUCTS("/fxml/StarterProducts.fxml"),
    STARTER_CHART("/fxml/StarterChart.fxml"),
    STARTER_SALES("/fxml/StarterSales.fxml"),
    STARTER_OPTIONS("/fxml/StarterOptions.fxml"),
    SUCCESS_NOTIF("/fxml/SuccessNotif.fxml"),
    TAXES("/fxml/Taxes.fxml"),
    WARNING_NOTIF("/fxml/WarningNotif.fxml"),
    WELCOME("/fxml/Welcome.fxml"),
    WITHDRAW("/fxml/Withdraw.fxml");

    private final String path;

    StagesPaths(String path) { this.path = path; }

    public String getPath() { return path; }

}

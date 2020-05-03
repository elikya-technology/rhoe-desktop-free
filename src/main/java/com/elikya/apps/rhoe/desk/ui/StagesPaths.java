/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.ui;

/**
 *
 * @author Mafole Loemelah
 */
public enum StagesPaths {
    
    ABOUT("/fxml/About.fxml"),
    ACCOUNT("/fxml/Account.fxml"),
    CODE_VERIFIER("/fxml/CodeVerifier.fxml"),
    DELETION_DIALOG("/fxml/DeletionDialog.fxml"),
    ERROR_NOTIF("/fxml/ErrorNotif.fxml"),
    ENTERPRISE("/fxml/Enterprise.fxml"),
    EXIT_APPLICATION("/fxml/ExitApplication.fxml"),
    FEEDBACK("/fxml/Feedback.fxml"),
    HELP("/fxml/Help.fxml"),
    INFO_NOTIF("/fxml/InfoNotif.fxml"),
    ITEMS_PRODUCT("/fxml/ItemsProducts.fxml"),
    LANGUAGE("/fxml/Language.fxml"),
    LOGIN("/fxml/Login.fxml"),
    LOGS_RESUME("/fxml/LogsResume.fxml"),
    MAIN("/fxml/Main.fxml"),
    MENU("/fxml/Menu.fxml"),
    MORE("/fxml/More.fxml"),
    NEW_PASSWORD("/fxml/Password.fxml"),
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
    SUCCESS_NOTIF("/fxml/SuccessNotif.fxml"),
    TAXES("/fxml/Taxes.fxml"),
    WARNING_NOTIF("/fxml/WarningNotif.fxml"),
    WITHDRAW("/fxml/Withdraw.fxml");

    private final String path;

    StagesPaths(String path) { this.path = path; }

    public String getPath() { return path; }

}

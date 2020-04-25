/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.util;

import com.elikya.apps.rhoe.desk.entity.Product;
import com.elikya.apps.rhoe.desk.entity.SaleLine;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;

/**
 *
 * @author Mafole Loemelah
 */
public class TableViewOperation {

    public static void handleSelection(TableView<?> table) {
        table.getSelectionModel().getSelectedItems().addListener((
                ListChangeListener.Change<?> selection) -> {
            int size = selection.getList().size();
            switch (size) {
                case 0: disableItems(table.getContextMenu(), MenuItemType.GENERAL, true);
                    break;
                case 1: disableItems(table.getContextMenu(), MenuItemType.GENERAL, false);
                    break;
                default: 
                    disableItems(table.getContextMenu(), MenuItemType.GENERAL, false);
                    disableItems(table.getContextMenu(), MenuItemType.SINGULAR, true);
                    break;
            }
        });
    }
    
    private static void disableItems(ContextMenu menu, MenuItemType type, boolean value) {
        if (type.equals(MenuItemType.GENERAL))
            menu.getItems().forEach(item -> item.setDisable(value));
        else
            menu.getItems().stream().filter(item -> item.getClass() != SeparatorMenuItem.class)
                    .filter(item -> item.getId().startsWith("_")).forEachOrdered(item -> item.setDisable(value));
    }

    public static void setTableSelectionModel(TableView<?> table) {
        TableView.TableViewSelectionModel<?> selectionMode = table.getSelectionModel();
        selectionMode.setSelectionMode(SelectionMode.MULTIPLE);
    }
    
    public static void setSaleProductsTableCellFactory(FactoryContext context, TableColumn<SaleLine, Product> column) {
        column.setCellFactory((param) -> new TableCell<SaleLine, Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty)
                    switch (context) {
                        case CATEGORY: setText(String.valueOf(item.getCategory())); break;
                        case NAME: setText(item.getLabel()); break;
                        case PRODUCT_NUMBER: setText(item.getNumber()); break;
                        case SERIAL_NUMBER: setText(item.getSerialNumber()); break;
                        default: setText(NumbersFormatter
                                .getFormattedString(item.getConvertedUnitPriceTax())); break;
                    }
            }
        });
    }
      
    private enum MenuItemType { SINGULAR, GENERAL }
    
    public   enum FactoryContext {NAME, PRODUCT_NUMBER, SERIAL_NUMBER, UNIT_PRICE, CATEGORY}
}

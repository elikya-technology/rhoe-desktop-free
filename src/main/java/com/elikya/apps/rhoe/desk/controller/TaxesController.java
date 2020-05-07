/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.exporters.TaxExporter;
import com.elikya.apps.rhoe.desk.entity.Tax;
import com.elikya.apps.rhoe.desk.service.TaxService;
import com.elikya.apps.rhoe.desk.observers.impl.ValidationObserverImpl;
import com.elikya.apps.rhoe.desk.observers.interfaces.ValidationObserver;
import com.elikya.apps.rhoe.desk.ui.*;
import com.elikya.apps.rhoe.desk.util.ApplicationCurrency;
import com.elikya.apps.rhoe.desk.util.NumbersFormatter;
import com.elikya.apps.rhoe.desk.util.Configs;
import com.elikya.apps.rhoe.desk.util.TableViewOperation;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class TaxesController implements Initializable, ValidationObserver {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private JFXTextField nameField;
    @FXML private JFXTextField costField;
    @FXML private JFXTextField percentField;
    @FXML private JFXTextArea descriptionArea;
    @FXML private JFXButton save;
    @FXML private CustomTextField searchText;
    @FXML private TableView<Tax> taxesTable;
    @FXML private TableColumn<Tax, String> name;
    @FXML private TableColumn<Tax, BigDecimal> cost;
    @FXML private TableColumn<Tax, BigDecimal> percent;
    @FXML private TableColumn<Tax, Double> description;
    @FXML private MenuItem excel;
    @FXML private MenuItem delete;
    @FXML private AnchorPane formPane;
    @FXML private MenuItem _edit;
    @FXML private JFXButton searchBtn;
    @FXML private SplitPane splitPane;

    private TaxService taxService;

    private Tax updatableTax;
    private List<Tax> taxList;
    private Properties lang;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lang = ControlsHandler.getLanguage();
        ValidationObserverImpl.register(this);
        TableViewOperation.setTableSelectionModel(taxesTable);
        TableViewOperation.handleSelection(taxesTable);
        ControlsHandler.keepFloatValues(costField);
        ControlsHandler.keepFloatValues(percentField);
        ControlsHandler.handleSearchZone(searchText, searchBtn);
        setCellsValueFactory();
        setPercentCellFactory();
        setCostCellFactory();
        setLanguage();
        addEventHandlers();
        setFormEventHandler();
        handleNumerics(costField, percentField);
        handleNumerics(percentField, costField);
        queryTaxes();
        putTaxesInTable();
        SplitPaneDividerManager.resize(splitPane);
    }

    @Autowired
    private void setTaxService(TaxService taxService) {
        this.taxService = taxService;
    }

    public void addEventHandlers() {
        setCloseEventHandler();
        setSaveEventHandler();
        setNameEventHandler();
        setDeleteEventHandler();
        setExcelEventHandler();
        setEditEventHandler();
    }

    public void putTaxesInTable() {
        FilteredList<Tax> items = filterTaxes();
        SortedList<Tax> sortedItems = sortTaxes(items);
        taxesTable.setItems(sortedItems);
        taxesTable.refresh();
        setSearchFieldProperty(items);
    }

    private SortedList<Tax> sortTaxes(FilteredList<Tax> items) {
        SortedList<Tax> sortedItems = new SortedList<>(items);
        sortedItems.comparatorProperty().bind(taxesTable.comparatorProperty());
        return sortedItems;
    }

    private FilteredList<Tax> filterTaxes() {
        return new FilteredList<>(FXCollections
                    .observableArrayList(taxList), data -> true);
    }

    @Override
    public void processUpdateValidation() {
        updatableTax = taxesTable.getSelectionModel().getSelectedItem();
        fillForm();
        ControlsHandler.disableControls(taxesTable, searchText, true);
    }

    @Override
    public void processDeletionValidation() {
        Platform.runLater(() -> {
            List<Tax> items = taxesTable.getSelectionModel().getSelectedItems();
            taxService.deleteAll(items);
            taxList.removeAll(items);
            putTaxesInTable();
        });
    }

    private void queryTaxes() {
        taxList = taxService.getAll();
    }

    public void setLanguage() {
        title.setText(lang.getProperty("taxes"));
        nameField.setPromptText(lang.getProperty("name"));
        name.setText(lang.getProperty("name"));
        costField.setPromptText(lang.getProperty("cost")
                .concat(" (" + Configs.get()
                        .getProperty("currency") + ")"));
        cost.setText(lang.getProperty("cost") + " ("
                + ApplicationCurrency.getActualCurrency() + ")");
        percentField.setPromptText(lang.getProperty("percent") + " (%)");
        percent.setText(lang.getProperty("percent") + " (%)");
        descriptionArea.setPromptText(lang.getProperty("description"));
        description.setText(lang.getProperty("description"));
        save.setText(lang.getProperty("save"));
        searchText.setPromptText(lang.getProperty("search"));
        excel.setText(lang.getProperty("export"));
        _edit.setText(lang.getProperty("edit"));
        delete.setText(lang.getProperty("delete"));
        taxesTable.setPlaceholder(new Label(
                lang.getProperty("table_prompt")));
    }
    
    private void setCellsValueFactory() {
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        cost.setCellValueFactory(new PropertyValueFactory<>("convertedCost"));
        percent.setCellValueFactory(new PropertyValueFactory<>("percent"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    private void setCostCellFactory() {
        cost.setCellFactory(param -> new TableCell<Tax, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                if (!empty) {
                    if (item.doubleValue() <= 0) setText("-");
                    else setText(NumbersFormatter.getFormattedString(item));
                }
            }
        });
    }

    private void setCloseEventHandler() {close.setOnAction(event -> {
        ValidationObserverImpl.unregister(this);
        Stages.close(event);
    });}
    
    private void setSaveEventHandler() {
        save.setOnAction(event -> Platform.runLater(() -> {
            if (costValueIsEmpty() && percentageValueIsEmpty())
                Notifier.notify(StagesPaths.WARNING_NOTIF, lang.getProperty("no_tax_value"));
            else choosePersistenceAction();
        }));
    }

    private void choosePersistenceAction(){
        try {
            double costValue = getCostValue();
            double percentValue = getPercentageValue();
            if (updatableTax != null) processUpdate(costValue, percentValue);
            else processAdding(costValue, percentValue);
            emptyFields();
            taxesTable.refresh();
        } catch (DataIntegrityViolationException exception) {
            Notifier.notify(StagesPaths.WARNING_NOTIF, lang.getProperty("tax_name_taken"));
        } catch(NumberFormatException exception) {
            Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("invalid_tax_value"));
        }
    }

    private double getCostValue() {
        return costValueIsEmpty() ? 0 : Double.parseDouble(costField.getText());
    }

    private boolean costValueIsEmpty() {
        return costField.getText().trim().isEmpty();
    }

    private double getPercentageValue() {
        return percentageValueIsEmpty() ? 0 : Double.parseDouble(percentField.getText());
    }

    private boolean percentageValueIsEmpty() {
        return percentField.getText().trim().isEmpty();
    }

    private void processAdding(double costValue, double percentValue) {
        Tax tax = new Tax(nameField.getText(), BigDecimal.valueOf(percentValue),
                BigDecimal.valueOf(costValue), descriptionArea.getText());
        taxService.save(tax);
        taxList.add(taxService.getLast());
        putTaxesInTable();
    }

    private void processUpdate(double costValue, double percentValue) {
        Tax.update(updatableTax, nameField.getText(), BigDecimal.valueOf(percentValue)
                , BigDecimal.valueOf(costValue), descriptionArea.getText());
        taxService.update(updatableTax);
        queryTaxes();
        putTaxesInTable();
        ControlsHandler.disableControls(taxesTable, searchText, false);
    }

    private void setNameEventHandler() {
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) save.setDisable(true);
            else save.setDisable(false);
        });
    }
    
    private void setDeleteEventHandler() {
        delete.setOnAction(e -> Stages.showDialog(StagesPaths.DELETION_DIALOG));
    }
    
    private void setExcelEventHandler() {
        excel.setOnAction(e -> Platform.runLater(() -> {
            List<Tax> items = taxesTable.getSelectionModel().getSelectedItems();
            if (!items.isEmpty())
                TaxExporter.export(items);
        }));
    }
    
    private void setEditEventHandler() {
        _edit.setOnAction(event -> {
            CodeVerifierController.setContext(CodeVerifierController.VerificationContext.UPDATING);
            Stages.showDialog(StagesPaths.CODE_VERIFIER);
        });
    }
    
    private void setSearchFieldProperty(FilteredList<Tax> items) {
        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) items.setPredicate(p -> true);
            else {
                String text = newValue.toLowerCase();
                filterTax(items, text);
                taxesTable.refresh();
            }
        });
    }
    
    private void handleNumerics(JFXTextField first, JFXTextField second) {
        first.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                second.setEditable(true);
                second.setFocusTraversable(true);
            } else {
                second.setEditable(false);
                second.setFocusTraversable(false);
            }
        });
    }
    
    private void setPercentCellFactory() {
        percent.setCellFactory(param -> new TableCell<Tax, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                if (!empty)
                    if (item.doubleValue() <= 0) setText("-");
                    else setText(NumbersFormatter.getFormattedString(item));
            }
        });
    }
    
    private void emptyFields() {
        nameField.clear();
        costField.clear();
        percentField.clear();
        descriptionArea.clear();
        if (updatableTax != null) updatableTax = null;
        if (taxesTable.isDisabled())
            ControlsHandler.disableControls(taxesTable, searchText, false);
        nameField.requestFocus();
    }
    
    private void fillForm() {
        nameField.setText(updatableTax.getName());
        if (updatableTax.getCost().doubleValue() > 0)
            costField.setText(String.valueOf(updatableTax.getCost()));
        if (updatableTax.getPercent().doubleValue() > 0)
            percentField.setText(String.valueOf(updatableTax.getPercent()));
        descriptionArea.setText(updatableTax.getDescription());
    }
    
    private void setFormEventHandler() {
        ContextMenu contextMenu = new ContextMenu();
        formPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem item = new MenuItem(lang.getProperty("empty_fields"));
                item.setOnAction(e -> {
                    emptyFields();
                    contextMenu.hide();
                });
                contextMenu.getItems().setAll(item);
                contextMenu.setHideOnEscape(true);
                disableFormMenuItem(item);
                contextMenu.show(formPane, event.getScreenX(), event.getScreenY());
            }
        });
        formPane.setOnMousePressed(event -> contextMenu.hide());
    }
    
    private void disableFormMenuItem(MenuItem item) {
        if (save.isDisabled()) item.setDisable(true);
        else item.setDisable(false);
    }

    private void filterTax(FilteredList<Tax> items, String text) {
        Platform.runLater(() -> items.setPredicate(prod -> prod.getName().toLowerCase().contains(text)
                || prod.getDescription().toLowerCase().contains(text)
                || String.valueOf(prod.getCost()).contains(text)
                || String.valueOf(prod.getPercent()).contains(text)));
    }
    
}

/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.entity.Payment;
import com.elikya.apps.rhoe.desk.observers.interfaces.CRUDMaster;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class DebtsController implements Initializable, CRUDMaster {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private SplitPane splitPane;
    @FXML private TableView<?> debtsTable;
    @FXML private TableColumn<?, ?> saleNumber;
    @FXML private TableColumn<?, ?> amount;
    @FXML private TableColumn<?, ?> dateTime;
    @FXML private TableColumn<?, ?> rest;
    @FXML private MenuItem excel;
    @FXML private MenuItem _edit;
    @FXML private MenuItem delete;
    @FXML private CustomTextField searchText;
    @FXML private JFXButton searchBtn;
    @FXML private AnchorPane formPane;
    @FXML private JFXTextField nameField;
    @FXML private JFXTextField percentField;
    @FXML private JFXTextField costField;
    @FXML private JFXTextArea descriptionArea;
    @FXML private JFXButton save;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setCloseEventHandler();
        setLanguage();
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setLanguage() {
        Properties lang = ControlsHandler.getLanguage();
        title.setText(lang.getProperty("debts"));
    }


}

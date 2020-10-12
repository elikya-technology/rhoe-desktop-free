/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.entity.Payment;
import com.elikya.apps.rhoe.desk.observers.interfaces.CRUDMaster;
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
    @FXML private JFXTextField nameField;
    @FXML private JFXTextField costField;
    @FXML private JFXTextField percentField;
    @FXML private JFXTextArea descriptionArea;
    @FXML private JFXButton save;
    @FXML private CustomTextField searchText;
    @FXML private TableView<Payment> taxesTable;
    @FXML private TableColumn<Payment, String> name;
    @FXML private TableColumn<Payment, BigDecimal> cost;
    @FXML private TableColumn<Payment, BigDecimal> percent;
    @FXML private TableColumn<Payment, Double> description;
    @FXML private MenuItem excel;
    @FXML private MenuItem delete;
    @FXML private AnchorPane formPane;
    @FXML private MenuItem _edit;
    @FXML private JFXButton searchBtn;
    @FXML private SplitPane splitPane;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setCloseEventHandler();
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }


}

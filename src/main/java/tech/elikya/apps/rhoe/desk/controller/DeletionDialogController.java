/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.controller;

import tech.elikya.apps.rhoe.desk.observers.impl.CRUDMasterImpl;
import tech.elikya.apps.rhoe.desk.ui.ControlsHandler;
import tech.elikya.apps.rhoe.desk.ui.Stages;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class DeletionDialogController implements Initializable {

    @FXML
    private Label text;
    @FXML
    private JFXButton yes;
    @FXML
    private Label title;
    @FXML
    private JFXButton close;
    @FXML
    private JFXButton no;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setLanguage();
        setCloseEventHandler();
        setNoEventHandler();
        setYesEventHandler();
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setLanguage() {
        Properties lang = ControlsHandler.getLanguage();
        title.setText(lang.getProperty("deletion"));
        text.setText(lang.getProperty("deletion_question"));
        yes.setText(lang.getProperty("yes"));
        no.setText(lang.getProperty("no"));
    }

    private void setNoEventHandler() {
        no.setOnAction(Stages::close);
    }

    private void setYesEventHandler() {
        yes.setOnAction(event -> {
            Stages.close(event);
            CRUDMasterImpl.executeDeleteRecord();
        });
    }

}

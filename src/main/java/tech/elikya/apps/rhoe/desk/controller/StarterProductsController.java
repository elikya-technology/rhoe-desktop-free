/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.controller;

import tech.elikya.apps.rhoe.desk.ui.ControlsHandler;
import tech.elikya.apps.rhoe.desk.ui.Stages;
import tech.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

@Component
public class StarterProductsController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private Label text;
    @FXML private JFXButton next;
    @FXML private JFXButton previous;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLanguage();
        setCloseEventHandler();
        setNextEventHandler();
        setPreviousEventHandler();
    }

    private void setLanguage() {
        Properties lang = ControlsHandler.getLanguage();
        title.setText(lang.getProperty("products"));
        text.setText(lang.getProperty("starter_products"));
        next.setText(lang.getProperty("next"));
        previous.setText(lang.getProperty("previous"));
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void setNextEventHandler() {
        next.setOnAction(event -> {
            Stages.close(event);
            Stages.showDialog(StagesPaths.STARTER_SALES);
        });
    }

    private void setPreviousEventHandler() {
        previous.setOnAction(event -> {
            Stages.close(event);
            Stages.showDialog(StagesPaths.STARTER_MORE);
        });
    }
}

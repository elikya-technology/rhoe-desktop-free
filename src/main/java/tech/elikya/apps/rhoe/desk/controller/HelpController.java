/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.controller;

import tech.elikya.apps.rhoe.desk.ui.ControlsHandler;
import tech.elikya.apps.rhoe.desk.ui.Stages;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.text.TextFlow;
import org.springframework.stereotype.Component;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class HelpController implements Initializable {

    @FXML
    private Label title;
    @FXML
    private JFXButton close;
    @FXML
    private TitledPane sales;
    @FXML
    private TitledPane products;
    @FXML
    private TitledPane others;
    @FXML
    private TextFlow salesText;
    @FXML
    private TextFlow productsText;
    @FXML
    private TextFlow othersText;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateLanguage();
        setHelpTexts();
        setCloseEventHandler();
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void updateLanguage() {
        Properties language = ControlsHandler.getLanguage();
        title.setText(language.getProperty("help"));
        sales.setText(language.getProperty("sales"));
        others.setText(language.getProperty("others"));
        products.setText(language.getProperty("products"));
    }

    private void setHelpTexts() {
    }
}

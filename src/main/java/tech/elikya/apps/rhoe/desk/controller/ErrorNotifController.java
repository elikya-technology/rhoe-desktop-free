/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
public class ErrorNotifController implements Initializable {

    @FXML
    private Label text;

    private static String content;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        text.setText(content);
    } 
    
    public static void setContent(String c) {content = c;}   
    
}

/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.host.WebTargetBuilder;
import com.elikya.apps.rhoe.desk.util.Configs;
import com.elikya.apps.rhoe.desk.util.Numbers;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class PreloaderController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebTargetBuilder.buildSubscriberTarget();
        Configs.checkPropertiesFile();
        Numbers.checkNumbersFile();
        Configs.load();
        Numbers.load();
    }

}

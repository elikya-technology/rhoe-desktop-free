/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.host.ServerTargetBuilder;
import com.elikya.apps.rhoe.desk.util.Configs;
import com.elikya.apps.rhoe.desk.util.Numbers;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class PreloaderController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ServerTargetBuilder.buildTarget();
        Configs.checkPropertiesFile();
        Numbers.checkNumbersFile();
        Configs.load();
        Numbers.load();
    }

}

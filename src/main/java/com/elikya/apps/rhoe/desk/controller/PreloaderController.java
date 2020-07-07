/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.configs.RhoeConfig;
import com.elikya.apps.rhoe.desk.configs.NumbersConfig;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class PreloaderController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RhoeConfig.checkPropertiesFile();
        NumbersConfig.checkNumbersFile();
        RhoeConfig.load();
        NumbersConfig.load();
    }

}

/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.ui;

import javafx.scene.control.SplitPane;

public class SplitPaneDividerManager {

    public static void resize(SplitPane splitPane) {
        double position = 0.28;
        splitPane.setDividerPositions(position);
        SplitPane.Divider divider = splitPane.getDividers().get(0);
        divider.positionProperty().addListener((observable, oldValue, newValue) -> divider.setPosition(position));
    }

}

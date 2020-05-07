/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.util;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyCodeText {

    public static String getAdaptedText(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        String text = keyEvent.getText();
        if (code == KeyCode.AMPERSAND || code == KeyCode.DIGIT1)
            return "1";
        else if (code == KeyCode.DIGIT2)
            return "2";
        else if (code == KeyCode.QUOTEDBL || code == KeyCode.DIGIT3)
            return "3";
        else if (code == KeyCode.QUOTE || code == KeyCode.DIGIT4)
            return "4";
        else if (code == KeyCode.LEFT_PARENTHESIS || code == KeyCode.DIGIT5)
            return "5";
        else if (code == KeyCode.MINUS || code == KeyCode.DIGIT6)
            return "6";
        else if (code == KeyCode.DIGIT7)
            return "7";
        else if (code == KeyCode.UNDERSCORE || code == KeyCode.DIGIT8)
            return "8";
        else if (code == KeyCode.DIGIT9)
            return "9";
        else if (code == KeyCode.DIGIT0)
            return "0";
        else {
            return text;
        }
    }
}

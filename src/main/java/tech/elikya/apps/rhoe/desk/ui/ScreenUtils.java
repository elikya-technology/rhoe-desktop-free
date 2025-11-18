/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.ui;

import static tech.elikya.apps.rhoe.desk.ui.Stages.showNextStage;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Mafole Loemelah
 */
public class ScreenUtils {
    private static final Rectangle2D SCREEN_BOUNDS = Screen.getPrimary().getVisualBounds();
    
    public static void setDelay(Stage stage, double duration, NextStageContext context) {
        PauseTransition delay = new PauseTransition(Duration.seconds(duration));
        delay.setOnFinished(event -> {
            stage.close();
            if (context.equals(NextStageContext.EFFECTIVE)) showNextStage();
        });
        delay.play();
    }
    
    public static double getScreenWidth() {
        return SCREEN_BOUNDS.getWidth();
    } 
    
    public static double getScreenHeight() {
        return SCREEN_BOUNDS.getHeight();
    }
    
    public static void setFadeTransition(int seconds, Parent parent) {
        FadeTransition transition = new FadeTransition(Duration
                .seconds(seconds), parent);
        transition.setFromValue(.10);
        transition.setToValue(1.0);
        transition.setCycleCount(1);
        transition.setAutoReverse(true);
        transition.play();
    }
        
    public enum NextStageContext{NONE, EFFECTIVE}
    
}

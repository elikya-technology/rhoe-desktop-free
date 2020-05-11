/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.ui;

import com.elikya.apps.rhoe.desk.util.LanguageResource;
import com.elikya.apps.rhoe.desk.util.Configs;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.util.Locale;
import java.util.Properties;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import org.controlsfx.control.textfield.CustomTextField;

/**
 *
 * @author Mafole Loemelah
 */
public class ControlsHandler {

    private static JFXDrawer drawer;

    public static void setDrawer(JFXDrawer _drawer) { drawer = _drawer; }

    public static void closeDrawer() {
        drawer.close();
        drawer.setSidePane();
        drawer.setPrefWidth(0.0);
        drawer.setDefaultDrawerSize(0);
    }

    public static Tooltip createTooltip(String color, String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setStyle("-fx-background-color :" + color + ";"
                + "-fx-background-radius : 5px; -fx-font-family : Arial;");
        tooltip.setWrapText(true);
        return tooltip;
    }

    public static void keepIntegerValues(JFXTextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*")) 
                        textField.setText(textField.getText().replaceAll("[^\\d]", ""));
                });
    }

    public static void keepFloatValues(JFXTextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*\\.\\d*")) 
                    textField.setText(textField.getText().replaceAll("[^\\d.]", ""));
            });
    }

    public static void handlePicture(ImageView imageView) {
        String path = Configs.get().getProperty("picture");
        File file = new File(path);
        if (file.exists()) {
            Image image = new Image("file:".concat(path), imageView.getFitWidth(),
                    imageView.getFitHeight(), true, true);
            imageView.setImage(image);
        }
        imageView.setClip(new Circle(100, 100, 100));
    }

    public static Properties getLanguage() {
        Properties read = Configs.get();
        String language = read.getProperty("language");
        return language.equals("English")
                ? LanguageResource.read(LanguageResource.Target.LANG_ENGLISH)
                : LanguageResource.read(LanguageResource.Target.LANG_FRENCH);
    }

    public static void handleSearchZone(CustomTextField textField, JFXButton button) {
        setSearchFieldProperty(textField, button);
        button.setOnAction(e -> textField.clear());
    }

    private static void setSearchFieldProperty(CustomTextField textField, JFXButton button) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) button.setVisible(false);
            else button.setVisible(true);
        });
    }

    public static void disableControls(TableView<?> tableView, CustomTextField field, boolean value) {
        tableView.setDisable(value);
        field.setDisable(value);
    }
    
}

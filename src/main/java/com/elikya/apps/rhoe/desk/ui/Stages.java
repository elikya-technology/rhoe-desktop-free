/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.ui;

import com.elikya.apps.rhoe.desk.ui.ScreenUtils.NextStageContext;
import com.elikya.apps.rhoe.desk.configs.RhoeConfig;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mafole Loemelah
 */
public class Stages {

    private static double dragOffsetX;
    private static double dragOffsetY;
    private static AnchorPane mainContainer;
    private static ConfigurableApplicationContext context;
    private static Image appIcon = new Image("icons/favicon.png");

    public static void setApplicationContext(ConfigurableApplicationContext _context) {
        context = _context;
    }

    public static void setMainContainer(AnchorPane _container) {
        mainContainer = _container;
    }

    public static void showLargeStage(StagesPaths name) {
        try {
            FXMLLoader loader = new FXMLLoader(Stages.class.getResource(name.getPath()));
            loader.setControllerFactory(context::getBean);
            Parent parent = loader.load();
            AnchorPane.setTopAnchor(parent, 0.0);
            AnchorPane.setBottomAnchor(parent, 0.0);
            AnchorPane.setLeftAnchor(parent, 0.0);
            AnchorPane.setRightAnchor(parent, 0.0);
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(parent);
        } catch (IOException exception) {
            Logger.getLogger(Stages.class.getName()).log(Level.SEVERE,
                    "LARGE STAGE NOT SHOWN", exception);
        }
    }

    public static void showDialog(StagesPaths name) {
        try {
            Stage stage = buildStage(name);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setAlwaysOnTop(true);
            stage.getIcons().add(appIcon);
            stage.show();
        } catch (IOException exception) {
            Logger.getLogger(Stages.class.getName()).log(Level.SEVERE,
                    "DIALOG NOT SHOWN", exception);
        }
    }

    private static void handleMousePressed(Stage stage, MouseEvent event) {
        dragOffsetX = event.getScreenX() - stage.getX();
        dragOffsetY = event.getScreenY() - stage.getY();
    }

    private static void handleMouseDragged(Stage stage, MouseEvent event) {
        stage.setX(event.getScreenX() - dragOffsetX);
        stage.setY(event.getScreenY() - dragOffsetY);
    }

    public static void close(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.hide();
    }

    public static void showPreloader() {
        try {
            Parent root = FXMLLoader.load(Stages.class.getResource(StagesPaths.PRELOADER.getPath()));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.getIcons().add(appIcon);
            stage.show();
            ScreenUtils.setFadeTransition(5, root);
            ScreenUtils.setDelay(stage, 9, NextStageContext.EFFECTIVE);
        } catch (IOException exception) {
            Logger.getLogger(Stages.class.getName()).log(Level.SEVERE,
                    "PRELOADER NOT SHOWN", exception);
        }
    }

    public static void showNextStage() {
        Properties prop = RhoeConfig.get();
        if (prop.getProperty("language").trim().isEmpty()) showDialog(StagesPaths.LANGUAGE);
        else if (prop.getProperty("mail_address").trim().isEmpty()) showDialog(StagesPaths.ACCOUNT);
        else if (prop.getProperty("enterprise").trim().isEmpty()) showDialog(StagesPaths.ENTERPRISE);
        else if (!prop.getProperty("password").trim().isEmpty()) showDialog(StagesPaths.LOGIN);
        else showMainApplication();
    }

    public static void showMainApplication() {
        try {
            Parent root = FXMLLoader.load(Stages.class.getResource(StagesPaths.MAIN.getPath()));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("Elikya Rhoe");
            handleMainApplicationExitRequest(stage);
            stage.getIcons().add(appIcon);
            stage.show();
        } catch (IOException exception) {
            Logger.getLogger(Stages.class.getName()).log(Level.SEVERE,
                    "MAIN APPLICATION STAGE NOT SHOWN", exception);
        }
    }

    private static void handleMainApplicationExitRequest(Stage stage) {
        stage.setOnCloseRequest(e -> {
            if (e.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST) {
                e.consume();
                Properties configs = RhoeConfig.get();
                if (Boolean.parseBoolean(configs.getProperty("enclosing_layout"))) {
                    showDialog(StagesPaths.EXIT_APPLICATION);
                } else {
                    Platform.exit();
                }
            }
        });
    }

    public static void showResponsiveDialog(StagesPaths name, StageSize stageSize) {
        Platform.runLater(() -> {
            try {
                Stage stage = buildStage(name);
                Map<String, Double> size = getActualStageSize(stageSize);
                stage.setWidth(size.get("x"));
                stage.setHeight(size.get("y"));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.getIcons().add(appIcon);
                stage.show();
            } catch (IOException exception) {
                Logger.getLogger(Stages.class.getName()).log(Level.SEVERE,
                        "LARGER DIALOG NOT SHOWN", exception);
            }
        });
    }

    private static Stage buildStage(StagesPaths name) throws IOException {
        FXMLLoader loader = new FXMLLoader(Stages.class.getResource(name.getPath()));
        loader.setControllerFactory(context::getBean);
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Stage stage = new Stage(StageStyle.UNDECORATED);
        scene.setOnMousePressed(event -> handleMousePressed(stage, event));
        scene.setOnMouseDragged(event -> handleMouseDragged(stage, event));
        stage.setScene(scene);
        disableResponsiveDialogCloseRequest(stage);
        return stage;
    }

    private static void disableResponsiveDialogCloseRequest(Stage stage) {
        stage.setOnCloseRequest(e -> {
            if (e.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST) {
                e.consume();
            }
        });
    }

    private static Map<String, Double> getActualStageSize(StageSize stageSize) {
        switch (stageSize) {
            case LARGER: return getStageSize(0.19, 0.10);
            case LARGE: return getStageSize(0.56, 0.2);
            case MEDIUM: return getStageSize(0.69, 0.18);
            default: return getStageSize(0.64, 0.13);
        }
    }

    public static Map<String, Double> getStageSize(double width, double height) {
        double screenWidth = ScreenUtils.getScreenWidth();
        double screenHeight = ScreenUtils.getScreenHeight();
        double stageWidth = screenWidth - (screenWidth * width);
        double stageHeight = screenHeight - (screenHeight * height);
        Map<String, Double> size = new HashMap<>();
        size.put("x", stageWidth);
        size.put("y", stageHeight);
        return size;
    }
    
}

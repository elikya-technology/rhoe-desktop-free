/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.controller;

import tech.elikya.apps.rhoe.desk.configs.RhoeConfig;
import tech.elikya.apps.rhoe.desk.observers.impl.CurrencyObserverImpl;
import tech.elikya.apps.rhoe.desk.observers.impl.DecimalsObserverImpl;
import tech.elikya.apps.rhoe.desk.observers.impl.LanguageObserverImpl;
import tech.elikya.apps.rhoe.desk.ui.ControlsHandler;
import tech.elikya.apps.rhoe.desk.ui.Notifier;
import tech.elikya.apps.rhoe.desk.ui.Stages;
import tech.elikya.apps.rhoe.desk.ui.StagesPaths;
import tech.elikya.apps.rhoe.desk.util.InputRegex;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class OptionsController implements Initializable {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private Tab general;
    @FXML private Tab myOcculus;
    @FXML private JFXButton folder;
    @FXML private JFXTextField enterpriseName;
    @FXML private JFXTextField enterpriseSlogan;
    @FXML private ImageView picture;
    @FXML private JFXComboBox<String> language;
    @FXML private Label languageLabel;
    @FXML private JFXButton save;
    @FXML private Label decimalsLabel;
    @FXML private JFXTextField decimalsField;
    @FXML private Label currencyLabel;
    @FXML private JFXTextField currency;
    @FXML private Label enterpriseLabel;
    @FXML private Label loginLabel;
    @FXML private JFXPasswordField password;
    @FXML private Label actionLabel;
    @FXML private JFXCheckBox closeSale;
    @FXML private JFXCheckBox closeProduct;
    @FXML private Label minOnSaleLabel;
    @FXML private JFXComboBox<String> minOnSale;
    @FXML private JFXTextField enterpriseAddress;
    @FXML private JFXCheckBox advancedCurrency;
    @FXML private JFXTextField defaultCurrencyUnit;
    @FXML private JFXTextField secondCurrency;
    @FXML private JFXTextField conversionValue;
    @FXML private Label taxLabel;
    @FXML private JFXTextField vat;
    @FXML private VBox advancedCurrencyZone;

    private Properties lang;
    private Properties configs;
    private Map<String, String> sentinels;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configs = RhoeConfig.get();
        lang = ControlsHandler.getLanguage();
        setControlsTooltips();
        setCloseEventHandler();
        setLanguage();
        spreadOptions();
        ControlsHandler.circleImage(picture, 100, 100, 100);
        ControlsHandler.keepFloatValues(conversionValue);
        ControlsHandler.keepFloatValues(vat);
        addLanguages();
        ControlsHandler.keepIntegerValues(decimalsField);
        addMinOnSaleItems();
        setSentinels();
        setSaveEventHandler();
        setAdvancedCurrencySelectedProperty();
        setFolderEventHandler();
    }

    private void notifyChangedOption() {
        if (!configs.getProperty("currency").equals(sentinels.get("currency"))
                || !configs.getProperty("second_currency_symbol").equals(sentinels.get("second_currency"))
                || !configs.getProperty("advanced_currency_features").equals(sentinels.get("advanced_currency"))
                || !configs.getProperty("converted_value").equals(sentinels.get("rate")))
            CurrencyObserverImpl.executeUpdate();
        if (!configs.getProperty("language").equals(sentinels.get("language")))
            LanguageObserverImpl.executeUpdate();
        if (!configs.getProperty("decimals").equals(sentinels.get("decimals")))
            DecimalsObserverImpl.executeUpdate();
    }

    private void spreadOptions() {
        Platform.runLater(() -> {
            setMyOcculusOptions();
            setGeneralOptions();
        });
    }

    private void setLanguage() {
        title.setText(lang.getProperty("options"));
        save.setText(lang.getProperty("save"));
        setGeneralControlsLanguage();
        setMyOcculusControlsLanguage();
    }

    private void setGeneralOptions() {
        language.getSelectionModel().select(configs.getProperty("language"));
        decimalsField.setText(String.valueOf(configs.getProperty("decimals")));
        currency.setText(configs.getProperty("currency"));
        minOnSale.getSelectionModel().select(lang.getProperty(configs.getProperty("min_on_sale")));
        closeProduct.setSelected(Boolean.parseBoolean(configs.getProperty("close_product")));
        closeSale.setSelected(Boolean.parseBoolean(configs.getProperty("close_sale")));
        secondCurrency.setText(configs.getProperty("second_currency_symbol"));
        conversionValue.setText(configs.getProperty("converted_value"));
        vat.setText(configs.getProperty("vat"));
        handleAdvancedCurrencyZone();
    }

    private void handleAdvancedCurrencyZone() {
        boolean isActivated = Boolean.parseBoolean(configs.getProperty("advanced_currency_features"));
        advancedCurrency.setSelected(isActivated);
        advancedCurrencyZone.setDisable(!isActivated);
    }

    private void setMyOcculusOptions() {
        enterpriseName.setText(configs.getProperty("enterprise"));
        enterpriseSlogan.setText(configs.getProperty("business_words"));
        enterpriseAddress.setText(configs.getProperty("address"));
        password.setText(configs.getProperty("password"));
    }

    private void setMyOcculusControlsLanguage() {
        loginLabel.setText(lang.getProperty("authentication"));
        general.setText(lang.getProperty("general"));
        myOcculus.setText(lang.getProperty("my_rhoe"));
        enterpriseLabel.setText(lang.getProperty("my_enterprise"));
        enterpriseName.setPromptText(lang.getProperty("enterprise_name"));
        enterpriseSlogan.setPromptText(lang.getProperty("business_few_words"));
        password.setPromptText(lang.getProperty("password"));
        enterpriseAddress.setPromptText(lang.getProperty("address") + ": "
                + lang.getProperty("address_pattern"));
    }

    private void setGeneralControlsLanguage() {
        languageLabel.setText(lang.getProperty("language"));
        language.setPromptText(lang.getProperty("language"));
        currency.setPromptText(lang.getProperty("default_currency"));
        decimalsField.setPromptText(lang.getProperty("decimals_number"));
        decimalsLabel.setText(lang.getProperty("decimals"));
        currencyLabel.setText(lang.getProperty("currency"));
        actionLabel.setText(lang.getProperty("action_on_save"));
        closeSale.setText(lang.getProperty("close_new_sale"));
        closeProduct.setText(lang.getProperty("close_new_product"));
        minOnSaleLabel.setText(lang.getProperty("products_limit_qty"));
        minOnSale.setPromptText(lang.getProperty("limit"));
        advancedCurrency.setText(lang.getProperty("advanced_currency"));
        defaultCurrencyUnit.setPromptText(lang.getProperty("default_currency_unit"));
        secondCurrency.setPromptText(lang.getProperty("second_currency"));
        conversionValue.setPromptText(lang.getProperty("conversion_value"));
        taxLabel.setText(lang.getProperty("taxes"));
        vat.setPromptText(lang.getProperty("vat"));
    }

    private void setCloseEventHandler() {
        close.setOnAction(Stages::close);
    }

    private void addLanguages() {
        language.setItems(FXCollections
                .observableArrayList("English", "French"));
    }

    private void addMinOnSaleItems() {
        minOnSale.setItems(FXCollections.observableArrayList(lang.getProperty("minimum_quantity"), "1"));
    }

    private void setControlsTooltips() {
        folder.setTooltip(ControlsHandler.createTooltip("#FF9800"
                , lang.getProperty("file_explorer")));
    }

    private void setAdvancedCurrencySelectedProperty() {
        advancedCurrency.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) advancedCurrencyZone.setDisable(false);
            else advancedCurrencyZone.setDisable(true);
        });
    }

    private void setSaveEventHandler() {
        save.setOnAction(event -> {
            if (!anyRequiredFieldIsEmpty() && advancedCurrencyIsValid()
                    && enterpriseAddressIsValid()) {
                replaceConfigs();
                RhoeConfig.write(configs);
                Notifier.notify(StagesPaths.SUCCESS_NOTIF, lang.getProperty("settings_saved"));
                notifyChangedOption();
                Stages.close(event);
            }
        });
    }

    private boolean anyRequiredFieldIsEmpty() {
        boolean result = decimalsField.getText().trim().isEmpty()
                || currency.getText().trim().isEmpty()
                || enterpriseName.getText().trim().isEmpty()
                || enterpriseSlogan.getText().trim().isEmpty();
        if (result) {
            Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("required_field_empty"));
            return true;
        }
        return false;
    }

    private boolean enterpriseAddressIsValid() {
        if (!enterpriseAddress.getText().isEmpty()) {
            boolean isValid = enterpriseAddress.getText().matches(InputRegex.ADDRESS.regex);
            if (isValid) return true;
            else {
                Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("invalid_address"));
                return false;
            }
        }
        return true;
    }

    private boolean advancedCurrencyIsValid() {
        if (advancedCurrency.isSelected()) {
            boolean result = secondCurrency.getText().trim().isEmpty();
            if (result) {
                Notifier.notify(StagesPaths.WARNING_NOTIF, lang.getProperty("provide_second_currency"));
                return false;
            }
            if (!rateIsDouble()) {
                Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("invalid_converted_value"));
                return false;
            }
        }
        return true;
    }

    private void replaceConfigs() {
        configs.replace("second_currency_symbol", secondCurrency.getText());
        configs.replace("converted_value", conversionValue.getText());
        configs.replace("advanced_currency_features", String.valueOf(advancedCurrency.isSelected()));
        configs.replace("enterprise", enterpriseName.getText());
        configs.replace("address", enterpriseAddress.getText());
        configs.replace("business_words", enterpriseSlogan.getText());
        configs.replace("password", password.getText());
        configs.replace("language", language.getSelectionModel().getSelectedItem());
        configs.replace("decimals", decimalsField.getText());
        configs.replace("currency", currency.getText());
        configs.replace("vat", vat.getText());
        configs.replace("close_sale", String.valueOf(closeSale.isSelected()));
        configs.replace("close_product", String.valueOf(closeProduct.isSelected()));
        String value = minOnSale.getSelectionModel().getSelectedItem().trim()
                .equals("1") ? "1" : "minimum_quantity";
        configs.replace("min_on_sale", value);
    }

    private boolean rateIsDouble() {
        try {
            Double.parseDouble(conversionValue.getText());
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private void setFolderEventHandler() {
        folder.setOnAction(event -> {
            FileChooser chooser = getFileChooser();
            File imagePath = chooser.showOpenDialog(null);
            handleSelectedFile(imagePath);
        });
    }

    private FileChooser getFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(lang.getProperty("file_explorer"));
        String pathname = System.getProperty("user.home") + File.separator + "Pictures";
        chooser.setInitialDirectory(new File(pathname));
        String[] extensions = {"*.JPG", "*.JPEG", "*.jpeg", "*.jpg", "*.PNG", "*.png"};
        chooser.getExtensionFilters().addAll(new FileChooser
                .ExtensionFilter("Images Formats", extensions));
        return chooser;
    }

    private void handleSelectedFile(File imagePath) {
        if (imagePath != null)
            replaceImage(imagePath);
    }

    private void replaceImage(File imagePath) {
        Image image = new Image("file:" + imagePath.toString(), picture.getFitWidth()
                , picture.getFitHeight(), true, true);
        picture.setImage(image);
        configs.replace("picture", imagePath.toString());
    }

    private void setSentinels() {
        sentinels = new HashMap<>(1);
        sentinels.put("language", configs.getProperty("language"));
        sentinels.put("decimals", configs.getProperty("decimals"));
        sentinels.put("second_currency", configs.getProperty("second_currency_symbol"));
        sentinels.put("advanced_currency", configs.getProperty("advanced_currency_features"));
        sentinels.put("currency", configs.getProperty("currency"));
        sentinels.put("rate", configs.getProperty("converted_value"));
    }

}

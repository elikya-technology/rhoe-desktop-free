/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.host.BackendService;
import com.elikya.apps.rhoe.desk.host.Subscriber;
import com.elikya.apps.rhoe.desk.observers.impl.CurrencyObserverImpl;
import com.elikya.apps.rhoe.desk.observers.impl.DecimalsObserverImpl;
import com.elikya.apps.rhoe.desk.observers.impl.LanguageObserverImpl;
import com.elikya.apps.rhoe.desk.observers.impl.ValidationObserverImpl;
import com.elikya.apps.rhoe.desk.observers.interfaces.ValidationObserver;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.util.LicenseListener;
import com.elikya.apps.rhoe.desk.util.InputRegex;
import com.elikya.apps.rhoe.desk.util.Configs;
import com.jfoenix.controls.*;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

/**
 * FXML Controller class
 *
 * @author Mafole Loemelah
 */
@Component
public class OptionsController extends Application implements Initializable, ValidationObserver {

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
    @FXML private JFXButton synchronize;
    @FXML private Label decimalsLabel;
    @FXML private JFXTextField decimalsField;
    @FXML private Label currencyLabel;
    @FXML private JFXTextField currency;
    @FXML private Label enterpriseLabel;
    @FXML private Label loginLabel;
    @FXML private JFXPasswordField password;
    @FXML private Label account;
    @FXML private JFXTextField mailAddress;
    @FXML private JFXTextField deadline;
    @FXML private Label actionLabel;
    @FXML private Label licenseTitle;
    @FXML private JFXCheckBox closeSale;
    @FXML private JFXCheckBox closeProduct;
    @FXML private Label minOnSaleLabel;
    @FXML private JFXComboBox<String> minOnSale;
    @FXML private JFXTextField enterpriseAddress;
    @FXML private JFXCheckBox advancedCurrency;
    @FXML private JFXTextField defaultCurrencyUnit;
    @FXML private JFXTextField secondCurrency;
    @FXML private JFXTextField conversionValue;
    @FXML private VBox advancedCurrencyZone;
    @FXML private Label howToLabel;
    @FXML private Hyperlink faq;

    private Properties lang;
    private Properties configs;
    private Map<String, String> sentinels;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configs = Configs.get();
        lang = ControlsHandler.getLanguage();
        setControlsTooltips();
        setCloseEventHandler();
        setLanguage();
        spreadOptions();
        ControlsHandler.handlePicture(picture);
        if (LicenseListener.licenseIsValid()) {
            ValidationObserverImpl.register(this);
            ControlsHandler.keepFloatValues(conversionValue);
            addLanguages();
            ControlsHandler.keepIntegerValues(decimalsField);
            addMinOnSaleItems();
            setSentinels();
            setSaveEventHandler();
            setAdvancedCurrencySelectedProperty();
            setFolderEventHandler();
        }
        setFaqEventHandler();
        setSynchronizeEventHandler();
    }

    @Override
    public void processUpdateValidation() {
        if (accountIsUpdated())
            BackendService.requestUpdateAccount(buildSubscriber());
        replaceConfigs();
        Configs.write(configs);
        Notifier.notify(StagesPaths.SUCCESS_NOTIF, lang.getProperty("settings_saved"));
        notifyChangedOption();
        ValidationObserverImpl.unregister(this);
    }

    private Subscriber buildSubscriber() {
        return Subscriber.builder().email(mailAddress.getText()).id(configs.getProperty("subs_key")).build();
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

    @Override
    public void processDeletionValidation() {}

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
        mailAddress.setText(configs.getProperty("mail_address"));
        LocalDate dueDate = LocalDate.parse(configs.getProperty("due_date"));
        deadline.setText(LocalDate.now().isBefore(dueDate)
                ? dueDate.toString() : lang.getProperty("none"));
    }

    private void setMyOcculusControlsLanguage() {
        loginLabel.setText(lang.getProperty("authentication"));
        general.setText(lang.getProperty("general"));
        myOcculus.setText(lang.getProperty("my_rhoe"));
        enterpriseLabel.setText(lang.getProperty("my_enterprise"));
        enterpriseName.setPromptText(lang.getProperty("enterprise_name"));
        enterpriseSlogan.setPromptText(lang.getProperty("business_few_words"));
        password.setPromptText(lang.getProperty("password"));
        mailAddress.setPromptText(lang.getProperty("username"));
        account.setText(lang.getProperty("mail_address"));
        deadline.setPromptText(lang.getProperty("deadline"));
        enterpriseAddress.setPromptText(lang.getProperty("address") + ": "
                + lang.getProperty("address_pattern"));
        licenseTitle.setText(lang.getProperty("license"));
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
        howToLabel.setText(lang.getProperty("how_to"));
    }

    private void setCloseEventHandler() {
        close.setOnAction(event -> {
            ValidationObserverImpl.unregister(this);
            Stages.close(event);
        });
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
        synchronize.setTooltip(ControlsHandler.createTooltip("#263238"
                , lang.getProperty("synchronize")));
    }

    private void setAdvancedCurrencySelectedProperty() {
        advancedCurrency.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) advancedCurrencyZone.setDisable(false);
            else advancedCurrencyZone.setDisable(true);
        });
    }

    private void setSynchronizeEventHandler() {
        synchronize.setOnAction(event -> {
            OptionalInt months = BackendService.requestNotSynchedMonths();
            months.ifPresent(value -> {
                if (value > 0) {
                    String newDate = LicenseListener.getNewDueDate(value);
                    Properties properties = Configs.get();
                    properties.replace("due_date", newDate);
                    Configs.write(properties);
                    deadline.setText(newDate);
                    Notifier.notify(StagesPaths.INFO_NOTIF, lang.getProperty("restart_app"));
                } else {
                    Notifier.notify(StagesPaths.INFO_NOTIF, lang.getProperty("no_payment"));
                }
            });
        });
    }

    private void setFaqEventHandler() {
        faq.setOnAction(event -> {
            HostServices hostServices = getHostServices();
            hostServices.showDocument("http://localhost:8080/faq");
            Stages.close(event);
        });
    }

    private void setSaveEventHandler() {
        save.setOnAction(event -> {
            if (!anyRequiredFieldIsEmpty() && advancedCurrencyIsValid()
                    && enterpriseAddressIsValid() && emailIsValid()) {
                if(accountIsUpdated()) {
                    if (BackendService.emailExists(mailAddress.getText())) {
                        Notifier.notify(StagesPaths.WARNING_NOTIF, lang.getProperty("mail_taken"));
                    } else {
                        CodeVerifierController.setEmail(mailAddress.getText());
                        showCodeVerifier(event);
                    }
                } else {
                    showCodeVerifier(event);
                }
            }
        });
    }

    private boolean anyRequiredFieldIsEmpty() {
        boolean result = decimalsField.getText().trim().isEmpty()
                || currency.getText().trim().isEmpty()
                || enterpriseName.getText().trim().isEmpty()
                || enterpriseSlogan.getText().trim().isEmpty()
                || mailAddress.getText().trim().isEmpty();
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

    private boolean emailIsValid() {
        boolean result = mailAddress.getText().matches(InputRegex.EMAIL.regex);
        if (!result) {
            Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("invalid_email"));
            return false;
        }
        return true;
    }

    private void replaceConfigs() {
        configs.replace("second_currency_symbol", secondCurrency.getText());
        configs.replace("converted_value", conversionValue.getText());
        configs.replace("advanced_currency_features", String.valueOf(advancedCurrency.isSelected()));
        configs.replace("mail_address", mailAddress.getText());
        configs.replace("enterprise", enterpriseName.getText());
        configs.replace("address", enterpriseAddress.getText());
        configs.replace("business_words", enterpriseSlogan.getText());
        configs.replace("password", password.getText());
        configs.replace("language", language.getSelectionModel().getSelectedItem());
        configs.replace("decimals", decimalsField.getText());
        configs.replace("currency", currency.getText());
        configs.replace("close_sale", String.valueOf(closeSale.isSelected()));
        configs.replace("close_product", String.valueOf(closeProduct.isSelected()));
        String value = minOnSale.getSelectionModel().getSelectedItem().trim()
                .equals("1") ? "1" : "minimum_quantity";
        configs.replace("min_on_sale", value);
    }

    private void showCodeVerifier(ActionEvent event) {
        CodeVerifierController.setContext(CodeVerifierController.VerificationContext.UPDATING);
        Stages.showDialog(StagesPaths.CODE_VERIFIER);
        Stages.close(event);
    }

    private boolean accountIsUpdated() {
        return !configs.getProperty("mail_address").equals(sentinels.get("mail_address"));
    }

    private boolean rateIsDouble() {
        try {
            Double.parseDouble(conversionValue.getText());
            return true;
        } catch (NumberFormatException exception) {
            System.out.println("INVALID CONVERTER");
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
        sentinels.put("mail_address", configs.getProperty("mail_address"));
    }

    @Override
    public void start(Stage primaryStage) {}
}

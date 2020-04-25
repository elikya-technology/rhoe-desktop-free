/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.exporters.CategoryExporter;
import com.elikya.apps.rhoe.desk.exporters.ProviderExporter;
import com.elikya.apps.rhoe.desk.entity.Category;
import com.elikya.apps.rhoe.desk.entity.Provider;
import com.elikya.apps.rhoe.desk.service.CategoryService;
import com.elikya.apps.rhoe.desk.service.ProviderService;
import com.elikya.apps.rhoe.desk.observers.impl.SaveUpdateObserverImpl;
import com.elikya.apps.rhoe.desk.observers.impl.ValidationObserverImpl;
import com.elikya.apps.rhoe.desk.observers.interfaces.ValidationObserver;
import com.elikya.apps.rhoe.desk.ui.*;
import com.elikya.apps.rhoe.desk.util.*;
import com.elikya.apps.rhoe.desk.util.Numbers.NumberTarget;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 *
 * @author Mafole Loemelah
 */
@Component
public class MoreController implements Initializable, ValidationObserver {

    @FXML private Label title;
    @FXML private JFXButton close;
    @FXML private Tab categoriesTab;
    @FXML private AnchorPane categoryForm;
    @FXML private JFXTextField categoryNameField;
    @FXML private JFXTextField categoryNumberField;
    @FXML private JFXTextArea categoryDescriptionArea;
    @FXML private JFXButton categorySave;
    @FXML private CustomTextField categorySearchText;
    @FXML private TableView<Category> categoriesTable;
    @FXML private MenuItem categoryExcel;
    @FXML private MenuItem categoryDelete;
    @FXML private TableColumn<Category, String> categoryName;
    @FXML private TableColumn<Category, String> categoryNumber;
    @FXML private TableColumn<Category, String> categoryDescription;
    @FXML private Tab providersTab;
    @FXML private AnchorPane providerForm;
    @FXML private JFXTextField providerNameField;
    @FXML private JFXTextField providerAddressField;
    @FXML private JFXTextField providerPhoneField;
    @FXML private JFXTextField providerEmailField;
    @FXML private JFXTextArea providerDescriptionArea;
    @FXML private JFXButton providerSave;
    @FXML private CustomTextField providerSearchText;
    @FXML private TableView<Provider> providersTable;
    @FXML private MenuItem providerExcel;
    @FXML private MenuItem providerDelete;
    @FXML private TableColumn<Provider, String> providerName;
    @FXML private TableColumn<Provider, String> providerAddress;
    @FXML private TableColumn<Provider, String> providerPhone;
    @FXML private TableColumn<Provider, String> providerEmail;
    @FXML private TableColumn<Provider, String> providerDescription;
    @FXML private MenuItem _categoryProducts;
    @FXML private MenuItem _providerProducts;
    @FXML private MenuItem _providerEdit;
    @FXML private MenuItem _categoryEdit;
    @FXML private JFXButton catSearchButton;
    @FXML private JFXButton provSearchBtn;
    @FXML private TableColumn<Category, Integer> catProdsNumber;
    @FXML private TableColumn<Category, Integer> catProdsQty;
    @FXML private TableColumn<Category, BigDecimal> catStockPrice;
    @FXML private JFXTextField catProdsNumbFld;
    @FXML private JFXTextField catProdsQtyFld;
    @FXML private JFXTextField catStockPriceFld;
    @FXML private SplitPane catSplitPane;
    @FXML private SplitPane provSplitPane;

    private ProviderService providerService;
    private CategoryService categoryService;
    
    private Properties lang;
    private Category updatableCategory;
    private Provider updatableProvider;
    private List<Category> deletableCategories = new ArrayList<>(1);
    private List<Provider> deletableProviders = new ArrayList<>(1);
    private final Function<TableView<?>, List<?>> selectedItems = table
            -> table.getSelectionModel().getSelectedItems();
    private String currency;
    private List<Provider> providersList;
    private List<Category> categoriesList;
    private TargetedItem target;
    private String optionalText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ValidationObserverImpl.register(this);
        currency = ApplicationCurrency.getActualCurrency();
        setLanguage();
        initProviders();
        initCategories();
        setCloseEventHandler();
    }

    @Override
    public void processDeletionValidation() {
        Platform.runLater(() -> {
            if (!deletableCategories.isEmpty()) deleteCategories();
            if (!deletableProviders.isEmpty()) deleteProviders();
        });
    }

    @Override
    public void processUpdateValidation() {
        if (target.equals(TargetedItem.CATEGORY)) handleChangingCategory();
        else handleChangingProvider();
    }

    private void handleChangingProvider() {
        updatableProvider = (Provider) selectedItems.apply(providersTable).get(0);
        fillProviderForm(updatableProvider);
        ControlsHandler.disableControls(providersTable, providerSearchText, true);
    }

    private void handleChangingCategory() {
        updatableCategory = (Category) selectedItems.apply(categoriesTable).get(0);
        fillCategoryForm(updatableCategory);
        ControlsHandler.disableControls(categoriesTable, categorySearchText, true);
    }

    @Autowired
    private void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @Autowired
    private void setProviderService(ProviderService providerService) {
        this.providerService = providerService;
    }

    private void setLanguage() {
        lang = ControlsHandler.getLanguage();
        optionalText = " (" + lang.getProperty("optional") + ")";
        title.setText(lang.getProperty("more"));
        setProviderLanguage();
        setCategoryLanguage();
    }

    private void initProviders() {
        providersList = providerService.getAll();
        SplitPaneDividerManager.resize(provSplitPane);
        setProvidersTableCellsValuesFactories();
        putProvidersInTable();
        setNameEventHandler(providerNameField, providerSave);
        setProviderSaveEventHandler();
        setEditEventHandler(TargetedItem.PROVIDER, _providerEdit);
        addFormContextMenu(providerForm, TargetedItem.PROVIDER);
        TableViewOperation.handleSelection(providersTable);
        TableViewOperation.setTableSelectionModel(providersTable);
        setProviderDeleteEventHandler();
        setItemsProductsEventHandler(TargetedItem.PROVIDER, _providerProducts);
        ControlsHandler.handleSearchZone(providerSearchText, provSearchBtn);
        setProviderExcelEventHandler();
        setProvidersTableClickHandler();
    }

    public void putProvidersInTable() {
        FilteredList<Provider> filteredList = filterProviders();
        SortedList<Provider> sortedItems = new SortedList<>(filteredList);
        sortedItems.comparatorProperty().bind(providersTable.comparatorProperty());
        providersTable.setItems(sortedItems);
        providersTable.refresh();
        setProviderSearchTextProperty(filteredList);
    }

    private void initCategories() {
        categoriesList = categoryService.getAll();
        SplitPaneDividerManager.resize(catSplitPane);
        setCategoriesTableCellsValuesFactories();
        putCategoriesInTable();
        TableViewOperation.setTableSelectionModel(categoriesTable);
        addFormContextMenu(categoryForm, TargetedItem.CATEGORY);
        setCategorySaveEventHandler();
        setNameEventHandler(categoryNameField, categorySave);
        TableViewOperation.handleSelection(categoriesTable);
        setEditEventHandler(TargetedItem.CATEGORY, _categoryEdit);
        setCategoryDeleteEventHandler();
        setCategoryNameProperty();
        setItemsProductsEventHandler(TargetedItem.CATEGORY, _categoryProducts);
        ControlsHandler.handleSearchZone(categorySearchText, catSearchButton);
        setCategoryExcelEventHandler();
        setCategoriesTableClickHandler();
        setTotalFieldsValues();
        setCatStockPriceCellFactory();
    }

    public void putCategoriesInTable() {
        FilteredList<Category> filteredList = filterCategories();
        SortedList<Category> sortedItems = new SortedList<>(filteredList);
        sortedItems.comparatorProperty().bind(categoriesTable.comparatorProperty());
        categoriesTable.setItems(sortedItems);
        categoriesTable.refresh();
        setCategorySearchTextProperty(filteredList);
    }

    private void setProviderLanguage() {
        providerNameField.setPromptText(lang.getProperty("name"));
        providerName.setText(lang.getProperty("name"));
        providerAddressField.setPromptText(lang.getProperty("address")
                + " : " + lang.getProperty("address_pattern") + optionalText);
        providerAddress.setText(lang.getProperty("address"));
        providerPhoneField.setPromptText(lang.getProperty("phone_number")
                + " : " + lang.getProperty("phone_number_pattern") + optionalText);
        providerPhone.setText(lang.getProperty("phone_number"));
        providerEmailField.setPromptText(lang.getProperty("email") + optionalText);
        providerEmail.setText(lang.getProperty("email"));
        providerDescriptionArea.setPromptText(lang.getProperty("description") + optionalText);
        providerDescription.setText(lang.getProperty("description"));
        providerSave.setText(lang.getProperty("save"));
        providerDelete.setText(lang.getProperty("delete"));
        _providerEdit.setText(lang.getProperty("edit"));
        _providerProducts.setText(lang.getProperty("products"));
        providersTable.setPlaceholder(new Label(lang.getProperty("table_prompt")));
        providerExcel.setText(lang.getProperty("export"));
        providersTab.setText(lang.getProperty("providers"));
        categorySearchText.setPromptText(lang.getProperty("search"));
        providerSearchText.setPromptText(lang.getProperty("search"));
    }

    private void setCategoryLanguage() {
        categoryNameField.setPromptText(lang.getProperty("name"));
        categoryNumberField.setPromptText(lang.getProperty("category_number"));
        categoryDescriptionArea.setPromptText(lang.getProperty("description") + optionalText);
        categorySave.setText(lang.getProperty("save"));
        categoryName.setText(lang.getProperty("name"));
        categoryNumber.setText(lang.getProperty("category_number"));
        categoryDescription.setText(lang.getProperty("description"));
        categoriesTable.setPlaceholder(new Label(lang.getProperty("table_prompt")));
        categoryDelete.setText(lang.getProperty("delete"));
        _categoryEdit.setText(lang.getProperty("edit"));
        _categoryProducts.setText(lang.getProperty("products"));
        categoryExcel.setText(lang.getProperty("export"));
        categoriesTab.setText(lang.getProperty("categories"));
        catProdsNumber.setText(lang.getProperty("products_number"));
        catProdsQty.setText(lang.getProperty("products_quantity"));
        catStockPrice.setText(lang.getProperty("price") + " (" + currency + ")");
        catProdsNumbFld.setPromptText(lang.getProperty("total_prods_number"));
        catProdsQtyFld.setPromptText(lang.getProperty("total_prods_qty"));
        catStockPriceFld.setPromptText(lang.getProperty("total_price") + " (" + currency + ")");
    }

    private void setCloseEventHandler() {
        close.setOnAction((event) -> {
            nullUpdatableCategory();
            nullUpdatableProvider();

            ValidationObserverImpl.unregister(this);

            Stages.close(event);
        });
    }

    private void setCategoriesTableCellsValuesFactories() {
        categoryName.setCellValueFactory(new PropertyValueFactory<>("label"));
        categoryNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        categoryDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        catProdsNumber.setCellValueFactory(new PropertyValueFactory<>("productsNumber"));
        catProdsQty.setCellValueFactory(new PropertyValueFactory<>("productsQty"));
        catStockPrice.setCellValueFactory(new PropertyValueFactory<>("productsStockPrice"));
    }

    private void setCatStockPriceCellFactory() {
        catStockPrice.setCellFactory(param -> new TableCell<Category, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                if (!empty) {
                    if (item == null || item.doubleValue() <= 0)
                        setText("0");
                    else
                        setText(NumbersFormatter.getFormattedString(item));

                }
            }
        });
    }

    private void setProvidersTableCellsValuesFactories() {
        providerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        providerName.setCellValueFactory(new PropertyValueFactory<>("label"));
        providerEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        providerDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        providerPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }
    
    private void setTotalFieldsValues() {
        int number = computeTotalProductsNumber();
        catProdsNumbFld.setText(String.valueOf(number));
        int qty = computeTotalProductsQty();
        catProdsQtyFld.setText(String.valueOf(qty));
        BigDecimal price = computeTotalStockPrice();
        catStockPriceFld.setText(NumbersFormatter.getFormattedString(price));
    }

    private int computeTotalProductsNumber() {
        return categoriesList.stream().mapToInt(Category::getProductsNumber).sum();
    }

    private int computeTotalProductsQty() {
        return categoriesList.stream().mapToInt(Category::getProductsQty).sum();
    }

    private BigDecimal computeTotalStockPrice() {
        return categoriesList.stream().map(Category::getProductsStockPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private FilteredList<Category> filterCategories() {
        return new FilteredList<>(FXCollections
                .observableArrayList(categoriesList), data -> true);
    }
    
    private FilteredList<Provider> filterProviders() {
        return new FilteredList<>(FXCollections
                .observableArrayList(providersList), data -> true);
    }

    private void setNameEventHandler(JFXTextField field, JFXButton button) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) button.setDisable(true);
            else button.setDisable(false);
        });
    }

    private void setCategorySaveEventHandler() {
        categorySave.setOnAction(event -> {
            chooseCategoryPersistenceAction();
            categoriesTable.refresh();
            processCategoryPostPersistActions();
        });
    }

    private void processCategoryPostPersistActions() {
        clearCategoryFields();
        categoryNameField.requestFocus();
        nullUpdatableCategory();
        enableCategoriesTable();
    }

    private void chooseCategoryPersistenceAction() {
        try {
            if (updatableCategory != null) updateCategory();
            else addCategory();
        } catch (DataIntegrityViolationException exception) {
            Notifier.notify(StagesPaths.WARNING_NOTIF, lang.getProperty("category_name_taken"));
        }
    }

    private void addCategory() {
        Category category = Category.builder().label(categoryNameField.getText())
                .number(categoryNumberField.getText()).description(categoryDescriptionArea.getText()).build();
        Category added = categoryService.save(category);
        categoriesList.add(added);
        putCategoriesInTable();
        Numbers.incrementNumber(NumberTarget.CATEGORY);
        Notifier.notify(StagesPaths.SUCCESS_NOTIF, lang.getProperty("category_saved"));
    }

    private void updateCategory() {
        Category.update(updatableCategory, categoryNameField.getText(),
                categoryNumberField.getText(), categoryDescriptionArea.getText());
        categoryService.update(updatableCategory);
        refreshCategoryOnProducts();
        ControlsHandler.disableControls(categoriesTable, categorySearchText, false);
        Notifier.notify(StagesPaths.SUCCESS_NOTIF, lang.getProperty("category_updated"));
    }

    private void refreshCategoryOnProducts() {
        if (updatableCategory.getProductsNumber() > 0)
            SaveUpdateObserverImpl.updateFirstRegistered();
    }

    private void setProviderSaveEventHandler() {
        providerSave.setOnAction(event -> {
            if (!providerAddressField.getText().trim().isEmpty()
                    && !providerAddressField.getText().matches(InputRegex.ADDRESS.regex))
                Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("invalid_address"));
            else if (!providerPhoneField.getText().trim().isEmpty()
                    && !providerPhoneField.getText().matches(InputRegex.PHONE_NUMBER.regex))
                Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("invalid_phone"));
            else if (!providerEmailField.getText().trim().isEmpty()
                        && !providerEmailField.getText().matches(InputRegex.EMAIL.regex))
                Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("invalid_email"));
            else chooseProviderPersistenceAction();
            providersTable.refresh();
        });
    }

    private void chooseProviderPersistenceAction() {
        try {
            if (updatableProvider != null) updateProvider();
            else addProvider();
            processProviderPostPersistActions();
        } catch (DataIntegrityViolationException exception) {
            Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("provider_name_taken"));
        }
    }

    private void addProvider() {
        Provider provider = Provider.builder().label(providerNameField.getText())
                .address(providerAddressField.getText())
                .email(providerEmailField.getText()).phone(providerPhoneField.getText())
                .description(providerDescriptionArea.getText()).build();
        Provider p = providerService.save(provider);
        providersList.add(p);
        putProvidersInTable();
        Notifier.notify(StagesPaths.SUCCESS_NOTIF, lang.getProperty("provider_saved"));
    }

    private void updateProvider() {
        Provider.update(updatableProvider, providerNameField.getText(),
                providerEmailField.getText(), providerAddressField.getText(),
                providerPhoneField.getText(), providerDescriptionArea.getText());
        providerService.update(updatableProvider);
        Notifier.notify(StagesPaths.SUCCESS_NOTIF, lang.getProperty("provider_updated"));
        ControlsHandler.disableControls(providersTable, providerSearchText, false);
    }

    private void setCategoryDeleteEventHandler() {
        categoryDelete.setOnAction(e -> {
            ObservableList<Category> deletableItems = categoriesTable.getSelectionModel().getSelectedItems();
            if (deletableCategoryIsNotEmpty(deletableItems)) {
                Notifier.notify(StagesPaths.WARNING_NOTIF, lang.getProperty("delete_products_first"));
            } else {
                deletableCategories.addAll(deletableItems);
                Stages.showDialog(StagesPaths.DELETION_DIALOG);
            }
        });
    }

    private boolean deletableCategoryIsNotEmpty(ObservableList<Category> deletableItems) {
        return deletableItems.stream().anyMatch(it -> it.getProductsNumber() > 0);
    }

    private void setProviderDeleteEventHandler() {
        providerDelete.setOnAction(e -> {
            deletableProviders.addAll(providersTable.getSelectionModel().getSelectedItems());
            Stages.showDialog(StagesPaths.DELETION_DIALOG);
        });
    }
    
    private void setCategoryExcelEventHandler() {
        categoryExcel.setOnAction(e -> Platform.runLater(() -> {
            List<Category> items = categoriesTable.getSelectionModel().getSelectedItems();
            if (!items.isEmpty())
                CategoryExporter.export(items);
        }));
    }
    
    private void setProviderExcelEventHandler() {
        providerExcel.setOnAction(e -> Platform.runLater(() -> {
            List<Provider> items = providersTable.getSelectionModel().getSelectedItems();
            if (!items.isEmpty())
                ProviderExporter.export(items);
        }));
    }

    private void deleteProviders() {
        providerService.deleteAll(deletableProviders);
        providersList.removeAll(deletableProviders);
        deletableProviders.clear();
        putProvidersInTable();
        Notifier.notify(StagesPaths.SUCCESS_NOTIF, lang.getProperty("provider_deleted"));
    }

    private void deleteCategories() {
        categoryService.deleteAll(deletableCategories);
        categoriesList.removeAll(deletableCategories);
        deletableCategories.clear();
        putCategoriesInTable();
        Notifier.notify(StagesPaths.SUCCESS_NOTIF, lang.getProperty("category_deleted"));
    }

    private void setEditEventHandler(TargetedItem _target, MenuItem item) {
        item.setOnAction(event -> {
            target = _target;
            CodeVerifierController.setContext(CodeVerifierController.VerificationContext.UPDATING);
            Stages.showDialog(StagesPaths.CODE_VERIFIER);
        });
    }

    private void setCategoryNameProperty() {
        categoryNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty())
                categoryNumberField.clear();
            else setCategoryNumber(newValue);
        });
    }

    private void setCategoryNumber(String newValue) {
        String chars = Numbers.pickChars(newValue);
        String number;
        if (updatableCategory != null) {
            number = updatableCategory.getNumber().split("-")[1];
        } else {
            number = Numbers.pickNumber(NumberTarget.CATEGORY);
        }
        categoryNumberField.setText(chars.concat("-").concat(number));
    }

    private void setCategorySearchTextProperty(FilteredList<Category> categories) {
        categorySearchText.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) categories.setPredicate(p -> true);
            else searchCategory(categories, newValue);
            
        });
    }

    private void searchCategory(FilteredList<Category> categories, String newValue) {
        String text = newValue.toLowerCase();
        filterCategories(categories, text);
        categoriesTable.refresh();
        categoriesTable.setItems(categories);
    }

    private void setItemsProductsEventHandler(TargetedItem target, MenuItem item) {
        item.setOnAction(e -> callItemsLayout(target));
    }

    private void callItemsLayout(TargetedItem target) {
        Object selected = target.equals(TargetedItem.CATEGORY) ? selectedItems
                .apply(categoriesTable).get(0) : selectedItems.apply(providersTable).get(0);
        if (selected != null) {
            ItemsProductsController.setItem(selected);
            Stages.showResponsiveDialog(StagesPaths.ITEMS_PRODUCT, StageSize.LARGE);
        }
    }
    
    private void setCategoriesTableClickHandler() {
        categoriesTable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2)
                callItemsLayout(TargetedItem.CATEGORY);
        });
    }
    
    private void setProvidersTableClickHandler() {
        providersTable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2)
                callItemsLayout(TargetedItem.PROVIDER);
        });
    }

    private void setProviderSearchTextProperty(FilteredList<Provider> providers) {
        providerSearchText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) providers.setPredicate(p -> true);
            else searchProvider(providers, newValue);
        });
    }

    private void searchProvider(FilteredList<Provider> providers, String newValue) {
        String text = newValue.toLowerCase();
        filterProvider(providers, text);
        providersTable.refresh();
    }

    private void processProviderPostPersistActions() {
        clearProviderFields();
        providerNameField.requestFocus();
        nullUpdatableProvider();
        enableProvidersTable();
    }

    private void enableProvidersTable() {
        if (providersTable.isDisabled()) ControlsHandler.disableControls(
                providersTable, providerSearchText, false);
    }

    private void nullUpdatableProvider() {
        if (updatableProvider != null) updatableProvider = null;
    }

    private void clearProviderFields() {
        providerNameField.clear();
        providerAddressField.clear();
        providerPhoneField.clear();
        providerEmailField.clear();
        providerDescriptionArea.clear();
    }

    private void enableCategoriesTable() {
        if (categoriesTable.isDisabled()) ControlsHandler.disableControls(
                categoriesTable, categorySearchText, false);
    }

    private void nullUpdatableCategory() {
        if (updatableCategory != null) updatableCategory = null;
    }

    private void clearCategoryFields() {
        categoryNameField.clear();
        categoryNumberField.clear();
        categoryDescriptionArea.clear();
    }

    private void addFormContextMenu(AnchorPane pane, TargetedItem target) {
        ContextMenu contextMenu = new ContextMenu();
        pane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem item = new MenuItem(lang.getProperty("empty_fields"));
                setFromMenuItemEventHandler(target, contextMenu, item);
                contextMenu.getItems().setAll(item);
                contextMenu.setHideOnEscape(true);
                disableFormMenuItem(target, item);
                contextMenu.show(pane, event.getScreenX(), event.getScreenY());
            }
            event.consume();
        });
        pane.setOnMousePressed(event -> contextMenu.hide());
    }

    private void setFromMenuItemEventHandler(TargetedItem target, ContextMenu contextMenu, MenuItem item) {
        item.setOnAction(e -> {
            if (target.equals(TargetedItem.CATEGORY)) processCategoryPostPersistActions();
            else processProviderPostPersistActions();
            contextMenu.hide();
        });
    }

    private void disableFormMenuItem(TargetedItem target, MenuItem item) {
        if (target.equals(TargetedItem.CATEGORY))
            if (categorySave.isDisabled()) item.setDisable(true);
            else item.setDisable(false);
        else
            if (providerSave.isDisabled()) item.setDisable(true);
            else item.setDisable(false);
    }

    private void fillCategoryForm(Category category) {
        categoryNameField.setText(category.getLabel());
        categoryNumberField.setText(category.getNumber());
        categoryDescriptionArea.setText(category.getDescription());
    }

    private void fillProviderForm(Provider provider) {
        providerNameField.setText(provider.getLabel());
        providerAddressField.setText(provider.getAddress());
        providerPhoneField.setText(provider.getPhone());
        providerEmailField.setText(provider.getEmail());
        providerDescriptionArea.setText(provider.getDescription());
    }

    private void filterCategories(FilteredList<Category> categories, String text) {
        Platform.runLater(() ->
                categories.setPredicate(cat -> cat.getDescription().toLowerCase().contains(text)
                || cat.getLabel().toLowerCase().contains(text)
                || cat.getNumber().toLowerCase().contains(text)
                || String.valueOf(cat.getProductsNumber()).contains(text)
                || String.valueOf(cat.getProductsQty()).contains(text)
                || String.valueOf(cat.getProductsStockPrice()).contains(text)));
    }
    
    private void filterProvider(FilteredList<Provider> providers, String text) {
        providers.setPredicate(prov -> prov.getAddress().toLowerCase().contains(text)
                || prov.getDescription().toLowerCase().contains(text)
                || prov.getEmail().toLowerCase().contains(text)
                || prov.getLabel().toLowerCase().contains(text)
                || prov.getPhone().contains(text));
    }
    
    private enum TargetedItem {CATEGORY, PROVIDER}
}

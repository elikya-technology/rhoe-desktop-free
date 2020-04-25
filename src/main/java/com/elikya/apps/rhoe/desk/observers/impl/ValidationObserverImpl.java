/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.observers.impl;

import com.elikya.apps.rhoe.desk.observers.interfaces.ValidationObserver;

import java.util.ArrayList;
import java.util.List;

public class ValidationObserverImpl {
    private static List<ValidationObserver> validationObservers = new ArrayList<>(1);

    public static void register(ValidationObserver observer) {
        validationObservers.add(observer);
    }

    public static void unregister(ValidationObserver observer) {
        validationObservers.remove(observer);
    }

    public static void unregisterAll() {
        validationObservers.clear();
    }

    public static void executeProcessUpdate() {
        ValidationObserver observer = getObserver();
        if (observer != null) observer.processUpdateValidation();
    }

    private static ValidationObserver getObserver() {
        int index = validationObservers.size() - 1;
        return validationObservers.get(index);
    }

    public static void executeProcessDelete() {
        ValidationObserver observer = getObserver();
        if (observer != null) observer.processDeletionValidation();
    }

    public static void executeProcessStockingUp() {
        ValidationObserver observer = getObserver();
        if (observer != null) observer.processStockingUp();
    }

    public static void executeProcessWithdraw() {
        ValidationObserver observer = getObserver();
        if (observer != null) observer.processWithdraw();
    }

    public static void processDeleteOnFirstRegistered() {
        ValidationObserver observer = validationObservers.get(0);
        if (observer != null)
            observer.processDeletionValidation();
    }

}

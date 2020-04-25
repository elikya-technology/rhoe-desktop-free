/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.observers.impl;

import com.elikya.apps.rhoe.desk.observers.interfaces.SaveUpdateObserver;

import java.util.ArrayList;
import java.util.List;

public class SaveUpdateObserverImpl {
    private static List<SaveUpdateObserver> saveUpdateObservers = new ArrayList<>(1);

    public static void register(SaveUpdateObserver observer){
        saveUpdateObservers.add(observer);
    }

    public static void unregister(SaveUpdateObserver observer) {
        saveUpdateObservers.remove(observer);
    }

    public static void unregisterAll() {
        saveUpdateObservers.clear();
    }

    public static void executeAddRecord() {
        SaveUpdateObserver observer = getObserver();
        observer.addRecord();
    }

    public static void executeUpdateRecord() {
        SaveUpdateObserver observer = getObserver();
        observer.updateRecord();
    }

    public static void updateFirstRegistered() {
        SaveUpdateObserver saveUpdateObserver = saveUpdateObservers.get(0);
        if (saveUpdateObserver != null)
            saveUpdateObserver.updateRecord();
    }

    private static SaveUpdateObserver getObserver() {
        int index = saveUpdateObservers.size() - 1;
        return saveUpdateObservers.get(index);
    }
}

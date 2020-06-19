/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.observers.impl;

import com.elikya.apps.rhoe.desk.observers.interfaces.CRUDMaster;

import java.util.ArrayList;
import java.util.List;

public class CRUDMasterImpl {
    private static List<CRUDMaster> CRUDMasters = new ArrayList<>(1);

    public static void register(CRUDMaster observer){
        CRUDMasters.add(observer);
    }

    public static void unregister(CRUDMaster observer) {
        CRUDMasters.remove(observer);
    }

    public static void unregisterAll() {
        CRUDMasters.clear();
    }

    public static void executeAddRecord() {
        CRUDMaster observer = getObserver();
        observer.addRecord();
    }

    public static void executeUpdateRecord() {
        CRUDMaster observer = getObserver();
        observer.updateRecord();
    }

    public static void executeDeleteRecord() {
        CRUDMaster observer = getObserver();
        observer.deleteRecord();
    }

    public static void updateFirstRegistered() {
        CRUDMaster CRUDMaster = CRUDMasters.get(0);
        if (CRUDMaster != null)
            CRUDMaster.updateRecord();
    }

    private static CRUDMaster getObserver() {
        int index = CRUDMasters.size() - 1;
        return CRUDMasters.get(index);
    }

}

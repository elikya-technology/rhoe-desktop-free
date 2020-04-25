/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.observers.impl;

import com.elikya.apps.rhoe.desk.observers.interfaces.CurrencyObserver;

public class CurrencyObserverImpl {
    private static CurrencyObserver currencyObserver;

    public static void register(CurrencyObserver observer) {
            currencyObserver = observer;
    }

    public static void unregister() {
        currencyObserver = null;
    }

    public static void executeUpdate() {
        if (currencyObserver != null)
            currencyObserver.updateCurrency();
    }
}

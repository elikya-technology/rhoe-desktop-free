/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.observers.impl;

import tech.elikya.apps.rhoe.desk.observers.interfaces.CurrencyObserver;

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

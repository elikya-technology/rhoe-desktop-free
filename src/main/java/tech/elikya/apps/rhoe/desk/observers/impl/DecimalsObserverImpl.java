/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.observers.impl;

import tech.elikya.apps.rhoe.desk.observers.interfaces.DecimalsObserver;

public class DecimalsObserverImpl {
    private static DecimalsObserver decimalsObserver;

    public static void register(DecimalsObserver observer) {
        decimalsObserver = observer;
    }

    public static void unregister() {
        decimalsObserver = null;
    }

    public static void executeUpdate() {
        if (decimalsObserver != null)
            decimalsObserver.updateDecimals();
    }
}

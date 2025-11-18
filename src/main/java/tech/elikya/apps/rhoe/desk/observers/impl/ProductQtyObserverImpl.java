/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.observers.impl;

import tech.elikya.apps.rhoe.desk.observers.interfaces.ProductQtyObserver;

public class ProductQtyObserverImpl {
    private static ProductQtyObserver productQtyObserver = null;

    public static void register(ProductQtyObserver observer) {
        productQtyObserver = observer;
    }

    public static void unregister() {
        productQtyObserver = null;
    }

    public static void executeUpdateQty(int quantity) {
        if (productQtyObserver != null)
            productQtyObserver.updateQty(quantity);
    }


}

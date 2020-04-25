/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.observers.interfaces;

public interface ValidationObserver {

    void processUpdateValidation();

    default void processDeletionValidation(){}

    default void processStockingUp(){}

    default void processWithdraw(){}

}

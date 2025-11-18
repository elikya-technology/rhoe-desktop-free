/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.observers.interfaces;

public interface CRUDMaster {
    default void addRecord(){};
    default void updateRecord(){};
    default void deleteRecord(){};
}

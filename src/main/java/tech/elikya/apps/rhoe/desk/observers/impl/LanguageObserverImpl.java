/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.observers.impl;

import tech.elikya.apps.rhoe.desk.observers.interfaces.LanguageObserver;

import java.util.HashSet;
import java.util.Set;

public class LanguageObserverImpl {
    private static Set<LanguageObserver> languageObservers = new HashSet<>(1);

    public static void register(LanguageObserver observer) {
        languageObservers.add(observer);
    }

    public static void executeUpdate() {
        languageObservers.forEach(LanguageObserver::updateLanguage);
    }
}

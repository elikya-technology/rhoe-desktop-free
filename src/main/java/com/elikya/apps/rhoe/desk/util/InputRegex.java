/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.util;

/**
 *
 * @author Mafole Loemelah
 */
public enum  InputRegex {
    ADDRESS("\\d+(,( ?\\w*. ?)*){3}"),
    PHONE_NUMBER("(\\+?\\d+)? ?\\d+"),
    EMAIL(".+@.+\\.\\w+");

    public String regex;

    InputRegex(String regex) {
            this.regex = regex;
        }
}

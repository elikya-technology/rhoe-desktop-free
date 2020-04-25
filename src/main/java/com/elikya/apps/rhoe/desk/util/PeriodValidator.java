/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.util;

import java.time.LocalDate;
import java.time.Period;

/**
 *
 * @author Mafole Loemelah
 */
public class PeriodValidator {

    public static boolean isValid(LocalDate from, LocalDate to) {
        int i = Period.between(from, to).getDays();
        return i >= 0;
    }
    
}

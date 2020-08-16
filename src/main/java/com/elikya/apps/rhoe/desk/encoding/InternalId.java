/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.encoding;

import java.util.Optional;

public class InternalId {

    public static String get() {
        String home = System.getProperty("user.home");
        Optional<String> result = MD5HashEncoder.encode(home);
        return result.orElse("q1W@e3R$");
    }

}

/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.encoding;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class MD5HashEncoder {

    public static Optional<String> encode(String data) {
        Optional<String> result =  Optional.empty();
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            String hexString = bytesToHex(hash);
            result = Optional.of(hexString);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String bytesToHex(byte[] array) {
        return DatatypeConverter.printHexBinary(array).toLowerCase();
    }

}

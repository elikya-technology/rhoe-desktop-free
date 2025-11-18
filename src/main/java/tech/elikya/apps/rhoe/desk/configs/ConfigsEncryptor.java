/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.configs;

import tech.elikya.apps.rhoe.desk.encoding.InternalId;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.properties.EncryptableProperties;

import java.util.Properties;

public class ConfigsEncryptor {

    public static StandardPBEStringEncryptor getStringEncryptor() {
        StandardPBEStringEncryptor stringEncryptor = new StandardPBEStringEncryptor();
        stringEncryptor.setPassword(InternalId.get());
        stringEncryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        stringEncryptor.setIvGenerator(new RandomIvGenerator());
        return stringEncryptor;
    }

    public static Properties encryptProperties(Properties properties) {
        StandardPBEStringEncryptor encryptor = getStringEncryptor();
        Properties encryptedProperties = new EncryptableProperties(encryptor);
        properties.forEach((key, value) -> encryptedProperties
                .put(encryptor.encrypt(key.toString()), encryptor.encrypt(value.toString())));
        return encryptedProperties;
    }

    public static Properties decryptProperties(Properties properties) {
        StandardPBEStringEncryptor encryptor = getStringEncryptor();
        Properties decryptedProperties = new EncryptableProperties(encryptor);
        properties.forEach((key, value) -> decryptedProperties
                .put(encryptor.decrypt(key.toString()), encryptor.decrypt(value.toString())));
        return decryptedProperties;
    }

    public static String encryptString(String string) {
        StandardPBEStringEncryptor encryptor = getStringEncryptor();
        return encryptor.encrypt(string);
    }

    public static String decryptString(String string) {
        StandardPBEStringEncryptor encryptor = getStringEncryptor();
        return encryptor.decrypt(string);
    }

}

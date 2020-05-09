/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.util;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Properties;

/**
 *
 * @author Mafole Loemelah
 */
public class Numbers {

    private static final String FILE_PATH = "cf/items.properties";
    private static Properties numbers;

    public static void load() {
        if (numbers == null) {
            Properties properties = new Properties();
            try {
                InputStream inputStream = new FileInputStream(FILE_PATH);
                properties.load(inputStream);
                numbers = ConfigsEncryptor.decryptProperties(properties);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private static Properties read() {
        return numbers;
    }
    
    public static String pickNumber(NumberTarget target) {
        DecimalFormat format = new DecimalFormat("000");
        switch (target) {
            case CATEGORY: return format.format(Integer.parseInt(read().getProperty("categories")));
            case PRODUCT: return format.format(Integer.parseInt(read().getProperty("products")));
            default: return format.format(Integer.parseInt(read().getProperty("sales")));
        }
    }
    
    public static void incrementNumber(NumberTarget target) {
        int n;
        Properties numbers = read();
        switch (target) {
            case CATEGORY:
                n = Integer.parseInt(numbers.getProperty("categories"));
                numbers.replace("categories", String.valueOf(++n));
                break;
            case PRODUCT:
                n = Integer.parseInt(numbers.getProperty("products"));
                numbers.replace("products", String.valueOf(++n));
                break;
            default:
                n = Integer.parseInt(numbers.getProperty("sales"));
                numbers.replace("sales", String.valueOf(++n));
                break;
        }
        write(numbers);
    }

    private static void write(Properties properties) {
        try (OutputStream outputStream = new FileOutputStream(FILE_PATH)) {
            Properties encrypted = ConfigsEncryptor.encryptProperties(properties);
            encrypted.store(outputStream, null);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void checkNumbersFile() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (!parent.exists()) parent.mkdirs();
                if (file.createNewFile())
                    initNumbers();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void initNumbers() {
        Properties properties = new Properties();
        properties.put("sales", "1");
        properties.put("categories", "1");
        properties.put("products", "1");
        write(properties);
    }

    public static String pickChars(String name) {
        return (name.length() <= 3) ? name.toUpperCase()
                : name.substring(0, 3).toUpperCase();
    }
    
    public enum NumberTarget { CATEGORY, PRODUCT, SALE }
}

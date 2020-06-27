/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.host;

import com.elikya.apps.rhoe.desk.configs.ConfigsEncryptor;
import com.elikya.apps.rhoe.desk.configs.RhoeConfig;

import java.io.*;
import java.util.Properties;

public class HostId {

    public static void writeId(String uuid) {
        try {
            File uuidFile = File.createTempFile("rhoe", ".zip");
            try (PrintWriter writer = new PrintWriter(uuidFile.getPath())) {
                String encryptedUuid = ConfigsEncryptor.encryptString(uuid);
                writer.println(encryptedUuid);
            }
            uuidFile.setReadOnly();
            Properties configs = RhoeConfig.get();
            configs.replace("temp_file_path", uuidFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readId() {
        StringBuilder uuid = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(RhoeConfig.get().getProperty("temp_file_path")));
            String character;
            while ((character = reader.readLine()) != null) {
                uuid.append(character);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ConfigsEncryptor.decryptString(uuid.toString());
    }

    public static boolean uuidFileExists() {
        String filePath = RhoeConfig.get().getProperty("temp_file_path");
        if (filePath.isEmpty()) {
            return false;
        } else {
            File file = new File(filePath);
            return file.exists();
        }
    }

}

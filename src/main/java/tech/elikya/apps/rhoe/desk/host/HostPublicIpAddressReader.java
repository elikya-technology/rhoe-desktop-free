/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.host;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class HostPublicIpAddressReader {

    public static String read() {
        String address = "";
        try {
            URL url = new URL("http://bot.whatismyipaddress.com");
            InputStreamReader inputStream = new InputStreamReader(url.openStream());
            BufferedReader reader = new BufferedReader(inputStream);
            address = reader.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

}

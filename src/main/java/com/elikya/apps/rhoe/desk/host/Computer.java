/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.host;

import lombok.ToString;

import java.io.Serializable;

@ToString
public class Computer implements Serializable {

    private String osName;
    private String osVersion;
    private String osArch;
    private String ipAddress;

    private static Computer computer;

    private Computer() {
        osName = System.getProperty("os.name");
        osVersion = System.getProperty("os.version");
        osArch = System.getProperty("os.arch");
        ipAddress = HostPublicIpAddressReader.read();
    }

    public static Computer getInstance() {
        if (computer == null)
            computer = new Computer();
        return computer;
    }

}

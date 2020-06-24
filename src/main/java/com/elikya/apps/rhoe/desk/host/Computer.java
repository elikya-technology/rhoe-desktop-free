/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.host;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder @Data
@NoArgsConstructor @AllArgsConstructor
public class Computer implements Serializable {

    private String osName;
    private String osVersion;
    private String osArch;
    private String ipAddress;

}

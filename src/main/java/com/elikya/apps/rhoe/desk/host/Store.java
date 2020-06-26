/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.host;

import lombok.*;

@Builder @Data @ToString
@NoArgsConstructor @AllArgsConstructor
public class Store {

    private String uuid;
    private String name;
    private String about;
    private Computer computer;

}

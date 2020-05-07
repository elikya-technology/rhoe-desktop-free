/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.host;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Data @ToString @Builder
public class Feedback implements Serializable {

    private String name;
    private String email;
    private String subject;
    private String content;

}

/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.host;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class Subscriber {

    private String id;
    private String email;
    private String subscribedOn;

    public boolean isEmpty() {
        return email.isEmpty() && id.isEmpty();
    }
}

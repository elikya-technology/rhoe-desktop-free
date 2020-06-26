/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.host;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerConnectionTest {

    @Test
    public void testIsConnected() {
        ServerConnection connection = ServerConnection.getInstance();
        assertEquals(connection.isConnected(), false);
    }

}
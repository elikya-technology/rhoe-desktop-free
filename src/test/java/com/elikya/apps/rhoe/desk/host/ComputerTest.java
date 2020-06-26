/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.host;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComputerTest {

    @Test
    public void testComputerConstruction() {
        Computer computer = Computer.getInstance();
        System.out.println(computer.toString());
//        assertEquals(computer.getIpAddress(), System.getProperty("os.arch"));
    }

}
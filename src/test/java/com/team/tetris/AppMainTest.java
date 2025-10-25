package com.team.tetris;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class AppMainTest {

    @Test
    void main_prints_hello_world() {
        var originalOut = System.out;
        var out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        try {
            App.main(new String[0]);
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(
                out.toString().toLowerCase().contains("hello world"),
                "App.main() should print greeting");
    }
}

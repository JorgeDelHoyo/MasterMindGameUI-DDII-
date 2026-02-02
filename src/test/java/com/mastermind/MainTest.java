package com.mastermind;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    @Test
    public void testMain() {
        // Verificamos que al lanzar el main NO lance errores
        // assertDoesNotThrow asegura que el programa se lanza sin errores
        assertDoesNotThrow(() -> {
            new Main();

            Main.main(new String[]{});
        });

        // Solo visual
        System.out.println("El main ha terminado sin errores");
    }
}

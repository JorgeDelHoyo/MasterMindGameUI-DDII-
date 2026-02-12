package com.mastermind;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.awt.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;

public class MasterMindLogicTest {

    private final Color[] PALETTE = {Color.RED, Color.GREEN, Color.BLUE,Color.YELLOW};
    private final String[] LABELS = {"R","G","B","Y"};


    @Test
    public void testConstructor() {
        MasterMindLogic logic = spy(MasterMindLogic.class) ;
        Mockito.when(logic.generateSecret(4)).thenReturn(PALETTE);
        logic.init(4);

        String secreto = logic.showSecret();

        // Comprobaciones
        assertNotNull(secreto, "El secreto no puede ser nulo");
        assertFalse(secreto.isEmpty(), "El secreto no puede estar vacío");
        assertTrue( secreto.contains("R") || secreto.contains("G") || secreto.contains("B") || secreto.contains("Y"));
        assertEquals(4, secreto.length(),"El secreto debe tener 4 caracteres de longitud");
        assertEquals("RGBY", secreto);
    }

    // =========================================================================
    // 1. TEST DEL CONSTRUCTOR Y GENERADOR
    // =========================================================================
    @Test
    public void testConstructorYGeneracionAleatoria() {
        MasterMindLogic logic = new MasterMindLogic(PALETTE, 4, LABELS);

        // Usar showSecret para sacar el secreto autogenerado
        String secreto = logic.showSecret();

        // Comprobaciones
        assertNotNull(secreto, "El secreto no puede ser nulo");
        assertEquals(4, secreto.length(),"El secreto debe tener 4 caracteres de longitud");
    }

    // =========================================================================
    // 2. TEST DE TRADUCCIÓN
    // =========================================================================
    @Test
    public void testShowSecret_TraduccionCorrecta() {
        MasterMindLogic logic = new MasterMindLogic(PALETTE, 2,LABELS){
            @Override
            public Color[] generateSecret(int secretLength) {
                // 100% FORZADO ROJO-VERDE
                return new Color[]{Color.RED,Color.GREEN};
            }
        };

        String resultado = logic.showSecret();

        assertEquals("RG",resultado, "Debebria traducir Rojo->R y Verde->G");
    }


    // =========================================================================
    // 3. TEST DE LÓGICA DE JUEGO
    // =========================================================================
    @Test
    public void testCheckGuess_Ganador_TodoNegras() {
        // ARRANGE
        MasterMindLogic logic = new MasterMindLogic(PALETTE,4,LABELS) {
            @Override
            public Color[] generateSecret(int secretLength) {
                return new Color[]{Color.RED,Color.RED,Color.RED,Color.RED};
            }
        };

        // ACT
        Color[] intentoGanador = {Color.RED,Color.RED,Color.RED,Color.RED};
        MasterMindLogic.Result resultado = logic.checkGuess(intentoGanador);

        // ASSERT
        assertEquals(4, resultado.blacks, "Deben haber 4 negras");
        assertEquals(0, resultado.whites, "Deben haber 0 blancas");
    }


    // =========================================================================
    // 4. TEST DE LÓGICA DE JUEGO
    // =========================================================================
    @Test
    public void testCheckGuess_Desordenado_TodoBlancas() {
        // ARRANGE
        MasterMindLogic logic = new MasterMindLogic(PALETTE, 2, LABELS) {
            @Override
            public Color[] generateSecret(int secretLength) {
                return new Color[]{Color.RED,Color.BLUE};
            }
        };
        // ACT
        Color[] intentoBlancas = {Color.BLUE, Color.RED};
        MasterMindLogic.Result resultado = logic.checkGuess(intentoBlancas);

        // ASSERT
        assertEquals(2, resultado.whites, "Deben haber 2 blancas");
        assertEquals(0, resultado.blacks, "Deben haber 0 negras");
    }
    // =========================================================================
    // 5. TEST DE LÓGICA DE JUEGO
    // =========================================================================
    @Test
    void testCheckGuess_FalloTotal() {
        // ARRANGE
        MasterMindLogic logic = new MasterMindLogic(PALETTE, 2, LABELS) {
            @Override
            public Color[] generateSecret (int secretLength) {
                return new Color[]{Color.RED, Color.RED};
            }
        };

        // ACT
        Color[] intentoFallo = {Color.BLUE, Color.BLUE};
        MasterMindLogic.Result resultado = logic.checkGuess(intentoFallo);

        // ASSERT
        assertEquals(0,resultado.whites, "Deben haber 0 blancas");
        assertEquals(0,resultado.blacks, "Deben haber 0 negras");
    }

    // =========================================================================
    // 6. TEST DE LÓGICA DE JUEGO
    // =========================================================================
    @Test
    public void testCheckGuess_ColoresRepetidos() {
        // ARRANGE
        MasterMindLogic logic = new MasterMindLogic(PALETTE, 2,LABELS) {
            @Override
            public Color[] generateSecret(int secretLength) {
                return new Color[]{Color.RED, Color.BLUE};
            }
        };

        // ACT
        Color[] intentoRepetido = {Color.RED, Color.RED};
        MasterMindLogic.Result resultado = logic.checkGuess(intentoRepetido);

        // ASSERT
        assertEquals(0,resultado.whites,"Debe haber 0 blancas");
        assertEquals(1,resultado.blacks, "Debe haber 1 negra");
    }
}
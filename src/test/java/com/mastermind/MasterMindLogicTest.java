package com.mastermind;

import org.junit.jupiter.api.Test;
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
        // ARRANGE (Constructor completo)
        MasterMindLogic logic = spy(MasterMindLogic.class) ;
        Mockito.when(logic.generateSecret(4)).thenReturn(PALETTE);
        logic.init(4);

        // Usar showSecret para sacar el secreto autogenerado
        String secreto = logic.showSecret();

        // Comprobaciones
        assertNotNull(secreto, "El secreto no puede ser nulo");
        assertFalse(secreto.isEmpty(), "El secreto no puede estar vacío");
        assertTrue( secreto.contains("R") || secreto.contains("G") || secreto.contains("B") || secreto.contains("Y"));
        assertEquals(4, secreto.length(),"El secreto debe tener 4 caracteres de longitud");
    }

    
    

    // =========================================================================
    // 1. TEST DEL CONSTRUCTOR Y GENERADOR (generateSecret)
    // =========================================================================
    @Test
    public void testConstructorYGeneracionAleatoria() {
        // ARRANGE (Constructor completo)
        MasterMindLogic logic = new MasterMindLogic(PALETTE, 4, LABELS);

        // Usar showSecret para sacar el secreto autogenerado
        String secreto = logic.showSecret();

        // Comprobaciones
        assertNotNull(secreto, "El secreto no puede ser nulo");
        assertEquals(4, secreto.length(),"El secreto debe tener 4 caracteres de longitud");
    }

    // =========================================================================
    // 2. TEST DE TRADUCCIÓN (showSecret)
    // =========================================================================
    @Test
    public void testShowSecret_TraduccionCorrecta() {
        // 1. CONTEXTO (Datos fijos para saber qué esperar)
        // PALETA: Posición 0 es ROJO (RED), su etiqueta es "R"
        //         Posición 1 es VERDE (GREEN), su etiqueta es "G"

        // 2. PREPARAR LA MÁQUINA TRUCADA
        MasterMindLogic logic = new MasterMindLogic(PALETTE, 2,LABELS){
            @Override
            public Color[] generateSecret(int secretLength) {
                // 100% FORZADO ROJO-VERDE
                return new Color[]{Color.RED,Color.GREEN};
            }
        };

        // 3. PEDIR LA TRADUCCIÓN
        // La función showSecret() recorre los colores del secreto.
        // 1. Ve Color.RED -> Busca en la paleta -> Es el índice 0 -> Coge etiqueta "R".
        // 2. Ve Color.GREEN -> Busca en la paleta -> Es el índice 1 -> Coge etiqueta "G".
        String resultado = logic.showSecret();

        // 4. VERIFICAR
        assertEquals("RG",resultado, "Debebria traducir Rojo->R y Verde->G");
    }


    // =========================================================================
    // 3. TEST DE LÓGICA DE JUEGO (checkGuess) - CASO: GANAR
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
    // 4. TEST DE LÓGICA DE JUEGO (checkGuess) - CASO: TODO BLANCAS
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
    // 5. TEST DE LÓGICA DE JUEGO (checkGuess) - CASO: NINGÚN ACIERTO
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
    // 6. TEST DE LÓGICA DE JUEGO (checkGuess Repetidos que sobran)
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
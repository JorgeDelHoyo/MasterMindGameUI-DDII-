package com.mastermind;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MasterMindLogicTest {

    private final Color[] PALETTE = {Color.RED, Color.GREEN, Color.BLUE,Color.YELLOW};
    private final String[] LABELS = {"R","G","B","Y"};

    // =========================================================================
    // 1. TEST DEL CONSTRUCTOR Y GENERADOR (generateSecret)
    // =========================================================================
    @Test
    public void testConstructorYGeneracionAleatoria() {
        // Aquí NO trucamos nada, queremos ver si el original funciona.
        int longitud = 4;
        MasterMindLogic logic = new MasterMindLogic(PALETTE,longitud,LABELS);

        // Verificamos que se ha generado un secreto (aunque no sepamos cuál es)
        String secreto = logic.showSecret();

        assertNotNull(secreto,"El secreto no debe ser nulo");
        assertEquals(longitud, secreto.length(), "El secreto debe tener longitud 4 (son 4 letras)");
    }

    // =========================================================================
    // 2. TEST DE TRADUCCIÓN (showSecret)
    // =========================================================================
    @Test
    public void testShowSecret_TraduccionCorrecta() {
        // ARRANGE (forzamos que el color secreto sea Rojo,Verde
        MasterMindLogic logic = new MasterMindLogic(PALETTE,2,LABELS) {
             @Override
            public Color[] generateSecret(int len) {
                 return new Color[]{Color.RED, Color.GREEN};
             }
        };

        // ACT: Pedimos el texto
        String resultado = logic.showSecret();

        // ASSERT: Como Labels son "R","G","B
        assertEquals("RG",resultado,"Deberia traducir los colores a sus etiquetas");
    }
    // =========================================================================
    // 3. TEST DE LÓGICA DE JUEGO (checkGuess) - CASO: GANAR
    // =========================================================================
    @Test
    public void testCheckGuess_Ganador_todoNegras() {
        // ARRANGE: Secreto = ROJO,ROJO,ROJO,ROJO
        MasterMindLogic logic = new MasterMindLogic(PALETTE,4,LABELS){
            @Override
            public Color[] generateSecret(int len) {
                return new Color[]{Color.RED,Color.RED,Color.RED,Color.RED};
            }
        };

        // ACT: Jugamos lo mismo
        Color[] intento = {Color.RED,Color.RED,Color.RED,Color.RED};
        MasterMindLogic.Result resultado = logic.checkGuess(intento);

        // ASSERT
        assertEquals(4,resultado.blacks,"Deben ser 4 negras");
        assertEquals(0,resultado.whites,"Deben ser 0 blancas");
    }

    // =========================================================================
    // 4. TEST DE LÓGICA DE JUEGO (checkGuess) - CASO: TODO BLANCAS
    // =========================================================================
    @Test
    public void testCheckGuess_Desordenado_TodoBlancas() {
        // ARRANGE: Secreto = ROJO, VERDE
        MasterMindLogic logic = new MasterMindLogic(PALETTE, 2, LABELS) {
            @Override
            public Color[] generateSecret(int len) {
                return new Color[]{Color.RED, Color.GREEN};
            }
        };

        // ACT: Jugamos al revés -> VERDE, ROJO
        Color[] intento = {Color.GREEN,Color.RED};
        MasterMindLogic.Result resultado = logic.checkGuess(intento);

        // ASSERT
        assertEquals(0, resultado.blacks, "0 negras (posiciones mal)");
        assertEquals(2, resultado.whites, "2 blancas (colores bien)");
    }
    // =========================================================================
    // 5. TEST DE LÓGICA DE JUEGO (checkGuess) - CASO: NINGÚN ACIERTO
    // =========================================================================
    @Test
    void testCheckGuess_FalloTotal() {
        // ARRANGE: Secreto = ROJO, ROJO
        MasterMindLogic logic = new MasterMindLogic(PALETTE, 2, LABELS) {
            @Override
            public Color[] generateSecret(int len) {
                return new Color[]{Color.RED, Color.RED};
            }
        };

        // ACT: Jugamos AZUL, AZUL
        Color[] intento = {Color.BLUE, Color.BLUE};
        MasterMindLogic.Result resultado = logic.checkGuess(intento);

        // ASSERT
        assertEquals(0, resultado.blacks);
        assertEquals(0, resultado.whites);
    }

    // =========================================================================
    // 6. TEST DE LÓGICA COMPLEJA (Repetidos)
    // =========================================================================
    /*
     Este es el test más importante para tu examen. Verifica que el código
     no cuente blancas de más cuando hay colores repetidos en el intento
     pero no en el secreto.
    */
    @Test
    void testCheckGuess_RepetidosSobran() {
        // ARRANGE: Secreto = ROJO, AZUL (Solo hay 1 rojo)
        MasterMindLogic logic = new MasterMindLogic(PALETTE, 2, LABELS) {
            @Override
            public Color[] generateSecret(int len) {
                return new Color[]{Color.RED, Color.BLUE};
            }
        };

        // ACT: Jugamos ROJO, ROJO (Metemos 2 rojos)
        Color[] intento = {Color.RED, Color.RED};
        MasterMindLogic.Result resultado = logic.checkGuess(intento);

        // ASSERT
        // Posicion 0: ROJO vs ROJO -> Coincide -> 1 NEGRA
        // Posicion 1: AZUL vs ROJO -> No coincide.
        // ¿El segundo ROJO es blanca? NO, porque el único rojo del secreto ya se gastó con la negra.
        assertEquals(1, resultado.blacks, "El primer rojo es acierto exacto");
        assertEquals(0, resultado.whites, "El segundo rojo no debe contar porque no quedan rojos libres");
    }
}
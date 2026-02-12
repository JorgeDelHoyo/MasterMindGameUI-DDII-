package com.mastermind;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MasterMindUITest {

    @Mock
    private MasterMindLogic logicMock;

    private MasterMindUI ui;
    private JFrame frame;
    private MockedStatic<JOptionPane> mockedJOptionPane;

    // Listas para acceder a los componentes privados
    private ArrayList<JButton[]> guessRows;
    private ArrayList<JButton[]> pinRows;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // 1. Mock JOptionPane en el Hilo Principal
        mockedJOptionPane = mockStatic(JOptionPane.class);
        mockedJOptionPane.when(() -> JOptionPane.showMessageDialog(any(), any()))
                .thenAnswer(invocation -> null);

        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        String[] labels = {"R", "G", "B", "Y"};
        int rounds = 10;

        // 2. Inicializar UI DIRECTAMENTE
        ui = new MasterMindUI(colors, labels, rounds, logicMock);

        // 3. Ocultar la ventana para que no moleste visualmente
        Frame[] frames = Frame.getFrames();
        for (Frame f : frames) {
            if (f.isVisible() && f.getTitle().equals("MasterMind")) {
                frame = (JFrame) f;
                frame.setVisible(false);
            }
        }

        Field guessField = MasterMindUI.class.getDeclaredField("guessRows");
        guessField.setAccessible(true);
        guessRows = (ArrayList<JButton[]>) guessField.get(ui);

        Field pinField = MasterMindUI.class.getDeclaredField("pinRows");
        pinField.setAccessible(true);
        pinRows = (ArrayList<JButton[]>) pinField.get(ui);
    }

    @AfterEach
    public void tearDown() {
        if (mockedJOptionPane != null) {
            mockedJOptionPane.close();
        }
        if (frame != null) {
            frame.dispose();
        }
    }

    // =========================================================================
    // 1. TEST DE PINTADO
    // =========================================================================
    @Test
    public void testPintarCirculoCorrectamente() throws Exception {
        JButton btnRojo = encontrarBotonPorTexto(frame, "R");
        JButton slot = guessRows.get(0)[0];

        assertNotEquals(Color.RED, obtenerColor(slot));

        btnRojo.doClick(); // Selecciona color
        slot.doClick();    // Pinta círculo

        assertEquals(Color.RED, obtenerColor(slot), "El círculo debe ser rojo tras el clic");
    }

    // =========================================================================
    // 2. TEST DE VALIDACIONES
    // =========================================================================
    @Test
    public void testNoPintaEnCondicionesInvalidas() throws Exception {
        JButton slotFila0 = guessRows.get(0)[0];
        JButton slotFila1 = guessRows.get(1)[0];
        JButton btnRojo = encontrarBotonPorTexto(frame, "R");

        // Caso A: Click sin seleccionar color
        slotFila0.doClick();
        assertNotEquals(Color.RED, obtenerColor(slotFila0), "No debe pintar sin color seleccionado");

        // Caso B: Click en fila incorrecta
        btnRojo.doClick(); // Seleccionamos rojo
        slotFila1.doClick(); // Click en fila 1 (la activa es la 0)

        assertNotEquals(Color.RED, obtenerColor(slotFila1), "No debe pintar en fila inactiva");
    }

    // =========================================================================
    // 3. TEST: FILA INCOMPLETA
    // =========================================================================
    @Test
    public void testCheckConFilaIncompleta() throws Exception {
        JButton btnCheck = encontrarBotonPorTexto(frame, "Check");
        JButton btnRojo = encontrarBotonPorTexto(frame, "R");

        // Pintar solo 1 círculo
        btnRojo.doClick();
        guessRows.get(0)[0].doClick();

        // Click Check
        btnCheck.doClick();

        // Verificar mensaje "Please fill all slots"
        verificarMensajeContiene("Please fill all slots");

        // Asegurar que NO se llamó a la lógica
        verify(logicMock, never()).checkGuess(any());
    }

    // =========================================================================
    // 4. TEST: GANAR JUEGO (4 negras)
    // =========================================================================
    @Test
    public void testGanarJuego() throws Exception {
        JButton btnCheck = encontrarBotonPorTexto(frame, "Check");

        // Rellenar fila completa
        rellenarFila(0, Color.RED);

        // Mock: 4 Negras
        when(logicMock.checkGuess(any())).thenReturn(new MasterMindLogic.Result(4, 0));

        btnCheck.doClick();

        // Verificar mensaje "You guessed it!"
        verificarMensajeContiene("You guessed it!");
    }

    // =========================================================================
    // 5. TEST: PERDER JUEGO (Último turno)
    // =========================================================================
    @Test
    public void testPerderJuegoEnUltimoTurno() throws Exception {
        JButton btnCheck = encontrarBotonPorTexto(frame, "Check");

        // Forzar turno 9 usando Reflection
        Field currentRowField = MasterMindUI.class.getDeclaredField("currentRow");
        currentRowField.setAccessible(true);
        currentRowField.set(ui, 9);

        rellenarFila(9, Color.RED);

        // Mock: Fallo y Secreto
        when(logicMock.checkGuess(any())).thenReturn(new MasterMindLogic.Result(0, 0));
        when(logicMock.showSecret()).thenReturn("SECRET_XYZ");

        btnCheck.doClick();

        // Verificar mensaje "You lost" y el secreto
        verificarMensajeContiene("You lost");
        verificarMensajeContiene("SECRET_XYZ");
    }

    // =========================================================================
    // 6. TEST: PINTADO DE PINES
    // =========================================================================
    @Test
    public void testPintadoDePinesResultados() throws Exception {
        JButton btnCheck = encontrarBotonPorTexto(frame, "Check");
        rellenarFila(0, Color.RED);

        // Mock: 1 Negra, 1 Blanca
        when(logicMock.checkGuess(any())).thenReturn(new MasterMindLogic.Result(1, 1));

        btnCheck.doClick();

        // Verificar mensaje informativo
        verificarMensajeContiene("Black: 1. White: 1");

        // Verificar Pines Visualmente
        JButton[] pines = pinRows.get(0);
        assertEquals(Color.BLACK, obtenerColor(pines[0]));
        assertEquals(Color.WHITE, obtenerColor(pines[1]));
    }


    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private void rellenarFila(int filaIndex, Color c) throws Exception {
        String texto = (c == Color.RED) ? "R" : "G";
        JButton btnColor = encontrarBotonPorTexto(frame, texto);
        if (btnColor != null) btnColor.doClick();

        JButton[] fila = guessRows.get(filaIndex);
        for (JButton slot : fila) {
            slot.doClick();
        }
    }

    // Captura TODOS los mensajes y busca si alguno coincide
    private void verificarMensajeContiene(String textoEsperado) {
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        mockedJOptionPane.verify(() -> JOptionPane.showMessageDialog(any(), captor.capture()), atLeast(1));

        List<Object> todosLosMensajes = captor.getAllValues();
        boolean encontrado = todosLosMensajes.stream()
                .map(Object::toString)
                .anyMatch(msg -> msg.contains(textoEsperado));

        assertTrue(encontrado, "Se esperaba mensaje con: '" + textoEsperado + "'. Mensajes recibidos: " + todosLosMensajes);
    }

    private Color obtenerColor(JButton circulo) throws Exception {
        Method getColorMethod = circulo.getClass().getMethod("getColor");
        return (Color) getColorMethod.invoke(circulo);
    }

    private JButton encontrarBotonPorTexto(Container container, String texto) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (texto.equals(btn.getText())) return btn;
            } else if (comp instanceof Container) {
                JButton encontrado = encontrarBotonPorTexto((Container) comp, texto);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }
}
package com.mastermind;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import javax.swing.*;
import java.awt.*;

public class MasterMindUITest {

    @Mock
    private MasterMindLogic logicMock;

    private MasterMindUI ui;
    private JFrame frame;

    @BeforeEach
    void setUp() throws Exception {
        // Datos de prueba
        Color[] colors = {Color.RED, Color.BLUE};
        String[] labels = {"R","B"};
        int rounds = 10;

        // Inicializar UI con Mock de lógica
        // Lo hacemos en el hilo de Swing para evitar problemas
        SwingUtilities.invokeAndWait(() -> {
            ui = new MasterMindUI(colors,labels,rounds,logicMock);
        });

        // Capturar JFrame (saber si la ventana es la activa)
        frame = (JFrame) Frame.getFrames()[0];
    }

    @AfterEach
    public void tearDown() {
        if (frame != null) {
            frame.dispose();
        }
    }

}

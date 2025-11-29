import java.awt.*;
import javax.swing.*;

public class MasterMindUI {

    // ----- Constants -----
    private static final int PIN_SIZE = 10;
    private static final int GUESS_SIZE = 35;

    private static final Color BG_COLOR = new Color(194, 243, 213);
    private static final Color BASE_COLOR = new Color(187, 183, 172);

    private Color selectedColor = null;

    private java.util.List<Circle[]> guessRows = new java.util.ArrayList<>();
    private java.util.List<Circle[]> pinRows = new java.util.ArrayList<>();
    private int currentRow = 0;
    private MasterMindLogic logic;


    // Circle class
    private static class Circle extends JButton {
        private Color color;
        private final int diameter;

        public Circle(Color color, int diameter, boolean clickable) {
            this.color = color;
            this.diameter = diameter;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);

            if (!clickable) setEnabled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(color);
            g.fillOval(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(diameter, diameter);
        }

        public void setCircleColor(Color c) {
            this.color = c;
            repaint();
        }
    }

    // ----- Helper functions -----
    // Create styled JButton
    private JButton createStyledButton(String text, Color bg, int size) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(size, size));
        b.setBackground(bg);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        return b;
    }

    // Pin panel
    private JPanel createPinPanel() {
        JPanel pinPanel = new JPanel();
        Circle[] pinArray = new Circle[4];

        pinPanel.setBackground(BG_COLOR);
        pinPanel.setLayout(new GridLayout(2, 2, 5, 5));

        for (int j = 0; j < 4; j++) {
            Circle pin = new Circle(BASE_COLOR, PIN_SIZE, false);
            pinArray[j] = pin;
            pinPanel.add(pin);
        }

        pinRows.add(pinArray);

        return pinPanel;
    }

    // Guess panel
    private JPanel createGuessPanel() {
        JPanel guessPanel = new JPanel();
        Circle[] guessArray = new Circle[4];
        guessPanel.setBackground(BG_COLOR);
        guessPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        for (int j = 0; j < 4; j++) {
            Circle btn = new Circle(BASE_COLOR, GUESS_SIZE, true);
            guessArray[j] = btn;
            int rowIndex = guessRows.size();  // save the row index before adding
            btn.addActionListener(e -> {
                // Only allow coloring the CURRENT row
                if (rowIndex == currentRow && selectedColor != null) {
                    btn.setCircleColor(selectedColor);
                }
            });

            guessPanel.add(btn);
        }
        guessRows.add(guessArray);
        return guessPanel;
    }

    // Round row
    private JPanel createRoundRow() {
        JPanel roundPanel = new JPanel();
        roundPanel.setBackground(BG_COLOR);
        roundPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 0));

        roundPanel.add(createPinPanel());
        roundPanel.add(createGuessPanel());

        return roundPanel;
    }

    // Label showing selected color
    private JLabel createColorDisplayLabel(String text, Color bg, int size) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(bg);
        label.setPreferredSize(new Dimension(size, size));
        return label;
    }

    // Bottom panel with colors & button
    private JPanel createBottomPanel(Color[] colors, String[] labels) {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(BG_COLOR);
        bottomPanel.setLayout(new BorderLayout(10, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(BG_COLOR);
        controlPanel.setLayout(new GridLayout(1, 2, 10, 0));
        controlPanel.setPreferredSize(new Dimension(200, 50));

        JButton checkBtn = createStyledButton("Check", BASE_COLOR, 50);
        checkBtn.addActionListener(e -> {
            currentRow++;
        });
        JLabel selected = createColorDisplayLabel("Selected", BASE_COLOR, 50);

        controlPanel.add(checkBtn);
        controlPanel.add(selected);

        JPanel colorPanel = new JPanel();
        colorPanel.setBackground(BG_COLOR);
        colorPanel.setLayout(new GridLayout(1, colors.length, 0, 0));

        for (int i = 0; i < colors.length; i++) {
            JButton btn = createStyledButton(labels[i], colors[i], 50);
            final Color chosen = colors[i];
            // Change the color of selected label
            btn.addActionListener(e -> {
                selectedColor = chosen;
                selected.setBackground(selectedColor);
            });

            colorPanel.add(btn);
        }

        bottomPanel.add(colorPanel, BorderLayout.WEST);
        bottomPanel.add(controlPanel, BorderLayout.EAST);

        return bottomPanel;
    }

    // Main UI
    public MasterMindUI(Color[] colors, String[] labels, int rounds, MasterMindLogic logic) {
        // Main frame
        JFrame frame = new JFrame("MasterMind");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        // Frame with rounds
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setLayout(new GridLayout(rounds, 1, 0, 0));
        // Creating rounds
        for (int i = 0; i < rounds; i++) {
            centerPanel.add(createRoundRow());
        }
        frame.add(centerPanel, BorderLayout.CENTER);
        // Creating panel with buttons
        frame.add(createBottomPanel(colors, labels), BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }
}

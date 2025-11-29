import java.awt.*;
import javax.swing.*;

public class MasterMindUI {

    // Constants
    private static final int PIN_SIZE = 10;
    private static final int GUESS_SIZE = 35;
    private static final Color BG_COLOR = new Color(194, 243, 213);

    private Color selectedColor = null;

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

    // Helper Functions

    // Create a styled JButton
    private JButton createStyledButton(String text, Color bg, int size) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(size, size));
        b.setBackground(bg);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        return b;
    }

    // Create a panel for 4 pins
    private JPanel createPinPanel(Color base) {
        JPanel pinPanel = new JPanel();
        pinPanel.setBackground(BG_COLOR);
        pinPanel.setLayout(new GridLayout(2, 2, 5, 5));

        for (int j = 0; j < 4; j++) {
            pinPanel.add(new Circle(base, PIN_SIZE, false));
        }

        return pinPanel;
    }

    // Create a panel for 4 guess buttons
    private JPanel createGuessPanel(Color base) {
        JPanel guessPanel = new JPanel();
        guessPanel.setBackground(BG_COLOR);
        guessPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        for (int j = 0; j < 4; j++) {
            Circle btn = new Circle(base, GUESS_SIZE, true);
            btn.addActionListener(e -> {
                if (selectedColor != null) {
                    btn.setCircleColor(selectedColor);
                }
            });
            guessPanel.add(btn);
        }

        return guessPanel;
    }

    // Create a row for a round
    private JPanel createRoundRow(Color base) {
        JPanel roundPanel = new JPanel();
        roundPanel.setBackground(BG_COLOR);
        roundPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 0));

        roundPanel.add(createPinPanel(base));
        roundPanel.add(createGuessPanel(base));

        return roundPanel;
    }

    // Selected color
    private JLabel createColorDisplayLabel(String text, Color bg, int size) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);                       // allow background color
        label.setBackground(bg);
        label.setPreferredSize(new Dimension(size, size));
        return label;
    }


    // Create bottom panel (color selection + control buttons)
    private JPanel createBottomPanel(Color[] colors, String[] labels, Color base) {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(BG_COLOR);
        bottomPanel.setLayout(new BorderLayout(10, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Control buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(BG_COLOR);
        controlPanel.setLayout(new GridLayout(1, 2, 10, 0));
        controlPanel.setPreferredSize(new Dimension(200, 50));
        JButton checkBtn = createStyledButton("Check", base, 50);
        JLabel selected = createColorDisplayLabel("Selected", base, 50);
        controlPanel.add(checkBtn);
        controlPanel.add(selected);

        // Color buttons
        JPanel colorPanel = new JPanel();
        colorPanel.setBackground(BG_COLOR);
        colorPanel.setLayout(new GridLayout(1, colors.length, 0, 0));
        for (int i = 0; i < colors.length; i++) {
            JButton btn = createStyledButton(labels[i], colors[i], 50);
            final Color chosen = colors[i];
            btn.addActionListener(e -> {
                selectedColor = chosen;
                selected.setBackground(selectedColor);
                selected.repaint();
            });
            colorPanel.add(btn);
        }

        bottomPanel.add(colorPanel, BorderLayout.WEST);
        bottomPanel.add(controlPanel, BorderLayout.EAST);

        return bottomPanel;
    }

    // UI setup
    public MasterMindUI() {
        Color[] colors = {
                new Color(240, 17, 17),
                new Color(104, 227, 70),
                new Color(45, 64, 189),
                new Color(191, 83, 40),
                new Color(242, 135, 34),
                new Color(204, 94, 235)
        };
        Color base = new Color(187, 183, 172);
        String[] labels = {"R", "V", "A", "M", "N", "L"};

        JFrame frame = new JFrame("MasterMind");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Center panel with 10 rounds
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setLayout(new GridLayout(10, 1, 0, 0));
        for (int i = 0; i < 10; i++) {
            centerPanel.add(createRoundRow(base));
        }
        frame.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel
        frame.add(createBottomPanel(colors, labels, base), BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new MasterMindUI();
    }
}

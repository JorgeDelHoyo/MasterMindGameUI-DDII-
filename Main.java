import javax.swing.*;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {

        Color[] palette = {
                new Color(240, 17, 17),
                new Color(104, 227, 70),
                new Color(45, 64, 189),
                new Color(191, 83, 40),
                new Color(242, 135, 34),
                new Color(204, 94, 235)
        };

        String[] labels = {"R", "V", "A", "M", "N", "L"};
        int rounds = 10;
        int secretLength = 4;

        // Create logic
        MasterMindLogic logic = new MasterMindLogic(palette, rounds, secretLength);

        // Create UI
        SwingUtilities.invokeLater(() -> {
            new MasterMindUI(palette, labels, rounds, logic);
        });
    }
}

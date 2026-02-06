package com.mastermind;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MasterMindLogic {

    private Color[] SECRET;
    private final Color[] PALETTE;
    private final String[] LABELS;

    // Constructor
    public MasterMindLogic(Color[] palette, int secretLength, String[] labels) {
        this.PALETTE = palette;
        init(secretLength);
        this.LABELS = labels;
    }

	public void init(int secretLength) {
		this.SECRET = generateSecret(secretLength);
	}
    
    public MasterMindLogic() {
    	this.PALETTE = new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
		this.LABELS = new String[]{"R", "G", "B", "Y"};
	}

    // Secret generator
    public Color[] generateSecret(int secretLength) {
        Random random = new Random();
        Color[] secret = new Color[secretLength];
        for (int i = 0; i < secretLength; i++) {
            secret[i] = PALETTE[random.nextInt(PALETTE.length)];
        }
        return secret;
    }

    // Check guess and return the number of black and white pins
    public Result checkGuess(Color[] guess) {
        int blacks = 0;
        int whites = 0;

        // Storing colors for white pin calculation
        ArrayList<Color> guessSlots = new ArrayList<>();
        ArrayList<Color> secretSlots = new ArrayList<>();

        // Count black pins
        for (int i = 0; i < SECRET.length; i++) {
            Color g = guess[i];
            Color s = SECRET[i];
            if (g == s) {
                blacks++;
            } else {
                guessSlots.add(g);
                secretSlots.add(s);
            }
        }

        // Count white pins
        for (Color guessSlot : guessSlots) {
            if (secretSlots.contains(guessSlot)) {
                whites++;
                secretSlots.remove(guessSlot);
            }
        }

        return new Result(blacks, whites);
    }

    // Result structure
    public static class Result {
        public int blacks, whites;

        public Result(int b, int w) {
            blacks = b;
            whites = w;
        }
    }

    // Returns the correct answer as a String of labels. Example: RVLR
    public String showSecret() {
        StringBuilder result = new StringBuilder();
        for (Color c : SECRET) {
            for (int i = 0; i < PALETTE.length; i++) {
                if (PALETTE[i].equals(c)) {
                    result.append(LABELS[i]);
                    break;
                }
            }
        }
        return result.toString();
    }
}
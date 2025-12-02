package states;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.BonusCard;

public class BonusCardManager {
    private Map<Integer, BonusCard> allBonusCards; // ID -> card (loaded from JSON)
    private List<BonusCard> activeBonusCards;       // currently active in-game

    public BonusCardManager() {
        this.allBonusCards = new HashMap<>();
        this.activeBonusCards = new ArrayList<>();
        loadBonusCards(); // Implement JSON parsing here
    }

    public void activateBonus(int bonusId) {
        BonusCard card = allBonusCards.get(bonusId);
        if (card != null) {
            activeBonusCards.add(card);
            System.out.println("Bonus Activated: " + card.getDescription());
        }
    }

    public void loadBonusCards() {
        // Implement JSON parsing to populate allBonusCards map
        // Example:
        // allBonusCards.put(1, new BonusCard(1, "Increased Wealth", effectsMap, 5, "path/to/image"));
    }
}

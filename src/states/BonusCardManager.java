package states;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.Color;

import org.json.JSONArray;
import org.json.JSONObject;

import data.BonusCard;
import data.SpecialEventCard;

public class BonusCardManager {
    private Map<Integer, BonusCard> allBonusCards; // ID -> card (loaded from JSON)
    private List<BonusCard> activeBonusCards;       // currently active in-game

    public BonusCardManager() {
        this.allBonusCards = new HashMap<>();
        this.activeBonusCards = new ArrayList<>();
        loadBonusCards(); // Implement JSON parsing here
        System.out.println("BonusCardManager initialized with " + allBonusCards.size() + " bonus cards.");
    }

    public void activateBonus(int bonusId) {
        BonusCard card = allBonusCards.get(bonusId);
        if (card != null && !activeBonusCards.contains(card)) {
            activeBonusCards.add(card);
        }
    }

    public void updateBonuses(Map<String, Float> gameStats) {
        // 1. apply effects
        for (BonusCard bonus : activeBonusCards) {
            Map<String, Float> effects = bonus.getEffects();
            for (String stat : effects.keySet()) {
                // Aaply effect to gameStats 
                float currentVal = gameStats.get(stat);
                float newVal = Math.max(0.0f, Math.min(1.0f, currentVal + effects.get(stat)));
                gameStats.put(stat, newVal);
            }
            // 2. decrement duration
            bonus.decrementTurn();

            // 3. check if expired to start fading

            if (bonus.isExpired()) {
                // start fading logic here if needed
                bonus.expireCard();
            }
        }

        // 3. remove fully dead/expired bonus cards (using removeIf for safety)
        activeBonusCards.removeIf(BonusCard::isFullyDead);
    }

    // method to read JSON file content as a string
    public String readJsonFile(String filePath) {
        try {
            File file = new File(filePath);

            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder jsonContent = new StringBuilder();
            String line; // variable to hold each line read from the file

            while ((line = reader.readLine()) != null) { // read each line of the file
                jsonContent.append(line);
            }

            reader.close(); // close the reader, freeing up resources

            String fileContentString = jsonContent.toString(); 
        
            return fileContentString; 

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void loadBonusCards() {
        // Implement JSON parsing to populate allBonusCards map

        String jsonContent = readJsonFile("res/bonus-cards.json");
        if (jsonContent == null) return;

        try {
            JSONObject root = new JSONObject(jsonContent);
            JSONArray cardsArray = root.getJSONArray("bonusCards");

            for (int i = 0; i < cardsArray.length(); i++) {
                JSONObject cardObj = cardsArray.getJSONObject(i);

                // read basic fields

                Integer id = cardObj.getInt("id");
                String title = cardObj.getString("title");
                String description = cardObj.getString("description");

                // read card colour

                JSONObject colourObj = cardObj.getJSONObject("colour");
                Color cardColor = new Color(colourObj.getInt("r"), colourObj.getInt("g"), colourObj.getInt("b"));

                // read effects
                
                JSONObject effectsObj = cardObj.getJSONObject("effects");
                Map<String, Float> effects = new HashMap<>();

                for (String key : effectsObj.keySet()) {
                    effects.put(key, (float) effectsObj.getDouble(key));
                }

                // read duration

                int duration = cardObj.getInt("duration");

                // create final BonusCard object

                BonusCard card = new BonusCard(id, title, description, cardColor, effects, duration);
            
                allBonusCards.put(id, card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<BonusCard> getActiveBonusCards() {
        return activeBonusCards;
    }
}

package states;

import java.awt.Graphics;
import javax.swing.JOptionPane;

import java.io.IOException;

import core.App;

import data.Card;
import data.SpecialChoice;
import data.Choice;
import data.Achievement;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Map;
import java.util.Stack;
import java.io.FileReader;
import data.TableResult;
import java.io.BufferedReader;

public class PlayingState implements GameState {

    // core game state

    private App app;
    private PlayingStateRenderer renderer;

    private Integer year = 1; 

    private String monarchName;

    private String[] activeBonusCards;

    Map<String, Float> stats = new LinkedHashMap<>();

    Map<String, Object> activeFlags = new HashMap<>();

    private List<Card> cards;

    private Card currentCard;

    private Stack<Card> recentlyPlayedStack = new Stack<Card>();

    private Map<String, Float> progressionCounters = new HashMap<>();

    public PlayingState(App app, String name) {
        this.app = app;
        this.monarchName = name;
        this.renderer = new PlayingStateRenderer(this);

        initBonusCards();
        initStats();

        // fetching cards

        this.cards = new ArrayList<>();

        parseCards();

        if (!cards.isEmpty()) {
            currentCard = cards.get(0); // TEMP start with the first card
        }
    }

    private void initBonusCards() {
        this.activeBonusCards = new String[] {
            "", "", "", ""
        };
    }

    private void initStats() {
        stats.put("people", 0.5f);
        stats.put("wealth", 0.5f);
        stats.put("knowledge", 0.5f);
        stats.put("army", 0.5f);
    }

    public Float calculateCardWeight(Card card) {
        // TODO
        return 0.0f;
    }

    public void endTurnUpdates() {
        // TODO

        if (currentCard.getTags().contains("special")) {
            
        }
    }

    public Integer drawWeightedCard(Float[] probTable, Integer[] aliasTable, Integer N) {
        // TODO
        return 0;
    }

    public TableResult setupAliasTables(List<Float> weights, Float totalWeight, Integer N) {
        // TODO
        return new TableResult(null, null);
    }

    public void cardSelection() {
        List<Float> weights = new ArrayList<>();

        Float totalWeight = 0.0f;
        Integer N = cards.size();

        Card chosenCard = null;
        Card previousCard = currentCard;

        // calculate card weights and update total weight

        for (Card card: cards) {
            Float weight = calculateCardWeight(card);
            weights.add(weight);
            totalWeight += weight;
        }

        if (totalWeight == 0.0f) {  // error check, select at random uniformly
            Integer randomIndex = (int)(Math.random() * N);
            chosenCard = cards.get(randomIndex);
        } else {
            // weighted random selection

            TableResult tableResult = setupAliasTables(weights, totalWeight, N);

            // select a card using the draw a card algorithm

            Integer chosenIndex = drawWeightedCard(tableResult.getProbTable(), tableResult.getAliasTable(), N);

            chosenCard = cards.get(chosenIndex);
        }

        // update the stack and set the chosen card

        recentlyPlayedStack.push(previousCard);

        while (recentlyPlayedStack.size() > 5) {
            recentlyPlayedStack.pop();
        }

        currentCard = chosenCard;
    }

    public void checkIfGameOver() {
        for (Map.Entry<String, Float> statEntry : stats.entrySet()) {
            String name = statEntry.getKey();
            float value = statEntry.getValue();
            String reason = "";


            if (value <= 0.0f) {
                reason = name + " too low";
            } else if (value >= 1.0f) {
                reason = name + " too high";
            }

            System.out.println(reason);

            // if a reason was found, end the game and exit the method immediately
            if (!reason.isEmpty()) {
                app.setCurrentState(new GameOverState(app, year, monarchName, reason)); // switch to game over state
                return; // stop checking as soon as one game-over condition is met
            }
        }
    }

    public void makeChoice(Choice choice) {
        Choice selectedChoice = choice;

        for (String effectKey : selectedChoice.getEffects().keySet()) {
            // Apply each effect to the game state
            Float effectValue = selectedChoice.getEffects().get(effectKey);

            // update stats based on effect key

            if (stats.containsKey(effectKey)) {
                Float currentValue = stats.get(effectKey);
                Float newValue = currentValue + effectValue;

                // clamp the value between 0 and 1
                stats.put(effectKey, Math.max(0.0f, Math.min(1.0f, newValue)));
            }
        }

        // update active flags based on choice flags

        for (String flagKey : selectedChoice.getFlags().keySet()) {
            Object flagValue = selectedChoice.getFlags().get(flagKey);

            activeFlags.put(flagKey, flagValue);
        }

        System.out.println(stats.get("people") + ", " + stats.get("wealth") + ", " + stats.get("knowledge") + ", " + stats.get("army"));

        checkIfGameOver();

        year += 1;

        cardSelection();
    }

    @Override
    public void update() {
        // Update game logic
    }

    @Override
    public MouseInteractable getInputHandler() {
        // delegate input handling to the renderer
        return this.renderer;
    }

    // Method to read the file and return the content as a string
    public String readJsonFile() {
        try {
            File file = new File("res/cards.json");

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
            JOptionPane.showMessageDialog(app, "Error reading card JSON file.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public Choice parseChoice(JSONObject choiceContent) {
        // get contents of choice

        String choiceText = choiceContent.getString("text");

        Integer achievementId = choiceContent.has("achievementId") ? choiceContent.getInt("achievementId") : null;
        Integer bonusCardId = choiceContent.has("bonusCardId") ? choiceContent.getInt("bonusCardId") : null;

        JSONObject effects = choiceContent.getJSONObject("effects");
        JSONObject flags = choiceContent.getJSONObject("flags");

        // initialise empty hash maps to hold the effects and flags

        Map<String, Float> effectsMap = new HashMap<>();
        Map<String, Object> flagsMap = new HashMap<>();

        // populate the effects map with the effects

        for (String key : effects.keySet()) {
            effectsMap.put(key, effects.getFloat(key));
        }

        // populate the flags map with the flags

        for (String key : flags.keySet()) {
            flagsMap.put(key, flags.get(key));
        }

        return new Choice(choiceText, effectsMap, flagsMap, achievementId, bonusCardId);
    }

    public void parseCards() {
        String jsonContent = readJsonFile();

        // verify the content right before parsing
        System.out.println("DEBUG: JSON String to be Parsed: " + jsonContent); 

        if (jsonContent != null && !jsonContent.trim().isEmpty()) { // check if the content is not null or empty
            try {
                // parse JSON content and populate card list

                JSONObject rootObject = new JSONObject(jsonContent); // parse the root JSON object
                JSONArray cardsArray = rootObject.getJSONArray("cards"); // get the array of cards
                
                for (int i = 0; i < cardsArray.length(); i++) { // for each card in the array...
                    JSONObject cardJson = cardsArray.getJSONObject(i); // get the card JSON object

                    // get card attributes 

                    Integer id = cardJson.getInt("id");
                    String text = cardJson.getString("text");
                    String characterName = cardJson.getString("characterName");
                    String imagePath = cardJson.getString("imagePath");


                    // parsing choices

                    JSONObject choicesObject = cardJson.getJSONObject("choices");

                    JSONObject leftChoice = choicesObject.getJSONObject("left");
                    JSONObject rightChoice = choicesObject.getJSONObject("right");

                    Choice left = parseChoice(leftChoice);
                    Choice right = parseChoice(rightChoice);

                    // parsing tags

                    JSONArray tagsArray = cardJson.getJSONArray("tags");
                    List<String> tags = new ArrayList<>();

                    for (int j = 0; j < tagsArray.length(); j++) {
                        tags.add(tagsArray.getString(j));
                    }

                    // create card object and add to list
                    
                    Card card = new Card(id, text, characterName, tags, left, right, imagePath);
                    this.cards.add(card);                    
                }
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(app, "Error reading card JSON file.", "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error: JSON content is empty or null.");
        }
    }

    @Override
    public void render(Graphics g, int width, int height) { 
        renderer.render(g, this, width, height);
    }

    // getter methods

    public Card getCurrentCard() { return currentCard; }
    public Map<String, Float> getStats() { return stats; }
    public String getMonarchName() { return monarchName; }
    public Integer getYear() { return year; }
    public String[] getActiveBonusCards() { return activeBonusCards; }
    public PlayingStateRenderer getRenderer() { return renderer; }
    public List<Card> getCards() { return cards; }
}
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
import java.util.function.Consumer;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Map;
import java.util.Stack;
import java.util.Random;
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

    private List<Runnable> allProgressionRules = new ArrayList<>();

    public PlayingState(App app, String name) {
        this.app = app;
        this.monarchName = name;
        this.renderer = new PlayingStateRenderer(this);

        initBonusCards();
        initStats();
        initProgressionRules();

        // fetching cards

        this.cards = new ArrayList<>();

        parseCards();

        if (!cards.isEmpty()) {
            currentCard = cards.get(0); // TEMP start with the first card
        }
    }

    private void initProgressionRules() {
        allProgressionRules.add(this::updateSpecialEventCounters);
        allProgressionRules.add(this::updateStatCriticalityCounters);
        allProgressionRules.add(this::updateWarRule);
        allProgressionRules.add(this::updateFamineRule);
        allProgressionRules.add(this::updateMarriageRefusalCounter);
        allProgressionRules.add(this::updateWitchRelationship);
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
        if (recentlyPlayedStack.contains(card)) { // instant disqualification if card is recently played
            return 0.0f;
        }
        
        Float weight = 1.0f; // base weight

        // apply all progression counters

        for (Map.Entry<String, Float> counterEntry : progressionCounters.entrySet()) { 
            String counterName = counterEntry.getKey(); // e.g 'pressure_war'   
            Float counterValue = counterEntry.getValue(); // e.g 0.5

            // remove the prefix to get the tag to check
            String tagToCheck = counterName.replace("pressure_", ""); // e.g 'pressure_war' -> 'war'

            if (card.getTags().contains(tagToCheck)) { // check if card has the relevant tag
                System.out.println("Card '" + card.getId() + "' matched tag '" + tagToCheck + "' with value " + counterValue);
                weight *= counterValue; // apply the multiplier based on progression counter
            }
        }

        return weight;
    }

    public void endTurnUpdates() {
        for (Runnable rule : allProgressionRules) {
            rule.run();
        }

        System.out.println("DEBUG: current flags state: " + activeFlags.toString());
        System.out.println("DEBUG: current progression counters: " + progressionCounters.toString());
    }

    private void updateSpecialEventCounters() {
        if (currentCard.getTags().contains("special")) {
            progressionCounters.put("pressure_special", 0.01f);
        } else {
            if (!progressionCounters.containsKey("pressure_special")) {
                progressionCounters.put("pressure_special", 0.05f);
            } else {
                Float currentCounter = progressionCounters.get("pressure_special");
                progressionCounters.put("pressure_special", currentCounter + 0.05f);
            }
        }
    }

    private void updateStatCriticalityCounters() {
        for (Map.Entry<String, Float> statEntry : stats.entrySet()) {
            String statName = statEntry.getKey();
            float statValue = statEntry.getValue();
            String counterName = "pressure_" + statName;
            
            float currentCounter = progressionCounters.getOrDefault(counterName, 0.0f);
            float pressureIncrease = 0.0f;

            // --- UPDATED BOUNDS ---
            final float LOWER_BOUND = 0.4f; // Pressure starts building at 40%
            final float UPPER_BOUND = 0.6f; // Pressure starts building at 60%
            final float PRESSURE_MULTIPLIER = 5.0f; 

            if (statValue < LOWER_BOUND) {
                // Calculate gap (e.g., 0.4 - 0.3 = 0.1 gap)
                float gap = LOWER_BOUND - statValue;
                pressureIncrease = gap * PRESSURE_MULTIPLIER; 
                
            } else if (statValue > UPPER_BOUND) {
                // Calculate gap (e.g., 0.7 - 0.6 = 0.1 gap)
                float gap = statValue - UPPER_BOUND; 
                pressureIncrease = gap * PRESSURE_MULTIPLIER;
            }

            // Apply the pressure
            if (pressureIncrease > 0) {
                this.progressionCounters.put(counterName, currentCounter + pressureIncrease);
            } else {
                this.progressionCounters.put(counterName, 1.0f); // Reset
            }
        }
    }

    private void updateWarRule() {
        if (activeFlags.containsKey("at_war") && (Boolean)activeFlags.get("at_war")) {
            Float currentCounter = progressionCounters.getOrDefault("pressure_war", 0.0f);
            currentCounter += 0.03f;
            progressionCounters.put("pressure_war", currentCounter);
        } else {
            progressionCounters.put("pressure_war", 1.0f);
        }
    }

    private void updateFamineRule() {
        if (activeFlags.containsKey("famine") && (Boolean)activeFlags.get("famine")) {
            Float currentCounter = progressionCounters.getOrDefault("pressure_famine", 0.0f);
            currentCounter += 0.04f;
            progressionCounters.put("pressure_famine", currentCounter);
        } else {
            progressionCounters.put("pressure_famine", 1.0f);
        }
    }

    private void updateMarriageRefusalCounter() {
        int refusalCount = activeFlags.containsKey("count_marriage_declined") ? (Integer)activeFlags.get("count_marriage_declined") : 0;
        
        if (refusalCount >= 3) {
            progressionCounters.put("pressure_marriage", 10.0f);
        } else {
            progressionCounters.put("pressure_marriage", 1.0f);
        }
    }

    private void updateWitchRelationship() {
        // It accesses 'this.gameState' and 'this.progressionCounters'
        String witchRel = activeFlags.containsKey("rel_witch") ? (String)activeFlags.get("rel_witch") : "neutral";

        Float currentCounter = progressionCounters.getOrDefault("pressure_witch", 0.0f);

        switch (witchRel) {
            case "angry":
                progressionCounters.put("pressure_witch", currentCounter - 0.05f); 
                break;
            case "friendly":
                progressionCounters.put("pressure_witch", currentCounter + 0.05f);
                break;
            default:
                progressionCounters.put("pressure_witch", 1.0f); // Reset to neutral
                break;
        }
    }

    public TableResult setupAliasTables(List<Float> weights, Float totalWeight, Integer N) {
        Float[] probTable = new Float[N];
        Integer[] aliasTable = new Integer[N];

        double[] scaledProbs = new double[N]; // use double for better floating point precision during calculations

        Stack<Integer> small = new Stack<>(); // stack for small probabilities
        Stack<Integer> large = new Stack<>(); // stack for large probabilities

        // scale the probabilities and sort into stacks

        for (int i = 0; i < N; i++) {
            double scaledProb = weights.get(i) * N / totalWeight; // scale the probability, so that the average is 1
            scaledProbs[i] = scaledProb;

            // sort the index i into the small or large stack
            if (scaledProb < 1.0) {
                small.push(i); 
            } else {
                large.push(i); 
            }
        }

        // fill the buckets

        while (!small.isEmpty() && !large.isEmpty()) { // while both stacks have indices
            Integer smallIndex = small.pop();
            Integer largeIndex = large.pop();

            probTable[smallIndex] = (float) scaledProbs[smallIndex];
            aliasTable[smallIndex] = largeIndex;

            // update the large items remaining probability

            double remainingProb = scaledProbs[largeIndex] - (1.0 - scaledProbs[smallIndex]);
            scaledProbs[largeIndex] = remainingProb;

            // re-sort the large index

            if (remainingProb < 1.0) {
                small.push(largeIndex);
            } else {
                large.push(largeIndex);
            }
        }

        // handle remaining indices

        while (!large.isEmpty()) {
            Integer largeIndex = large.pop();
            probTable[largeIndex] = 1.0f;
        }

        while (!small.isEmpty()) {
            Integer smallIndex = small.pop();
            probTable[smallIndex] = 1.0f; 
        }

        // return the resulting tables

        System.out.println("DEBUG: Probability Table: ");
        for (Float prob : probTable) {
            System.out.print(prob + " ");
        }
        System.out.println();

        System.out.println("DEBUG: Alias Table: ");
        for (Integer alias : aliasTable) {
            System.out.print(alias + " ");
        }
        System.out.println();

        return new TableResult(probTable, aliasTable);
    }


    public Integer drawWeightedCard(Float[] Prob, Integer[] Alias, Integer N) {
        Integer randomBucketIndex = (int)(Math.random() * N); 
        Float randomProb = (float)(Math.random());

        if (randomProb < Prob[randomBucketIndex]) {
            return randomBucketIndex; // return the main card index
        } else {
            return Alias[randomBucketIndex]; // return the alias card index
        }
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
            System.out.println("DEBUG: weight for card " + card.getId() + ": " + weight);
            weights.add(weight);
            totalWeight += weight;
        }

        System.out.println("DEBUG: total weight calculated: " + totalWeight);

        if (totalWeight == 0.0f || totalWeight == N.floatValue()) {  // error check, select at random uniformly
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

        System.out.println("DEBUG: recentlyPlayedStack size: " + recentlyPlayedStack.size());
        System.out.println("DEBUG: recentlyPlayedStack contents: " + recentlyPlayedStack.toString());

        while (recentlyPlayedStack.size() >= 5) { // limit stack size to 5
            recentlyPlayedStack.pop();
        }

        recentlyPlayedStack.push(previousCard);

        System.out.println("DEBUG: recentlyPlayedStack size: " + recentlyPlayedStack.size());
        System.out.println("DEBUG: recentlyPlayedStack contents: " + recentlyPlayedStack.toString());

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

        endTurnUpdates();

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
            if (key == "reject_marriage") {
                Integer currentRefusals = activeFlags.containsKey("count_marriage_declined") ? (Integer)activeFlags.get("count_marriage_declined") : 0;
                flagsMap.put(key, currentRefusals + 1);
            } else {
                flagsMap.put(key, flags.get(key));
            }
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
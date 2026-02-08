package states;

import java.awt.Graphics;
import javax.swing.JOptionPane;
import javax.xml.crypto.Data;

import java.io.IOException;

import core.App;

import data.Card;
import data.SpecialChoice;
import data.SpecialEventCard;
import data.Choice;
import data.Achievement;
import data.BonusCard;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Map;
import java.util.Stack;
import java.util.Deque; 
import java.util.ArrayDeque; 
import java.util.Random;
import java.io.FileReader;
import data.TableResult;
import java.io.BufferedReader;

public class PlayingState implements GameState {

    // core game state

    private App app;
    private PlayingStateRenderer renderer;
    private SpecialEventManager eventManager;
    private BonusCardManager bonusCardManager;

    private Integer year = 1; 

    private String monarchName;

    Map<String, Float> stats = new LinkedHashMap<>();

    Map<String, Object> activeFlags = new HashMap<>();

    private List<Card> cards;

    private Card currentCard;

    private Deque<Integer> recentlyPlayedQueue = new ArrayDeque<>();

    private Map<String, Float> progressionCounters = new HashMap<>();

    private List<Runnable> allProgressionRules = new ArrayList<>();

    private static final Map<Integer, Integer> yearMilestones = new HashMap<>();

    public PlayingState(App app, String name) {
        this.app = app;
        this.monarchName = name;
        this.renderer = new PlayingStateRenderer(this);
        this.eventManager = new SpecialEventManager();
        this.bonusCardManager = new BonusCardManager();

        initStats();
        initProgressionRules();
        initMilestones();

        // fetching cards

        this.cards = new ArrayList<>();

        parseCards();

        if (!cards.isEmpty()) {
            cardSelection(); 
        }
    }

    private void initMilestones() {
        // Key = Year, Value = Achievement ID
        yearMilestones.put(1, 3);
        yearMilestones.put(10, 4);
        yearMilestones.put(20, 5);
        yearMilestones.put(30, 6);
        yearMilestones.put(40, 7);
        yearMilestones.put(50, 8);
    }

    private void initProgressionRules() {
        allProgressionRules.add(this::updateSpecialEventCounters);
        allProgressionRules.add(this::updateStatCriticalityCounters);
        allProgressionRules.add(this::updateWarRule);
        allProgressionRules.add(this::updateFamineRule);
        allProgressionRules.add(this::updateMarriageRefusalCounter);
        allProgressionRules.add(this::updateWitchRelationship);
    }

    private void initStats() {
        stats.put("people", 0.5f);
        stats.put("wealth", 0.5f);
        stats.put("knowledge", 0.5f);
        stats.put("army", 0.5f);
    }

    public void checkChoiceAchievements(Choice choice) {
        Integer achievementId = choice.getAchievementId();

        if (achievementId != null) {
            DatabaseManager.updateAchievement(achievementId);
        }

    }

    public void checkCollectorAchievement() {
        if (bonusCardManager.getActiveBonusCards().size() >= 4) {
            DatabaseManager.updateAchievement(9); // collector achievement ID
        }
    }

    public void checkBonusCardAchievements() {
        for (BonusCard bonusCard : bonusCardManager.getActiveBonusCards()) {
            if (bonusCard.getId() == 1) {  // heir to the throne achievement
                DatabaseManager.updateAchievement(1);
            } else if (bonusCard.getId() == 5) { // the peoples monarch achievement
                DatabaseManager.updateAchievement(2);
            }
        }
    }

    public void checkYearAchievements() {
        if (yearMilestones.containsKey(year)) {
            int achievementId = yearMilestones.get(year);
            DatabaseManager.updateAchievement(achievementId);
        }
    }

    public void checkAchievements(Choice choice) {
        checkChoiceAchievements(choice);
        checkBonusCardAchievements();
        checkCollectorAchievement();
        checkYearAchievements();
    }

    public Float calculateCardWeight(Card card) {
        if (recentlyPlayedQueue.contains(card.getId())) { // instant disqualification if card is recently played
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
                // System.out.println("Card '" + card.getId() + "' matched tag '" + tagToCheck + "' with value " + counterValue);
                weight *= counterValue; // apply the multiplier based on progression counter
            }
        }

        return weight;
    }

    public void endTurnUpdates() {
        for (Runnable rule : allProgressionRules) {
            rule.run();
        }
    }

    private void updateSpecialEventCounters() {
        if (currentCard.getTags().contains("special")) {
            progressionCounters.put("pressure_special", 0.01f);
        } else {
            if (!progressionCounters.containsKey("pressure_special")) {
                progressionCounters.put("pressure_special", 0.05f);
            } else {
                Float currentCounter = progressionCounters.get("pressure_special");
                progressionCounters.put("pressure_special", currentCounter + 0.5f);
            }
        }
    }

    private void updateStatCriticalityCounters() {
        for (Map.Entry<String, Float> statEntry : stats.entrySet()) {
            String statName = statEntry.getKey();
            float statValue = statEntry.getValue();

            String counterName = "pressure_" + statName;
            float currentCounter = progressionCounters.getOrDefault(counterName, 0.0f);

            if (statValue < 0.2f || statValue > 0.8f) {
                currentCounter += 0.02f; // increase likelihood if stat is in critical range 
            } else {
                currentCounter = 1.0f; // reset if stat is in safe range
            }

            progressionCounters.put(counterName, currentCounter);
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
        Boolean isMarried = activeFlags.containsKey("is_married") ? (Boolean)activeFlags.get("is_married") : false;

        if (isMarried) {
            progressionCounters.put("pressure_marriage", 0.0f);
            return;
        }

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

            double remainingProb = (scaledProbs[largeIndex] + scaledProbs[smallIndex]) - 1.0;
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

        Card chosenCard = null;
        Card previousCard = currentCard;

        // update the queue and set the chosen card

        // enqueue the previous card ID
        if (previousCard != null) {
            recentlyPlayedQueue.add(previousCard.getId());
        }

        // dequeue oldest if size exceeds 5
        while (recentlyPlayedQueue.size() >= 5) {
            recentlyPlayedQueue.poll(); // .poll() removes the Head (the oldest item)
        }

        List<Float> weights = new ArrayList<>();

        Float totalWeight = 0.0f;
        Integer N = cards.size();

        // calculate card weights and update total weight

        for (Card card: cards) {
            Float weight = calculateCardWeight(card);
            weights.add(weight);
            totalWeight += weight;
        }

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

        // debug prints

        System.out.println("Chosen Card ID: " + chosenCard.getId());
        System.out.println("Chosen Card Text: " + chosenCard.getText());
        System.out.println("Chosen Card Tags: " + chosenCard.getTags().toString());
        System.out.println("Current Progression Counters: " + progressionCounters.toString());
        System.out.println("Current Stats: " + stats.toString());
        System.out.println("Recently Played Queue: " + recentlyPlayedQueue.toString());

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

    public void applyChoiceEffects(Choice choice) {
        for (String effectKey : choice.getEffects().keySet()) {
            // Apply each effect to the game state
            Float effectValue = choice.getEffects().get(effectKey);

            // update stats based on effect key

            if (stats.containsKey(effectKey)) {
                Float currentValue = stats.get(effectKey);
                Float newValue = currentValue + effectValue;

                // clamp the value between 0 and 1
                stats.put(effectKey, Math.max(0.0f, Math.min(1.0f, newValue)));
            }
        }
    }

    public void applyFlags(Choice choice) {
        for (String flagKey : choice.getFlags().keySet()) {
            Object flagValue = choice.getFlags().get(flagKey);

            if (flagKey.equals("reject_marriage") && !(activeFlags.containsKey("count_marriage_declined"))) {
                activeFlags.put("count_marriage_declined", 0);
            } else if (flagKey.equals("reject_marriage") && (activeFlags.containsKey("count_marriage_declined"))) {
                Integer currentRefusals = (Integer)activeFlags.get("count_marriage_declined");
                activeFlags.put("count_marriage_declined", currentRefusals + 1);
                continue; // skip the general flag application below
            }

            System.out.println("Applying flag: " + flagKey + " with value: " + flagValue.toString());

            activeFlags.put(flagKey, flagValue);
        }
    }

    public Boolean isEventOver(Choice choice) {
        if (choice.getFlags().containsKey("game_over") && (Boolean)choice.getFlags().get("game_over")) {
            app.setCurrentState(new GameOverState(app, year, monarchName, choice.getFlags().get("reason").toString()));
            return true;
        }

        eventManager.advanceStory(((SpecialChoice) choice));
        
        // update the current card for the UI
        if (eventManager.isEventActive()) {
            this.currentCard = eventManager.getCurrentEventCard();
        } else {
            // Event ended
            DatabaseManager.updateAchievement(10); // survive visit from the count achievement ID
            return true;
        }

        return false;
    }

    public Boolean handleSpecialEvent(Choice choice) {
        System.out.println(activeFlags.toString());

        if (checkSpecialEvents()) { // check for a new event trigger
            eventManager.startEvent(activeFlags.get("special").toString().replace("_event", "")); // e.g 'vampire_event' -> 'vampire'

            this.currentCard = eventManager.getCurrentEventCard();

            this.renderer.renderEvents();

            return true; // exit early to handle special event first
        } 
        
        if (eventManager.isEventActive()) { // handle ongoing special event
            // handle special event progression
            if (isEventOver(choice)) {
                JOptionPane.showMessageDialog(app, "You have successfully concluded the " + activeFlags.get("special").toString().replace("_event", "") + " event!", "Event Concluded", JOptionPane.INFORMATION_MESSAGE);

                cardSelection(); // event ended, select a normal card
            }

            this.renderer.renderEvents();

            return true; // always return even if special event ended to ensure player will not immediately die due to stats or other factors
        }

        return false;
    }

    public void makeChoice(Choice choice) {

        applyChoiceEffects(choice);
        applyFlags(choice);
        year += 1;

        if (handleSpecialEvent(choice)) {
            return;
        }

        if (choice.getBonusCardId() != null && !bonusCardManager.getActiveBonusCards().contains(choice.getBonusCardId())) { // only activate if the bonus card is not already active
            bonusCardManager.activateBonus(choice.getBonusCardId());
        }

        bonusCardManager.updateBonuses(this.stats);

        System.out.println(stats.get("people") + ", " + stats.get("wealth") + ", " + stats.get("knowledge") + ", " + stats.get("army"));

        checkIfGameOver();

        endTurnUpdates();

        checkAchievements(choice);

        cardSelection();

        this.renderer.renderEvents();
    }

    private Boolean checkSpecialEvents() {
        // check for special events based on active flags or game state

        if (activeFlags.containsKey("start_vampire_event") && (Boolean)activeFlags.get("start_vampire_event")) {
            // trigger the vampire event
            activeFlags.put("special", "vampire_event");
            activeFlags.remove("start_vampire_event");
            System.out.println("DEBUG: triggering vampire special event.");

            return true;
        }

        if (activeFlags.containsKey("start_writer_event") && (Boolean)activeFlags.get("start_writer_event")) {
            // trigger the writer event
            activeFlags.put("special", "writer_event");
            activeFlags.remove("start_writer_event");
            System.out.println("DEBUG: triggering writer special event.");

            return true;
        }

        return false;
    }

    @Override
    public void update() {
        // Update game logic

        for (BonusCard bonus : bonusCardManager.getActiveBonusCards()) {
            bonus.updateAnimation();
        }
    }

    @Override
    public MouseInteractable getInputHandler() {
        // delegate input handling to the renderer
        return this.renderer;
    }

    // Method to read the cards JSON file and return the content as a string
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

        Choice c = new Choice(choiceText, effectsMap, flagsMap, bonusCardId, achievementId);
        return c;
    }

    public void parseCards() {
        String jsonContent = readJsonFile("res/cards.json");

        // verify the content right before parsing
        // System.out.println("DEBUG: JSON String to be Parsed: " + jsonContent); 

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
                JOptionPane.showMessageDialog(app, "Error reading card JSON file.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
        
                app.setCurrentState(new MainMenuState(app)); // change to main menu state on error
            }
        } else {
            JOptionPane.showMessageDialog(app, "Error reading card JSON file.", "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error: JSON content is empty or null.");

            app.setCurrentState(new MainMenuState(app)); // change to main menu state on error
        }
    }

    @Override
    public void render(Graphics g, int width, int height) { 
        renderer.render(g, this, width, height);
    }

    // getter methods

    public Card getCurrentCard() { return currentCard; }
    public SpecialEventManager getEventManager() { return eventManager; }
    public Map<String, Float> getStats() { return stats; }
    public String getMonarchName() { return monarchName; }
    public List<BonusCard> getActiveBonusCards() { return bonusCardManager.getActiveBonusCards(); }
    public Integer getYear() { return year; }
    public PlayingStateRenderer getRenderer() { return renderer; }
    public List<Card> getCards() { return cards; }
}
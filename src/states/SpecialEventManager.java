package states;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Card;
import data.SpecialChoice;
import data.SpecialEventCard;

import java.util.List;

public class SpecialEventManager {

    // master map: event Name -> list of cards
    // e.g. 'vampire' -> [Card 1, Card 2 ...]
    private Map<String, Map<Integer, SpecialEventCard>> allEvents; 
    
    // track if an event is active
    private boolean isEventActive = false;
    private String currentEventName = null;
    private Integer currentEventCardId = null;

    public SpecialEventManager() {
        this.allEvents = new HashMap<>();
        
        // Load special event cards from JSON or other data source
        loadEventCards();

        System.out.println("SpecialEventManager initialized with events: " + allEvents.keySet());
    }

    // core logic

    public void startEvent(String eventName) {
        if (allEvents.containsKey(eventName)) {
            this.isEventActive = true;
            this.currentEventName = eventName;
            this.currentEventCardId = 1; // each event starts with card ID 1
        } else {
            System.out.println(allEvents.keySet());
            System.out.println("ERROR: event " + eventName + " does not exist.");
        }
    }

    public void advanceStory(SpecialChoice choice) {
        this.currentEventCardId = choice.getNodeId(); // get next card ID from choice and set current card ID

        // check if the next card exists in the current event's deck, if not end the event
        if (!allEvents.get(currentEventName).containsKey(this.currentEventCardId)) {
            endEvent();
            return;
        }

        // if the next card ID is null, end the event

        if (this.currentEventCardId == null) { // terminal card, event is over
            endEvent();
        }
    }

    public void endEvent() {
        this.isEventActive = false;
        this.currentEventName = null;
        this.currentEventCardId = null;
    }

    // getters

    public boolean isEventActive() {
        return isEventActive;
    }

    public SpecialEventCard getCurrentEventCard() {
        System.out.println("Getting current event card: " + currentEventName + " ID: " + currentEventCardId);

        // debug print available cards
        for (Map.Entry<Integer, SpecialEventCard> entry : allEvents.get(currentEventName).entrySet()) {
            System.out.println("Card ID: " + entry.getKey() + " Text: " + entry.getValue().getText());
        }

        System.out.println("Available cards for event: " + allEvents.get(currentEventName).keySet());
        return allEvents.get(currentEventName).get(currentEventCardId);
    }
    
    // JSON parsing logic goes here, populating eventCards

    // method to read the JSON file and return the content as a string
    public String readJsonFile(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            if (is == null) {
                throw new IOException("Could not find " + filePath + " in resources");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            
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

    public String getCurrentEventName() {
        return currentEventName;
    }

    public SpecialChoice parseChoice(JSONObject choiceJson) {
        String text = choiceJson.getString("text");

        // Parse effects
        JSONObject effectsJson = choiceJson.getJSONObject("effects");
        Map<String, Float> effects = new HashMap<>();
        for (String key : effectsJson.keySet()) {
            effects.put(key, effectsJson.getFloat(key));
        }

        // Parse flags

        JSONObject flagsJson = choiceJson.has("flags") ? choiceJson.getJSONObject("flags") : new JSONObject(); // default to empty if no flags
        Map<String, Object> flags = new HashMap<>();
        for (String key : flagsJson.keySet()) {
            flags.put(key, flagsJson.get(key));
        }

        // parse achievementId and nodeId

        Integer achievementId = choiceJson.has("achievementId") ? choiceJson.getInt("achievementId") : null;
        
        if (choiceJson.isNull("nextCardId")) { // handle null case
            return new SpecialChoice(text, effects, flags, achievementId, null);
        }

        Integer nodeId = choiceJson.has("nextCardId") ? choiceJson.getInt("nextCardId") : null; 

        System.out.println("Parsing choice with text: " + text + " Achievement ID: " + achievementId + " Next Card ID: " + nodeId);

        return new SpecialChoice(text, effects, flags, achievementId, nodeId);
    }

    public SpecialEventCard parseCard(JSONObject cardJson) {
        Integer id = cardJson.getInt("id");
        String text = cardJson.getString("text");
        String characterName = cardJson.getString("characterName");
        String imagePath = cardJson.getString("imagePath");

        // Parse choices
        JSONObject choicesJson = cardJson.getJSONObject("choices");
        
        JSONObject leftChoiceJson = choicesJson.getJSONObject("left");
        JSONObject rightChoiceJson = choicesJson.getJSONObject("right");

        SpecialChoice leftChoice = parseChoice(leftChoiceJson);
        SpecialChoice rightChoice = parseChoice(rightChoiceJson);

        SpecialEventCard card = new SpecialEventCard(id, text, characterName, leftChoice, rightChoice, imagePath);

        return card;
    }

    private void loadEventCards() {
        String jsonContent = readJsonFile("/res/special-event.json");
        if (jsonContent == null) return;

        try {
            JSONObject root = new JSONObject(jsonContent);
            
            // Loop through each event key (e.g. 'vampire') 
            for (String eventName : root.keySet()) {
                JSONArray cardsArray = root.getJSONArray(eventName);
                Map<Integer, SpecialEventCard> eventDeck = new HashMap<>();

                // Parse the cards for this event
                for (int i = 0; i < cardsArray.length(); i++) {
                    JSONObject cardJson = cardsArray.getJSONObject(i);
                    SpecialEventCard card = parseCard(cardJson); // returns a card object with the special choice also parsed correctly

                    eventDeck.put(card.getId(), card); // store card by its ID for efficient retrieval
                }
                
                // Store this deck in the master map
                allEvents.put(eventName, eventDeck);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
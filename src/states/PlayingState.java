package states;
import states.GameState;
import ui.CardComponent;
import ui.ButtonComponent;
import ui.CardComponent;
import ui.StatImageComponent;
import ui.BonusCardComponent;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import java.io.IOException;
import java.lang.reflect.Array;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

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
import java.io.FileReader;
import java.io.BufferedReader;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.io.File;

public class PlayingState implements GameState, MouseInteractable {

    // game state and ui

    private Point mouse = new Point();

    private Rectangle cardObject;

    private Rectangle decisionLeft, decisionRight;

    private Rectangle bonusCard1, bonusCard2, bonusCard3, bonusCard4;

    private Rectangle[] bonusCards = {bonusCard1, bonusCard2, bonusCard3, bonusCard4};

    private Rectangle peopleStatIcon;
    private Rectangle wealthStatIcon;
    private Rectangle knowledgeStatIcon;
    private Rectangle armyStatIcon;

    private StatImageComponent peopleStatImage;
    private StatImageComponent wealthStatImage;
    private StatImageComponent knowledgeStatImage;
    private StatImageComponent armyStatImage;

    // core game state

    private App app;

    private Integer year = 1; 

    private String monarchName;

    private String[] activeBonusCards;

    Map<String, Float> stats = new LinkedHashMap<>();

    Map<String, Object> activeFlags = new HashMap<>();

    private Float time = 0.0f;

    // card handling

    private List<Card> cards;

    private Map<Card, Float> weightedDeck;

    private Card currentCard;

    public PlayingState(App app, String name) {
        this.app = app;
        this.monarchName = name;

        initUIComponents();
        initStats();

        // fetching cards

        this.cards = new ArrayList<>();

        parseCards();

        if (!cards.isEmpty()) {
            currentCard = cards.get(0); // TEMP start with the first card
        }
    }

    // Initializes UI component objects once to avoid creating them in render().
    private void initUIComponents() {
        this.cardObject = new Rectangle();
        this.decisionLeft = new Rectangle();
        this.decisionRight = new Rectangle();
        this.bonusCards = new Rectangle[] {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};
        this.peopleStatIcon = new Rectangle();
        this.wealthStatIcon = new Rectangle();
        this.knowledgeStatIcon = new Rectangle();
        this.armyStatIcon = new Rectangle();

        this.peopleStatImage = new StatImageComponent("src/assets/stats/people.png");
        this.wealthStatImage = new StatImageComponent("src/assets/stats/wealth.png");
        this.knowledgeStatImage = new StatImageComponent("src/assets/stats/knowledge.png");
        this.armyStatImage = new StatImageComponent("src/assets/stats/army.png");

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

    public void cardSelection() {
        // logic for selecting a card from JSON file

        // temp for now just pick a random card

        int randomIndex = (int)(Math.random() * cards.size());
        currentCard = cards.get(randomIndex);
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

    @Override
    public void mousePressed(MouseEvent e) {
        Choice selectedChoice = null;

        if (decisionLeft.contains(e.getPoint())) {
            // Handle left decision
            System.out.println("Left decision made");

            selectedChoice = currentCard.getLeft();

        } else if (decisionRight.contains(e.getPoint())) {
            // Handle right decision
            System.out.println("Right decision made");

            selectedChoice = currentCard.getRight();
        }

        for (String effectKey : selectedChoice.getEffects().keySet()) {
            // Apply each effect to the game state
            Float effectValue = selectedChoice.getEffects().get(effectKey);

            // update stats based on effect key

            for (Map.Entry<String, Float> statEntry : stats.entrySet()) {
                String name = statEntry.getKey();
                Float value = statEntry.getValue();

                if (effectKey.equals(name)) {
                    Float change = value + effectValue;

                    // clamp the value between 0 and 1
                    stats.put(name, Math.max(0.0f, Math.min(1.0f, change)));
                }
            }
        }

        System.out.println(stats.get("people") + ", " + stats.get("wealth") + ", " + stats.get("knowledge") + ", " + stats.get("army"));

        checkIfGameOver();

        year += 1;

        cardSelection();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouse = e.getPoint();
    }

    @Override
    public void update() {
        // Update game logic
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

        // update the active flags map with any new flags from this choice

        if (!flagsMap.isEmpty()) {
            for (Map.Entry<String, Object> flagEntry : flagsMap.entrySet()) { // update active flags map
                String flagName = flagEntry.getKey();
                Object flagValue = flagEntry.getValue();

                activeFlags.put(flagName, flagValue);
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

    private void drawPanel(Graphics g, int panelX, int panelY, int panelWidth, int panelHeight) {
        // Draw panel background
        g.setColor(new Color(179, 221, 254));
        g.fillRect(panelX, panelY, panelWidth, panelHeight);
    }

    private void drawStats(Graphics g, int panelX, int panelWidth, int panelY) {
        // Draw and align stat icons and update percentage filled

        int iconSize = (int)(panelWidth * 0.09); 
        int gap = (int)(panelWidth * 0.04);
        int totalIconsWidth = (iconSize * 4) + (gap * 3);
        int iconsStartX = panelX + (panelWidth - totalIconsWidth) / 2;
        int statIconStartY = 10; 

        peopleStatIcon.setBounds(iconsStartX, statIconStartY, iconSize, iconSize);
        wealthStatIcon.setBounds(iconsStartX + (iconSize + gap), statIconStartY, iconSize, iconSize);
        knowledgeStatIcon.setBounds(iconsStartX + 2 * (iconSize + gap), statIconStartY, iconSize, iconSize);
        armyStatIcon.setBounds(iconsStartX + 3 * (iconSize + gap), statIconStartY, iconSize, iconSize);
        
        peopleStatImage.draw(g, peopleStatIcon, mouse);
        wealthStatImage.draw(g, wealthStatIcon, mouse);
        knowledgeStatImage.draw(g, knowledgeStatIcon, mouse);
        armyStatImage.draw(g, armyStatIcon, mouse);

        // updating percentage filled of the stats, this will run every 16ms so will appear instantaneous as each decision is made without needing to manaually update when effects are put in place 

        peopleStatImage.updatePercentageFilled(g, peopleStatIcon, stats.get("people")); 
        wealthStatImage.updatePercentageFilled(g, wealthStatIcon, stats.get("wealth"));
        knowledgeStatImage.updatePercentageFilled(g, knowledgeStatIcon, stats.get("knowledge"));
        armyStatImage.updatePercentageFilled(g, armyStatIcon, stats.get("army"));
    }

    private void drawOverlayText(Graphics g) {
        // Monarch name and current year in top left

        g.setColor(Color.BLACK);
        g.drawString("Monarch: " + monarchName, 10, 30);
        g.drawString("YEAR: " + String.valueOf(year), 10, 50); 
    }

    private void drawCard(Graphics g, int panelX, int panelY, int panelWidth, int panelHeight) {
        // Draw card

        int cardWidth = (int)(panelWidth * 0.5);
        int cardHeight = (int)(panelHeight * 0.7);

        cardObject.setBounds(panelX + (panelWidth - cardWidth) / 2, panelY + (int)(panelHeight * 0.1), cardWidth, cardHeight);

        if (currentCard != null) {
            CardComponent.draw(g, cardObject, mouse, currentCard.getText(), currentCard.getCharacterName(), currentCard.getImagePath());
        }
    }

    private void drawDecisions(Graphics g, int panelX, int panelY, int panelWidth, int panelHeight) {
        // Draw and align decision buttons either side of the card 

        int decisionButtonWidth = (int)(panelWidth * 0.2);
        int decisionButtonY = panelHeight / 2;

        decisionLeft.setBounds(panelX - decisionButtonWidth - 20, decisionButtonY, decisionButtonWidth, 50);
        decisionRight.setBounds(panelX + panelWidth + 20, decisionButtonY, decisionButtonWidth, 50);

        if (currentCard != null) {
            ButtonComponent.draw(g, currentCard.getLeft().getText(), decisionLeft, mouse);
            ButtonComponent.draw(g, currentCard.getRight().getText(), decisionRight, mouse);
        }
    }

    private void drawBonusCards(Graphics g, int panelX, int panelY, int panelWidth, int panelHeight) {
        // Bonus cards (aligned at the bottom of the panel)

        int bonusCardWidth = (int)(panelWidth * 0.2);
        int bonusCardHeight = (int)(panelHeight * 0.15);

        int bonusCardGap = (int)(panelWidth * 0.03);

        int totalBonusCardsWidth = (bonusCardWidth * 4) + (bonusCardGap * 3); // total width of all bonus cards and gaps

        int bonusCardsStartX = panelX + (panelWidth - totalBonusCardsWidth) / 2; 
        int bonusCardsY = panelY + panelHeight - bonusCardHeight - (int)(panelHeight * 0.03);

        for (int i = 0; i < bonusCards.length; i++) {
            bonusCards[i].setBounds(bonusCardsStartX + (i * (bonusCardWidth + bonusCardGap)), bonusCardsY, bonusCardWidth, bonusCardHeight);
        }

        // drawing bonus cards

        for (int i = 0; i < bonusCards.length; i++) { // draw each bonus card
            BonusCardComponent.draw(g, activeBonusCards[i], bonusCards[i], mouse);
        }
    }

    @Override
    public void render(Graphics g, int width, int height) { 

        Font generalFont = new Font("Telegraf", Font.PLAIN, 20);

        g.setFont(generalFont);

        // panel layout 

        float panelWidthRatio = 0.6f; // panel takes up 60% of the screen width
        float panelHeightRatio = 0.9f; // panel takes up 90% of the screen height
        int panelWidth = (int)(width * panelWidthRatio);
        int panelHeight = (int)(height * panelHeightRatio);
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2; 

        // call helper methods to draw each part 
        drawPanel(g, panelX, panelY, panelWidth, panelHeight);
        drawStats(g, panelX, panelWidth, panelY);
        drawOverlayText(g);
        drawCard(g, panelX, panelY, panelWidth, panelHeight);
        drawDecisions(g, panelX, panelY, panelWidth, panelHeight);
        drawBonusCards(g, panelX, panelY, panelWidth, panelHeight);
    }
}
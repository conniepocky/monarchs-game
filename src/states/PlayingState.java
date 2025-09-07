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

    private Point mouse = new Point();

    private Rectangle cardObject;

    private Rectangle decisionLeft, decisionRight;

    private Rectangle bonusCard1, bonusCard2, bonusCard3, bonusCard4;

    private Rectangle[] bonusCards = {bonusCard1, bonusCard2, bonusCard3, bonusCard4};

    private Rectangle peopleStatIcon;
    private Rectangle moneyStatIcon;
    private Rectangle knowledgeStatIcon;
    private Rectangle armyStatIcon;

    private App app;

    private Integer year = 1; 

    private String monarchName;

    private String[] activeBonusCards;

    private Float peopleStat = 0.5f;
    private Float moneyStat = 0.5f;
    private Float knowledgeStat = 0.5f;
    private Float armyStat = 0.5f;

    private Float time = 0.0f;

    private List<Card> cards;

    private Card currentCard;

    public PlayingState(App app, String name) {
        this.app = app;

        this.monarchName = name;

        this.activeBonusCards = new String[] {
            "",
            "",
            "",
            ""
        };

        // ui set up 

        this.cardObject = new Rectangle();
        this.decisionLeft = new Rectangle();
        this.decisionRight = new Rectangle();

        this.bonusCard1 = new Rectangle();
        this.bonusCard2 = new Rectangle();
        this.bonusCard3 = new Rectangle();
        this.bonusCard4 = new Rectangle();

        this.bonusCards = new Rectangle[] {bonusCard1, bonusCard2, bonusCard3, bonusCard4};

        this.peopleStatIcon = new Rectangle();
        this.moneyStatIcon = new Rectangle();
        this.knowledgeStatIcon = new Rectangle();
        this.armyStatIcon = new Rectangle();

        // fetching cards

        this.cards = new ArrayList<>();

        parseCards();

        if (!cards.isEmpty()) {
            currentCard = cards.get(0); // start with the first card
        }
    }

    public void CardSelection() {
        // logic for selecting a card from JSON file
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (decisionLeft.contains(e.getPoint())) {
            // Handle left decision
            System.out.println("Left decision made");
        } else if (decisionRight.contains(e.getPoint())) {
            // Handle right decision
            System.out.println("Right decision made");
        }
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
            e.printStackTrace();
        }

        return null;
    }

    public Choice parseChoice(JSONObject choiceContent) {
        String choiceText = choiceContent.getString("text");

        Integer achievementId = choiceContent.has("achievementId") ? choiceContent.getInt("achievementId") : null;
        Integer bonusCardId = choiceContent.has("bonusCardId") ? choiceContent.getInt("bonusCardId") : null;

        JSONObject effects = choiceContent.getJSONObject("effects");


        Map<String, Integer> effectsMap = new HashMap<>();

        for (String key : effects.keySet()) {
            effectsMap.put(key, effects.getInt(key));
            System.out.println("Effect Key: " + key + ", Value: " + effects.get(key));
        }

        System.out.println("Choice Effects: " + effects.toString());

        return new Choice(choiceText, effectsMap, achievementId, bonusCardId);
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

                    // create card object and add to list
                    
                    Card card = new Card(id, text, characterName, left, right, imagePath);
                    this.cards.add(card);                    
                }
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Error: JSON content is empty or null.");
        }
    }

    @Override
    public void render(Graphics g, int width, int height) { 

        // fonts

        Font generalFont = new Font("Telegraf", Font.PLAIN, 20);

        g.setFont(generalFont);
    
        // panel alignment

        float panelWidthRatio = 0.6f; // panel takes up 60% of the screen width
        float panelHeightRatio = 0.9f; // panel takes up 90% of the screen height

        int panelWidth = (int)(width * panelWidthRatio);
        int panelHeight = (int)(height * panelHeightRatio);

        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2; 
        
        g.setColor(new Color(179, 221, 254));
        g.fillRect(panelX, panelY, panelWidth, panelHeight);

        // draw and align stat icons and update percentage filled

        StatImageComponent peopleStatImage = new StatImageComponent("src/assets/stats/people.png");
        StatImageComponent moneyStatImage = new StatImageComponent("src/assets/stats/money.png");
        StatImageComponent knowledgeStatImage = new StatImageComponent("src/assets/stats/knowledge.png");
        StatImageComponent armyStatImage = new StatImageComponent("src/assets/stats/army.png");

        int iconSize = (int)(panelWidth * 0.09); 
        int gap = (int)(panelWidth * 0.04);
        int totalIconsWidth = (iconSize * 4) + (gap * 3);
        int iconsStartX = panelX + (panelWidth - totalIconsWidth) / 2;
        int statIconStartY = 10; 

        peopleStatIcon.setBounds(iconsStartX, statIconStartY, iconSize, iconSize);
        moneyStatIcon.setBounds(iconsStartX + (iconSize + gap), statIconStartY, iconSize, iconSize);
        knowledgeStatIcon.setBounds(iconsStartX + 2 * (iconSize + gap), statIconStartY, iconSize, iconSize);
        armyStatIcon.setBounds(iconsStartX + 3 * (iconSize + gap), statIconStartY, iconSize, iconSize);
        
        peopleStatImage.draw(g, peopleStatIcon, mouse);
        moneyStatImage.draw(g, moneyStatIcon, mouse);
        knowledgeStatImage.draw(g, knowledgeStatIcon, mouse);
        armyStatImage.draw(g, armyStatIcon, mouse);

        peopleStatImage.updatePercentageFilled(g, peopleStatIcon, peopleStat); 
        moneyStatImage.updatePercentageFilled(g, moneyStatIcon, moneyStat);
        knowledgeStatImage.updatePercentageFilled(g, knowledgeStatIcon, knowledgeStat);
        armyStatImage.updatePercentageFilled(g, armyStatIcon, armyStat);

        // monarch name and current year in top left

        g.setColor(Color.BLACK);
        g.drawString("Monarch: " + monarchName, 10, 30);
        g.drawString("YEAR: " + String.valueOf(year), 10, 50); 
    
        // draw card

        int cardWidth = (int)(panelWidth * 0.5);
        int cardHeight = (int)(panelHeight * 0.7);

        cardObject.setBounds(panelX + (panelWidth - cardWidth) / 2, panelY + (int)(panelHeight * 0.1), cardWidth, cardHeight);
        CardComponent.draw(g, cardObject, mouse, currentCard.getText(), currentCard.getCharacterName(), currentCard.getImagePath());


        // draw + align decision buttons either side of the card 

        int buttonFontSize = (int)(panelHeight * 0.025);

        g.setFont(new Font("Telegraf", Font.PLAIN, buttonFontSize));

        int decisionButtonWidth = (int)(panelWidth * 0.2);
        int decisionButtonY = panelHeight / 2;

        decisionLeft.setBounds(panelX - decisionButtonWidth - 20, decisionButtonY, decisionButtonWidth, 50);
        decisionRight.setBounds(panelX + panelWidth + 20, decisionButtonY, decisionButtonWidth, 50);

        ButtonComponent.draw(g, currentCard.getLeft().getText(), decisionLeft, mouse);
        ButtonComponent.draw(g, currentCard.getRight().getText(), decisionRight, mouse);

        // bonus cards (aligned at the bottom of the panel)

        int bonusCardWidth = (int)(panelWidth * 0.2);
        int bonusCardHeight = (int)(panelHeight * 0.15);

        int bonusCardGap = (int)(panelWidth * 0.03);

        int totalBonusCardsWidth = (bonusCardWidth * 4) + (bonusCardGap * 3);
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
}
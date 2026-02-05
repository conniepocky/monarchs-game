package states;

import ui.StatImageComponent;
import ui.CardComponent;
import ui.ButtonComponent;
import ui.StatImageComponent;
import ui.BonusCardComponent;

import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JOptionPane;

import java.io.IOException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import core.App;

import data.Card;
import data.SpecialChoice;
import data.BonusCard;
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
import java.awt.Point;

public class PlayingStateRenderer implements MouseInteractable {

    private PlayingState state;

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

    private Map<String, Color> panelColour = new HashMap<>();
    private Map<String, Color> cardColour = new HashMap<>();
    private Map<String, Color> statColour = new HashMap<>();

    private String currentColourPalette = "default";

    public PlayingStateRenderer(PlayingState state) {
        this.state = state;

        initUIComponents();
        initColours();
    }

    private void initColours() {

        currentColourPalette = "default";

        // panel colours
        panelColour.put("default", new Color(179, 221, 254));
        panelColour.put("vampire", new Color(191, 18, 77));
        panelColour.put("writer", new Color(100, 13, 95));

        // card colours
        cardColour.put("default", new Color(113, 163, 193));
        cardColour.put("vampire", new Color(239, 136, 173));
        cardColour.put("writer", new Color(234, 34, 100));

        // stat colours
        statColour.put("default", new Color(255, 0, 0, 128));
        statColour.put("vampire", new Color(103, 178, 216, 128));
        statColour.put("writer", new Color(247, 141, 96, 128));
    }

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
    }

    public void render(Graphics g, PlayingState state, int width, int height) {
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
        drawStats(g, state, panelX, panelWidth, panelY);
        drawOverlayText(g, state);
        drawCard(g, state, panelX, panelY, panelWidth, panelHeight);
        drawDecisions(g, state, panelX, panelY, panelWidth, panelHeight);
        drawBonusCards(g, state, panelX, panelY, panelWidth, panelHeight);
    }

    private void drawPanel(Graphics g, int panelX, int panelY, int panelWidth, int panelHeight) {
        // Draw panel background
        g.setColor(panelColour.get(currentColourPalette));
        g.fillRect(panelX, panelY, panelWidth, panelHeight);
    }

    private void drawStats(Graphics g, PlayingState state, int panelX, int panelWidth, int panelY) {
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

        peopleStatImage.updatePercentageFilled(g, peopleStatIcon, state.getStats().get("people"), statColour.get(currentColourPalette)); 
        wealthStatImage.updatePercentageFilled(g, wealthStatIcon, state.getStats().get("wealth"), statColour.get(currentColourPalette));
        knowledgeStatImage.updatePercentageFilled(g, knowledgeStatIcon, state.getStats().get("knowledge"), statColour.get(currentColourPalette));
        armyStatImage.updatePercentageFilled(g, armyStatIcon, state.getStats().get("army"), statColour.get(currentColourPalette));
    }

    private void drawOverlayText(Graphics g, PlayingState state) {
        // Monarch name and current year in top left

        g.setColor(Color.BLACK);
        g.drawString("Monarch: " + state.getMonarchName(), 10, 30);
        g.drawString("YEAR: " + String.valueOf(state.getYear()), 10, 50); 
    }

    private void drawCard(Graphics g, PlayingState state, int panelX, int panelY, int panelWidth, int panelHeight) {
        // draw card

        int cardWidth = (int)(panelWidth * 0.5);
        int cardHeight = (int)(panelHeight * 0.7);

        cardObject.setBounds(panelX + (panelWidth - cardWidth) / 2, panelY + (int)(panelHeight * 0.1), cardWidth, cardHeight);

        if (state.getCurrentCard() != null) {
            CardComponent.draw(g, cardObject, mouse, state.getCurrentCard().getText(), state.getCurrentCard().getCharacterName(), state.getCurrentCard().getImagePath(), cardColour.get(currentColourPalette));
        }
    }

    private void drawDecisions(Graphics g, PlayingState state, int panelX, int panelY, int panelWidth, int panelHeight) {
        // Draw and align decision buttons either side of the card 

        int decisionButtonWidth = (int)(panelWidth * 0.2);
        int decisionButtonY = panelHeight / 2;

        decisionLeft.setBounds(panelX - decisionButtonWidth - 20, decisionButtonY, decisionButtonWidth, 50);
        decisionRight.setBounds(panelX + panelWidth + 20, decisionButtonY, decisionButtonWidth, 50);

        if (state.getCurrentCard() != null) {
            ButtonComponent.draw(g, state.getCurrentCard().getLeft().getText(), decisionLeft, mouse);
            ButtonComponent.draw(g, state.getCurrentCard().getRight().getText(), decisionRight, mouse);
        }
    }

    private void drawBonusCards(Graphics g, PlayingState state, int panelX, int panelY, int panelWidth, int panelHeight) {
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

        List<BonusCard> activeBonusCards = state.getActiveBonusCards();

        for (int i = 0; i < bonusCards.length; i++) {  // draw each bonus card
            if (i >= activeBonusCards.size()) {
                // empty placeholder, no active bonus card
                BonusCardComponent.drawPlaceholder(g, bonusCards[i]);
            } else {
                BonusCard bonusCard = activeBonusCards.get(i);
                
                BonusCardComponent.draw(g, bonusCard, bonusCards[i], mouse);
            }
        }
    }

    public void renderEvents() {
        String eventName = state.getEventManager().getCurrentEventName();

        if (eventName == null) {
            currentColourPalette = "default";
            return;
        }

        switch (eventName) {
            case "vampire":
                currentColourPalette = "vampire";
                break;
            case "writer":
                currentColourPalette = "writer";
                break;
            // add more cases for other events as needed
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // Handle mouse wheel movement here
        System.out.println("Mouse wheel moved: " + e.getWheelRotation());
    }
 
    @Override
    public void mousePressed(MouseEvent e) {
        String clickedItem = getClickedArea(e.getPoint());

        if (clickedItem == null) {
            return;
        }

        // handle bonus card clicks
        if (clickedItem.startsWith("bonusCard_")) {
            int index = Integer.parseInt(clickedItem.split("_")[1]);
            List<BonusCard> activeBonuses = state.getActiveBonusCards();

            if (index < activeBonuses.size()) {
                BonusCard clickedBonus = activeBonuses.get(index);
                JOptionPane.showMessageDialog(null, "Bonus Card Details:\n" +
                    "Title: " + clickedBonus.getTitle() + "\n" +
                    "Description: " + clickedBonus.getDescription() + "\n" +
                    "Turns Remaining: " + clickedBonus.getTurnsRemaining(),
                    "Bonus Card Info",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            return; // exit after handling bonus card click
        }

        // handle clicks based on the clicked area
        switch (clickedItem) {
            case "leftChoice":
                System.out.println("Left decision made");
                state.makeChoice(state.getCurrentCard().getLeft());
                break;
                
            case "rightChoice":
                System.out.println("Right decision made");
                state.makeChoice(state.getCurrentCard().getRight());
                break;
        }
    }

    public String getClickedArea(Point mousePoint) {
        if (decisionLeft.contains(mousePoint)) {
            return "leftChoice";
        }
        
        if (decisionRight.contains(mousePoint)) {
            return "rightChoice";
        }
        
        // clicking on bonus cards
        for (int i = 0; i < bonusCards.length; i++) {
            if (bonusCards[i].contains(mousePoint)) {
                return "bonusCard_" + i; // will return "bonusCard_0", "bonusCard_1", etc.
            }
        }
        
        // clicked in an empty area
        return null; 
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouse = e.getPoint();
    }
}
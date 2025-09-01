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

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import core.App;

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

    @Override
    public void render(Graphics g, int width, int height) { 

        // background image 

        // try {
        //     File file = new File("src/assets/bg.png");
        //     Image img = ImageIO.read(file);
        //     g.drawImage(img, 0, 0, width, height, null);
        // } catch (IOException e) {
        //     e.printStackTrace(); // Handle error
        // }

        // fonts

        Font generalFont = new Font("Telegraf", Font.PLAIN, 20);

        g.setFont(generalFont);
    
        // panel alignment

        float panelWidthRatio = 0.6f; // Panel takes up 60% of the screen width
        float panelHeightRatio = 0.9f; // Panel takes up 90% of the screen height

        int panelWidth = (int)(width * panelWidthRatio);
        int panelHeight = (int)(height * panelHeightRatio);

        int panelX = (width - panelWidth) / 2; // Center the panel horizontally
        int panelY = (height - panelHeight) / 2; // Center the panel vertically
        
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
        CardComponent.draw(g, cardObject, mouse, "Card text", "Character Name", "src/assets/characters/general.png");


        // draw + align decision buttons either side of the card 

        int buttonFontSize = (int)(panelHeight * 0.025);

        g.setFont(new Font("Telegraf", Font.PLAIN, buttonFontSize));

        int decisionButtonWidth = (int)(panelWidth * 0.2);
        int decisionButtonY = panelHeight / 2;

        decisionLeft.setBounds(panelX - decisionButtonWidth - 20, decisionButtonY, decisionButtonWidth, 50);
        decisionRight.setBounds(panelX + panelWidth + 20, decisionButtonY, decisionButtonWidth, 50);

        ButtonComponent.draw(g, "Decision Left", decisionLeft, mouse);
        ButtonComponent.draw(g, "Decision Right", decisionRight, mouse);

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
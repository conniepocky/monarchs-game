package states;
import states.GameState;
import ui.CardComponent;
import ui.ButtonComponent;
import ui.CardComponent;
import ui.StatImageComponent;
import ui.BonusCardComponent;

import java.awt.Graphics;
import java.awt.Rectangle;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import core.App;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;

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

        Font generalFont = new Font("Telegraf", Font.PLAIN, 20);
        Font buttonFont = new Font("Telegraf", Font.PLAIN, 15);

        g.setFont(generalFont);

        // stats images rendering and updating based on percentage

        StatImageComponent peopleStatImage = new StatImageComponent("src/assets/people.png");
        StatImageComponent moneyStatImage = new StatImageComponent("src/assets/money.png");
        StatImageComponent knowledgeStatImage = new StatImageComponent("src/assets/knowledge.png");
        StatImageComponent armyStatImage = new StatImageComponent("src/assets/army.png");

        // alignment of stat icons at the top of the screen, centered horizontally

        int iconWidth = 50;
        int iconHeight = 50;
        int gap = 20;
        int numIcons = 4;
        
        int totalWidth = numIcons * iconWidth + (numIcons - 1) * gap;
        
        int startX = (width - totalWidth) / 2;
        int y = 10; 
    
        peopleStatIcon.setBounds(startX, y, iconWidth, iconHeight);
        moneyStatIcon.setBounds(startX + (iconWidth + gap), y, iconWidth, iconHeight);
        knowledgeStatIcon.setBounds(startX + 2 * (iconWidth + gap), y, iconWidth, iconHeight);
        armyStatIcon.setBounds(startX + 3 * (iconWidth + gap), y, iconWidth, iconHeight);

        // draw stat icons and update percentage filled
        
        peopleStatImage.draw(g, peopleStatIcon, mouse);
        moneyStatImage.draw(g, moneyStatIcon, mouse);
        knowledgeStatImage.draw(g, knowledgeStatIcon, mouse);
        armyStatImage.draw(g, armyStatIcon, mouse);

        peopleStatImage.updatePercentageFilled(g, peopleStatIcon, peopleStat); 
        moneyStatImage.updatePercentageFilled(g, moneyStatIcon, moneyStat);
        knowledgeStatImage.updatePercentageFilled(g, knowledgeStatIcon, knowledgeStat);
        armyStatImage.updatePercentageFilled(g, armyStatIcon, armyStat);

        // monarch name and current year 

        g.setColor(Color.BLACK);
        g.drawString("Monarch: " + monarchName, 10, 30);
        g.drawString("Year: " + String.valueOf(year), 10, 50); 

        cardObject.setBounds((width-200)/2, (height/3)-100, 200, 300);
        CardComponent.draw(g, cardObject, mouse, "Card Text", "Character Name", "");

        // decision buttons either side of the card 

        g.setFont(buttonFont);

        decisionLeft.setBounds(cardObject.x - 220, cardObject.y + 100, 200, 50);
        decisionRight.setBounds(cardObject.x + cardObject.width + 20, cardObject.y + 100, 200, 50);
        ButtonComponent.draw(g, "Decision Left", decisionLeft, mouse);
        ButtonComponent.draw(g, "Decision Right", decisionRight, mouse);

        // bonus cards, bottom of the screen in middle

        int bonusCardWidth = 100;
        int bonusCardHeight = 100;

        int bonusCardX = (width - (bonusCardWidth * 4 + 30)) / 2;
        int bonusCardY = height - bonusCardHeight - 30;
        
        for (int i = 0; i < bonusCards.length; i++) {
            bonusCards[i].setBounds(bonusCardX + (i * (bonusCardWidth + 10)), bonusCardY, bonusCardWidth, bonusCardHeight);
        }

        for (int i = 0; i < bonusCards.length; i++) {
            BonusCardComponent.draw(g, activeBonusCards[i], bonusCards[i], mouse);
        }
    }
}
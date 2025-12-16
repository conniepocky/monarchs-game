package data;

import java.awt.Color;
import java.util.Map;

public class BonusCard {
    private Integer id;
    private String title;
    private String description;
    private Color cardColor;
    private Map<String, Float> effects;
    private Integer turnsRemaining;

    private Float opacity = 1.0f;
    private Boolean isFadingOut = false;

    public BonusCard(Integer id, String title, String description, Color cardColor, Map<String, Float> effects, int duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.cardColor = cardColor;
        this.effects = effects;
        this.turnsRemaining = duration;
    }

    public void decrementTurn() {
        this.turnsRemaining --;
    }

    private float lerp(float current, float target, float speed) {
        return current + (target - current) * speed;
    }

    public void updateAnimation() {
        // if the card is dying, go to 0.0 (Invisible). 
        // if it's alive, go to 1.0 (Visible).
        float target = isFadingOut ? 0.0f : 1.0f;

        // 2. apply the linear interpolation algorithm

        this.opacity = lerp(this.opacity, target, 0.1f); // moves 10% closer each update

        // snap to Target
        // lerp technically never reaches the target (zeno's paradox).
        // so if we are very close (within 0.01), force it to finish.
        if (Math.abs(target - this.opacity) < 0.01f) {
            this.opacity = target;
        }
    }

    public void expireCard() {
        if (this.opacity <= 0.0f) {
            this.isFadingOut = true;
        }
    }

    public boolean isFullyDead() {
        return isFadingOut && this.opacity <= 0.01f;
    }

    public boolean isExpired() {
        return turnsRemaining <= 0;
    }

    public String getDescription() { return description; }
    public String getTitle() { return title; }
    public Color getCardColor() { return cardColor; }
    public Float getOpacity() { return opacity; }
    public Map<String, Float> getEffects() { return effects; }
    public Integer getId() { return id; }
    public Integer getTurnsRemaining() { return turnsRemaining; }
}
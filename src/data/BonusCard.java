package data;

import java.util.Map;

public class BonusCard {
    private Integer id;
    private String description;
    private Map<String, Float> effects;
    private Integer turnsRemaining;
    private String imagePath;

    public BonusCard(Integer id, String description, Map<String, Float> effects, int duration, String imagePath) {
        this.id = id;
        this.description = description;
        this.effects = effects;
        this.turnsRemaining = duration;
        this.imagePath = imagePath;
    }

    public void decrementTurn() {
        this.turnsRemaining--;
    }

    public boolean isExpired() {
        return turnsRemaining <= 0;
    }

    public String getDescription() { return description; }
    public Map<String, Float> getEffects() { return effects; }
    public Integer getId() { return id; }
    public Integer getTurnsRemaining() { return turnsRemaining; }
    public String getImagePath() { return imagePath; }
}
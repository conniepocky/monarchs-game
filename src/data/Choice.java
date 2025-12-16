package data;

import java.util.Map;

public class Choice {
    protected String text;                      
    protected Map<String, Float> effects; // key: stat name, Value: change amount
    protected Map<String, Object> flags; // key: flag name, Value: flag value

    // optional fields for bonus cards and achievements

    protected Integer bonusCardId;              
    protected Integer achievementId;

    public Choice(String text, Map<String, Float> effects, Map<String, Object> flags, Integer bonusCardId, Integer achievementId) {
        this.text = text;
        this.effects = effects;
        this.flags = flags;
        this.bonusCardId = bonusCardId;
        this.achievementId = achievementId; 

        System.out.println("DEBUG: Choice Constructor - Bonus ID is: " + bonusCardId);
    }

    // Getters
    public String getText() {
        return text;
    }

    public Map<String, Float> getEffects() {
        return effects;
    }

    public Map<String, Object> getFlags() {
        return flags;
    }

    public Integer getBonusCardId() {
        return bonusCardId;
    }

    public Integer getAchievementId() {
        return achievementId;
    }
}

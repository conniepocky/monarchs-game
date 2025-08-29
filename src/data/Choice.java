package data;

import java.util.Map;

public class Choice {
    protected String text;                      
    protected Map<String, Integer> effects; // key: stat name, Value: change amount

    // optional fields for bonus cards and achievements

    private Integer bonusCardId;              
    protected Integer achievementId;

    public Choice(String text, Map<String, Integer> effects, Integer bonusCardId, Integer achievementId) {
        this.text = text;
        this.effects = effects;
        this.bonusCardId = bonusCardId;
        this.achievementId = achievementId; 
    }

    // Getters
    public String getText() {
        return text;
    }

    public Map<String, Integer> getEffects() {
        return effects;
    }

    public Integer getBonusCardId() {
        return bonusCardId;
    }

    public Integer getAchievementId() {
        return achievementId;
    }
}

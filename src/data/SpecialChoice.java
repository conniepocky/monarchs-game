package data;
import java.util.Map;

public class SpecialChoice extends Choice { 
    private SpecialEventCard node;

    public SpecialChoice(String text, Map<String, Integer> effects, Integer achievementId, SpecialEventCard node) {
        super(text, effects, null, achievementId);

        this.node = node;
    }

    // Getters

    public String getText() {
        return text;
    }

    public Map<String, Integer> getEffects() {
        return effects;
    }
    
    public Integer getAchievementId() {
        return achievementId;
    }

    public SpecialEventCard getNode() {
        return node;
    }
}

package data;
import java.util.Map;

public class SpecialChoice extends Choice { 
    private SpecialEventCard node;

    public SpecialChoice(String text, Map<String, Float> effects, Map<String,Object> flags, Integer achievementId, SpecialEventCard node) {
        super(text, effects, flags, null, achievementId);

        this.node = node;
    }

    // Getters

    public String getText() {
        return text;
    }

    public Map<String, Float> getEffects() {
        return effects;
    }
    
    public Integer getAchievementId() {
        return achievementId;
    }

    public SpecialEventCard getNode() {
        return node;
    }
}

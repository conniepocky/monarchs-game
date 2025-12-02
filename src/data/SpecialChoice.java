package data;
import java.util.Map;

public class SpecialChoice extends Choice { 
    private Integer nodeId;

    public SpecialChoice(String text, Map<String, Float> effects, Map<String,Object> flags, Integer achievementId, Integer nodeId) {
        super(text, effects, flags, null, achievementId);

        this.nodeId = nodeId;
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

    public Integer getNodeId() {
        return nodeId;
    }
}

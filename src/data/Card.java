package data;
import java.util.List;
import data.Choice;

public class Card {
    private Integer id;
    private String text;
    private String characterName;
    private List<String> tags;
    private Choice left;
    private Choice right;
    private String imagePath;

    public Card(Integer id, String text, String characterName, List<String> tags, Choice left, Choice right, String imagePath) {
        this.id = id;
        this.text = text;
        this.characterName = characterName;
        this.tags = tags;
        this.left = left;
        this.right = right;
        this.imagePath = imagePath;
    }

    public Integer getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getCharacterName() {
        return characterName;
    }

    public List<String> getTags() {
        return tags;
    }

    public Choice getLeft() {
        return left;
    }

    public Choice getRight() {
        return right;
    }

    public String getImagePath() {
        return imagePath;
    }
}

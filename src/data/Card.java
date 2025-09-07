package data;
import data.Choice;

public class Card {
    private Integer id;
    private String text;
    private String characterName;
    private Choice left;
    private Choice right;
    private String imagePath;

    public Card(Integer id, String text, String characterName, Choice left, Choice right, String imagePath) {
        this.id = id;
        this.text = text;
        this.characterName = characterName;
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

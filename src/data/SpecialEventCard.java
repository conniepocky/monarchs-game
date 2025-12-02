package data;

public class SpecialEventCard extends Card {
    public SpecialEventCard(Integer id, String text, String characterName, Choice left, Choice right, String imagePath) {
        super(id, text, characterName, null, left, right, imagePath);
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

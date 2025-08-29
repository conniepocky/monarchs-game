package data;

public class SpecialEventCard {
    private Integer id;
    private String text;
    private String characterName;
    private SpecialChoice left;
    private SpecialChoice right;
    private String imagePath;

    public SpecialEventCard(Integer id, String text, String characterName, SpecialChoice left, SpecialChoice right, String imagePath) {
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

    public SpecialChoice getLeft() {
        return left;
    }

    public SpecialChoice getRight() {
        return right;
    }

    public String getImagePath() {
        return imagePath;
    }
}

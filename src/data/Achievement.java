package data;

import java.util.Date;

public class Achievement {
    private Integer id;
    private String name;
    private String description;
    private Integer unlocked;
    private Date timestamp;

    public Achievement(Integer id, String name, String description, Integer unlocked, Date timestamp) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unlocked = unlocked; // 1 for unlocked, 0 for locked
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public Integer getUnlocked() {
        return unlocked;
    }
    public Date getTimestamp() {
        return timestamp;
    }
}

package data;

import java.util.Date;

public class Monarch {
    private Integer id;
    private String monarchName;
    private Integer reignLength;
    private String causeOfDeath;
    private Date timestamp;

    public Monarch(Integer id, String monarchName, Integer reignLength, String causeOfDeath, Date timestamp) {
        this.id = id;
        this.monarchName = monarchName;
        this.reignLength = reignLength;
        this.causeOfDeath = causeOfDeath;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }
    public String getMonarchName() {
        return monarchName;
    }
    public Integer getReignLength() {
        return reignLength;
    }
    public String getCauseOfDeath() {
        return causeOfDeath;
    }
    public Date getTimestamp() {
        return timestamp;
    }
}

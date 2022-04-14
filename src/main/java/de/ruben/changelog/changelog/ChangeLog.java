package de.ruben.changelog.changelog;

import java.util.Date;
import java.util.UUID;

public class ChangeLog {

    private UUID uuid;
    private Date createDate;
    private ChangeLogType changeLogType;
    private String creator, title, message;


    public ChangeLog(UUID uuid, Date createDate, ChangeLogType changeLogType, String creator, String title, String message) {
        this.uuid = uuid;
        this.createDate = createDate;
        this.changeLogType = changeLogType;
        this.creator = creator;
        this.title = title;
        this.message = message;
    }

    public ChangeLog(){}

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public ChangeLogType getChangeLogType() {
        return changeLogType;
    }

    public void setChangeLogType(ChangeLogType changeLogType) {
        this.changeLogType = changeLogType;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ChangeLog{" +
                "uuid=" + uuid +
                ", createDate=" + createDate +
                ", changeLogType=" + changeLogType +
                ", creator='" + creator + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

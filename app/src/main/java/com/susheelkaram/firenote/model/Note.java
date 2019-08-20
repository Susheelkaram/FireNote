package com.susheelkaram.firenote.model;

/**
 * Created by Susheel Kumar Karam
 * Website - SusheelKaram.com
 */
public class Note {
    private String title;
    private String content;
    private long timeStamp;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}

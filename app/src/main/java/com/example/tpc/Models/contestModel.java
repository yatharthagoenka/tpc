package com.example.tpc.Models;

public class contestModel {

    private String contestName;
    private String contestStart;
    private String contestDuration;
    private String contestLink;
    private String contestPlatform;

    // Constructor
    public contestModel(String contestName,String contestStart,String contestDuration,String contestLink,String contestPlatform) {
        this.contestName = contestName;
        this.contestStart = contestStart;
        this.contestDuration = contestDuration;
        this.contestLink = contestLink;
        this.contestPlatform = contestPlatform;
    }

    // Getter and Setter
    public String getContestName() {
        return contestName;
    }

    public void setContestName(String contestName) {
        this.contestName = contestName;
    }

    public String getContestStart() {
        return contestStart;
    }

    public void setContestStart(String contestStart) {
        this.contestStart = contestStart;
    }

    public String getContestDuration() {
        return contestDuration;
    }

    public void setContestDuration(String contestDuration) {
        this.contestDuration = contestDuration;
    }

    public String getContestLink() {
        return contestLink;
    }

    public void setContestLink(String contestLink) {
        this.contestLink = contestLink;
    }

    public String getContestPlatform() {
        return contestPlatform;
    }

    public void setContestPlatform(String contestPlatform) {
        this.contestPlatform = contestPlatform;
    }

}
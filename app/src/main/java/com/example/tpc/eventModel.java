package com.example.tpc;

public class eventModel {

//    private String eventDay;
//    private String eventMonth;
    private String eventDate;
    private String eventName;
    private String eventDomain;
    private String eventRegCount;
    private String currRollno;
    private String docID;
    private String isAdmin;
    private String eventDuration;
    private String eventDesc;
    private String currUsername;

    public eventModel(String eventDate, String eventName, String eventDomain, String eventRegCount, String currRollno, String docID, String isAdmin, String eventDuration, String eventDesc, String currUsername) {
//        this.eventDay = eventDay;
//        this.eventMonth = eventMonth;
        this.eventDate = eventDate;
        this.eventName = eventName;
        this.eventDomain = eventDomain;
        this.eventRegCount = eventRegCount;
        this.eventDuration = eventDuration;
        this.currRollno = currRollno;
        this.docID = docID;
        this.isAdmin = isAdmin;
        this.eventDesc = eventDesc;
        this.currUsername = currUsername;
    }

    public String getCurrUsername() {
        return currUsername;
    }

    public void setCurrUsername(String currUsername) {
        this.currUsername = currUsername;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(String eventDuration) {
        this.eventDuration = eventDuration;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getCurrRollno() {
        return currRollno;
    }

    public void setCurrRollno(String currRollno) {
        this.currRollno = currRollno;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }


    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDomain() {
        return eventDomain;
    }

    public void setEventDomain(String eventDomain) {
        this.eventDomain = eventDomain;
    }

    public String getEventRegCount() {
        return eventRegCount;
    }

    public void setEventRegCount(String eventRegCount) {
        this.eventRegCount = eventRegCount;
    }
}

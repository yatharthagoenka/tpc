package com.example.tpc;

public class eventModel {

    private String eventDay;
    private String eventMonth;
    private String eventName;
    private String eventDomain;
    private String eventRegCount;
    private String currRollno;
    private String docID;
    private String isAdmin;

    public eventModel(String eventDay, String eventMonth, String eventName, String eventDomain, String eventRegCount, String currRollno, String docID, String isAdmin) {
        this.eventDay = eventDay;
        this.eventMonth = eventMonth;
        this.eventName = eventName;
        this.eventDomain = eventDomain;
        this.eventRegCount = eventRegCount;
        this.currRollno = currRollno;
        this.docID = docID;
        this.isAdmin = isAdmin;
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

    public String getEventDay() {
        return eventDay;
    }

    public void setEventDay(String eventDay) {
        this.eventDay = eventDay;
    }

    public String getEventMonth() {
        return eventMonth;
    }

    public void setEventMonth(String eventMonth) {
        this.eventMonth = eventMonth;
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

package com.jcryptosync.domain;

public class Token {
    private String firstClientId;
    private String secondClientId;
    private String sessionId;

    private String dateCreate;
    private int lifeHours;

    byte[] digest;


    public String getFirstClientId() {
        return firstClientId;
    }

    public void setFirstClientId(String firstClientId) {
        this.firstClientId = firstClientId;
    }

    public String getSecondClientId() {
        return secondClientId;
    }

    public void setSecondClientId(String secondClientId) {
        this.secondClientId = secondClientId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public int getLifeHours() {
        return lifeHours;
    }

    public void setLifeHours(int lifeHours) {
        this.lifeHours = lifeHours;
    }

    public byte[] getDigest() {
        return digest;
    }

    public void setDigest(byte[] digest) {
        this.digest = digest;
    }
}

package com.example.hikecw;

public class Observation {
    private long id;
    private long hikeId;
    private String date;
    private String comment;
    private String observe;

    public Observation(long id, long hikeId, String date, String comment, String observe) {
        this.id = id;
        this.hikeId = hikeId;
        this.date = date;
        this.comment = comment;
        this.observe = observe;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public long getHikeId() {
        return hikeId;
    }
    public void setHikeId(long hikeId) {
        this.hikeId = hikeId;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getObserve() {
        return observe;
    }
    public void setObserve(String observate) {
        this.observe = observe;
    }
}

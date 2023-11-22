package com.example.hikecw;

public class Hike {
    private long id;
    private String title;
    private String location;
    private String date;
    private String park;
    private int length;
    private String level;
    private String description;
    private int pay;
    private byte[] image;

    public Hike() {
    }

    public Hike(long id, String title, String location, String date, String park, int length, String level, String description, int pay, byte[] image) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.park = park;
        this.length = length;
        this.level = level;
        this.description = description;
        this.pay = pay;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPark() {
        return park;
    }

    public void setPark(String park) {
        this.park = park;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
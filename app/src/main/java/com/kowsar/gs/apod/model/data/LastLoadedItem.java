package com.kowsar.gs.apod.model.data;

public class LastLoadedItem {
    private String date;
    private String title;
    private String url;
    private String desc;
    private byte[] imageData;

    public LastLoadedItem(String date, String title, String url, String desc, byte[] imageData) {
        this.date = date;
        this.title = title;
        this.url = url;
        this.desc = desc;
        this.imageData = imageData;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }




}

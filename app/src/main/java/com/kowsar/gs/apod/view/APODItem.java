package com.kowsar.gs.apod.view;

public class APODItem {
    private String id;
    private String title;
    private String thumbURL;

    public APODItem(String id, String title, String thumbURL) {
        this.id = id;
        this.title = title;
        this.thumbURL = thumbURL;
    }

    private String itemURL;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }


}

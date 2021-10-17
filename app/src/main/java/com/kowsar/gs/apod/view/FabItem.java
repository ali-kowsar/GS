package com.kowsar.gs.apod.view;

import android.util.Log;

public class FabItem {
    private final String TAG= this.getClass().getSimpleName();
    private String title;
    private int id;
    private String thumbURL;
    private String itemURL;

    public FabItem(String title, int id, String thumbURL, String itemURL) {
        Log.d(TAG,"FabItem(): id="+id+", title="+title+", thumbURL="+thumbURL+", itemURL="+itemURL);
        this.title = title;
        this.id = id;
        this.thumbURL = thumbURL;
        this.itemURL = itemURL;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    public String getItemURL() {
        return itemURL;
    }

    public void setItemURL(String itemURL) {
        this.itemURL = itemURL;
    }

    @Override
    public String toString() {
        return "FabItem{" +
                "title='" + title + '\'' +
                ", id=" + id +
                ", thumbURL='" + thumbURL + '\'' +
                ", itemURL='" + itemURL + '\'' +
                '}';
    }
}

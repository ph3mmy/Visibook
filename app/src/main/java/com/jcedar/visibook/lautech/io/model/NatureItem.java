package com.jcedar.visibook.lautech.io.model;

/**
 * Created by Afolayan on 28/9/2015.
 */
public class NatureItem {
    private String mName;
    private String mDes;
    private int mThumbnail;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDes() {
        return mDes;
    }

    public void setDes(String des) {
        this.mDes = des;
    }

    public int getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.mThumbnail = thumbnail;
    }

}

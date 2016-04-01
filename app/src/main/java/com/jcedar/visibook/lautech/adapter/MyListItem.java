package com.jcedar.visibook.lautech.adapter;

import android.database.Cursor;

/**
 * Created by Afolayan on 20/1/2016.
 */
public class MyListItem{
    private String name;

    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return name;
    }

    public static MyListItem fromCursor(Cursor cursor) {
        //TODO return your MyListItem from cursor.
        MyListItem m = new MyListItem();
        return m;
    }
}

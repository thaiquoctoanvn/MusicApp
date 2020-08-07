package com.example.musicapp.object;

import java.util.ArrayList;

import io.realm.RealmObject;

public class RecentKeySearch extends RealmObject {
    private String keys;

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }
}

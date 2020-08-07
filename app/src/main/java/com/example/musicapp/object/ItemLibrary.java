package com.example.musicapp.object;

public class ItemLibrary {
    private int srcItemIcon;
    private String itemName;
    private String itemDetail;
    private String itemId;

    public ItemLibrary(int srcItemIcon, String itemName, String itemDetail, String itemId) {
        this.srcItemIcon = srcItemIcon;
        this.itemName = itemName;
        this.itemDetail = itemDetail;
        this.itemId = itemId;
    }

    public int getSrcItemIcon() {
        return srcItemIcon;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDetail() {
        return itemDetail;
    }

    public String getItemId() {
        return itemId;
    }

    public void setSrcItemIcon(int srcItemIcon) {
        this.srcItemIcon = srcItemIcon;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemDetail(String itemDetail) {
        this.itemDetail = itemDetail;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}

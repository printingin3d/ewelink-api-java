
package com.github.realzimboguy.ewelink.api.model.home;

public class Thing {
    private int itemType;
    private ItemData itemData;
    private int index;

    public int getItemType() {
        return itemType;
    }
    public ItemData getItemData() {
        return itemData;
    }
    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "Thing{" +
                "itemType=" + itemType +
                ", itemData=" + itemData +
                ", index=" + index +
                '}';
    }
}

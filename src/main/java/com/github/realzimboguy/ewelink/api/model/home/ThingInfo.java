
package com.github.realzimboguy.ewelink.api.model.home;

import java.util.List;


public class ThingInfo {
    private List<Thing> thingList;
    private Integer total;

    @Override
    public String toString() {
        return "ThingInfo{" +
                "thingList=" + thingList +
                ", total=" + total +
                '}';
    }

    public List<Thing> getThingList() {
        return thingList;
    }

    public Integer getTotal() {
        return total;
    }

}

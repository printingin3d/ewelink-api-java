
package com.github.realzimboguy.ewelink.api.model.home;

public class Data {
    private FamilyInfo familyInfo;
    private ThingInfo thingInfo;

    @Override
    public String toString() {
        return "Data{" +
                "familyInfo=" + familyInfo +
                ", thingInfo=" + thingInfo +
                '}';
    }

    public FamilyInfo getFamilyInfo() {
        return familyInfo;
    }

    public ThingInfo getThingInfo() {
        return thingInfo;
    }
}

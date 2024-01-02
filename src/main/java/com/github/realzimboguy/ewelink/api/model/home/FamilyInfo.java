
package com.github.realzimboguy.ewelink.api.model.home;

import java.util.List;


public class FamilyInfo {
    private List<Family> familyList;
    private String currentFamilyId;
    private Boolean hasChangedCurrentFamily;

    @Override
    public String toString() {
        return "FamilyInfo{" +
                "familyList=" + familyList +
                ", currentFamilyId='" + currentFamilyId + '\'' +
                ", hasChangedCurrentFamily=" + hasChangedCurrentFamily +
                '}';
    }

    public List<Family> getFamilyList() {
        return familyList;
    }

    public String getCurrentFamilyId() {
        return currentFamilyId;
    }

    public Boolean getHasChangedCurrentFamily() {
        return hasChangedCurrentFamily;
    }
}

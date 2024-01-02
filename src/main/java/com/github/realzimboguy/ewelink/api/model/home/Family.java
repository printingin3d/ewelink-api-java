
package com.github.realzimboguy.ewelink.api.model.home;

import java.util.List;


public class Family {
    private String id;
    private String apikey;
    private String name;
    private Integer index;
    private List<Room> roomList;
    private Integer familyType;
    private List<Object> members;
    public String getId() {
        return id;
    }
    public String getApikey() {
        return apikey;
    }
    public String getName() {
        return name;
    }
    public Integer getIndex() {
        return index;
    }
    public List<Room> getRoomList() {
        return roomList;
    }
    public Integer getFamilyType() {
        return familyType;
    }
    public List<Object> getMembers() {
        return members;
    }
}

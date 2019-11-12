package com.example.locationalarm.alarm;

public class Alarm {
    private String name;
    private String address;
    private Boolean isActive;

    public Alarm(String _name, String _address, Boolean _isActive) {
        //We will need this when restoring alarms from DB
        name = _name;
        address = _address;
        isActive = _isActive;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress() {
        return this.address;
    }

    public void setIsActive(Boolean _isActive) {
        this.isActive = _isActive;
    }
    public Boolean getIsActive() {
        return this.isActive;
    }

    public boolean equals(Alarm alarm) {
        return name.equals(alarm.name) &&
                address.equals(alarm.address) &&
                isActive == alarm.isActive;
    }
}
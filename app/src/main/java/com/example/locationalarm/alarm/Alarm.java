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

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setActive(Boolean _isActive) {
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
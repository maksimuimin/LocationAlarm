package com.example.locationalarm.alarm;

public class Alarm {
    private String name;
    private String address;
    private Boolean isActive;

    public Alarm(String _name, String _address, Boolean _isActive) {
        //We will need this when restoring alarms from DB
        this.setName(_name);
        this.setAddress(_address);
        this.setActive(_isActive);
    }

    public Alarm(String _name, String _address) {
        this.setName(_name);
        this.setAddress(_address);
        this.setActive(true);
    }


    public void setName(String _name) {
        this.name = _name;
    }
    public String getName() {
        return this.name;
    }

    public void setAddress(String _address) {
        this.address = _address;
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
package com.example.locationalarm.alarm;

public class Alarm {
    private int id;
    private String name;
    private String address;
    private Boolean isActive;

    public Alarm(int _id, String _name, String _address, Boolean _isActive) {
        id = _id;
        name = _name;
        address = _address;
        isActive = _isActive;
    }

    public int getId() { return id; }

    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }

    public void setAddress(String address) { this.address = address; }
    public String getAddress() { return this.address; }

    public void setIsActive(boolean _isActive) { this.isActive = _isActive; }
    public boolean getIsActive() { return this.isActive; }

    public boolean equals(Alarm alarm) {
        return id == alarm.id &&
                name.equals(alarm.name) &&
                address.equals(alarm.address) &&
                isActive == alarm.isActive;
    }
}
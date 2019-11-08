package com.example.locationalarm;

import android.os.Parcel;
import android.os.Parcelable;

public class Alarm implements Parcelable {
    private String name;
    private String address;
    private Boolean isActive;

    Alarm(String _name, String _address, Boolean _isActive) {
        this.setName(_name);
        this.setAddress(_address);
        this.setActive(_isActive);
    }

    Alarm(String _name, String _address) {
        this.setName(_name);
        this.setAddress(_address);
        this.setActive(true);
    }

    /** recreate object from parcel */
    private Alarm(Parcel in) {
        name = in.readString();
        address = in.readString();
        isActive = in.readByte() != 0;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    String getName() {
        return this.name;
    }

    void setAddress(String _address) {
        this.address = _address;
    }

    String getAddress() {
        return this.address;
    }

    void setActive(Boolean _isActive) {
        this.isActive = _isActive;
    }

    Boolean getIsActive() {
        return this.isActive;
    }

    public static final Parcelable.Creator<Alarm> CREATOR
            = new Parcelable.Creator<Alarm>() {
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeByte((byte) (isActive ? 1 : 0));
    }
}
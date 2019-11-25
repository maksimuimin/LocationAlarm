package sleepless_nights.location_alarm.alarm;

public class Alarm {
    private int id;
    private String name = "";
    private String address = "";
    private boolean active;
    private double latitude;
    private double longitude;
    private float radius;


    public int getId() {
        return id;
    }
    public Alarm setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }
    public Alarm setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }
    public Alarm setAddress(String address) {
        this.address = address;
        return this;
    }

    public boolean isActive() {
        return active;
    }
    public Alarm setActive(boolean active) {
        this.active = active;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }
    public Alarm setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }
    public Alarm setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public float getRadius() {
        return radius;
    }
    public Alarm setRadius(float radius) {
        this.radius = radius;
        return this;
    }

    public boolean equals(Alarm alarm) {
        return id == alarm.id &&
                name.equals(alarm.name) &&
                address.equals(alarm.address) &&
                active == alarm.active &&
                latitude == alarm.latitude &&
                longitude == alarm.longitude &&
                radius == alarm.radius;
    }

}
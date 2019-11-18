package sleepless_nights.location_alarm.alarm;

public class Alarm {
    private int id;
    private String name;
    private String address;
    private Boolean isActive;
    private double latitude;
    private double longitude;
    private float radius;


    public Alarm(int id, String name, String address, Boolean isActive,
                 double latitude, double longitude, float radius) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.isActive = isActive;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public int getId() { return id; }

    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }

    public void setAddress(String address) { this.address = address; }
    public String getAddress() { return this.address; }

    public void setIsActive(boolean isActive) { this.isActive = isActive; }
    public boolean getIsActive() { return this.isActive; }

    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLatitude() { return latitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }
    public double getLongitude() { return longitude; }

    public void setRadius(float radius) { this.radius = radius; }
    public float getRadius() { return radius; }

    public boolean equals(Alarm alarm) {
        return id == alarm.id &&
                name.equals(alarm.name) &&
                address.equals(alarm.address) &&
                isActive == alarm.isActive &&
                latitude == alarm.latitude &&
                longitude == alarm.longitude &&
                radius == alarm.radius;
    }
}
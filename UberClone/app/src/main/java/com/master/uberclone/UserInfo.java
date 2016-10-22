package com.master.uberclone;

public class UserInfo {
    private String Id, Mobile;
    private double Latitude, Longitude;

    public UserInfo(String Id, String Mobile, double Latitude, double Longitude) {
        this.Id = Id;
        this.Mobile = Mobile;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
    }

    public String getId() {
        return Id;
    }

    public String getMobile() {
        return Mobile;
    }

    public double getLatitude() {
        return Latitude;
    }

    public double getLongitude() {
        return Longitude;
    }
}

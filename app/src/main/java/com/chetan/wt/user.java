package com.chetan.wt;

public class user {

    String id;
    String name;
    String email;
    String degree;
    String city;
    int wallet;
    String durl;
    Double latitude,longitude;
    public user() {
    }
    public user(String id, String name, String email, String degree, String city, String durl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.degree = degree;
        this.city = city;
        this.durl = durl;
    }
    public user(String id, String name, String email, String degree, String city) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.degree = degree;
        this.city = city;
        this.durl="https://i.imgur.com/tGbaZCY.jpg";
    }
    public user(String id, String name, String email, String degree, String city, String durl,Double latitude,Double longitude) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.degree = degree;
        this.city = city;
        this.durl = durl;
        this.latitude=latitude;
        this.longitude=longitude;
    }
    public user(String id, String name, String email, String degree, String city,Double latitude,Double longitude) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.degree = degree;
        this.city = city;
        this.durl = durl;
        this.latitude=latitude;
        this.longitude=longitude;
    }
    public int getWallet() {
        return wallet;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
    }

    public String getDurl() {
        return durl;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDegree() {
        return degree;
    }

    public String getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}

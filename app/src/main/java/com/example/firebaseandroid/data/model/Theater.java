package com.example.firebaseandroid.data.model;

public class Theater {
    private String theaterId;
    private String name;
    private String address;
    private String city;
    private String phoneNumber;
    private int totalSeats;
    private String imageUrl;

    public Theater() {}

    public Theater(String theaterId, String name, String address, String city) {
        this.theaterId = theaterId;
        this.name = name;
        this.address = address;
        this.city = city;
        this.totalSeats = 100;
    }

    public String getTheaterId() { return theaterId; }
    public void setTheaterId(String theaterId) { this.theaterId = theaterId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
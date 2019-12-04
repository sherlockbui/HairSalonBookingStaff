package com.example.hairsalonbookingstaff.Model;

public class BarberServices {
    private String id_service;
    private String name;
    private long price;

    public BarberServices(String id_service, String name, long price) {
        this.id_service = id_service;
        this.name = name;
        this.price = price;
    }

    public String getId_service() {
        return id_service;
    }

    public void setId_service(String id_service) {
        this.id_service = id_service;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}

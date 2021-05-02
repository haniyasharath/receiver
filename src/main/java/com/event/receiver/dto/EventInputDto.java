package com.event.receiver.dto;

public class EventInputDto {
    private boolean fuellid;
    private String city;

    public EventInputDto() {
    }

    public EventInputDto(boolean fuellid, String city) {
        this.fuellid = fuellid;
        this.city = city;
    }

    public boolean isFuellid() {
        return fuellid;
    }

    public void setFuellid(boolean fuellid) {
        this.fuellid = fuellid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "EventInputDto{" +
                "fuellid=" + fuellid +
                ", city='" + city + '\'' +
                '}';
    }
}

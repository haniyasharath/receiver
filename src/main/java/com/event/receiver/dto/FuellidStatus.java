package com.event.receiver.dto;

import java.io.Serializable;
import java.time.LocalTime;

public class FuellidStatus implements Serializable {
    private boolean isOpen;
    private LocalTime localTime;

    public FuellidStatus() { }

    public FuellidStatus(boolean isOpen, LocalTime localTime) {
        this.isOpen = isOpen;
        this.localTime = localTime;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }
}

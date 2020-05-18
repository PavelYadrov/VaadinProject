package com.netcracker.dto;

public enum Status {
    ACTIVE("active"),FROZEN("frozen"),BANNED("banned");
    String stat;
    private Status(String stat){
        this.stat=stat;
    }
}

package com.mycompany.decision;

public class NetworkEntry {
    private String ip;
    private String mac;
    private String name;

    public NetworkEntry(String ip, String mac, String name) {
        this.ip = ip;
        this.mac = mac;
        this.name = name;
    }

    // Getters y Setters
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}

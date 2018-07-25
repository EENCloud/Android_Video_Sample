package com.example.eagleeyenetworks.eagleeye_mediaplayer.models;

public class EENListDevice {
    protected String type;
    protected String esn;
    protected String name;

    public EENListDevice(String esn, String name, String type) {
        this.esn = esn;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getESN() {
        return esn;
    }

    public boolean isCamera() {
        return type.equals("camera");
    }

    @Override
    public String toString() {
        return String.format("%s (%s)".format(name, esn));
    }
}

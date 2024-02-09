package com.amalitech.extensions;

public class GlobalAPIConnection {
    private static final APIConnection instance = new APIConnection();
    
    public static APIConnection getInstance() {
        return instance;
    }
}

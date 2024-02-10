package com.amalitech.extensions;


/**
 * Provides a global singleton instance of the APIConnection class.
 */
public class GlobalAPIConnection {
    
    /**
     * The singleton instance of APIConnection.
     */
    private static volatile APIConnection instance = null;
    
    /**
     * Private constructor to prevent direct instantiation.
     */
    private GlobalAPIConnection() {}
    
    /**
     * Retrieves the singleton instance of APIConnection.
     *
     * @return the APIConnection instance.
     */
    public static APIConnection getInstance() {
        // Double-checked locking for thread-safe lazy initialization
        if (instance == null) {
            synchronized (GlobalAPIConnection.class) {
                if (instance == null) {
                    instance = new APIConnection();
                }
            }
        }
        return instance;
    }
    
}

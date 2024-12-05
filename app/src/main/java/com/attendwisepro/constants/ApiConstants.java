package com.attendwisepro.constants;

public class ApiConstants {
    // Use your local network IP address here (e.g., "192.168.1.100")
    // For development, you can also use Android's special hostname for localhost
    public static final String BASE_URL = "http://10.0.2.2:3578/";  // Default for Android emulator
    public static final String WIFI_BASE_URL = "http://192.168.204.105:3578/";  // Updated to your actual IP
    public static final String API_VERSION = "api/";  // Removed v1 since our API doesn't use versioning
    
    // Use this to switch between emulator and WiFi URLs
    public static final boolean USE_WIFI = true;  // Set to true to use WiFi IP
    
    public static final String FULL_API_URL = USE_WIFI ? WIFI_BASE_URL + API_VERSION : BASE_URL + API_VERSION;
}

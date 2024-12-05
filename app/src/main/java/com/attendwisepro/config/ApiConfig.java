package com.attendwisepro.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import java.net.InetAddress;

public class ApiConfig {
    public static final String DEFAULT_HOST = "192.168.52.105";
    public static final int DEFAULT_PORT = 3578;
    private static final String TAG = "ApiConfig";
    private static final String PREF_NAME = "ApiConfig";
    private static final String PREF_CUSTOM_HOST = "SERVER_IP";

    public static String getBaseUrl(Context context) {
        String host = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(PREF_CUSTOM_HOST, DEFAULT_HOST);
        Log.d(TAG, "Using host: " + host);
        String baseUrl = String.format("http://%s:%d/api/", host, DEFAULT_PORT);
        Log.d(TAG, "Using base URL: " + baseUrl);
        return baseUrl;
    }

    public static void validateServerConfig(Context context) {
        String host = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(PREF_CUSTOM_HOST, DEFAULT_HOST);
        
        try {
            InetAddress address = InetAddress.getByName(host);
            Log.d(TAG, "Server IP validation - Attempting to reach: " + host);
            boolean reachable = address.isReachable(5000);
            Log.d(TAG, "Server IP validation - " + host + " is " + (reachable ? "reachable" : "not reachable"));
            
            if (!reachable) {
                Log.w(TAG, "Server IP validation - Unable to reach server at " + host);
                tryAlternateServer(context, host);
            }
        } catch (Exception e) {
            Log.e(TAG, "Server IP validation - Error checking server: " + e.getMessage());
            tryAlternateServer(context, host);
        }
    }

    private static void tryAlternateServer(Context context, String currentHost) {
        String[] knownServers = {
            "192.168.52.105",
            "192.168.155.105",
            "192.168.1.105"
        };

        for (String serverIp : knownServers) {
            if (serverIp.equals(currentHost)) continue; // Skip current host

            try {
                InetAddress address = InetAddress.getByName(serverIp);
                Log.d(TAG, "Trying alternate server: " + serverIp);
                if (address.isReachable(3000)) {
                    Log.d(TAG, "Found reachable server at: " + serverIp);
                    setCustomHost(context, serverIp);
                    //ApiClient.resetClient(); // This line is commented out because ApiClient is not defined in the provided code
                    return;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error checking alternate server " + serverIp + ": " + e.getMessage());
            }
        }
    }
    
    public static void setCustomHost(Context context, String host) {
        Log.d(TAG, "Setting custom host: " + host);
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_CUSTOM_HOST, host).apply();
    }
    
    public static void clearCustomHost(Context context) {
        Log.d(TAG, "Clearing custom host");
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(PREF_CUSTOM_HOST).apply();
    }
    
    private static boolean isEmulator(Context context) {
        return Build.PRODUCT.contains("sdk") || 
               Build.MODEL.contains("Emulator") || 
               Build.MODEL.toLowerCase().contains("android sdk built for x86") ||
               Settings.Secure.getString(context.getContentResolver(), 
                   Settings.Secure.ANDROID_ID).equals("89ABCDEF-0123-4456-7890-ABCDEFABCDEF");
    }
}

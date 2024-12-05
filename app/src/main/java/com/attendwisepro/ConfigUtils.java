package com.attendwisepro;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;
import com.attendwisepro.network.ApiClient;
import com.attendwisepro.config.ApiConfig;

public class ConfigUtils {
    private static final String TAG = "ConfigUtils";
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_SERVER_IP = "serverIp";

    public static void init(Context context) {
        // No need to call updateNetworkSecurityConfig here
    }

    public static void setServerIp(Context context, String serverAddress) {
        try {
            // Save to encrypted preferences
            SharedPreferences prefs = getEncryptedSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_SERVER_IP, serverAddress);
            editor.apply();
    
            // Update API configuration
            ApiConfig.setCustomHost(context, serverAddress);
    
            // Reset the API client to use the new address
            ApiClient.resetClient();
    
            // Log the new server address
            Log.d(TAG, "New server address set: " + serverAddress);
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Error setting server IP", e);
        }
    }

    public static String getServerIp(Context context) {
        try {
            SharedPreferences prefs = getEncryptedSharedPreferences(context);
            String serverIp = prefs.getString(KEY_SERVER_IP, "");

            if (serverIp.isEmpty()) {
                // If no custom IP is set, get the default from ApiConfig
                serverIp = ApiConfig.DEFAULT_HOST;
                // Save it to encrypted preferences
                setServerIp(context, serverIp);
            }

            Log.d(TAG, "Retrieved server address: " + serverIp);
            return serverIp;
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Error getting server IP", e);
            return ApiConfig.DEFAULT_HOST; // Fallback to default
        }
    }

    private static SharedPreferences getEncryptedSharedPreferences(Context context) 
            throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        
        return EncryptedSharedPreferences.create(
            PREFS_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }
}

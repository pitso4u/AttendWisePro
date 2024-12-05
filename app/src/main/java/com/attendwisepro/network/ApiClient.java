package com.attendwisepro.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.attendwisepro.config.ApiConfig;
import com.attendwisepro.utils.UserSession;

import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String PREF_NAME = "ApiClientPrefs";
    private static final String PREF_SERVER_IP = "server_ip";
    private static final String DEFAULT_SERVER_IP = "192.168.52.105";
    private static final int PORT = 3578;
    
    private static Retrofit retrofit = null;
    private static OkHttpClient client;
    private static ApiService apiService;

    public static String getServerIp(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREF_SERVER_IP, DEFAULT_SERVER_IP);
    }

    public static void setServerIp(Context context, String ip) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_SERVER_IP, ip).apply();
        // Reset retrofit instance to use new IP
        resetClient();
        init(context);
    }

    public static void resetServerIp(Context context) {
        setServerIp(context, DEFAULT_SERVER_IP);
    }

    public static void init(Context context) {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        String serverIp = getServerIp(context);
        String baseUrl = String.format("http://%s:%d/api/", serverIp, PORT);
        Log.d(TAG, "Initializing API client with base URL: " + baseUrl);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static ApiService getApiService(Context context) {
        if (apiService == null) {
            init(context);
        }
        return apiService;
    }

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            Log.d(TAG, "getClient: Creating new Retrofit instance");
            retrofit = buildRetrofit(context);
        }
        return retrofit;
    }

    private static Retrofit buildRetrofit(Context context) {
        OkHttpClient client = createOkHttpClient(context);

        return new Retrofit.Builder()
                .baseUrl(ApiConfig.getBaseUrl(context))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static OkHttpClient createOkHttpClient(Context context) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> 
            Log.d(TAG, "HTTP: " + message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(chain -> {
                // Log network state before each request
                logNetworkState(context);
                
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();
                
                // Add auth token if available
                UserSession userSession = new UserSession(context);
                if (userSession.isLoggedIn()) {
                    requestBuilder.addHeader("Authorization", userSession.getToken());
                }
                
                Request request = requestBuilder.build();
                
                try {
                    return chain.proceed(request);
                } catch (Exception e) {
                    Log.e(TAG, "Network error during request to " + request.url(), e);
                    if (e instanceof SocketTimeoutException) {
                        // Try to ping the server
                        boolean canPing = pingServer(context, getServerIp(context));
                        Log.d(TAG, "Server ping result: " + (canPing ? "success" : "failed"));
                    }
                    throw e;
                }
            });

        return builder.build();
    }

    private static boolean pingServer(Context context, String serverIp) {
        try {
            InetAddress address = InetAddress.getByName(serverIp);
            Log.d(TAG, "Attempting to ping " + serverIp);
            boolean reachable = address.isReachable(5000);
            Log.d(TAG, "Server " + serverIp + " is " + (reachable ? "reachable" : "not reachable"));
            return reachable;
        } catch (Exception e) {
            Log.e(TAG, "Error pinging server: " + e.getMessage());
            return false;
        }
    }

    private static void logNetworkState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = cm.getActiveNetwork();
        if (activeNetwork != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);
            if (capabilities != null) {
                Log.d(TAG, String.format("Network State: Network capabilities: Has WiFi: %b, Has Cellular: %b, Has Ethernet: %b, Has Internet: %b, Validated: %b",
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI),
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR),
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET),
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET),
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)));
            }
        }

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            Log.d(TAG, "WiFi Enabled: " + wifiManager.isWifiEnabled());
            Log.d(TAG, "WiFi State: " + wifiManager.getWifiState());
            
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                Log.d(TAG, "WiFi SSID: " + wifiInfo.getSSID());
                Log.d(TAG, "WiFi Strength: " + wifiInfo.getRssi());
                Log.d(TAG, "WiFi Speed: " + wifiInfo.getLinkSpeed());
            }
        }
    }

    public static void resetClient() {
        Log.d(TAG, "resetClient: Resetting Retrofit instance");
        retrofit = null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            Log.e(TAG, "isNetworkAvailable: ConnectivityManager is null");
            return false;
        }

        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            Log.e(TAG, "isNetworkAvailable: No active network");
            return false;
        }

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        if (capabilities == null) {
            Log.e(TAG, "isNetworkAvailable: No network capabilities");
            return false;
        }

        boolean hasWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        boolean hasCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        boolean hasEthernet = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
        boolean hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        boolean isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);

        // Log network state
        Log.d(TAG, "Network State: Network capabilities: " +
                "Has WiFi: " + hasWifi +
                ", Has Cellular: " + hasCellular +
                ", Has Ethernet: " + hasEthernet +
                ", Has Internet: " + hasInternet +
                ", Validated: " + isValidated);

        // Log WiFi specific information if available
        if (hasWifi) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                Log.d(TAG, "WiFi Enabled: " + wifiManager.isWifiEnabled());
                Log.d(TAG, "WiFi State: " + wifiManager.getWifiState());
                Log.d(TAG, "WiFi SSID: " + wifiManager.getConnectionInfo().getSSID());
                Log.d(TAG, "WiFi Strength: " + wifiManager.getConnectionInfo().getRssi());
                Log.d(TAG, "WiFi Speed: " + wifiManager.getConnectionInfo().getLinkSpeed());
            }
        }

        // Log active network capabilities
        Log.d(TAG, "Active Network Capabilities: " +
                "Has WiFi: " + hasWifi +
                ", Has Cellular: " + hasCellular +
                ", Has Ethernet: " + hasEthernet +
                ", Has Internet: " + hasInternet +
                ", Validated: " + isValidated);

        return hasInternet && isValidated;
    }
}

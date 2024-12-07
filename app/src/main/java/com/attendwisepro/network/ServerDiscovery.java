package com.attendwisepro.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.attendwisepro.config.ApiConfig;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerDiscovery {
    private static final String TAG = "ServerDiscovery";
    private static final int DISCOVERY_PORT = 3579; // Port for UDP broadcast
    private static final int SOCKET_TIMEOUT = 5000; // 5 seconds timeout
    private static final String DISCOVERY_MESSAGE = "ATTENDWISE_SERVER_DISCOVERY";
    private static final int BUFFER_SIZE = 1024;
    private static final String[] KNOWN_SUBNETS = {
        "192.168.52",
        "192.168.155",
        "192.168.1"
    };

    private final Context context;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private DatagramSocket socket;
    private boolean isListening;
    private OnServerFoundListener listener;

    public interface OnServerFoundListener {
        void onServerFound(String serverIp);
        void onDiscoveryFailed(String error);
    }

    public ServerDiscovery(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void startDiscovery(OnServerFoundListener listener) {
        this.listener = listener;
        isListening = true;

        executorService.execute(() -> {
            WifiManager.MulticastLock multicastLock = null;
            try {
                // Try known IPs first
                String foundServer = tryKnownServers();
                if (foundServer != null) {
                    notifyServerFound(foundServer);
                    return;
                }

                // If known IPs fail, try UDP broadcast
                WifiManager wifi = (WifiManager) context.getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE);
                multicastLock = wifi.createMulticastLock("serverDiscoveryLock");
                multicastLock.acquire();

                socket = new DatagramSocket(DISCOVERY_PORT);
                socket.setBroadcast(true);
                socket.setSoTimeout(SOCKET_TIMEOUT);

                // Send discovery packets to known subnets
                for (String subnet : KNOWN_SUBNETS) {
                    sendDiscoveryPacket(subnet + ".255");
                }

                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                Log.d(TAG, "Starting server discovery...");

                long startTime = System.currentTimeMillis();
                while (isListening && (System.currentTimeMillis() - startTime < SOCKET_TIMEOUT)) {
                    try {
                        socket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());
                        String serverIp = packet.getAddress().getHostAddress();

                        Log.d(TAG, "Received broadcast message: " + message + " from " + serverIp);

                        if (message.contains("ATTENDWISE_SERVER")) {
                            notifyServerFound(serverIp);
                            break;
                        }
                    } catch (SocketTimeoutException e) {
                        // Continue trying other subnets
                        continue;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Discovery error: " + e.getMessage());
                notifyError("Server discovery failed: " + e.getMessage());
            } finally {
                if (multicastLock != null && multicastLock.isHeld()) {
                    multicastLock.release();
                }
                stopDiscovery();
            }
        });
    }

    public void stopDiscovery() {
        isListening = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    private String tryKnownServers() {
        // First try the default configured server
        String defaultServer = ApiConfig.DEFAULT_HOST;
        try {
            InetAddress address = InetAddress.getByName(defaultServer);
            Log.d(TAG, "Trying configured server: " + defaultServer);
            if (address.isReachable(3000)) {
                Log.d(TAG, "Found reachable server at: " + defaultServer);
                return defaultServer;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking configured server " + defaultServer + ": " + e.getMessage());
        }

        // Check network type
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = cm.getActiveNetwork();
        if (activeNetwork != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);
            if (capabilities != null && !capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.d(TAG, "Device is not on WiFi, skipping local network discovery");
                return null;
            }
        }

        String[] knownServers = {
            "192.168.83.105",
            "192.168.52.105",
            "192.168.155.105",
            "192.168.1.105"
        };

        for (String serverIp : knownServers) {
            try {
                InetAddress address = InetAddress.getByName(serverIp);
                Log.d(TAG, "Trying known server: " + serverIp);
                if (address.isReachable(3000)) {
                    Log.d(TAG, "Found reachable server at: " + serverIp);
                    return serverIp;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error checking known server " + serverIp + ": " + e.getMessage());
            }
        }
        return null;
    }

    private void sendDiscoveryPacket(String broadcastAddress) {
        try {
            InetAddress address = InetAddress.getByName(broadcastAddress);
            byte[] buffer = DISCOVERY_MESSAGE.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, DISCOVERY_PORT);
            socket.send(packet);
            Log.d(TAG, "Sent discovery packet to " + broadcastAddress);
        } catch (Exception e) {
            Log.e(TAG, "Error sending discovery packet to " + broadcastAddress + ": " + e.getMessage());
        }
    }

    private void notifyServerFound(String serverIp) {
        mainHandler.post(() -> {
            if (listener != null) {
                listener.onServerFound(serverIp);
                // Update the API configuration
                ApiConfig.setCustomHost(context, serverIp);
                // Reset the API client to use the new server
                ApiClient.resetClient();
            }
        });
    }

    private void notifyError(String error) {
        mainHandler.post(() -> {
            if (listener != null) {
                listener.onDiscoveryFailed(error);
            }
        });
    }
}

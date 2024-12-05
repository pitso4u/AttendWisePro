package com.attendwisepro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ServerConfigActivity extends AppCompatActivity {

    private static final String TAG = "ServerConfigActivity";
    private EditText editTextServerIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_config);

        ConfigUtils.init(this); // Initialize with current IP

        editTextServerIp = findViewById(R.id.editTextServerIp);
        Button buttonSaveIp = findViewById(R.id.buttonSaveIp);

        String currentServerIp = ConfigUtils.getServerIp(this);
        Log.d(TAG, "Current Server IP: " + currentServerIp);
        editTextServerIp.setText(currentServerIp);

        buttonSaveIp.setOnClickListener(v -> {
            String newServerIp = editTextServerIp.getText().toString();
            if (!newServerIp.isEmpty() && isValidIp(newServerIp)) {
                ConfigUtils.setServerIp(this, newServerIp);
                Toast.makeText(ServerConfigActivity.this, "Server IP updated successfully", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "New Server IP set: " + newServerIp);

                // Navigate back to LoginActivity
                Intent intent = new Intent(ServerConfigActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ServerConfigActivity.this, "Please enter a valid IP address", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidIp(String ip) {
        String regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(regex);
    }
}

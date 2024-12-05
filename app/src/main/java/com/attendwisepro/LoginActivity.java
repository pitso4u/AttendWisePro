package com.attendwisepro;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.attendwisepro.network.ApiClient;
import com.attendwisepro.models.LoginRequest;
import com.attendwisepro.models.LoginResponse;
import com.attendwisepro.network.ApiService;
import com.attendwisepro.network.ServerDiscovery;
import com.attendwisepro.utils.UserSession;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    
    private EditText emailInput;
    private EditText passwordInput;
    private EditText serverIpInput;
    private Button loginButton;
    private Button discoverServerButton;
    private ProgressBar progressBar;
    private UserSession userSession;
    private ServerDiscovery serverDiscovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        serverIpInput = findViewById(R.id.serverIpInput);
        loginButton = findViewById(R.id.loginButton);
        discoverServerButton = findViewById(R.id.discoverServerButton);
        progressBar = findViewById(R.id.progressBar);

        userSession = new UserSession(this);
        serverDiscovery = new ServerDiscovery(this);
        
        // Set current server IP
        serverIpInput.setText(ApiClient.getServerIp(this));

        loginButton.setOnClickListener(v -> attemptLogin());
        discoverServerButton.setOnClickListener(v -> startServerDiscovery());

        if (userSession.isLoggedIn()) {
            navigateToDashboard();
            return;
        }
    }

    private void attemptLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String serverIp = serverIpInput.getText().toString().trim();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty() || serverIp.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update server IP
        ApiClient.setServerIp(this, serverIp);

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        // Create login request
        LoginRequest loginRequest = new LoginRequest(email, password);
        Log.d(TAG, "handleLogin: Sending login request for email: " + email);
        
        ApiClient.getClient(this).create(ApiService.class).login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: Login successful");
                    handleSuccessfulLogin(response.body());
                } else {
                    Log.e(TAG, "onResponse: Login failed with code: " + response.code());
                    handleLoginError(response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);
                Log.e(TAG, "onFailure: Login request failed", t);
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startServerDiscovery() {
        // Disable inputs during discovery
        setInputsEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        
        serverDiscovery.startDiscovery(new ServerDiscovery.OnServerFoundListener() {
            @Override
            public void onServerFound(String serverIp) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    setInputsEnabled(true);
                    serverIpInput.setText(serverIp);
                    Toast.makeText(LoginActivity.this, 
                        "Server found at: " + serverIp, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onDiscoveryFailed(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    setInputsEnabled(true);
                    Toast.makeText(LoginActivity.this, 
                        "Server discovery failed: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setInputsEnabled(boolean enabled) {
        emailInput.setEnabled(enabled);
        passwordInput.setEnabled(enabled);
        serverIpInput.setEnabled(enabled);
        loginButton.setEnabled(enabled);
        discoverServerButton.setEnabled(enabled);
    }

    private void handleSuccessfulLogin(LoginResponse loginResponse) {
        Log.d(TAG, "handleSuccessfulLogin: Saving user session");
        LoginResponse.UserData userData = loginResponse.getUser();
        userSession.createLoginSession(
            userData.getId(),
            userData.getUsername(),
            userData.getRole(),
            userData.getUsername(), // Using username as email until backend provides email
            loginResponse.getToken()
        );
        
        navigateToDashboard();
    }

    private void handleLoginError(Response<LoginResponse> response) {
        if (response.code() == 401) {
            Toast.makeText(this, R.string.error_invalid_credentials, Toast.LENGTH_LONG).show();
        } else if (response.code() == 403) {
            Toast.makeText(this, R.string.error_account_locked, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.error_login_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serverDiscovery != null) {
            serverDiscovery.stopDiscovery();
        }
    }
}

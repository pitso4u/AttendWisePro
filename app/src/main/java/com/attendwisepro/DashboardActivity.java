package com.attendwisepro;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.attendwisepro.adapters.RecentActivityAdapter;
import com.attendwisepro.models.CountResponse;
import com.attendwisepro.models.DashboardData;
import com.attendwisepro.models.RecentActivity;
import com.attendwisepro.network.ApiClient;
import com.attendwisepro.network.ApiService;
import com.attendwisepro.network.ServerDiscovery;
import com.attendwisepro.utils.UserSession;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity 
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "DashboardActivity";
    private static final int PERMISSION_REQUEST_CODE = 123;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView learnersPresentCount;
    private TextView learnersAbsentCount;
    private TextView employeesPresentCount;
    private TextView employeesAbsentCount;
    private MaterialCardView viewLearnerCard;
    private MaterialCardView viewEmployeeCard;
    private MaterialCardView scanQrCard;
    private MaterialCardView viewReportsCard;
    private MaterialCardView viewVisitorsCard;
    private MaterialCardView viewIncidentsCard;
    private UserSession userSession;
    private ApiService apiService;
    private ServerDiscovery serverDiscovery;
    private ProgressBar discoveryProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting DashboardActivity");
        setContentView(R.layout.activity_dashboard);

        userSession = new UserSession(this);
        
        // Check and request permissions before initializing ApiClient
        if (checkAndRequestPermissions()) {
            Log.d(TAG, "onCreate: Permissions already granted, initializing ApiClient");
            initializeApiClient();
        } else {
            Log.d(TAG, "onCreate: Requesting permissions");
        }
        
        setupToolbar();
        setupNavigationDrawer();
        setupServerDiscovery();
        setupQuickActions();
    }

    private boolean checkAndRequestPermissions() {
        Log.d(TAG, "checkAndRequestPermissions: Checking permissions");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            
            if (checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "checkAndRequestPermissions: WIFI_STATE permission not granted");
                permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "checkAndRequestPermissions: NETWORK_STATE permission not granted");
                permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
            }
            
            if (!permissions.isEmpty()) {
                Log.d(TAG, "checkAndRequestPermissions: Requesting permissions: " + permissions);
                requestPermissions(permissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        Log.d(TAG, "checkAndRequestPermissions: All permissions granted");
        return true;
    }

    private void initializeApiClient() {
        Log.d(TAG, "initializeApiClient: Initializing API client");
        apiService = ApiClient.getClient(this).create(ApiService.class);
        loadDashboardData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar),
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupServerDiscovery() {
        serverDiscovery = new ServerDiscovery(this);
    }

    private void setupQuickActions() {
        viewLearnerCard = findViewById(R.id.viewLearnerCard);
        viewEmployeeCard = findViewById(R.id.viewEmployeeCard);
        scanQrCard = findViewById(R.id.scanQrCard);
        viewReportsCard = findViewById(R.id.viewReportsCard);
        viewVisitorsCard = findViewById(R.id.viewVisitorsCard);
        viewIncidentsCard = findViewById(R.id.viewIncidentsCard);

        viewLearnerCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, LearnersActivity.class);
            startActivity(intent);
        });

        viewEmployeeCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmployeesActivity.class);
            startActivity(intent);
        });

        scanQrCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, QRScannerActivity.class);
            startActivity(intent);
        });

        viewReportsCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReportsActivity.class);
            startActivity(intent);
        });

        viewVisitorsCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, VisitorsActivity.class);
            startActivity(intent);
        });

        viewIncidentsCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, IncidentsActivity.class);
            startActivity(intent);
        });
    }

    private void loadDashboardData() {
        // Show loading state
        showLoading(true);

        // Check network connectivity first
        if (!ApiClient.isNetworkAvailable(this)) {
            showLoading(false);
            Toast.makeText(this, "No internet connection. Please check your network settings.", Toast.LENGTH_LONG).show();
            return;
        }

        // Try to discover server if connection fails
        fetchDataWithServerDiscovery();
    }

    private void fetchDataWithServerDiscovery() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        String token = userSession.getToken();

        // Get learner count
        apiService.getLearnerCount(token).enqueue(new Callback<CountResponse>() {
            @Override
            public void onResponse(Call<CountResponse> call, Response<CountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int totalLearners = response.body().getCount();
                    getEmployeeCount(apiService, token, totalLearners);
                } else {
                    showLoading(false);
                    handleApiError("Error fetching learner count", response);
                }
            }

            @Override
            public void onFailure(Call<CountResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    // Start server discovery
                    Log.d(TAG, "Connection failed, starting server discovery...");
                    ServerDiscovery discovery = new ServerDiscovery(DashboardActivity.this);
                    discovery.startDiscovery(new ServerDiscovery.OnServerFoundListener() {
                        @Override
                        public void onServerFound(String serverIp) {
                            Log.d(TAG, "New server found at: " + serverIp);
                            Toast.makeText(DashboardActivity.this, 
                                "Found server at: " + serverIp, Toast.LENGTH_SHORT).show();
                            // Retry loading data with new server
                            new Handler().postDelayed(() -> loadDashboardData(), 1000);
                        }

                        @Override
                        public void onDiscoveryFailed(String error) {
                            showLoading(false);
                            Log.e(TAG, "Server discovery failed: " + error);
                            Toast.makeText(DashboardActivity.this,
                                "Could not find server. Please check server status.", 
                                Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    showLoading(false);
                    handleApiError("Failed to fetch learner count", t);
                }
            }
        });
    }

    private void getEmployeeCount(ApiService apiService, String token, int totalLearners) {
        apiService.getEmployeeCount(token).enqueue(new Callback<CountResponse>() {
            @Override
            public void onResponse(Call<CountResponse> call, Response<CountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int totalEmployees = response.body().getCount();
                    getAttendanceData(apiService, token, totalLearners, totalEmployees);
                } else {
                    showLoading(false);
                    handleApiError("Error fetching employee count", response);
                }
            }

            @Override
            public void onFailure(Call<CountResponse> call, Throwable t) {
                showLoading(false);
                if (t instanceof SocketTimeoutException) {
                    String errorMessage = "Could not connect to server at " + ApiClient.getServerIp(DashboardActivity.this);
                    Toast.makeText(DashboardActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                } else {
                    handleApiError("Failed to fetch employee count", t);
                }
            }
        });
    }

    private void getAttendanceData(ApiService apiService, String token, int totalLearners, int totalEmployees) {
        apiService.getDashboardData(token).enqueue(new Callback<DashboardData>() {
            @Override
            public void onResponse(Call<DashboardData> call, Response<DashboardData> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    DashboardData data = response.body();
                    // Update with actual total counts from server
                    data.setTotalLearners(totalLearners);
                    data.setTotalEmployees(totalEmployees);
                    updateDashboardUI(data);
                } else {
                    handleApiError("Error fetching dashboard data", response);
                }
            }

            @Override
            public void onFailure(Call<DashboardData> call, Throwable t) {
                showLoading(false);
                if (t instanceof SocketTimeoutException) {
                    String errorMessage = "Could not connect to server at " + ApiClient.getServerIp(DashboardActivity.this);
                    Toast.makeText(DashboardActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                } else {
                    handleApiError("Failed to fetch dashboard data", t);
                }
            }
        });
    }

    private void showLoading(boolean show) {
        View dashboardContent = findViewById(R.id.dashboardContent);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        
        if (dashboardContent != null) {
            dashboardContent.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void handleApiError(String message, Response<?> response) {
        Log.e(TAG, message + ": " + response.code());
        String errorMessage = message;
        if (response.code() == 401) {
            errorMessage = "Session expired. Please login again.";
            userSession.clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void handleApiError(String message, Throwable t) {
        Log.e(TAG, message, t);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateDashboardUI(DashboardData data) {
        try {
            // Update learners section
            TextView learnersPresentCount = findViewById(R.id.learnersPresentCount);
            TextView learnersAbsentCount = findViewById(R.id.learnersAbsentCount);
            TextView learnersTotalCount = findViewById(R.id.learnersTotalCount);
            TextView learnerPercentText = findViewById(R.id.learnerPercentText);
            ProgressBar learnerProgress = findViewById(R.id.learnerProgress);

            int totalLearners = data.getTotalLearners();
            int absentLearners = data.getAbsentLearners();
            int presentLearners = totalLearners - absentLearners;

            learnersPresentCount.setText(String.valueOf(presentLearners));
            learnersAbsentCount.setText(String.valueOf(absentLearners));
            learnersTotalCount.setText(String.valueOf(totalLearners));

            int learnerAttendance = totalLearners > 0 ? (presentLearners * 100) / totalLearners : 0;
            learnerPercentText.setText(learnerAttendance + "%");
            learnerProgress.setProgress(learnerAttendance);

            // Update employees section
            int totalEmployees = data.getTotalEmployees();
            int absentEmployees = data.getAbsentEmployees();
            int presentEmployees = totalEmployees - absentEmployees;
            updateEmployeeAttendance(presentEmployees, totalEmployees);

            // Update recent activity if available
            List<RecentActivity> activities = data.getRecentActivities();
            if (activities != null && !activities.isEmpty()) {
                updateRecentActivity(activities);
            }

        } catch (Exception e) {
            Log.e(TAG, "updateDashboardUI: Error updating UI", e);
            Toast.makeText(this, "Error updating dashboard", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmployeeAttendance(int presentCount, int totalCount) {
        TextView employeesPresentCount = findViewById(R.id.employeesPresentCount);
        TextView employeesAbsentCount = findViewById(R.id.employeesAbsentCount);
        TextView employeesTotalCount = findViewById(R.id.employeesTotalCount);
        TextView employeePercentText = findViewById(R.id.employeePercentText);
        ProgressBar employeeProgress = findViewById(R.id.employeeProgress);

        int absentCount = totalCount - presentCount;
        employeesPresentCount.setText(String.valueOf(presentCount));
        employeesAbsentCount.setText(String.valueOf(absentCount));
        employeesTotalCount.setText(String.valueOf(totalCount));

        int attendancePercent = totalCount > 0 ? (presentCount * 100) / totalCount : 0;
        employeePercentText.setText(attendancePercent + "%");
        employeeProgress.setProgress(attendancePercent);
    }

    private void updateRecentActivity(List<RecentActivity> activities) {
        RecyclerView recyclerView = findViewById(R.id.recentActivityRecyclerView);
        if (recyclerView != null) {
            RecentActivityAdapter adapter = (RecentActivityAdapter) recyclerView.getAdapter();
            if (adapter != null) {
                adapter.updateActivities(activities);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Already on dashboard
        } else if (id == R.id.nav_learners) {
            startActivity(new Intent(this, LearnersActivity.class));
        } else if (id == R.id.nav_employees) {
            startActivity(new Intent(this, EmployeesActivity.class));
        } else if (id == R.id.nav_reports) {
            startActivity(new Intent(this, ReportsActivity.class));
        } else if (id == R.id.nav_server_config) {
            showServerConfigDialog();
        } else if (id == R.id.nav_logout) {
            userSession.clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showServerConfigDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_server_config, null);

        EditText serverIpInput = view.findViewById(R.id.serverIpInput);
        TextView currentIpText = view.findViewById(R.id.currentIpText);
        Button saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button autoDetectButton = view.findViewById(R.id.autoDetectButton);
        discoveryProgress = view.findViewById(R.id.discoveryProgress);

        // Show current IP
        String currentIp = ApiClient.getServerIp(this);
        currentIpText.setText("Current IP: " + currentIp);
        serverIpInput.setText(currentIp);

        AlertDialog dialog = builder.setView(view).create();

        saveButton.setOnClickListener(v -> {
            String newIp = serverIpInput.getText().toString().trim();
            if (!newIp.isEmpty()) {
                if (isValidIpAddress(newIp)) {
                    ApiClient.setServerIp(this, newIp);
                    Toast.makeText(this, "Server IP updated successfully", Toast.LENGTH_SHORT).show();
                    loadDashboardData();
                    dialog.dismiss();
                } else {
                    serverIpInput.setError("Invalid IP address format");
                }
            } else {
                serverIpInput.setError("IP address cannot be empty");
            }
        });

        autoDetectButton.setOnClickListener(v -> {
            discoveryProgress.setVisibility(View.VISIBLE);
            serverDiscovery.startDiscovery(new ServerDiscovery.OnServerFoundListener() {
                @Override
                public void onServerFound(String serverIp) {
                    discoveryProgress.setVisibility(View.GONE);
                    serverIpInput.setText(serverIp);
                    Toast.makeText(DashboardActivity.this, 
                        "Server found at: " + serverIp, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDiscoveryFailed(String error) {
                    discoveryProgress.setVisibility(View.GONE);
                    Toast.makeText(DashboardActivity.this, 
                        "Server discovery failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        cancelButton.setOnClickListener(v -> {
            serverDiscovery.stopDiscovery();
            dialog.dismiss();
        });

        dialog.setOnDismissListener(dialog1 -> serverDiscovery.stopDiscovery());

        dialog.show();
    }

    private boolean isValidIpAddress(String ip) {
        try {
            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }
            for (String part : parts) {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                initializeApiClient();
            } else {
                Toast.makeText(this, "Required permissions not granted", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}

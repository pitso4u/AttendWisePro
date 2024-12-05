package com.attendwisepro;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.attendwisepro.adapters.AttendanceRecordAdapter;
import com.attendwisepro.models.AttendanceRecord;
import com.attendwisepro.models.AttendanceResponse;
import com.attendwisepro.network.ApiClient;
import com.attendwisepro.network.ApiService;
import com.attendwisepro.utils.UserSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearnerAttendanceActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AttendanceRecordAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView emptyView;
    private String learnerId;
    private String learnerName;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learner_attendance);

        // Get learner details from intent
        learnerId = getIntent().getStringExtra("learner_id");
        learnerName = getIntent().getStringExtra("learner_name");

        // Initialize API service
        apiService = ApiClient.getClient(this).create(ApiService.class);

        setupToolbar();
        setupViews();
        loadAttendanceRecords();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.attendance_history));
            getSupportActionBar().setSubtitle(learnerName);
        }
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttendanceRecordAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadAttendanceRecords);
    }

    private void loadAttendanceRecords() {
        showLoading();

        // Get date range for the last 30 days
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        Date startDate = calendar.getTime();

        // Format dates for API
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDateStr = apiDateFormat.format(startDate);
        String endDateStr = apiDateFormat.format(endDate);

        // Make API call
        Call<AttendanceResponse> call = apiService.getStudentAttendance(
                "Bearer " + new UserSession(this).getToken(),
                learnerId,
                startDateStr,
                endDateStr);
        call.enqueue(new Callback<AttendanceResponse>() {
            @Override
            public void onResponse(Call<AttendanceResponse> call, Response<AttendanceResponse> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    AttendanceResponse attendanceResponse = response.body();
                    if (attendanceResponse.isSuccess()) {
                        List<AttendanceRecord> records = attendanceResponse.getRecords();
                        updateUI(records);
                    } else {
                        showError(attendanceResponse.getMessage());
                    }
                } else {
                    showError(getString(R.string.error_loading_attendance));
                }
            }

            @Override
            public void onFailure(Call<AttendanceResponse> call, Throwable t) {
                hideLoading();
                showError(getString(R.string.error_network));
            }
        });
    }

    private void updateUI(List<AttendanceRecord> records) {
        if (records.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter.updateRecords(records);
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

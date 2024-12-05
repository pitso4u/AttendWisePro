package com.attendwisepro;

import android.content.Intent;
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

import com.attendwisepro.adapters.IncidentAdapter;
import com.attendwisepro.models.Incident;
import com.attendwisepro.network.ApiClient;
import com.attendwisepro.network.ApiService;
import com.attendwisepro.utils.UserSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncidentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private IncidentAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView emptyView;
    private List<Incident> incidents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidents);

        setupToolbar();
        setupViews();
        loadIncidents();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.incidents);
        }
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        incidents = new ArrayList<>();
        adapter = new IncidentAdapter(this, incidents);
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadIncidents);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddIncidentActivity.class);
            startActivity(intent);
        });
    }

    private void loadIncidents() {
        showLoading();
        String token = "Bearer " + new UserSession(this).getToken();
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.getIncidents(token)
                .enqueue(new Callback<List<Incident>>() {
                    @Override
                    public void onResponse(Call<List<Incident>> call, Response<List<Incident>> response) {
                        hideLoading();
                        if (response.isSuccessful() && response.body() != null) {
                            updateUI(response.body());
                        } else {
                            Toast.makeText(IncidentsActivity.this,
                                    "Failed to load incidents", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Incident>> call, Throwable t) {
                        hideLoading();
                        Toast.makeText(IncidentsActivity.this,
                                "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private List<Incident> getDummyData() {
        List<Incident> dummyIncidents = new ArrayList<>();
        dummyIncidents.add(new Incident(
                "Network Outage",
                "Internet connectivity issues in Building A",
                new Date(),
                "Building A",
                "John Doe",
                "HIGH"
        ));
        dummyIncidents.add(new Incident(
                "AC Malfunction",
                "AC not working in Room 101",
                new Date(),
                "Room 101",
                "Jane Smith",
                "MEDIUM"
        ));
        return dummyIncidents;
    }

    private void updateUI(List<Incident> incidents) {
        if (incidents.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter.updateIncidents(incidents);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

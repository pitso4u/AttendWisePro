package com.attendwisepro;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.attendwisepro.adapters.VisitorAdapter;
import com.attendwisepro.models.Visitor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VisitorsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VisitorAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView emptyView;
    private List<Visitor> visitors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitors);

        setupToolbar();
        setupViews();
        loadVisitors();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.visitors);
        }
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        visitors = new ArrayList<>();
        adapter = new VisitorAdapter(this, visitors);
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadVisitors);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddVisitorActivity.class);
            startActivity(intent);
        });
    }

    private void loadVisitors() {
        showLoading();
        // TODO: Load visitors from API
        // For now, using dummy data
        List<Visitor> dummyData = getDummyData();
        updateUI(dummyData);
        hideLoading();
    }

    private List<Visitor> getDummyData() {
        List<Visitor> dummyVisitors = new ArrayList<>();
        dummyVisitors.add(new Visitor(
            "Michael Brown",
            "VIS001",
            "Meeting with IT Department",
            "John Doe",
            new Date(),
            null
        ));
        dummyVisitors.add(new Visitor(
            "Sarah Wilson",
            "VIS002",
            "Infrastructure Inspection",
            "Jane Smith",
            new Date(),
            new Date()
        ));
        return dummyVisitors;
    }

    private void updateUI(List<Visitor> visitors) {
        if (visitors.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter.updateVisitors(visitors);
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

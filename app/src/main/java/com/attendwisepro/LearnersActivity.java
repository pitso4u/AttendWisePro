package com.attendwisepro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.attendwisepro.adapters.LearnerAdapter;
import com.attendwisepro.models.Learner;
import com.attendwisepro.network.ApiClient;
import com.attendwisepro.network.ApiService;
import com.attendwisepro.utils.UserSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.recyclerview.widget.DiffUtil;

public class LearnersActivity extends AppCompatActivity implements LearnerAdapter.LearnerActionListener {
    private RecyclerView recyclerView;
    private LearnerAdapter adapter;
    private List<Learner> learnerList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private ApiService apiService;
    private UserSession userSession;
    private android.app.ProgressDialog progressDialog;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learners);

        mainHandler = new Handler(Looper.getMainLooper());
        
        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Learners");
        }

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        // Initialize API service and user session
        apiService = ApiClient.getClient(this).create(ApiService.class);
        userSession = new UserSession(this);

        // Setup RecyclerView with stable IDs
        learnerList = new ArrayList<>();
        adapter = new LearnerAdapter(this, learnerList, this);
        adapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Setup refresh listener
        swipeRefreshLayout.setOnRefreshListener(this::loadLearners);

        // Setup FAB click listener
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddLearnerActivity.class);
            startActivity(intent);
        });

        // Initialize progress dialog
        progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        // Initial data load
        loadLearners();
    }

    private void loadLearners() {
        swipeRefreshLayout.setRefreshing(true);
        
        if (!ApiClient.isNetworkAvailable(this)) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<List<Learner>> call = apiService.getLearners("Bearer " + userSession.getToken());
        call.enqueue(new Callback<List<Learner>>() {
            @Override
            public void onResponse(Call<List<Learner>> call, Response<List<Learner>> response) {
                if (isFinishing()) return;
                
                mainHandler.post(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (response.isSuccessful() && response.body() != null) {
                        List<Learner> newList = new ArrayList<>(response.body());
                        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new LearnerDiffCallback(learnerList, newList));
                        learnerList.clear();
                        learnerList.addAll(newList);
                        diffResult.dispatchUpdatesTo(adapter);
                    } else {
                        Toast.makeText(LearnersActivity.this, 
                            "Failed to load learners", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Learner>> call, Throwable t) {
                if (isFinishing()) return;
                
                mainHandler.post(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    String errorMessage = "Connection error. Please check your server connection.";
                    Toast.makeText(LearnersActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_learners, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();  // Using androidx SearchView
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLearners(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 3) {  // Only search when 3 or more characters are entered
                    searchLearners(newText);
                } else if (newText.isEmpty()) {
                    loadLearners();  // Load all learners when search is cleared
                }
                return true;
            }
        });
        
        return true;
    }

    private void searchLearners(String query) {
        if (!ApiClient.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<List<Learner>> call = apiService.searchLearners(
            "Bearer " + userSession.getToken(),
            query
        );
        
        call.enqueue(new Callback<List<Learner>>() {
            @Override
            public void onResponse(Call<List<Learner>> call, Response<List<Learner>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    learnerList.clear();
                    learnerList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(LearnersActivity.this, 
                        "Search failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Learner>> call, Throwable t) {
                Toast.makeText(LearnersActivity.this, 
                    "Connection error. Please check your server connection.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEditLearner(Learner learner) {
        Intent intent = new Intent(this, AddLearnerActivity.class);
        intent.putExtra("learner_id", learner.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteLearner(final Learner learner) {
        if (learner.getId() == null) {
            Toast.makeText(this, "Invalid learner ID", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
            .setTitle("Delete Learner")
            .setMessage("Are you sure you want to delete " + learner.getFullName() + "?")
            .setPositiveButton("Delete", (dialog, which) -> deleteLearner(learner.getId()))
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    public void onViewAttendance(Learner learner) {
        Intent intent = new Intent(this, LearnerAttendanceActivity.class);
        intent.putExtra("learner_id", learner.getId());
        intent.putExtra("learner_name", learner.getFullName());
        startActivity(intent);
    }

    private void deleteLearner(Integer learnerId) {
        if (!ApiClient.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);
        
        Call<Void> call = apiService.deleteLearner(
            "Bearer " + userSession.getToken(),
            String.valueOf(learnerId)  // Convert to string for API path parameter
        );
        
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                mainHandler.post(() -> {
                    showProgress(false);
                    if (response.isSuccessful()) {
                        Toast.makeText(LearnersActivity.this, 
                            "Learner deleted successfully", Toast.LENGTH_SHORT).show();
                        loadLearners();
                    } else {
                        String errorMsg = "Failed to delete learner";
                        if (response.code() == 404) {
                            errorMsg = "Learner not found. They may have been already deleted.";
                        } else if (response.code() == 403) {
                            errorMsg = "You don't have permission to delete learners.";
                        }
                        Toast.makeText(LearnersActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mainHandler.post(() -> {
                    showProgress(false);
                    String errorMsg = "Network error. Please check your connection.";
                    if (t.getMessage() != null && !t.getMessage().isEmpty()) {
                        errorMsg = "Error: " + t.getMessage();
                    }
                    Toast.makeText(LearnersActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showProgress(boolean show) {
        if (isFinishing()) return;
        
        if (show) {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        } else {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (searchView != null) {
            searchView.setOnQueryTextListener(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if (recyclerView != null) {
            recyclerView.setAdapter(null);
        }
        mainHandler.removeCallbacksAndMessages(null);
    }

    private static class LearnerDiffCallback extends DiffUtil.Callback {
        private final List<Learner> oldList;
        private final List<Learner> newList;

        LearnerDiffCallback(List<Learner> oldList, List<Learner> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Learner oldItem = oldList.get(oldItemPosition);
            Learner newItem = newList.get(newItemPosition);
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLearners(); // Refresh list when returning to this screen
    }
}

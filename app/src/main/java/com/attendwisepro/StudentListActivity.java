package com.attendwisepro;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.attendwisepro.adapters.StudentAdapter;
import com.attendwisepro.models.Learner;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class StudentListActivity extends AppCompatActivity implements StudentAdapter.OnStudentClickListener {
    private RecyclerView rvStudents;
    private StudentAdapter adapter;
    private TextInputEditText etSearch;
    private FloatingActionButton fabAddStudent;
    private CircularProgressIndicator progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupSearchListener();
        setupClickListeners();
        loadStudents();
    }

    private void initializeViews() {
        rvStudents = findViewById(R.id.rvStudents);
        etSearch = findViewById(R.id.etSearch);
        fabAddStudent = findViewById(R.id.fabAddStudent);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.students);
        }
    }

    private void setupRecyclerView() {
        adapter = new StudentAdapter(this);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(adapter);
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterStudents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClickListeners() {
        fabAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddLearnerActivity.class);
            startActivity(intent);
        });
    }

    private void loadStudents() {
        showLoading(true);
        // TODO: Load students from backend API
        // For now, using dummy data
        List<Learner> dummyData = new ArrayList<>();
        dummyData.add(new Learner(1, "John", "Doe", "john.doe@example.com", "1234567890", "Class 1-A", "Parent 1", "1234567890"));
        dummyData.add(new Learner(2, "Jane", "Smith", "jane.smith@example.com", "0987654321", "Class 1-A", "Parent 2", "0987654321"));
        adapter.setStudents(dummyData);
        showLoading(false);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvStudents.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onStudentClick(Learner student) {
        // TODO: Show student details
    }

    @Override
    public void onEditClick(Learner student) {
        Intent intent = new Intent(this, AddLearnerActivity.class);
        intent.putExtra("student_id", student.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Learner student) {
        new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_student)
            .setMessage(getString(R.string.delete_student_confirmation, student.getFullName()))
            .setPositiveButton(R.string.delete, (dialog, which) -> {
                // TODO: Delete student from backend
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }

    @Override
    public void onViewAttendanceClick(Learner student) {
        // TODO: Navigate to student attendance history
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

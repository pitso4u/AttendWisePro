package com.attendwisepro;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.attendwisepro.adapters.AttendanceAdapter;
import com.attendwisepro.models.Learner;
import com.attendwisepro.network.ApiClient;
import com.attendwisepro.network.ApiService;
import com.attendwisepro.utils.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarkAttendanceActivity extends AppCompatActivity {
    private static final String TAG = "MarkAttendanceActivity";
    private AutoCompleteTextView spinnerClass;
    private TextInputEditText etDate;
    private RecyclerView rvAttendance;
    private MaterialButton btnSubmit;
    private AttendanceAdapter attendanceAdapter;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting MarkAttendanceActivity");
        setContentView(R.layout.activity_mark_attendance);

        initializeViews();
        setupToolbar();
        setupDatePicker();
        setupClassSpinner();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initializeViews() {
        spinnerClass = findViewById(R.id.spinnerClass);
        etDate = findViewById(R.id.etDate);
        rvAttendance = findViewById(R.id.rvAttendance);
        btnSubmit = findViewById(R.id.btnSubmit);
        calendar = Calendar.getInstance();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.mark_attendance);
        }
    }

    private void setupDatePicker() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etDate.setText(dateFormat.format(calendar.getTime()));

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            etDate.setText(dateFormat.format(calendar.getTime()));
        };

        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void setupClassSpinner() {
        Log.d(TAG, "setupClassSpinner: Loading class list");
        String token = "Bearer " + new UserSession(this).getToken();
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<List<String>> call = apiService.getClasses(token);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Successfully loaded class list");
                    List<String> classes = response.body();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        MarkAttendanceActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        classes
                    );
                    spinnerClass.setAdapter(adapter);
                    
                    // Add listener to load learners when class is selected
                    spinnerClass.setOnItemClickListener((parent, view, position, id) -> {
                        String selectedClass = (String) parent.getItemAtPosition(position);
                        loadLearners(selectedClass);
                    });
                } else {
                    Log.e(TAG, "onResponse: Failed to load class list. Code: " + response.code());
                    Toast.makeText(MarkAttendanceActivity.this, "Failed to load classes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed to load class list", t);
                Toast.makeText(MarkAttendanceActivity.this, "Error loading classes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        attendanceAdapter = new AttendanceAdapter();
        rvAttendance.setLayoutManager(new LinearLayoutManager(this));
        rvAttendance.setAdapter(attendanceAdapter);
    }

    private void loadLearners(String selectedClass) {
        Log.d(TAG, "loadLearners: Loading learners for class: " + selectedClass);
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<List<Learner>> call = apiService.getLearnersByClass("Bearer " + new UserSession(this).getToken(), selectedClass);
        call.enqueue(new Callback<List<Learner>>() {
            @Override
            public void onResponse(Call<List<Learner>> call, Response<List<Learner>> response) {
                if (response.isSuccessful()) {
                    List<Learner> learners = response.body();
                    attendanceAdapter.setLearners(learners);
                } else {
                    Toast.makeText(MarkAttendanceActivity.this, "Failed to load learners", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Learner>> call, Throwable t) {
                Toast.makeText(MarkAttendanceActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> submitAttendance());
    }

    private void loadDummyData() {
        List<Learner> dummyLearners = new ArrayList<>();
        dummyLearners.add(new Learner(1, "John", "Doe", "john.doe@example.com", "1234567890", "Class 1-A", "Parent 1", "1234567890"));
        dummyLearners.add(new Learner(2, "Jane", "Smith", "jane.smith@example.com", "0987654321", "Class 1-A", "Parent 2", "0987654321"));
        attendanceAdapter.setLearners(dummyLearners);
    }

    private void submitAttendance() {
        if (spinnerClass.getText().toString().isEmpty()) {
            spinnerClass.setError(getString(R.string.select_class_error));
            return;
        }

        String selectedClass = spinnerClass.getText().toString();
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String date = apiDateFormat.format(calendar.getTime());
        String token = "Bearer " + new UserSession(this).getToken();

        Map<Integer, Boolean> attendanceMap = attendanceAdapter.getAttendanceMap();
        Map<String, Boolean> stringAttendanceMap = new HashMap<>();
        for (Map.Entry<Integer, Boolean> entry : attendanceMap.entrySet()) {
            stringAttendanceMap.put(entry.getKey().toString(), entry.getValue());
        }

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<ResponseBody> call = apiService.submitAttendance(token, selectedClass, date, stringAttendanceMap);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MarkAttendanceActivity.this, R.string.attendance_submitted, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MarkAttendanceActivity.this, "Failed to submit attendance", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MarkAttendanceActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

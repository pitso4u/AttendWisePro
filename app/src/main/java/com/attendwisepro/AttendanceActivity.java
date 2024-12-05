package com.attendwisepro;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.attendwisepro.adapters.AttendanceAdapter;
import com.attendwisepro.network.ApiClient;
import com.attendwisepro.network.ApiService;
import com.attendwisepro.models.Learner;
import com.attendwisepro.utils.UserSession;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceActivity extends AppCompatActivity {
    private AutoCompleteTextView classSpinner;
    private TextInputEditText datePicker;
    private RecyclerView attendanceRecyclerView;
    private ExtendedFloatingActionButton submitButton;
    private AttendanceAdapter attendanceAdapter;
    private Calendar selectedDate;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        setupToolbar();
        setupViews();
        setupDatePicker();
        setupClassSpinner();
        setupRecyclerView();
        setupSubmitButton();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.attendance);
        }
    }

    private void setupViews() {
        classSpinner = findViewById(R.id.classSpinner);
        datePicker = findViewById(R.id.datePicker);
        attendanceRecyclerView = findViewById(R.id.attendanceRecyclerView);
        submitButton = findViewById(R.id.submitButton);
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        selectedDate = Calendar.getInstance();
    }

    private void setupDatePicker() {
        datePicker.setText(dateFormat.format(selectedDate.getTime()));
        
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, day);
            datePicker.setText(dateFormat.format(selectedDate.getTime()));
            loadAttendanceData();
        };

        datePicker.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                this,
                dateSetListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });
    }

    private void setupClassSpinner() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<List<String>> call = apiService.getClasses("Bearer " + new UserSession(this).getToken());
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> classes = response.body();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        AttendanceActivity.this,
                        R.layout.item_dropdown,
                        classes
                    );
                    classSpinner.setAdapter(adapter);
                    classSpinner.setOnItemClickListener((parent, view, position, id) -> loadAttendanceData());
                } else {
                    Toast.makeText(AttendanceActivity.this, "Failed to load classes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(AttendanceActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        attendanceAdapter = new AttendanceAdapter();
        attendanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendanceRecyclerView.setAdapter(attendanceAdapter);
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> submitAttendance());
    }

    private void loadAttendanceData() {
        String selectedClass = classSpinner.getText().toString();
        if (selectedClass.isEmpty()) {
            Toast.makeText(this, R.string.select_class_error, Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        String token = "Bearer " + new UserSession(this).getToken();
        Call<List<Learner>> call = apiService.getLearnersByClass(token, selectedClass);
        call.enqueue(new Callback<List<Learner>>() {
            @Override
            public void onResponse(Call<List<Learner>> call, Response<List<Learner>> response) {
                if (response.isSuccessful()) {
                    List<Learner> learners = response.body();
                    attendanceAdapter.setLearners(learners);
                } else {
                    Toast.makeText(AttendanceActivity.this, "Failed to load learners", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Learner>> call, Throwable t) {
                Toast.makeText(AttendanceActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitAttendance() {
        Map<String, Boolean> attendanceMap = attendanceAdapter.getAttendanceMap();
        String selectedClass = classSpinner.getText().toString();
        String selectedDate = dateFormat.format(this.selectedDate.getTime());

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        String token = "Bearer " + new UserSession(this).getToken();
        Call<ResponseBody> call = apiService.submitAttendance(token, selectedClass, selectedDate, attendanceMap);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AttendanceActivity.this, R.string.attendance_submitted, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AttendanceActivity.this, "Failed to submit attendance", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AttendanceActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

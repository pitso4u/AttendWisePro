package com.attendwisepro;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.attendwisepro.adapters.AttendanceDetailsAdapter;
import com.attendwisepro.models.AttendanceDetail;
import com.attendwisepro.models.ReportResponse;
import com.attendwisepro.network.ApiClient;
import com.attendwisepro.network.ApiService;
import com.attendwisepro.utils.UserSession;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsActivity extends AppCompatActivity {
    private static final String TAG = "ReportsActivity";
    private TextInputEditText etStartDate;
    private TextInputEditText etEndDate;
    private AutoCompleteTextView spinnerClass;
    private MaterialButton btnGenerateReport;
    private MaterialCardView cardReport;
    private PieChart pieChart;
    private RecyclerView rvAttendanceDetails;
    private AttendanceDetailsAdapter attendanceAdapter;
    private Calendar startDate;
    private Calendar endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting ReportsActivity");
        setContentView(R.layout.activity_reports);

        initializeViews();
        setupToolbar();
        setupDatePickers();
        setupClassSpinner();
        setupClickListeners();
    }

    private void initializeViews() {
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        spinnerClass = findViewById(R.id.spinnerClass);
        btnGenerateReport = findViewById(R.id.btnGenerateReport);
        cardReport = findViewById(R.id.cardReport);
        pieChart = findViewById(R.id.pieChart);
        rvAttendanceDetails = findViewById(R.id.rvAttendanceDetails);
        
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.reports);
        }
    }

    private void setupDatePickers() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etStartDate.setText(dateFormat.format(startDate.getTime()));
        etEndDate.setText(dateFormat.format(endDate.getTime()));

        etStartDate.setOnClickListener(v -> showDatePicker(startDate, etStartDate));
        etEndDate.setOnClickListener(v -> showDatePicker(endDate, etEndDate));
    }

    private void showDatePicker(Calendar calendar, TextInputEditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, day) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                editText.setText(dateFormat.format(calendar.getTime()));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
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
                        ReportsActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        classes
                    );
                    spinnerClass.setAdapter(adapter);
                } else {
                    Log.e(TAG, "onResponse: Failed to load class list. Code: " + response.code());
                    Toast.makeText(ReportsActivity.this, "Failed to load classes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed to load class list", t);
                Toast.makeText(ReportsActivity.this, "Error loading classes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        btnGenerateReport.setOnClickListener(v -> generateReport());
    }

    private void generateReport() {
        Log.d(TAG, "generateReport: Generating report");
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String selectedDate = apiDateFormat.format(startDate.getTime());
        String selectedClass = spinnerClass.getText().toString();

        if (selectedClass.isEmpty()) {
            Log.w(TAG, "generateReport: No class selected");
            Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "generateReport: Fetching report for class: " + selectedClass + ", date: " + selectedDate);
        String token = "Bearer " + new UserSession(this).getToken();
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<ReportResponse> call = apiService.getReport(token, selectedDate, selectedClass);
        
        call.enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: Successfully generated report");
                    ReportResponse reportResponse = response.body();
                    cardReport.setVisibility(View.VISIBLE);
                    setupPieChart(reportResponse.getPresent(), reportResponse.getAbsent());
                    setupAttendanceDetails(reportResponse.getAttendanceDetails());
                } else {
                    Log.e(TAG, "onResponse: Failed to generate report. Code: " + response.code());
                    cardReport.setVisibility(View.GONE);
                    Toast.makeText(ReportsActivity.this, "Failed to generate report", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed to generate report", t);
                cardReport.setVisibility(View.GONE);
                Toast.makeText(ReportsActivity.this, "Error generating report", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPieChart(int present, int absent) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(present, getString(R.string.present)));
        entries.add(new PieEntry(absent, getString(R.string.absent)));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Color.rgb(76, 175, 80), Color.rgb(244, 67, 54));
        dataSet.setValueTextSize(14f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(getString(R.string.attendance_percentage));
        pieChart.setCenterTextSize(16f);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.invalidate();
    }

    private void setupAttendanceDetails(List<AttendanceDetail> details) {
        rvAttendanceDetails.setLayoutManager(new LinearLayoutManager(this));
        attendanceAdapter = new AttendanceDetailsAdapter(details);
        rvAttendanceDetails.setAdapter(attendanceAdapter);
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

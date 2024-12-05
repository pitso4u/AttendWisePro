package com.attendwisepro;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.attendwisepro.models.Incident;
import com.attendwisepro.network.ApiClient;
import com.attendwisepro.network.ApiService;
import com.attendwisepro.utils.UserSession;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddIncidentActivity extends AppCompatActivity {
    private TextInputLayout titleLayout;
    private TextInputLayout descriptionLayout;
    private TextInputLayout dateLayout;
    private TextInputLayout locationLayout;
    private TextInputLayout severityLayout;
    private Button submitButton;
    private View progressBar;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_incident);

        userSession = new UserSession(this);
        setupToolbar();
        setupViews();
        setupSeverityDropdown();
        setupDatePicker();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.add_incident);
        }
    }

    private void setupViews() {
        titleLayout = findViewById(R.id.titleLayout);
        descriptionLayout = findViewById(R.id.descriptionLayout);
        dateLayout = findViewById(R.id.dateLayout);
        locationLayout = findViewById(R.id.locationLayout);
        severityLayout = findViewById(R.id.severityLayout);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);

        submitButton.setOnClickListener(v -> submitIncident());
    }

    private void setupSeverityDropdown() {
        AutoCompleteTextView severityDropdown = (AutoCompleteTextView) severityLayout.getEditText();
        String[] severityLevels = new String[]{"LOW", "MEDIUM", "HIGH", "CRITICAL"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_dropdown, severityLevels);
        severityDropdown.setAdapter(adapter);
    }

    private void setupDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select incident date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build();

        dateLayout.getEditText().setOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "DATE_PICKER"));

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Date date = new Date(selection);
            dateLayout.getEditText().setText(android.text.format.DateFormat.getDateFormat(this).format(date));
        });
    }

    private void submitIncident() {
        if (!validateInput()) {
            return;
        }

        showLoading(true);

        String title = titleLayout.getEditText().getText().toString();
        String description = descriptionLayout.getEditText().getText().toString();
        String location = locationLayout.getEditText().getText().toString();
        String severity = severityLayout.getEditText().getText().toString();
        Date date = new Date(); // TODO: Parse from dateLayout

        Incident incident = new Incident(
            title,
            description,
            date,
            location,
            userSession.getUserName(),
            severity
        );

        // Get API service and token
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        String token = "Bearer " + userSession.getToken();

        // Make API call
        apiService.createIncident(token, incident).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(AddIncidentActivity.this, R.string.incident_added_success, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddIncidentActivity.this, R.string.error_saving_incident, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                showLoading(false);
                Toast.makeText(AddIncidentActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (TextUtils.isEmpty(titleLayout.getEditText().getText())) {
            titleLayout.setError(getString(R.string.field_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(descriptionLayout.getEditText().getText())) {
            descriptionLayout.setError(getString(R.string.field_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(dateLayout.getEditText().getText())) {
            dateLayout.setError(getString(R.string.field_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(locationLayout.getEditText().getText())) {
            locationLayout.setError(getString(R.string.field_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(severityLayout.getEditText().getText())) {
            severityLayout.setError(getString(R.string.field_required));
            isValid = false;
        }

        return isValid;
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        submitButton.setEnabled(!show);
        titleLayout.setEnabled(!show);
        descriptionLayout.setEnabled(!show);
        dateLayout.setEnabled(!show);
        locationLayout.setEnabled(!show);
        severityLayout.setEnabled(!show);
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

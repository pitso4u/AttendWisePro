package com.attendwisepro;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.attendwisepro.models.Visitor;
import com.attendwisepro.network.ApiClient;
import com.attendwisepro.network.ApiService;
import com.attendwisepro.utils.SharedPrefsUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVisitorActivity extends AppCompatActivity {
    private TextInputLayout visitorNameLayout;
    private TextInputLayout visitorIdLayout;
    private TextInputLayout purposeLayout;
    private TextInputLayout hostNameLayout;
    private TextInputLayout checkInLayout;
    private MaterialButton submitButton;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_visitor);

        setupToolbar();
        initializeViews();
        setupDatePicker();
        setupSubmitButton();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.add_visitor);
        }
    }

    private void initializeViews() {
        visitorNameLayout = findViewById(R.id.visitorNameLayout);
        visitorIdLayout = findViewById(R.id.visitorIdLayout);
        purposeLayout = findViewById(R.id.purposeLayout);
        hostNameLayout = findViewById(R.id.hostNameLayout);
        checkInLayout = findViewById(R.id.checkInLayout);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupDatePicker() {
        TextInputEditText checkInEditText = (TextInputEditText) checkInLayout.getEditText();
        if (checkInEditText != null) {
            checkInEditText.setOnClickListener(v -> {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText(R.string.select_date)
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

                datePicker.addOnPositiveButtonClickListener(selection -> {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = dateFormat.format(new Date(selection));
                    checkInEditText.setText(formattedDate);
                });

                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            });
        }
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                submitVisitorData();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (visitorNameLayout.getEditText() != null && 
            visitorNameLayout.getEditText().getText().toString().trim().isEmpty()) {
            visitorNameLayout.setError(getString(R.string.field_required));
            isValid = false;
        } else {
            visitorNameLayout.setError(null);
        }

        if (visitorIdLayout.getEditText() != null && 
            visitorIdLayout.getEditText().getText().toString().trim().isEmpty()) {
            visitorIdLayout.setError(getString(R.string.field_required));
            isValid = false;
        } else {
            visitorIdLayout.setError(null);
        }

        if (purposeLayout.getEditText() != null && 
            purposeLayout.getEditText().getText().toString().trim().isEmpty()) {
            purposeLayout.setError(getString(R.string.field_required));
            isValid = false;
        } else {
            purposeLayout.setError(null);
        }

        if (hostNameLayout.getEditText() != null && 
            hostNameLayout.getEditText().getText().toString().trim().isEmpty()) {
            hostNameLayout.setError(getString(R.string.field_required));
            isValid = false;
        } else {
            hostNameLayout.setError(null);
        }

        if (checkInLayout.getEditText() != null && 
            checkInLayout.getEditText().getText().toString().trim().isEmpty()) {
            checkInLayout.setError(getString(R.string.field_required));
            isValid = false;
        } else {
            checkInLayout.setError(null);
        }

        return isValid;
    }

    private void submitVisitorData() {
        progressBar.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);

        String visitorName = visitorNameLayout.getEditText().getText().toString().trim();
        String visitorId = visitorIdLayout.getEditText().getText().toString().trim();
        String purpose = purposeLayout.getEditText().getText().toString().trim();
        String hostName = hostNameLayout.getEditText().getText().toString().trim();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date checkInTime;
        try {
            checkInTime = dateFormat.parse(checkInLayout.getEditText().getText().toString().trim());
        } catch (Exception e) {
            checkInTime = new Date(); // Use current date if parsing fails
        }

        Visitor visitor = new Visitor(
            visitorName,
            visitorId,
            purpose,
            hostName,
            checkInTime,
            null // checkOutTime will be set when visitor checks out
        );

        // Get API service and token
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        String token = "Bearer " + SharedPrefsUtil.getToken(this);

        // Make API call
        apiService.createVisitor(token, visitor).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.GONE);
                submitButton.setEnabled(true);
                
                if (response.isSuccessful()) {
                    Toast.makeText(AddVisitorActivity.this, R.string.visitor_added_success, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddVisitorActivity.this, R.string.error_saving_visitor, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                submitButton.setEnabled(true);
                Toast.makeText(AddVisitorActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
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

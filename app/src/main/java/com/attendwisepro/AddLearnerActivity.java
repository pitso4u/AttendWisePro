package com.attendwisepro;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;

import com.attendwisepro.models.Learner;
import com.attendwisepro.network.ApiClient;
import com.attendwisepro.network.ApiService;
import com.attendwisepro.utils.SharedPrefsUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLearnerActivity extends AppCompatActivity {
    private TextInputEditText etFirstName;
    private TextInputEditText etLastName;
    private TextInputEditText etStudentId;
    private TextInputEditText etClass;
    private TextInputEditText etParentName;
    private TextInputEditText etParentContact;
    private MaterialButton btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_learner);

        initializeViews();
        setupToolbar();
        setupClickListeners();
    }

    private void initializeViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etStudentId = findViewById(R.id.etStudentId);
        etClass = findViewById(R.id.etClass);
        etParentName = findViewById(R.id.etParentName);
        etParentContact = findViewById(R.id.etParentContact);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.add_learner);
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveLearner());
    }

    private void saveLearner() {
        if (!validateInput()) {
            return;
        }

        // Create learner object with null ID for new learners
        Learner learner = new Learner(
            null,  // ID is null for new learners
            etFirstName.getText().toString().trim(),
            etLastName.getText().toString().trim(),
            "",  // email is optional
            "",  // phone is optional
            etClass.getText().toString().trim(),
            etParentName.getText().toString().trim(),
            etParentContact.getText().toString().trim()
        );

        // Show loading indicator
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.saving_learner));
        progressDialog.show();

        // Get API service instance
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        String token = "Bearer " + SharedPrefsUtil.getToken(this);

        // Make API call to save learner
        apiService.saveLearner(token, learner).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(AddLearnerActivity.this, R.string.learner_added_success, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddLearnerActivity.this, R.string.error_saving_learner, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AddLearnerActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (etFirstName.getText().toString().trim().isEmpty()) {
            etFirstName.setError(getString(R.string.field_required));
            isValid = false;
        }

        if (etLastName.getText().toString().trim().isEmpty()) {
            etLastName.setError(getString(R.string.field_required));
            isValid = false;
        }

        if (etStudentId.getText().toString().trim().isEmpty()) {
            etStudentId.setError(getString(R.string.field_required));
            isValid = false;
        }

        if (etClass.getText().toString().trim().isEmpty()) {
            etClass.setError(getString(R.string.field_required));
            isValid = false;
        }

        if (etParentContact.getText().toString().trim().isEmpty()) {
            etParentContact.setError(getString(R.string.field_required));
            isValid = false;
        }

        return isValid;
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

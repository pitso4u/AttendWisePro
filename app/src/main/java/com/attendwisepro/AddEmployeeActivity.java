package com.attendwisepro;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.attendwisepro.network.ApiClient;
import com.attendwisepro.network.ApiService;
import com.attendwisepro.models.Employee;
import com.attendwisepro.utils.SharedPrefsUtil;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.ProgressDialog;

public class AddEmployeeActivity extends AppCompatActivity {
    private TextInputLayout nameLayout;
    private TextInputLayout employeeIdLayout;
    private TextInputLayout departmentLayout;
    private TextInputLayout positionLayout;
    private TextInputLayout emailLayout;
    private MaterialButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        setupToolbar();
        setupViews();
        setupSaveButton();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.add_employee);
        }
    }

    private void setupViews() {
        nameLayout = findViewById(R.id.nameLayout);
        employeeIdLayout = findViewById(R.id.employeeIdLayout);
        departmentLayout = findViewById(R.id.departmentLayout);
        positionLayout = findViewById(R.id.positionLayout);
        emailLayout = findViewById(R.id.emailLayout);
        saveButton = findViewById(R.id.saveButton);

        // Setup department dropdown
        AutoCompleteTextView departmentDropdown = (AutoCompleteTextView) departmentLayout.getEditText();
        String[] departments = new String[]{"Administration", "Teaching", "Support Staff", "IT", "Maintenance"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_dropdown, departments);
        departmentDropdown.setAdapter(adapter);

        // Setup position dropdown
        AutoCompleteTextView positionDropdown = (AutoCompleteTextView) positionLayout.getEditText();
        String[] positions = new String[]{"Teacher", "Administrator", "Principal", "Coordinator", "Staff"};
        ArrayAdapter<String> positionAdapter = new ArrayAdapter<>(this, R.layout.item_dropdown, positions);
        positionDropdown.setAdapter(positionAdapter);
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> saveEmployee());
    }

    private void saveEmployee() {
        if (!validateInput()) {
            return;
        }

        String fullName = nameLayout.getEditText().getText().toString();
        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        Employee employee = new Employee(
            firstName,
            lastName,
            employeeIdLayout.getEditText().getText().toString(),
            departmentLayout.getEditText().getText().toString(),
            positionLayout.getEditText().getText().toString(),
            "", // Contact number will be added in a future update
            emailLayout.getEditText().getText().toString()
        );

        // Show loading dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.saving_employee));
        progressDialog.show();

        // Get API service and token
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        String token = "Bearer " + SharedPrefsUtil.getToken(this);

        // Make API call
        apiService.saveEmployee(token, employee).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(AddEmployeeActivity.this, R.string.employee_added_success, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddEmployeeActivity.this, R.string.error_saving_employee, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AddEmployeeActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (nameLayout.getEditText().getText().toString().trim().isEmpty()) {
            nameLayout.setError(getString(R.string.field_required));
            isValid = false;
        } else {
            nameLayout.setError(null);
        }

        if (employeeIdLayout.getEditText().getText().toString().trim().isEmpty()) {
            employeeIdLayout.setError(getString(R.string.field_required));
            isValid = false;
        } else {
            employeeIdLayout.setError(null);
        }

        if (departmentLayout.getEditText().getText().toString().trim().isEmpty()) {
            departmentLayout.setError(getString(R.string.field_required));
            isValid = false;
        } else {
            departmentLayout.setError(null);
        }

        if (positionLayout.getEditText().getText().toString().trim().isEmpty()) {
            positionLayout.setError(getString(R.string.field_required));
            isValid = false;
        } else {
            positionLayout.setError(null);
        }

        if (emailLayout.getEditText().getText().toString().trim().isEmpty()) {
            emailLayout.setError(getString(R.string.field_required));
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        return isValid;
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

// Generated by view binder compiler. Do not edit!
package com.attendwisepro.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.attendwisepro.R;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityReportsBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final MaterialButton btnGenerateReport;

  @NonNull
  public final MaterialCardView cardDateRange;

  @NonNull
  public final MaterialCardView cardFilters;

  @NonNull
  public final MaterialCardView cardReport;

  @NonNull
  public final TextInputEditText etEndDate;

  @NonNull
  public final TextInputEditText etStartDate;

  @NonNull
  public final PieChart pieChart;

  @NonNull
  public final RecyclerView rvAttendanceDetails;

  @NonNull
  public final AutoCompleteTextView spinnerClass;

  @NonNull
  public final Toolbar toolbar;

  private ActivityReportsBinding(@NonNull CoordinatorLayout rootView,
      @NonNull MaterialButton btnGenerateReport, @NonNull MaterialCardView cardDateRange,
      @NonNull MaterialCardView cardFilters, @NonNull MaterialCardView cardReport,
      @NonNull TextInputEditText etEndDate, @NonNull TextInputEditText etStartDate,
      @NonNull PieChart pieChart, @NonNull RecyclerView rvAttendanceDetails,
      @NonNull AutoCompleteTextView spinnerClass, @NonNull Toolbar toolbar) {
    this.rootView = rootView;
    this.btnGenerateReport = btnGenerateReport;
    this.cardDateRange = cardDateRange;
    this.cardFilters = cardFilters;
    this.cardReport = cardReport;
    this.etEndDate = etEndDate;
    this.etStartDate = etStartDate;
    this.pieChart = pieChart;
    this.rvAttendanceDetails = rvAttendanceDetails;
    this.spinnerClass = spinnerClass;
    this.toolbar = toolbar;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityReportsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityReportsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_reports, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityReportsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnGenerateReport;
      MaterialButton btnGenerateReport = ViewBindings.findChildViewById(rootView, id);
      if (btnGenerateReport == null) {
        break missingId;
      }

      id = R.id.cardDateRange;
      MaterialCardView cardDateRange = ViewBindings.findChildViewById(rootView, id);
      if (cardDateRange == null) {
        break missingId;
      }

      id = R.id.cardFilters;
      MaterialCardView cardFilters = ViewBindings.findChildViewById(rootView, id);
      if (cardFilters == null) {
        break missingId;
      }

      id = R.id.cardReport;
      MaterialCardView cardReport = ViewBindings.findChildViewById(rootView, id);
      if (cardReport == null) {
        break missingId;
      }

      id = R.id.etEndDate;
      TextInputEditText etEndDate = ViewBindings.findChildViewById(rootView, id);
      if (etEndDate == null) {
        break missingId;
      }

      id = R.id.etStartDate;
      TextInputEditText etStartDate = ViewBindings.findChildViewById(rootView, id);
      if (etStartDate == null) {
        break missingId;
      }

      id = R.id.pieChart;
      PieChart pieChart = ViewBindings.findChildViewById(rootView, id);
      if (pieChart == null) {
        break missingId;
      }

      id = R.id.rvAttendanceDetails;
      RecyclerView rvAttendanceDetails = ViewBindings.findChildViewById(rootView, id);
      if (rvAttendanceDetails == null) {
        break missingId;
      }

      id = R.id.spinnerClass;
      AutoCompleteTextView spinnerClass = ViewBindings.findChildViewById(rootView, id);
      if (spinnerClass == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      return new ActivityReportsBinding((CoordinatorLayout) rootView, btnGenerateReport,
          cardDateRange, cardFilters, cardReport, etEndDate, etStartDate, pieChart,
          rvAttendanceDetails, spinnerClass, toolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}

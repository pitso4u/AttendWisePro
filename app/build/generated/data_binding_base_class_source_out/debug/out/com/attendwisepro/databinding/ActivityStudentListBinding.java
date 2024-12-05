// Generated by view binder compiler. Do not edit!
package com.attendwisepro.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.attendwisepro.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityStudentListBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final TextInputEditText etSearch;

  @NonNull
  public final FloatingActionButton fabAddStudent;

  @NonNull
  public final TextInputLayout layoutSearch;

  @NonNull
  public final CircularProgressIndicator progressBar;

  @NonNull
  public final RecyclerView rvStudents;

  @NonNull
  public final Toolbar toolbar;

  private ActivityStudentListBinding(@NonNull CoordinatorLayout rootView,
      @NonNull TextInputEditText etSearch, @NonNull FloatingActionButton fabAddStudent,
      @NonNull TextInputLayout layoutSearch, @NonNull CircularProgressIndicator progressBar,
      @NonNull RecyclerView rvStudents, @NonNull Toolbar toolbar) {
    this.rootView = rootView;
    this.etSearch = etSearch;
    this.fabAddStudent = fabAddStudent;
    this.layoutSearch = layoutSearch;
    this.progressBar = progressBar;
    this.rvStudents = rvStudents;
    this.toolbar = toolbar;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityStudentListBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityStudentListBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_student_list, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityStudentListBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.etSearch;
      TextInputEditText etSearch = ViewBindings.findChildViewById(rootView, id);
      if (etSearch == null) {
        break missingId;
      }

      id = R.id.fabAddStudent;
      FloatingActionButton fabAddStudent = ViewBindings.findChildViewById(rootView, id);
      if (fabAddStudent == null) {
        break missingId;
      }

      id = R.id.layoutSearch;
      TextInputLayout layoutSearch = ViewBindings.findChildViewById(rootView, id);
      if (layoutSearch == null) {
        break missingId;
      }

      id = R.id.progressBar;
      CircularProgressIndicator progressBar = ViewBindings.findChildViewById(rootView, id);
      if (progressBar == null) {
        break missingId;
      }

      id = R.id.rvStudents;
      RecyclerView rvStudents = ViewBindings.findChildViewById(rootView, id);
      if (rvStudents == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      return new ActivityStudentListBinding((CoordinatorLayout) rootView, etSearch, fabAddStudent,
          layoutSearch, progressBar, rvStudents, toolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}

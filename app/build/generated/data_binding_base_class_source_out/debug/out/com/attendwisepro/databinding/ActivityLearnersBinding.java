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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.attendwisepro.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityLearnersBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final FloatingActionButton fabAdd;

  @NonNull
  public final RecyclerView recyclerView;

  @NonNull
  public final SwipeRefreshLayout swipeRefresh;

  @NonNull
  public final Toolbar toolbar;

  private ActivityLearnersBinding(@NonNull CoordinatorLayout rootView,
      @NonNull FloatingActionButton fabAdd, @NonNull RecyclerView recyclerView,
      @NonNull SwipeRefreshLayout swipeRefresh, @NonNull Toolbar toolbar) {
    this.rootView = rootView;
    this.fabAdd = fabAdd;
    this.recyclerView = recyclerView;
    this.swipeRefresh = swipeRefresh;
    this.toolbar = toolbar;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityLearnersBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityLearnersBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_learners, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityLearnersBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.fabAdd;
      FloatingActionButton fabAdd = ViewBindings.findChildViewById(rootView, id);
      if (fabAdd == null) {
        break missingId;
      }

      id = R.id.recyclerView;
      RecyclerView recyclerView = ViewBindings.findChildViewById(rootView, id);
      if (recyclerView == null) {
        break missingId;
      }

      id = R.id.swipeRefresh;
      SwipeRefreshLayout swipeRefresh = ViewBindings.findChildViewById(rootView, id);
      if (swipeRefresh == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      return new ActivityLearnersBinding((CoordinatorLayout) rootView, fabAdd, recyclerView,
          swipeRefresh, toolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}

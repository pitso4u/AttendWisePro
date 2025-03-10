// Generated by view binder compiler. Do not edit!
package com.attendwisepro.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.attendwisepro.R;
import com.google.android.material.card.MaterialCardView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemLearnerBinding implements ViewBinding {
  @NonNull
  private final MaterialCardView rootView;

  @NonNull
  public final ImageButton buttonMore;

  @NonNull
  public final TextView textClass;

  @NonNull
  public final TextView textName;

  @NonNull
  public final TextView textStudentId;

  private ItemLearnerBinding(@NonNull MaterialCardView rootView, @NonNull ImageButton buttonMore,
      @NonNull TextView textClass, @NonNull TextView textName, @NonNull TextView textStudentId) {
    this.rootView = rootView;
    this.buttonMore = buttonMore;
    this.textClass = textClass;
    this.textName = textName;
    this.textStudentId = textStudentId;
  }

  @Override
  @NonNull
  public MaterialCardView getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemLearnerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemLearnerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_learner, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemLearnerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonMore;
      ImageButton buttonMore = ViewBindings.findChildViewById(rootView, id);
      if (buttonMore == null) {
        break missingId;
      }

      id = R.id.textClass;
      TextView textClass = ViewBindings.findChildViewById(rootView, id);
      if (textClass == null) {
        break missingId;
      }

      id = R.id.textName;
      TextView textName = ViewBindings.findChildViewById(rootView, id);
      if (textName == null) {
        break missingId;
      }

      id = R.id.textStudentId;
      TextView textStudentId = ViewBindings.findChildViewById(rootView, id);
      if (textStudentId == null) {
        break missingId;
      }

      return new ItemLearnerBinding((MaterialCardView) rootView, buttonMore, textClass, textName,
          textStudentId);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}

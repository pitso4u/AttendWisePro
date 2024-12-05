package com.attendwisepro.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.attendwisepro.R;
import com.attendwisepro.models.RecentActivity;

import java.util.ArrayList;
import java.util.List;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ViewHolder> {
    private List<RecentActivity> activities;

    public RecentActivityAdapter() {
        this.activities = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentActivity activity = activities.get(position);
        holder.typeTextView.setText(activity.getType());
        holder.descriptionTextView.setText(activity.getDescription());
        holder.timestampTextView.setText(activity.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public void updateActivities(List<RecentActivity> newActivities) {
        this.activities.clear();
        this.activities.addAll(newActivities);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView typeTextView;
        TextView descriptionTextView;
        TextView timestampTextView;

        ViewHolder(View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }
    }
}

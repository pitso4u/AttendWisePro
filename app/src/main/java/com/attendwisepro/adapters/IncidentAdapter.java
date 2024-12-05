package com.attendwisepro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.attendwisepro.R;
import com.attendwisepro.models.Incident;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.ViewHolder> {
    private final Context context;
    private List<Incident> incidents;
    private final SimpleDateFormat dateFormat;

    public IncidentAdapter(Context context, List<Incident> incidents) {
        this.context = context;
        this.incidents = incidents;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_incident, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Incident incident = incidents.get(position);
        holder.titleTextView.setText(incident.getTitle());
        holder.descriptionTextView.setText(incident.getDescription());
        holder.dateTextView.setText(dateFormat.format(incident.getDate()));
        holder.locationTextView.setText(incident.getLocation());
        holder.severityTextView.setText(incident.getSeverity());
        holder.reportedByTextView.setText(incident.getReportedBy());
    }

    @Override
    public int getItemCount() {
        return incidents.size();
    }

    public void updateIncidents(List<Incident> newIncidents) {
        this.incidents = newIncidents;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        TextView dateTextView;
        TextView locationTextView;
        TextView severityTextView;
        TextView reportedByTextView;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            severityTextView = itemView.findViewById(R.id.severityTextView);
            reportedByTextView = itemView.findViewById(R.id.reportedByTextView);
        }
    }
}

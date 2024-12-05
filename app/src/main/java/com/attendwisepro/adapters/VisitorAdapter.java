package com.attendwisepro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.attendwisepro.R;
import com.attendwisepro.models.Visitor;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class VisitorAdapter extends RecyclerView.Adapter<VisitorAdapter.ViewHolder> {
    private final Context context;
    private List<Visitor> visitors;
    private final SimpleDateFormat dateFormat;

    public VisitorAdapter(Context context, List<Visitor> visitors) {
        this.context = context;
        this.visitors = visitors;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_visitor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Visitor visitor = visitors.get(position);
        holder.nameTextView.setText(visitor.getName());
        holder.idTextView.setText(visitor.getVisitorId());
        holder.purposeTextView.setText(visitor.getPurpose());
        holder.hostTextView.setText(visitor.getHostName());
        holder.checkInTextView.setText(dateFormat.format(visitor.getCheckInTime()));
        
        if (visitor.getCheckOutTime() != null) {
            holder.checkOutTextView.setVisibility(View.VISIBLE);
            holder.checkOutTextView.setText(dateFormat.format(visitor.getCheckOutTime()));
        } else {
            holder.checkOutTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return visitors.size();
    }

    public void updateVisitors(List<Visitor> newVisitors) {
        this.visitors = newVisitors;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView idTextView;
        TextView purposeTextView;
        TextView hostTextView;
        TextView checkInTextView;
        TextView checkOutTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            idTextView = itemView.findViewById(R.id.idTextView);
            purposeTextView = itemView.findViewById(R.id.purposeTextView);
            hostTextView = itemView.findViewById(R.id.hostTextView);
            checkInTextView = itemView.findViewById(R.id.checkInTextView);
            checkOutTextView = itemView.findViewById(R.id.checkOutTextView);
        }
    }
}

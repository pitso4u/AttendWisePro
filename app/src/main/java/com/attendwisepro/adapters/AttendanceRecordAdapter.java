package com.attendwisepro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.attendwisepro.R;
import com.attendwisepro.models.AttendanceRecord;
import com.attendwisepro.models.AttendanceRecord.AttendanceStatus;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AttendanceRecordAdapter extends RecyclerView.Adapter<AttendanceRecordAdapter.ViewHolder> {
    private final Context context;
    private List<AttendanceRecord> records;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    public AttendanceRecordAdapter(Context context, List<AttendanceRecord> records) {
        this.context = context;
        this.records = records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendance_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceRecord record = records.get(position);
        
        // Format date and time
        holder.dateText.setText(dateFormat.format(record.getTimestamp()));

        // Set status
        AttendanceStatus status = record.getStatus();
        int statusStringId;
        int statusColorId;
        
        switch (status) {
            case PRESENT:
                statusStringId = R.string.present;
                statusColorId = R.color.present_color;
                break;
            case LATE:
                statusStringId = R.string.late;
                statusColorId = R.color.warning_yellow;
                break;
            case ABSENT:
                statusStringId = R.string.absent;
                statusColorId = R.color.absent_color;
                break;
            default:
                statusStringId = R.string.absent;
                statusColorId = R.color.absent_color;
                break;
        }
        
        holder.statusText.setText(statusStringId);
        holder.statusText.setTextColor(ContextCompat.getColor(context, statusColorId));

        // Set notes if any
        if (record.getNotes() != null && !record.getNotes().isEmpty()) {
            holder.remarksText.setVisibility(View.VISIBLE);
            holder.remarksText.setText(record.getNotes());
        } else {
            holder.remarksText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void updateRecords(List<AttendanceRecord> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        TextView statusText;
        TextView remarksText;

        ViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            statusText = itemView.findViewById(R.id.statusText);
            remarksText = itemView.findViewById(R.id.remarksText);
        }
    }
}

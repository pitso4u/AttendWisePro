package com.attendwisepro.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.attendwisepro.R;
import com.attendwisepro.models.AttendanceDetail;
import java.util.List;

public class AttendanceDetailsAdapter extends RecyclerView.Adapter<AttendanceDetailsAdapter.ViewHolder> {
    private List<AttendanceDetail> attendanceDetails;

    public AttendanceDetailsAdapter(List<AttendanceDetail> attendanceDetails) {
        this.attendanceDetails = attendanceDetails;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceDetail detail = attendanceDetails.get(position);
        holder.tvDate.setText(detail.getDate());
        holder.tvPresent.setText(String.valueOf(detail.getPresent()));
        holder.tvAbsent.setText(String.valueOf(detail.getAbsent()));
        holder.tvPercentage.setText(String.format("%.1f%%", detail.getPercentage()));
    }

    @Override
    public int getItemCount() {
        return attendanceDetails.size();
    }

    public void updateData(List<AttendanceDetail> newDetails) {
        this.attendanceDetails = newDetails;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvPresent;
        TextView tvAbsent;
        TextView tvPercentage;

        ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPresent = itemView.findViewById(R.id.tvPresent);
            tvAbsent = itemView.findViewById(R.id.tvAbsent);
            tvPercentage = itemView.findViewById(R.id.tvPercentage);
        }
    }
}

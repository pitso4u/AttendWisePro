package com.attendwisepro.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.attendwisepro.R;
import com.attendwisepro.models.Learner;
import com.google.android.material.button.MaterialButtonToggleGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    private List<Learner> learners;
    private Map<Integer, Boolean> attendanceMap;

    public AttendanceAdapter() {
        this.learners = new ArrayList<>();
        this.attendanceMap = new HashMap<>();
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        Learner learner = learners.get(position);
        holder.bind(learner);
    }

    @Override
    public int getItemCount() {
        return learners.size();
    }

    public void setLearners(List<Learner> learners) {
        this.learners = learners;
        notifyDataSetChanged();
    }

    public Map<Integer, Boolean> getAttendanceMap() {
        return attendanceMap;
    }

    class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentName;
        private TextView tvStudentId;
        private MaterialButtonToggleGroup toggleAttendance;

        AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            toggleAttendance = itemView.findViewById(R.id.toggleAttendance);
        }

        void bind(Learner learner) {
            tvStudentName.setText(learner.getFullName());
            tvStudentId.setText(String.valueOf(learner.getId()));

            // Set initial state
            Integer learnerId = learner.getId();
            if (learnerId != null) {
                toggleAttendance.check(
                    attendanceMap.containsKey(learnerId) && attendanceMap.get(learnerId)
                        ? R.id.btnPresent
                        : R.id.btnAbsent
                );

                // Update attendance map when toggle changes
                toggleAttendance.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                    if (isChecked) {
                        attendanceMap.put(learnerId, checkedId == R.id.btnPresent);
                    }
                });
            } else {
                // Handle case where learner ID is null
                toggleAttendance.setEnabled(false);
            }
        }
    }
}

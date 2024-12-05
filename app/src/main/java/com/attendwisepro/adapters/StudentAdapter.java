package com.attendwisepro.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.attendwisepro.R;
import com.attendwisepro.models.Learner;
import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private List<Learner> students;
    private OnStudentClickListener listener;

    public interface OnStudentClickListener {
        void onStudentClick(Learner student);
        void onEditClick(Learner student);
        void onDeleteClick(Learner student);
        void onViewAttendanceClick(Learner student);
    }

    public StudentAdapter(OnStudentClickListener listener) {
        this.students = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        holder.bind(students.get(position));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public void setStudents(List<Learner> students) {
        this.students = students;
        notifyDataSetChanged();
    }

    public void filterStudents(String query) {
        // TODO: Implement search filtering
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentName;
        private TextView tvStudentId;
        private TextView tvClassName;
        private ImageButton btnMore;

        StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            btnMore = itemView.findViewById(R.id.btnMore);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onStudentClick(students.get(position));
                }
            });
        }

        void bind(Learner student) {
            tvStudentName.setText(student.getFullName());
            tvStudentId.setText(student.getId());
            tvClassName.setText(student.getClassName());

            btnMore.setOnClickListener(v -> showPopupMenu(v, student));
        }

        private void showPopupMenu(View view, Learner student) {
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.inflate(R.menu.menu_student_options);
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit) {
                    listener.onEditClick(student);
                    return true;
                } else if (itemId == R.id.action_delete) {
                    listener.onDeleteClick(student);
                    return true;
                } else if (itemId == R.id.action_view_attendance) {
                    listener.onViewAttendanceClick(student);
                    return true;
                }
                return false;
            });
            popup.show();
        }
    }
}

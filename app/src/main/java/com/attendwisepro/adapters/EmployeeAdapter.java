package com.attendwisepro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.attendwisepro.R;
import com.attendwisepro.models.Employee;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {
    private final Context context;
    private List<Employee> employees;

    public EmployeeAdapter(Context context, List<Employee> employees) {
        this.context = context;
        this.employees = employees;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee employee = employees.get(position);
        holder.nameTextView.setText(employee.getFullName());
        holder.idTextView.setText(employee.getEmployeeId());
        holder.departmentTextView.setText(employee.getDepartment());
        holder.positionTextView.setText(employee.getPosition());
        holder.emailTextView.setText(employee.getEmail());
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public void updateEmployees(List<Employee> newEmployees) {
        this.employees = newEmployees;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView idTextView;
        TextView departmentTextView;
        TextView positionTextView;
        TextView emailTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            idTextView = itemView.findViewById(R.id.idTextView);
            departmentTextView = itemView.findViewById(R.id.departmentTextView);
            positionTextView = itemView.findViewById(R.id.positionTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
        }
    }
}

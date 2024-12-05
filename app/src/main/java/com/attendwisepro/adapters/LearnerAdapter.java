package com.attendwisepro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import androidx.recyclerview.widget.DiffUtil;

public class LearnerAdapter extends RecyclerView.Adapter<LearnerAdapter.LearnerViewHolder> {
    private List<Learner> learners;
    private final Context context;
    private final LearnerActionListener actionListener;

    public interface LearnerActionListener {
        void onEditLearner(Learner learner);
        void onDeleteLearner(Learner learner);
        void onViewAttendance(Learner learner);
    }

    public LearnerAdapter(Context context, List<Learner> learners, LearnerActionListener listener) {
        this.context = context;
        this.learners = learners;
        this.actionListener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return learners.get(position).getId().hashCode();
    }

    @NonNull
    @Override
    public LearnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_learner, parent, false);
        return new LearnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LearnerViewHolder holder, int position) {
        holder.bind(learners.get(position));
    }

    @Override
    public int getItemCount() {
        return learners.size();
    }

    public void updateLearners(List<Learner> newLearners) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return learners.size();
            }

            @Override
            public int getNewListSize() {
                return newLearners.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return learners.get(oldItemPosition).getId().equals(
                    newLearners.get(newItemPosition).getId()
                );
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Learner oldLearner = learners.get(oldItemPosition);
                Learner newLearner = newLearners.get(newItemPosition);
                return oldLearner.equals(newLearner);
            }
        });
        
        this.learners = new ArrayList<>(newLearners);
        diffResult.dispatchUpdatesTo(this);
    }

    class LearnerViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView studentIdText;
        private final TextView classText;
        private final ImageButton moreButton;
        private PopupMenu popup;

        LearnerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textName);
            studentIdText = itemView.findViewById(R.id.textStudentId);
            classText = itemView.findViewById(R.id.textClass);
            moreButton = itemView.findViewById(R.id.buttonMore);
        }

        void bind(final Learner learner) {
            nameText.setText(learner.getFullName());
            studentIdText.setText(learner.getId() != null ? String.valueOf(learner.getId()) : "");
            classText.setText(learner.getClassName());

            // Clean up previous popup if it exists
            if (popup != null) {
                popup.dismiss();
            }

            moreButton.setOnClickListener(v -> showPopupMenu(v, learner));
        }

        private void showPopupMenu(View view, Learner learner) {
            popup = new PopupMenu(context, view);
            popup.inflate(R.menu.menu_learner_item);
            
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit) {
                    actionListener.onEditLearner(learner);
                    return true;
                } else if (itemId == R.id.action_delete) {
                    actionListener.onDeleteLearner(learner);
                    return true;
                } else if (itemId == R.id.action_view_attendance) {
                    actionListener.onViewAttendance(learner);
                    return true;
                }
                return false;
            });
            
            popup.show();
        }

        void cleanup() {
            if (popup != null) {
                popup.dismiss();
                popup = null;
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull LearnerViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cleanup();
    }
}

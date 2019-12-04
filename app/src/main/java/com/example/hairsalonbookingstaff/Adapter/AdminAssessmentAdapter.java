package com.example.hairsalonbookingstaff.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalonbookingstaff.Model.Assessment;
import com.example.hairsalonbookingstaff.R;

import java.util.List;

public class AdminAssessmentAdapter extends RecyclerView.Adapter<AdminAssessmentAdapter.MyHolder> {
    Context context;
    List<Assessment> assessmentList;

    public AdminAssessmentAdapter(Context context, List<Assessment> assessmentList) {
        this.context = context;
        this.assessmentList = assessmentList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_assessment_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.txt_date.setText(assessmentList.get(position).getDate());
        holder.txt_commend.setText(assessmentList.get(position).getCommend());
        holder.txt_rating.setText(assessmentList.get(position).getRating());
        holder.txt_time.setText(assessmentList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return assessmentList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView txt_date, txt_commend, txt_rating, txt_time;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_commend = itemView.findViewById(R.id.txt_commend);
            txt_rating = itemView.findViewById(R.id.txt_rating);
            txt_time = itemView.findViewById(R.id.txt_time);
        }
    }
}

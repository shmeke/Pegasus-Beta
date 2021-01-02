package com.sebastianstext.pegasusbeta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sebastianstext.pegasusbeta.DataStorage.WorkoutDates;
import com.sebastianstext.pegasusbeta.R;

import java.util.List;

public class DateSlider extends RecyclerView.Adapter<DateSlider.DateViewHolder> {
    private List<WorkoutDates> datesList;
    Context context;

    public DateSlider(List<WorkoutDates> datesList, Context context){
        this.datesList = datesList;
        this.context = context;
    }

    @NonNull
    @Override
    public DateSlider.DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View datesView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dateslider_item, parent, false);
        DateViewHolder dvh = new DateViewHolder(datesView);
        return dvh;
    }

    @Override
    public void onBindViewHolder(@NonNull DateSlider.DateViewHolder holder, int position) {
        holder.dateItem.setText(String.valueOf(datesList.get(position).getWorkoutDate()));

    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    public class DateViewHolder extends RecyclerView.ViewHolder{
        TextView dateItem;
        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateItem = itemView.findViewById(R.id.datevalue);
        }
    }
}

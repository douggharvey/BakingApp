package com.douglasharvey.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.douglasharvey.bakingapp.R;
import com.douglasharvey.bakingapp.models.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {

    public StepAdapter(ArrayList<Step> stepsList) {
        this.stepsList = stepsList;
    }

    private ArrayList<Step> stepsList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        Step step;
        @BindView(R.id.tv_step_number)
        TextView tvStepNumber;
        @BindView(R.id.tv_step_description)
        TextView tvStepDescription;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View inflatedView = inflater.inflate(R.layout.steps_list_item, parent, false);

        return new ViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.step = stepsList.get(position);
        holder.tvStepNumber.setText(Integer.toString(holder.step.getId()) +")");
        holder.tvStepDescription.setText(holder.step.getShortDescription());
    }

    @Override
    public int getItemCount() {
        if (stepsList == null) return 0;
        else return stepsList.size();
    }

}

package com.example.locationalarm;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmViewHolder> {
    private ArrayList<Alarm> alarms;

    AlarmListAdapter(ArrayList<Alarm> _data) {
        this.alarms = _data;
    }

    void add(Alarm _alarm) {
        alarms.add(_alarm);
        notifyItemInserted(alarms.size() - 1);
    }

    ArrayList<Alarm> getAlarms() {
        return this.alarms;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.alarm_item, parent, false);

        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlarmViewHolder holder, int position) {
        Alarm alarm = alarms.get(position);
        holder.setAlarm(alarm);

        holder.switchAlarmView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", " " + isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }
}

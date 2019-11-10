package com.example.locationalarm.alarm.ui.alrmlistfragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationalarm.R;
import com.example.locationalarm.alarm.Alarm;

import java.util.ArrayList;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmViewHolder> {
    private ArrayList<Alarm> alarms;

    AlarmListAdapter(ArrayList<Alarm> _data) {
        alarms = _data;
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
        holder.setAlarm(alarms.get(position));

        holder.switchAlarmView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", " " + isChecked);
                //TODO update alarm state
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public void setAlarms(ArrayList<Alarm> alarms) {
        this.alarms = alarms;
    }
}

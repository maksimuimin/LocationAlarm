package com.example.locationalarm.alarm.ui.alrmlistfragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationalarm.R;
import com.example.locationalarm.alarm.Alarm;

import java.util.ArrayList;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmViewHolder> {
    private static final String TAG = "AlarmListAdapter";
    private LiveData<ArrayList<Alarm>> alarms;

    AlarmListAdapter(LiveData<ArrayList<Alarm>> _data) {
        this.alarms = _data;
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
        ArrayList<Alarm> alarmList = alarms.getValue();
        if (alarmList == null) {
            Log.wtf(TAG, "onBindViewHolder with null alarmList");
            return;
        }
        holder.setAlarm(alarmList.get(position));

        holder.switchAlarmView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", " " + isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        ArrayList<Alarm> alarmList = alarms.getValue();
        if (alarmList == null) {
            return 0;
        }
        return alarmList.size();
    }
}

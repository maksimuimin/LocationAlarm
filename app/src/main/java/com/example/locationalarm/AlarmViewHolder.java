package com.example.locationalarm;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class AlarmViewHolder extends RecyclerView.ViewHolder {
    private TextView alarmNameView;
    private TextView addressView;
    Switch switchAlarmView;

    AlarmViewHolder(@NonNull View itemView) {
        super(itemView);

        alarmNameView = itemView.findViewById(R.id.name);
        addressView = itemView.findViewById(R.id.address);
        switchAlarmView = itemView.findViewById(R.id.switch_alarm);
    }

    void setAlarm(Alarm alarm) {
        alarmNameView.setText(alarm.getName());
        addressView.setText(alarm.getAddress());
        switchAlarmView.setChecked(alarm.getIsActive());
    }
}

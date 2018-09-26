package com.ghootenk.user.farmbot;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextClock;
import android.widget.TextView;

import com.ghootenk.user.farmbot.utils.TimePickerFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmActivity extends AppCompatActivity implements TimePickerFragment.DialogTimeListener{

    private TextView tvRepeatingTime;
    private SwitchCompat btnSwitch;
    private FloatingActionButton fab;
    private Context c;

    private AlarmReceiver alarmReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        TextClock textClock = findViewById(R.id.clock);

        fab = findViewById(R.id.fab);
        tvRepeatingTime = findViewById(R.id.tv_repeating_alarm);
        btnSwitch = findViewById(R.id.btn_switch);

        alarmReceiver = new AlarmReceiver();
        setAlarm();
    }

    public void backpestisida(View view) {
        Intent intent = new Intent(this, PengusirBurungActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDialogTimeSet(String tag, int hourOfDay, int minute) {
        // Siapkan time formatter-nya terlebih dahulu
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tvRepeatingTime.setText(dateFormat.format(calendar.getTime()));

    }

    final String TIME_PICKER_REPEAT_TAG = "TimePickerRepeat";

    public void setAlarm(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(), TIME_PICKER_REPEAT_TAG);
            }
        });

        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    String repeatTime = tvRepeatingTime.getText().toString();
                    alarmReceiver.setRepeatingAlarm(getBaseContext(), AlarmReceiver.TYPE_REPEATING,
                            repeatTime, "Set Alarm Success");
                } else {
                    alarmReceiver.cancelAlarm(c,AlarmReceiver.TYPE_REPEATING);
                }
            }
        });
    }
}

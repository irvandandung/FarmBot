package com.ghootenk.user.farmbot;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.ghootenk.user.farmbot.sync.AsyncResult;
import com.ghootenk.user.farmbot.sync.DownloadWebpageTask;
import com.ghootenk.user.farmbot.sync.HTTPAsyncGPIO;
import com.ghootenk.user.farmbot.utils.TimePickerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.ghootenk.user.farmbot.BuildConfig.DEVICE_IC;
import static com.ghootenk.user.farmbot.BuildConfig.URL_API;

public class AlarmActivity extends AppCompatActivity implements TimePickerFragment.DialogTimeListener{

    private TextView tvRepeatingTime;
    private SwitchCompat btnSwitch;
    private FloatingActionButton fab;
    private Context c;
    private Button btnRepeating;
    private AlarmReceiver alarmReceiver;

    private String mURL = URL_API + "/" + DEVICE_IC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        TextClock textClock = findViewById(R.id.clock);

        fab = findViewById(R.id.fab);
        tvRepeatingTime = findViewById(R.id.tv_repeating_alarm);
        btnSwitch = findViewById(R.id.btn_switch);
        btnRepeating = findViewById(R.id.btn_set_repeating_alarm);

        alarmReceiver = new AlarmReceiver();
        setAlarm();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //cek status lampu
        cekStatus();

        //cek realtime status lampu
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cekStatus();
                Log.d("MainActivity", "runDelay");
            }
        }, 3000);
    }

    private void cekStatus() {new DownloadWebpageTask(new AsyncResult() {
        @Override
        public void onResult(JSONObject object) {
            getStatus(object);
        }
    }).execute( mURL + "/gpio/data");
    }

    private void getStatus(JSONObject object) {
        try {
            JSONObject rows = object.getJSONObject("data");
            JSONObject result = rows.getJSONObject("result");
            String pin_5 = result.getString("5");
            Log.d("coba", pin_5);
            if (pin_5.equals("0")){
                Toast.makeText(getBaseContext(), "Relay Menyala", Toast.LENGTH_SHORT).show();

            }
            if (pin_5.equals("1")){
                Toast.makeText(getBaseContext(), "Relay Mati", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(getBaseContext(), "Pembacaan status gagal...", Toast.LENGTH_SHORT).show();
//                tombolmati.setVisibility(View.GONE);
//                tombol.setVisibility(View.VISIBLE);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
//            tombolmati.setVisibility(View.GONE);
//            tombol.setVisibility(View.VISIBLE);
//            Toast.makeText(getBaseContext(), "Tidak ada koneksi", Toast.LENGTH_SHORT).show();
        }
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

        btnRepeating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String repeatTime = tvRepeatingTime.getText().toString();
                alarmReceiver.setRepeatingAlarm(getApplicationContext(), AlarmReceiver.TYPE_REPEATING,
                        repeatTime, "Message");
            }
        });

//        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    String repeatTime = tvRepeatingTime.getText().toString();
//                    alarmReceiver.setRepeatingAlarm(getBaseContext(), AlarmReceiver.TYPE_REPEATING,
//                            repeatTime, "Set Alarm Success");
//                } else {
//                    alarmReceiver.cancelAlarm(c,AlarmReceiver.TYPE_REPEATING);
//                }
//            }
//        });
    }
}

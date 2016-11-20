package id.sch.smktelkom_mlg.project.xirpl306152433.istiqomahapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    final static int req = 1;
    TimePicker picker;
    Button buttonStart;
    Button buttonCancel;
    TextView textPrompt;
    TimePickerDialog timePickerYeah;
    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                calSet.add(Calendar.DATE, 1);
            }

            setAlarm(calSet);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textPrompt = (TextView) findViewById(R.id.promptID);
        buttonStart = (Button) findViewById(R.id.btSet);

        buttonStart.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                textPrompt.setText("");
                openTimerPicker(false);
            }
        });

        buttonCancel = (Button) findViewById(R.id.btCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

    }

    private void cancelAlarm() {
        textPrompt.setText("\n\n" + "Alarm Telah Dibatalkan!");

        Intent intent = new Intent(getBaseContext(), Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), req, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void openTimerPicker(boolean is24jam) {
        Calendar kalender = Calendar.getInstance();
        timePickerYeah = new TimePickerDialog(
                MainActivity.this,
                timeSetListener,
                kalender.get(Calendar.HOUR_OF_DAY),
                kalender.get(Calendar.MINUTE),
                true);

        timePickerYeah.setTitle("Set Alarm Anda");
        timePickerYeah.show();
    }

    public void setAlarm(Calendar targetCal) {
        textPrompt.setText("\n\n" + "Alarm Telah Diatur Pada: " + targetCal.getTime());

        Intent intent = new Intent(getBaseContext(), Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), req, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
    }
}

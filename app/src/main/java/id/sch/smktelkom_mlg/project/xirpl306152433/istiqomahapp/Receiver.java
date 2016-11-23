package id.sch.smktelkom_mlg.project.xirpl306152433.istiqomahapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

/**
 * Created by Dayinta-PC on 11/20/2016.
 */

public class Receiver extends BroadcastReceiver {
    MediaPlayer mp;

    @Override
    public void onReceive(Context c, Intent arg1) {
        mp = MediaPlayer.create(c, R.raw.mecca_56_22);
        mp.start();
        Toast.makeText(c, "Alarm Telah Menyala", Toast.LENGTH_SHORT).show();
    }
}


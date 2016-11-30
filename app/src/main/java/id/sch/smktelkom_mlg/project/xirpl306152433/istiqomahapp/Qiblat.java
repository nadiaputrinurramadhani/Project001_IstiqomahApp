package id.sch.smktelkom_mlg.project.xirpl306152433.istiqomahapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Qiblat extends AppCompatActivity {
    private static final String TAG = "Compass";

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SampleView kiblat;
    private float[] mValues;
    private double lonMosque;
    private double latMosque;
    private LocationManager lom;
    private LocationListener locListenD;
    private Button bMulai;

    //for finding north direct
    private final SensorEventListener mListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {

            bMulai = (Button) findViewById(R.id.buttonMulai);

            if (Config.DEBUG) Log.d(TAG,
                    "sensorChanged (" + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + ")");
            mValues = event.values;
            if (kiblat != null) {
                kiblat.invalidate();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        kiblat = new SampleView(this);
        setContentView(kiblat);

        // for calling the gps

        LocationManager lom = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location loc = lom.getLastKnownLocation("gps");

        // ask the Location Manager to send us location updates
        locListenD = new DispLocListener();

        // Requesting Permissions at Run TIme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                return;
            } else {
                configureButton();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();

                return;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void configureButton() {
        bMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lom.requestLocationUpdates("gps", 30000L, 10.0f, locListenD);
            }
        });
    }

    //finding kabah location
    private double QiblaCount(double lngMasjid, double latMasjid) {
        double lngKabah = 39.826206;//39.82616111;
        double latKabah = 21.422487;//21.42250833;
        double lKlM = (lngKabah - lngMasjid);
        double sinLKLM = Math.sin(lKlM * 2.0 * Math.PI / 360);
        double cosLKLM = Math.cos(lKlM * 2.0 * Math.PI / 360);
        double sinLM = Math.sin(latMasjid * 2.0 * Math.PI / 360);
        double cosLM = Math.cos(latMasjid * 2.0 * Math.PI / 360);
        double tanLK = Math.tan(latKabah * 2 * Math.PI / 360);
        double denominator = (cosLM * tanLK) - sinLM * cosLKLM;

        double Qibla;
        double direction;

        Qibla = Math.atan2(sinLKLM, denominator) * 180 / Math.PI;
        direction = Qibla < 0 ? Qibla + 360 : Qibla;
        return direction;

    }


    @Override
    protected void onResume() {
        if (Config.DEBUG) Log.d(TAG, "onResume");
        super.onResume();

        mSensorManager.registerListener(mListener, mSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    protected void onStop() {
        if (Config.DEBUG) Log.d(TAG, "onStop");
        mSensorManager.unregisterListener(mListener);
        super.onStop();
    }

    private class SampleView extends View {
        private Paint mPaint = new Paint();
        private Path mPath = new Path();
        private boolean mAnimate;


        public SampleView(Context context) {
            super(context);

            mPath.moveTo(0, -100);
            mPath.lineTo(20, 120);
            mPath.lineTo(0, 100);
            mPath.lineTo(-20, 120);
            mPath.close();
        }

        protected void onDraw(Canvas canvas) {
            Paint paint = mPaint;

            this.setBackgroundResource(R.drawable.kakbah);

            paint.setAntiAlias(true);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            int w = canvas.getWidth();
            int h = canvas.getHeight();
            int cx = w / 2;
            int cy = h / 2;
            float Qibla = (float) QiblaCount(lonMosque, latMosque);
            canvas.translate(cx, cy);
            if (mValues != null) {
                canvas.rotate(-(mValues[0] + Qibla));
            }
            canvas.drawPath(mPath, mPaint);
        }

        @Override
        protected void onAttachedToWindow() {
            mAnimate = true;
            if (Config.DEBUG) Log.d(TAG, "onAttachedToWindow. mAnimate=" + mAnimate);
            super.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            mAnimate = false;
            if (Config.DEBUG) Log.d(TAG, "onDetachedFromWindow. mAnimate=" + mAnimate);
            super.onDetachedFromWindow();
        }

    }

    private class DispLocListener implements LocationListener {
        public void onLocationChanged(Location loc) {
            latMosque = loc.getLatitude();
            lonMosque = loc.getLongitude();
        }

        public void onProviderDisabled(String provider) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}

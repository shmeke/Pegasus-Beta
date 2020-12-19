package com.sebastianstext.pegasusbeta.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.sebastianstext.pegasusbeta.Listeners.startListener;
import com.sebastianstext.pegasusbeta.Listeners.stopListener;
import com.sebastianstext.pegasusbeta.SensorDetectors.WorkoutDetector;

import java.text.ParseException;

public class StartWorkoutService extends Service implements SensorEventListener, startListener, stopListener {

    private WorkoutDetector startDetector;
    SensorManager sm;
    Sensor sensor;
    private boolean isServiceStarted = false;


    public void onCreate(){
        super.onCreate();
        startDetector = new WorkoutDetector();
        startDetector.registerStartListener(this);
        startDetector.registerStopListener(this);
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(StartWorkoutService.this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, startId, startId);

        return START_STICKY;
    }

    public void onTaskRemoved(Intent rootIntent){
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +100, restartServicePI);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void startSession(boolean startCommand) {
        if(!isServiceStarted){
            startWorkout();
            isServiceStarted = true;
        }
    }
    @Override
    public void stopCommand(boolean StopCommand) {
        if(isServiceStarted){
            stopWorkout();
            isServiceStarted = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            try {
                startDetector.detectStart(sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void startWorkout(){
        Intent i = new Intent(StartWorkoutService.this, WorkoutService.class);
        startService(i);
        Toast.makeText(StartWorkoutService.this, "Ridpass startat.", Toast.LENGTH_SHORT).show();
    }

    public void stopWorkout(){
        Intent i = new Intent(StartWorkoutService.this, WorkoutService.class);
        stopService(i);
        Toast.makeText(StartWorkoutService.this, "Ridpass avslutat.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}

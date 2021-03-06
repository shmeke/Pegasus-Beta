package com.sebastianstext.pegasusbeta.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.sebastianstext.pegasusbeta.DataStorage.Horse;
import com.sebastianstext.pegasusbeta.Listeners.energyListener;
import com.sebastianstext.pegasusbeta.Listeners.rotationListener;
import com.sebastianstext.pegasusbeta.Listeners.stepListener;
import com.sebastianstext.pegasusbeta.SensorDetectors.RotationDetector;
import com.sebastianstext.pegasusbeta.SensorDetectors.StepDetector;
import com.sebastianstext.pegasusbeta.SensorDetectors.WorkoutDetector;
import com.sebastianstext.pegasusbeta.Utils.DelayUtil;
import com.sebastianstext.pegasusbeta.Utils.RequestHandler;
import com.sebastianstext.pegasusbeta.Utils.SharedPrefManager;
import com.sebastianstext.pegasusbeta.Utils.URLs;
import com.sebastianstext.pegasusbeta.DataStorage.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.O)
public class WorkoutService extends Service implements stepListener, rotationListener, SensorEventListener, energyListener {
    public WorkoutService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    SensorManager sm;
    Sensor accel;
    Sensor rotation;
    private StepDetector simpleStepDetector;
    private WorkoutDetector workoutDetector;
    private RotationDetector turnDetect;
    private int numSteps, movementType, stepsTrav, stepsSkritt, stepsGallop;
    private int leftTurn, rightTurn, fullLeftVolt, fullRightVolt,quarterVoltLeft, quarterVoltRight, oldStepCount, stopCount, getTurnDirection;
    private double meters, metersTrav, metersSkritt, metersGallop, km, kps;
    private float oldVelEst, newVelEst, energyVal;
    private long stepTime;
    float stepTimeFin;
    private String horseName;
    private static final int RESET_TIMER = 1000;
    private static final int FINAL_RESET_TIMER = 5000;
    DelayUtil delay = new DelayUtil();
    @SuppressLint("SimpleDateFormat")
    Time start;
    Time oldTimeLeft = new Time(System.currentTimeMillis());
    Time oldTimeRight = new Time(System.currentTimeMillis());
    Horse horse;
    User user;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, startId, startId);

        return START_STICKY;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        rotation = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        turnDetect = new RotationDetector();
        turnDetect.registerListener(this);
        workoutDetector = new WorkoutDetector();
        workoutDetector.registerEneryListener(this);
        user = SharedPrefManager.getInstance(WorkoutService.this).getUser();
        horse = SharedPrefManager.getInstance(WorkoutService.this).getHorse();

        sm.registerListener(WorkoutService.this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(WorkoutService.this, rotation, SensorManager.SENSOR_DELAY_NORMAL);
        start = new Time(System.currentTimeMillis());




    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            try {
                simpleStepDetector.updateAccel(
                        sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION){
            try {
                turnDetect.detectTurning(
                        sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

       //
        //delay.delay(1000);



    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    public void detectTurn(float currentDegree, float oldDegree, long turnTime, long lastTurnTime) {


        if(oldDegree > currentDegree + 6.5f){
            leftTurn++;
        }
        else if(oldDegree < currentDegree - 6.5f){
            rightTurn++;
        }



        switch (leftTurn){
            case 5:
                getTurnDirection = 1;
                quarterVoltLeft++;
                leftTurn = 0;
                break;

            default:
                if((turnTime - lastTurnTime) / 1000000 > RESET_TIMER){
                    leftTurn = 0;
                }
                break;
        }

        switch (rightTurn){
            case 5:
                getTurnDirection = 2;
                quarterVoltRight++;
                rightTurn = 0;
                break;


            default:
                if((turnTime - lastTurnTime) / 1000000 > RESET_TIMER){
                    rightTurn = 0;
                }
                break;
        }

        switch (getTurnDirection) {

            case 1:
                Time fiveIncomingLeft = new Time(System.currentTimeMillis());

                long timeDiffLeft = fiveIncomingLeft.getTime() - oldTimeLeft.getTime();
                int secLeft = (int) (timeDiffLeft * 1000);

                if(quarterVoltLeft == 4 && secLeft < FINAL_RESET_TIMER) {
                    fullLeftVolt++;
                    quarterVoltLeft = 0;
                }

                oldTimeLeft = fiveIncomingLeft;

                break;

            case 2:
                Time fiveIncomingRight = new Time(System.currentTimeMillis());


                long timeDiffRight = fiveIncomingRight.getTime() - oldTimeRight.getTime();
                int secRight = (int) (timeDiffRight * 1000);

                if(quarterVoltRight == 4 && secRight < FINAL_RESET_TIMER) {
                    fullRightVolt++;
                    quarterVoltRight = 0;
                }

                oldTimeRight = fiveIncomingRight;

                break;



            default:

                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void tempWorkout(long timeNs,  long lastStepTimeNs, float velocityEstimate, float oldVelocityEstimate) {
        numSteps++;
        long startStep = lastStepTimeNs;
        oldVelEst = oldVelocityEstimate;

        long stopStep = timeNs;
        newVelEst = velocityEstimate;

        stepTime = stopStep - startStep;
        stepTimeFin = stepTime / 1000000;


        if(newVelEst > 14 && oldVelEst < -10){
            movementType = 1;
        }
        else if(newVelEst < 7 && oldVelEst > -5){
            movementType = 2;
        }
        else if(newVelEst > 7 && newVelEst < 14 && oldVelEst > -10){
            movementType = 3;
        }

        switch(movementType){
            case 1:
                stepsTrav++;
                metersTrav = (stepsTrav * 1.2);
                break;

            case 2:
                stepsSkritt++;
                metersSkritt = (stepsSkritt * 0.95);
                break;

            case 3:
                stepsGallop++;
                metersGallop = (stepsGallop * 1.9);
                break;

            default:

                break;
        }

        meters = (metersGallop + metersSkritt + metersTrav);
        km = (meters * 0.001);
        Time still = new Time(System.currentTimeMillis());

        //Time startTime = (Time) timeFormat.parse(String.valueOf(start));
        //Time stillTime = (Time) timeFormat.parse(String.valueOf(still));

        long timeElapsed = still.getTime() - start.getTime();

        if(oldStepCount < numSteps && stepTimeFin > 3000){
            stopCount++;

        }
        oldStepCount = numSteps;

        kps = (km) / (timeElapsed * 3600);
        saveSensorValues();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        updateWorkoutSession();
    }




    public void updateWorkoutSession(){
        final String id = String.valueOf(user.getId());
        final String username = user.getUsername();
        final String horsename = SharedPrefManager.getInstance(getApplicationContext()).getSpinnerItem();
        final String metersTravelled = String.valueOf(meters);
        final String averageSpeed = String.valueOf(kps);
        final String stops = String.valueOf(stopCount);
        final String distGallopp = String.valueOf(metersGallop);
        final String distSkritt = String.valueOf(metersSkritt);
        final String distTrav = String.valueOf(metersTrav);
        final String voltRight = String.valueOf(fullRightVolt);
        final String voltLeft = String.valueOf(fullLeftVolt);

        @SuppressLint("StaticFieldLeak")
        class UpdateWorkoutSession extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("username", username);
                params.put("horsename", horsename);
                params.put("meters", metersTravelled);
                params.put("speed", averageSpeed);
                params.put("stops", stops);
                params.put("distgallopp", distGallopp);
                params.put("distskritt", distSkritt);
                params.put("disttrav", distTrav);
                params.put("voltleft", voltLeft);
                params.put("voltright", voltRight);

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_WORKOUT, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    Log.i("tagconvertstr", s);
                    JSONObject jObj = new JSONObject(s);

                    if(!jObj.getBoolean("error")){

                        Toast.makeText(getApplicationContext(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }

        UpdateWorkoutSession uws = new UpdateWorkoutSession();
        uws.execute();
    }

    public void saveSensorValues(){
        final String Id = String.valueOf(user.getId());
        final String Username = user.getUsername();
        final String Horsename = SharedPrefManager.getInstance(getApplicationContext()).getSpinnerItem();
        final String NewVelocity = String.valueOf(newVelEst);
        final String OldVelocity = String.valueOf(oldVelEst);
        final String Steptime = String.valueOf(stepTimeFin);
        final String EnergyVal = String.valueOf(energyVal);


        class SaveSensorValues extends AsyncTask<Void, Void, String>{

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("id", Id);
                params.put("username", Username);
                params.put("horsename", Horsename);
                params.put("newvelocity", NewVelocity);
                params.put("oldvelocity", OldVelocity);
                params.put("steptime", Steptime);
                params.put("energyVal", EnergyVal);

                return requestHandler.sendPostRequest(URLs.URL_SENSORVALS, params);
            }
        }
        SaveSensorValues sv = new SaveSensorValues();
        sv.execute();
    }


    @Override
    public void energyTrans(float Energy) {
        energyVal = Energy;
    }
}

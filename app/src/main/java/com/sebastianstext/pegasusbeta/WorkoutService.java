package com.sebastianstext.pegasusbeta;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.sebastianstext.pegasusbeta.DataStorage.Horse;
import com.sebastianstext.pegasusbeta.Listeners.rotationListener;
import com.sebastianstext.pegasusbeta.Listeners.stepListener;
import com.sebastianstext.pegasusbeta.SensorDetectors.RotationDetector;
import com.sebastianstext.pegasusbeta.SensorDetectors.StepDetector;
import com.sebastianstext.pegasusbeta.Utils.RequestHandler;
import com.sebastianstext.pegasusbeta.Utils.SharedPrefManager;
import com.sebastianstext.pegasusbeta.Utils.URLs;
import com.sebastianstext.pegasusbeta.DataStorage.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.O)
public class WorkoutService extends Service implements stepListener, rotationListener, SensorEventListener {
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
    private RotationDetector turnDetect;
    private int numSteps, movementType, stepsTrav, stepsSkritt, stepsGallop;
    private int leftTurn, rightTurn, fullLeftVolt, fullRightVolt,quarterVoltLeft, quarterVoltRight, oldStepCount, stopCount, getTurnDirection;
    private double meters, metersTrav, metersSkritt, metersGallop, km, kps;
    private float oldVelEst, newVelEst;
    private long stepTime;
    float stepTimeFin;
    private static final int RESET_TIMER = 1000;
    private static final int FINAL_RESET_TIMER = 5000;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    Date start;
    Date oldTimeLeft;
    Date oldTimeRight;
    Horse horse;
    User user;

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
        user = SharedPrefManager.getInstance(WorkoutService.this).getUser();
        horse = SharedPrefManager.getInstance(WorkoutService.this).getHorse();

        sm.registerListener(WorkoutService.this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(WorkoutService.this, rotation, SensorManager.SENSOR_DELAY_NORMAL);
        start = new Date(System.currentTimeMillis());



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
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void detectTurn(float currentDegree, float oldDegree, long turnTime, long lastTurnTime) throws ParseException {


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
                Date fiveIncomingLeft = new Date(System.currentTimeMillis());

                Date timeNewLeft = (Date) timeFormat.parse(String.valueOf(fiveIncomingLeft));
                Date timeOldLeft = (Date) timeFormat.parse(String.valueOf(oldTimeLeft));
                long timeDiffLeft = timeNewLeft.getTime() - timeOldLeft.getTime();
                int secLeft = (int) (timeDiffLeft * 1000);

                if(quarterVoltLeft == 4 && secLeft < FINAL_RESET_TIMER) {
                    fullLeftVolt++;
                    quarterVoltLeft = 0;
                }

                oldTimeLeft = fiveIncomingLeft;

                break;

            case 2:
                Date fiveIncomingRight = new Date(System.currentTimeMillis());

                Date timeNewRight = (Date) timeFormat.parse(String.valueOf(fiveIncomingRight));
                Date timeOldRight = (Date) timeFormat.parse(String.valueOf(oldTimeLeft));
                long timeDiffRight = timeNewRight.getTime() - timeOldRight.getTime();
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
    public void tempWorkout(long timeNs,  long lastStepTimeNs, float velocityEstimate, float oldVelocityEstimate) throws ParseException {
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
        Date still = new Date(System.currentTimeMillis());
        String Start = String.valueOf(start);
        String Still = String.valueOf(still);
        Date startTime = (Date) timeFormat.parse(Start);
        Date stillTime = (Date) timeFormat.parse(Still);

        long timeElapsed = stillTime.getTime() - startTime.getTime();

        if(oldStepCount < numSteps && stepTimeFin > 3000){
            stopCount++;

        }
        oldStepCount = numSteps;

        kps = (km) / (timeElapsed * 3600);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateWorkoutSession();
    }

    public void updateWorkoutSession(){
        final String id = String.valueOf(user.getId());
        final String username = user.getUsername();
        final String horsename = horse.getName();
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
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        UpdateWorkoutSession uws = new UpdateWorkoutSession();
        uws.execute();
    }

}

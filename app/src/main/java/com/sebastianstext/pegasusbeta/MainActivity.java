package com.sebastianstext.pegasusbeta;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sebastianstext.pegasusbeta.DataStorage.Workout;
import com.sebastianstext.pegasusbeta.DataStorage.WorkoutDates;
import com.sebastianstext.pegasusbeta.Services.StartWorkoutService;
import com.sebastianstext.pegasusbeta.Services.WorkoutService;
import com.sebastianstext.pegasusbeta.UserRelatedClasses.AdditionalUserInfoActivity;
import com.sebastianstext.pegasusbeta.UserRelatedClasses.LoginActivity;
import com.sebastianstext.pegasusbeta.Utils.ExpandableListAdapter;
import com.sebastianstext.pegasusbeta.DataStorage.Horse;
import com.sebastianstext.pegasusbeta.Utils.RequestHandler;
import com.sebastianstext.pegasusbeta.Utils.SharedPrefManager;
import com.sebastianstext.pegasusbeta.Utils.URLs;
import com.sebastianstext.pegasusbeta.DataStorage.User;
import com.sebastianstext.pegasusbeta.adapter.DateSlider;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    User user;
    Workout workout;
    EditText txtDate;
    TextView txtWorkout, txtY;
    ImageButton btnDropdown;
    Button start, stop;
    Calendar calendar;
    DatePickerDialog Dpd;
    private Spinner horseSpinner, spinnerWorkout;
    ArrayList<String> HorseList = new ArrayList<>();
    ArrayList<String> WorkoutList = new ArrayList<>();
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;

    private String spinnerItem;
    private List<WorkoutDates> datesList = new ArrayList<>();
    private RecyclerView dateRecycler;
    private DateSlider dateSlider;


    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = SharedPrefManager.getInstance(this).getUser();
        workout = SharedPrefManager.getInstance(this).getWorkout();
        horseSpinner = findViewById(R.id.spinnerHorse);
        spinnerWorkout = findViewById(R.id.spinnerWorkout);
        txtWorkout = findViewById(R.id.txtWorkout);
        txtDate = findViewById(R.id.editTextDate);


        dateRecycler = findViewById(R.id.dateslider);
        dateRecycler.addItemDecoration(new DividerItemDecoration(MainActivity.this, LinearLayoutManager.HORIZONTAL));
        dateSlider = new DateSlider(datesList, getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        dateRecycler.setLayoutManager(linearLayoutManager);
        dateRecycler.setAdapter(dateSlider);

        pupulateDateSlider();


        txtDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                getWorkout();
            }
        });


       HorseList = SharedPrefManager.getInstance(getApplicationContext()).getArrayList("horselist");


    if(HorseList != null){
        ArrayAdapter<String> spinnerHorseArray = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, HorseList);
        spinnerHorseArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        horseSpinner.setAdapter(spinnerHorseArray);
    }




    horseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            spinnerItem = horseSpinner.getSelectedItem().toString();
            SharedPrefManager.getInstance(getApplicationContext()).saveSpinnerItem(spinnerItem);

            getHorseInformation();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    });
    }
    public void pupulateDateSlider(){
        Date startDate = new Date();
        Date endDate = new Date();
        endDate.getTime();

        Calendar c = Calendar.getInstance();
        startDate.setDate(2020-11-11);

        for(Date date = startDate; !date.after(endDate); c.setTime(date), c.add(Calendar.DATE,1)){
                    WorkoutDates vals = new WorkoutDates(String.valueOf(c));
            datesList.add(vals);
        }


    }


    public void onPause() {
        super.onPause();

        Intent i = new Intent(MainActivity.this, StartWorkoutService.class);
        startService(i);
        Toast.makeText(MainActivity.this, "Service Started", Toast.LENGTH_SHORT).show();
    }


    public void putValues(){
        ArrayList<String> countWorkouts = new ArrayList<>();

        WorkoutList = SharedPrefManager.getInstance(getApplicationContext()).getWorkoutList("workoutlist");
        int listSize = WorkoutList.size();
        int i;
        for(i = 0; i < listSize; i++){
            int passID = i + 1;
            countWorkouts.add("Pass: " + passID);
        }

        if(countWorkouts != null){
            ArrayAdapter<String> workoutsArray = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countWorkouts);
            workoutsArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerWorkout.setAdapter(workoutsArray);
        }

        spinnerWorkout.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initData(i);
                listView = findViewById(R.id.listdist);

                listAdapter = new ExpandableListAdapter(MainActivity.this, listDataHeader, listHash);
                listView.setAdapter(listAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        txtWorkout.setText("Du har gjort " + WorkoutList.size() + " ridpass detta datum.");






    }

    private void initData(int i) {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("Distans");
        listDataHeader.add("Volter");
        listDataHeader.add("Andra");

        try{
            JSONObject jObj = new JSONObject(WorkoutList.get(i));
            Log.i("tagconvertstr", String.valueOf(jObj));


            List<String> dist = new ArrayList<>();
            dist.add("Total distans: " + jObj.getInt("meters"));
            dist.add("Distans skrittat: " + jObj.getInt("metersskritt"));
            dist.add("Distans travat: " + jObj.getInt("meterstrav"));
            dist.add("Distans galopperat: " + jObj.getInt("metersgallopp"));

            List<String> volt = new ArrayList<>();
            volt.add("Antal höger volter: " + jObj.getInt("voltright"));
            volt.add("Antal vänster volter: " + jObj.getInt("voltleft"));

            List<String> others = new ArrayList<>();
            others.add("Antal stopp: " + jObj.getInt("stops"));
            others.add("Hastighet: " + jObj.getInt("speed"));

            listHash.put(listDataHeader.get(0),dist);
            listHash.put(listDataHeader.get(1),volt);
            listHash.put(listDataHeader.get(2), others);

        } catch (Exception e) {
            e.printStackTrace();
        }
 }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dropdown_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.newhorse:
                finish();
                Intent i = new Intent(MainActivity.this, AdditionalUserInfoActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                return true;
            case R.id.logout:
                finish();
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                Intent i2 = new Intent(MainActivity.this, LoginActivity.class);
                i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getHorseInformation(){
        final String HorseName =  horseSpinner.getSelectedItem().toString();
        final String Username = user.getUsername();


        class GetHorseInformation extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);

                try {

                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    //if no error in response
                    if (!obj.getBoolean("error")) {

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("horse");

                        //creating a new user object
                        Horse horse = new Horse(
                                userJson.getInt("height"),
                                userJson.getString("breed")
                        );

                        SharedPrefManager.getInstance(getApplicationContext()).putHorse(horse);

                    } else {
                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("name", HorseName);
                params.put("username", Username);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_HORSEINFO, params);
            }
        }

        GetHorseInformation ghi = new GetHorseInformation();
        ghi.execute();

    }

    public void getWorkout(){
        final String HorseName = horseSpinner.getSelectedItem().toString();
        final String Username = user.getUsername();
        final String Date = txtDate.getText().toString();


        class GetWorkout extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);

                try {

                    String json = response.replace(":\"[{", ":[{");
                    String jsonSecond = json.replace("}]\"", "}]");
                    String jsonFinal = jsonSecond.replace("\\\"", "\"");


                    JSONObject obj = new JSONObject(jsonFinal);

                    ArrayList<String> WorkoutList = new ArrayList<>();
                    //getting the user from the response

                    JSONArray jArray = obj.getJSONArray("workout");
                    for(int i = 0; i < jArray.length(); i++){
                        JSONObject jObj = jArray.getJSONObject(i);
                        WorkoutList.add(jObj.getString("workout"));
                    }

                    SharedPrefManager.getInstance(getApplicationContext()).saveWorkoutlistList(WorkoutList, "workoutlist");
                    putValues();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("horsename", HorseName);
                params.put("username", Username);
                params.put("date", Date);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_GETWORKOUT, params);
            }
        }
        GetWorkout gw = new GetWorkout();
        gw.execute();
    }


}
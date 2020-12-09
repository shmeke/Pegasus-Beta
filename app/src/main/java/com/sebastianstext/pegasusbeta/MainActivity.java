package com.sebastianstext.pegasusbeta;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.sebastianstext.pegasusbeta.UserRelatedClasses.AdditionalUserInfoActivity;
import com.sebastianstext.pegasusbeta.UserRelatedClasses.LoginActivity;
import com.sebastianstext.pegasusbeta.Utils.ExpandableListAdapter;
import com.sebastianstext.pegasusbeta.DataStorage.Horse;
import com.sebastianstext.pegasusbeta.Utils.RequestHandler;
import com.sebastianstext.pegasusbeta.Utils.SharedPrefManager;
import com.sebastianstext.pegasusbeta.Utils.URLs;
import com.sebastianstext.pegasusbeta.DataStorage.User;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    User user;
    EditText txtDate;
    TextView txtUsername;
    ImageButton btnDropdown;
    Button start, stop;
    Calendar calendar;
    DatePickerDialog Dpd;
    private Spinner horseSpinner;
    ArrayList<String> HorseList = new ArrayList<>();
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;


    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = SharedPrefManager.getInstance(this).getUser();
        horseSpinner = findViewById(R.id.spinnerHorse);
        txtUsername = findViewById(R.id.txtUsername);
        txtDate = findViewById(R.id.editTextDate);
        btnDropdown = findViewById(R.id.buttonDropdown);
        txtUsername.setText("Inloggad som: " + user.getUsername());
        start = findViewById(R.id.buttonStart);
        stop = findViewById(R.id.buttonStop);
        listView = findViewById(R.id.listDist);
        initData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listHash);
        listView.setAdapter(listAdapter);

        btnDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                Dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {

                        txtDate.setText(mYear + "-" + (mMonth + 1) + "-" + mDay);
                    }
                }, day, month, year);
                Dpd.show();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, WorkoutService.class);
                startService(i);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, WorkoutService.class);
                stopService(i);
            }
        });
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

    }

    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("Distans");
        listDataHeader.add("Volter");
        listDataHeader.add("Andra");


        List<String> dist = new ArrayList<>();
        dist.add("Total distans:");
        dist.add("Distans skrittat:");
        dist.add("Distans travat:");
        dist.add("Distans galopperat:");

        List<String> volt = new ArrayList<>();
        volt.add("Antal höger volter:");
        volt.add("Antal vänster volter:");

        List<String> others = new ArrayList<>();
        others.add("Antal stopp:");
        others.add("Hastighet;");

        listHash.put(listDataHeader.get(0),dist);
        listHash.put(listDataHeader.get(1),volt);
        listHash.put(listDataHeader.get(2), others);

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


        class GetHorseInformation extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);

                try {

                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object
                        Horse horse = new Horse(
                                userJson.getString("name"),
                                userJson.getInt("height"),
                                userJson.getString("breed")
                        );



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

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
            }
        }

        GetHorseInformation ghi = new GetHorseInformation();
        ghi.execute();

    }

    public void getWorkout(){
        final String HorseName = horseSpinner.getSelectedItem().toString();
        final String Username = user.getUsername();


        class GetWorkout extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);

                try {

                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject vals = obj.getJSONObject("workout");

                        //creating a new user object
                        Workout workout = new Workout(
                                vals.getInt("meters"),
                                vals.getInt("metersskritt"),
                                vals.getInt("meterstrav"),
                                vals.getInt("metersgalopp"),
                                vals.getInt("stops"),
                                vals.getInt("rightvolt"),
                                vals.getInt("leftvolt")
                        );



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
                return requestHandler.sendPostRequest(URLs.URL_GETWORKOUT, params);
            }
        }
        GetWorkout gw = new GetWorkout();
        gw.execute();
    }
}
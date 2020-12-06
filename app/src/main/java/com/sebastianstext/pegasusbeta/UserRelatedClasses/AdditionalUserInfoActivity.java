package com.sebastianstext.pegasusbeta.UserRelatedClasses;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sebastianstext.pegasusbeta.MainActivity;
import com.sebastianstext.pegasusbeta.R;
import com.sebastianstext.pegasusbeta.Utils.Horse;
import com.sebastianstext.pegasusbeta.Utils.RequestHandler;
import com.sebastianstext.pegasusbeta.Utils.SharedPrefManager;
import com.sebastianstext.pegasusbeta.Utils.URLs;
import com.sebastianstext.pegasusbeta.Utils.User;
import com.sebastianstext.pegasusbeta.WorkoutService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AdditionalUserInfoActivity extends AppCompatActivity {

    EditText txtHorseName, txtHorseHeight, txtHorseRace;
    Button Submit;
    User user;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additionaluserinfo);

        txtHorseName = findViewById(R.id.editTextName);
        txtHorseHeight = findViewById(R.id.editTextHeight);
        txtHorseRace = findViewById(R.id.editTextRace);
        Submit = findViewById(R.id.btnSubmit);
        user = SharedPrefManager.getInstance(AdditionalUserInfoActivity.this).getUser();
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAdditionalInfo();
            }
        });


    }

    private void submitAdditionalInfo() {
        final String Id = String.valueOf(user.getId());
        final String Username = user.getUsername();
        final String HorseName = txtHorseName.getText().toString().trim();
        final String HorseRace = txtHorseRace.getText().toString().trim();
        final String HorseHeight = txtHorseHeight.getText().toString().trim();



        class SubmitAdditionalInfo extends AsyncTask<Void, Void, String> {


            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("id", Id);
                params.put("username", Username);
                params.put("name", HorseName);
                params.put("breed", HorseRace);
                params.put("height", HorseHeight);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_ADINFO, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion


                try {
                    Log.i("tagconvertstr", "["+s+"]");
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        JSONObject userJson = obj.getJSONObject("horse");

                        //creating a new user object
                        Horse horse = new Horse(
                                userJson.getString("name"),
                                userJson.getString("breed"),
                                userJson.getInt("height")
                        );


                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).putHorse(horse);

                        //starting the profile activity
                        finish();

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        SubmitAdditionalInfo sai = new SubmitAdditionalInfo();
        sai.execute();
    }

}

package com.sebastianstext.pegasusbeta.UserRelatedClasses;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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


        @SuppressLint("StaticFieldLeak")
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
                params.put("race", HorseRace);
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
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    ArrayList<String> HorseList = new ArrayList<>();
                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONArray jArray = new JSONArray("horse");
                        for(int i = 0; i < jArray.length(); i++){
                            JSONObject jObj = jArray.getJSONObject(i);
                            HorseList.add(jObj.getString("name"));
                        }

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).horseArray(HorseList);

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

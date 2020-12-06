package com.sebastianstext.pegasusbeta.UserRelatedClasses;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sebastianstext.pegasusbeta.MainActivity;
import com.sebastianstext.pegasusbeta.R;
import com.sebastianstext.pegasusbeta.Utils.Horse;
import com.sebastianstext.pegasusbeta.Utils.RequestHandler;
import com.sebastianstext.pegasusbeta.Utils.SharedPrefManager;
import com.sebastianstext.pegasusbeta.Utils.TinyDB;
import com.sebastianstext.pegasusbeta.Utils.URLs;
import com.sebastianstext.pegasusbeta.Utils.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText editTextUsername, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);


        //if user presses on login
        //calling the method login
        findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getHorse();

            }
        });

        //if user presses on not registered
        findViewById(R.id.textViewRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open register screen
                finish();
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    private void userLogin() {
        //first getting the values
        final String username = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();

        //validating inputs
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Please enter your username");
            editTextUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter your password");
            editTextPassword.requestFocus();
            return;
        }

        //if everything is fine


        @SuppressLint("StaticFieldLeak")
        class UserLogin extends AsyncTask<Void, Void, String> {

            ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {


                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object
                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("username"),
                                userJson.getString("email")
                        );

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        //starting the profile activity
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
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
                params.put("username", username);
                params.put("password", password);

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_LOGIN, params);
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }

    public void getHorse(){

        final String username = editTextUsername.getText().toString();

        class GetHorse extends AsyncTask<Void, Void, String>{

            public void onPreExecute(){
                super.onPreExecute();
            }

            public void onPostExecute(String h){
                super.onPostExecute(h);


        try{
            String json = h.replace(":\"[{", ":[{");
                    String jsonSecond = json.replace("}]\"", "}]");
                            String jsonFinal = jsonSecond.replace("\\\"", "\"");
                            
            Log.i("tagconvertstr", jsonFinal);
            JSONObject obj = new JSONObject(jsonFinal);

            ArrayList<String> HorseList = new ArrayList<>();
            //getting the user from the response

            JSONArray jArray = obj.getJSONArray("horse");
            for(int i = 0; i < jArray.length(); i++){
                JSONObject jObj = jArray.getJSONObject(i);
                HorseList.add(jObj.getString("name"));

            }

            SharedPrefManager.getInstance(getApplicationContext()).saveArrayList(HorseList, "horselist");
            userLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }


            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_GETHORSE, params);
            }
        }
        GetHorse ul = new GetHorse();
        ul.execute();

    }

}

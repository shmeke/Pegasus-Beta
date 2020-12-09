package com.sebastianstext.pegasusbeta.UserRelatedClasses;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.sebastianstext.pegasusbeta.MainActivity;
import com.sebastianstext.pegasusbeta.R;
import com.sebastianstext.pegasusbeta.Utils.RequestHandler;
import com.sebastianstext.pegasusbeta.Utils.SharedPrefManager;
import com.sebastianstext.pegasusbeta.Utils.URLs;
import com.sebastianstext.pegasusbeta.DataStorage.User;

import java.util.HashMap;

public class AdditionalUserInfoActivity extends AppCompatActivity {

    EditText txtHorseName, txtHorseHeight, txtHorseRace;
    Button Submit, Cancel;
    User user;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additionaluserinfo);

        txtHorseName = findViewById(R.id.editTextName);
        txtHorseHeight = findViewById(R.id.editTextHeight);
        txtHorseRace = findViewById(R.id.editTextRace);
        Submit = findViewById(R.id.btnSubmit);
        Cancel = findViewById(R.id.btnCancel);
        user = SharedPrefManager.getInstance(AdditionalUserInfoActivity.this).getUser();
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAdditionalInfo();
            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(AdditionalUserInfoActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
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

        }

        //executing the async task
        SubmitAdditionalInfo sai = new SubmitAdditionalInfo();
        sai.execute();
    }

}

package com.sebastianstext.pegasusbeta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sebastianstext.pegasusbeta.UserRelatedClasses.LoginActivity;
import com.sebastianstext.pegasusbeta.UserRelatedClasses.RegisterActivity;
import com.sebastianstext.pegasusbeta.Utils.Horse;
import com.sebastianstext.pegasusbeta.Utils.SharedPrefManager;
import com.sebastianstext.pegasusbeta.Utils.TinyDB;
import com.sebastianstext.pegasusbeta.Utils.User;
import com.sebastianstext.pegasusbeta.ui.home.HomeFragment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    User user;
    TextView txtUsername, txtLogout;
    Button buttonStart;
    Spinner horseSpinner;
    ArrayList<String> HorseList = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = SharedPrefManager.getInstance(this).getUser();
        horseSpinner = findViewById(R.id.spinnerHorse);
        txtUsername = findViewById(R.id.txtUsername);
        txtLogout = findViewById(R.id.txtLogout);
        txtUsername.setText("Inloggad som: " + user.getUsername());
        buttonStart = findViewById(R.id.buttonStart);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //if user presses on not registered
        findViewById(R.id.txtLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open register screen
                finish();
                SharedPrefManager.getInstance(getApplicationContext()).logout();
            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(MainActivity.this, WorkoutService.class);
                ContextCompat.startForegroundService(MainActivity.this, i);
            }
        });


       HorseList = SharedPrefManager.getInstance(getApplicationContext()).getArrayList("horselist");


    if(HorseList != null){
        ArrayAdapter<String> spinnerHorseArray = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, HorseList);
        spinnerHorseArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        horseSpinner.setAdapter(spinnerHorseArray);
    }







    }

}
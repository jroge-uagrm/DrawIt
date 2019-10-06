package com.jriz.drawit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Para no mstrar el navbar, pero ya lo usé en el manifest
        //getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    public void startToDraw(View view){
        Intent intent=new Intent(this,DrawArea.class);
        startActivity(intent);
    }
    public void viewHowToDraw(View view){
        Intent intent=new Intent(this,HowToDraw.class);
        startActivity(intent);
    }
}

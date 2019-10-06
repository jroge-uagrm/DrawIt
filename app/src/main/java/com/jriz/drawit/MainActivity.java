package com.jriz.drawit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
        //To do not show the navBar, but I already used in the manifest
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

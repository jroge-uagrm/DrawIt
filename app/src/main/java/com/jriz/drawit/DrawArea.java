package com.jriz.drawit;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class DrawArea extends AppCompatActivity implements View.OnTouchListener {

    private Controller controller;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        controller = new Controller(this,this);
        controller.setOnTouchListener(this);
        setContentView(controller.getView());
        controller.setNewObject(Constants.NULL);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        controller.onOptionsItemSelected(menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        controller.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.resume();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        controller.onTouch(motionEvent);
        view.invalidate();
        return true;
    }
}

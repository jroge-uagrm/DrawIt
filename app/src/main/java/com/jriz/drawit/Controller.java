package com.jriz.drawit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jriz.drawit.Structure.Object;
import com.jriz.drawit.Structure.Point;
import com.jriz.drawit.Structure.Polygon;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

class Controller implements Runnable {

    private static final String TAG="myView";
    private AppCompatActivity appCompactActivity;
    private Thread thread;
    private boolean isItOk;
    private MyView myView;
    private int W, H;
    private Point actualPoint;
    private String action;
    private double startTime;
    private Object object;
    private Point act,ant;

    Controller(Context context,AppCompatActivity newAppCompatActivity) {
        myView = new MyView(context);
        W = myView.getWidth();
        H = myView.getHeight();
        object = new Object();
        startTime = 0;
        thread = null;
        action = Constants.NULL;
        actualPoint = null;
        appCompactActivity=newAppCompatActivity;
    }

    @SuppressLint("ClickableViewAccessibility")
    void setOnTouchListener(View.OnTouchListener l) {
        myView.setOnTouchListener(l);
    }

    MyView getView() {

        return this.myView;
    }

    @Override
    public void run() {
        while (isItOk) {
            if (!myView.isValid()){
                continue;
            }
            if (!action.equals(Constants.NULL)) {
                myView.setCanvas();
                if (!action.equals(Constants.NEW)) {
                    if (action.equals(Constants.POINT)) {
                        object.addPoint(actualPoint);
                    } else {
                        if (action.equals(Constants.CLOSED)) {
                            object.setClosedLastPolygon();
                            object.finishPolygon();
                        } else {
                            if (object.distanceBetweenLastTwoPointIsShort()) {
                                object.removeLastPoint();
                                object.finishPolygon();
                            }
                        }
                    }
                }
                action = Constants.NULL;
                drawObject();
                myView.unlockCanvas();
            }
        }
    }

    void onTouch(MotionEvent e) {
        actualPoint = new Point(e.getX(), e.getY());
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            action = Constants.POINT;
        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            action = Constants.NULL;
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            double timeHoldDown = e.getEventTime() - e.getDownTime();
            double timeBetweenTouches = e.getDownTime() - startTime;
            startTime = e.getDownTime();
            if (timeHoldDown >= Constants.TIME_HOLD_DOWN) {
                action = Constants.CLOSED;
            } else if (timeBetweenTouches < Constants.TIME_TOUCHES) {
                action = Constants.OPEN;
            }
        }
        myView.invalidate();
    }

    private void drawObject() {
        Point pointA, pointB;
        for (Polygon polygon : object.polygonList) {
            pointA = polygon.getPoint((byte) 0);
            if (polygon.pointList.size() > 1) {
                for (int i = 1; i < polygon.pointList.size(); i++) {
                    pointB = polygon.getPoint((byte) i);
                    myView.drawLine(pointA, pointB);
                    pointA = pointB;
                }
                if (polygon.isClosedPolygon) {
                    pointB = polygon.getFirstPoint();
                    myView.drawLine(pointA, pointB);
                }
            }
        }
    }

    void setNewObject(String objectJson) {
        if (objectJson.equals(Constants.NULL)) {
            object = new Object();
        } else {
            Gson gson = new Gson();
            this.object = gson.fromJson(objectJson, Object.class);
        }
        action = Constants.NEW;
        ant=new Point(0,0);
    }

    private Object getObject() {
        return this.object;

    }

    private boolean isEmpty() {
        return this.object.isEmpty();
    }

    void pause() {
        isItOk = false;
        while (true) {
            try {
                thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        }
        thread = null;
    }

    void resume() {
        isItOk = true;
        thread = new Thread(this);
        thread.start();
        drawObject();
    }

    void onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.newDraw:
                setNewObject(Constants.NULL);
                showLongMessage(Constants.SHOW_READYTOSTART);
                break;
            case R.id.saveDraw:
                saveDraw();
                break;
            case R.id.openDraw:
                openDraw();
                break;
            case R.id.printDraw:
                printDraw();
                break;
            default:
                showLongMessage(Constants.SHOW_UPS);
                break;
        }
    }

    private void saveDraw() {
        if (isEmpty()) {
            showLongMessage(Constants.SHOW_EMPTYDRAW);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(myView.getContext());
            builder.setTitle(Constants.SHOW_MSGTOSAVE);
            final EditText input = new EditText(myView.getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton(Constants.TXT_SAVE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String fileName = input.getText().toString();
                    String result = tryToOpenFile(fileName);
                    if (result.equals(Constants.INTERNAL_FAILEDTOOPEN)) {
                        result = tryToSaveFile(fileName, toJson(getObject()));
                        if (result.equals(Constants.INTERNAL_FAILEDTOSAVE))
                            showLongMessage(Constants.SHOW_UPS);
                        else
                            showShortMessage(Constants.SHOW_SAVED);
                    } else
                        showLongMessage(Constants.SHOW_USEDNAME);
                }
            });
            builder.setNegativeButton(Constants.TXT_CANCEL, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }

    private String tryToSaveFile(String fileName, String contentFile) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                appCompactActivity.openFileOutput(fileName + Constants.INTERNAL_DOTTXT, Context.MODE_APPEND)
            );
            outputStreamWriter.write(contentFile);
            outputStreamWriter.close();
            return Constants.INTERNAL_OK;
        } catch (Exception e) {
            return Constants.INTERNAL_FAILEDTOSAVE;
        }

    }

    private void openDraw() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myView.getContext());
        builder.setTitle(Constants.SHOW_MSGTOOPEN);
        final EditText input = new EditText(myView.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(Constants.TXT_OPEN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String paintName = input.getText().toString();
                String resultAfterOpen = tryToOpenFile(paintName);
                if (resultAfterOpen.equals(Constants.INTERNAL_FAILEDTOOPEN))
                    showLongMessage(Constants.SHOW_DRAWNOTEXIST);
                else {
                    setNewObject(resultAfterOpen);
                    showShortMessage(Constants.SHOW_OPENED);
                }
            }
        });
        builder.setNegativeButton(Constants.TXT_CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private String tryToOpenFile(String fileName) {
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            appCompactActivity.openFileInput(fileName + ".txt")
                    )
            );
            return bufferedReader.readLine();
        } catch (Exception e) {
            return Constants.INTERNAL_FAILEDTOOPEN;
        }
    }

    private void printDraw() {
        if (isEmpty()) {
            showLongMessage(Constants.SHOW_EMPTYDRAW);
        } else {
            object.finishPolygon();
            Intent intent = new Intent(myView.getContext(), BluetoothDevices.class);
            String json_object = toJson(object);
            intent.putExtra("objetojson",json_object);
            appCompactActivity.startActivity(intent);
        }
    }

    private String toJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    private void showShortMessage(String msg) {
        Toast.makeText(myView.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void showLongMessage(String msg) {
        Toast.makeText(myView.getContext(), msg, Toast.LENGTH_LONG).show();
    }
}
package com.jriz.drawit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;
import com.jriz.drawit.Structure.Object;
import com.jriz.drawit.Structure.Point;
import com.jriz.drawit.Structure.Polygon;

class Controller implements Runnable {

    private Thread thread;
    private boolean isItOk;
    private MyView myView;
    private int W, H;
    private Point actualPoint, oldPoint;
    private String action;
    private double startTime;
    private Object object;

    public Controller(Context context) {
        myView = new MyView(context);
        W = myView.getWidth();
        H = myView.getHeight();
        object = new Object();
        startTime = 0;
        thread = null;
        action = Constants.NULL;
        oldPoint = actualPoint = null;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setOnTouchListener(View.OnTouchListener l) {
        myView.setOnTouchListener(l);
    }

    public MyView getView() {

        return this.myView;
    }

    @Override
    public void run() {
        while (isItOk) {
            if (!myView.isValid()) {
                continue;
            }
            if (!action.equals(Constants.NULL)) {
                myView.setCanvas();
                if (!action.equals(Constants.NEW)) {
                    if (action.equals(Constants.POINT)) {
                        object.addPoint(convertir(actualPoint));
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
                dibujarObjeto();
                myView.unlockCanvas();
            }
        }
    }

    public void onTouch(MotionEvent e) {
        oldPoint = actualPoint;
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

    private void dibujarObjeto() {
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

    public void setNewObject(String objectJson) {
        if (objectJson.equals(Constants.NULL)) {
            object = new Object();
        } else {
            Gson gson = new Gson();
            this.object = gson.fromJson(objectJson, Object.class);
        }
        action = Constants.NEW;
    }

    public Object getObject() {
        return this.object;

    }

    private Point convertir(Point newActPoint) {
//        float newX = (((newActPoint.x * 100) / W) - 50) * 2;
//        float newY = (((newActPoint.x * 100) / H) - 50) * (-2);
//        act = new Point(newX, newY);
//        Point p = new Point(act.x - ant.x, act.y - ant.y);
//        ant = act;
//        //return p;
        return newActPoint;
    }

    public boolean isEmpty() {
        return this.object.isEmpty();
    }

    public void pause() {
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

    public void resume() {
        isItOk = true;
        thread = new Thread(this);
        thread.start();
    }

    public void onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.newDraw:
                setNewObject(Constants.NULL);
                break;
            case R.id.openDraw:
                 
                break;
            case R.id.saveDraw:
                break;
            case R.id.printDraw:
                break;
            default:
                break;
        }
    }
}
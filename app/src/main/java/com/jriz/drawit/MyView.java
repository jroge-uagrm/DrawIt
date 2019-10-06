package com.jriz.drawit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;

import com.jriz.drawit.Structure.Point;

import java.io.OutputStream;

public class MyView extends SurfaceView {

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    public MyView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
    }

    public void setCanvas() {
        canvas = surfaceHolder.lockCanvas();
        canvas.drawARGB(255, 255, 255, 255);
    }

    public void drawLine(Point A, Point B) {
        canvas.drawLine(A.x, A.y, B.x, B.y, paint);
    }

    public boolean isValid() {
        return surfaceHolder.getSurface().isValid();
    }

    public void unlockCanvas() {
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

}
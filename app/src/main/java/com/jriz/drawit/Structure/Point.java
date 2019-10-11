package com.jriz.drawit.Structure;

public class Point {

    public float x,y;

    public Point(float x, float y){
        this.x=x;
        this.y=y;
    }
    @Override
    public String toString(){
        return Integer.toString((int)x)+","+Integer.toString((int)y);
    }
}

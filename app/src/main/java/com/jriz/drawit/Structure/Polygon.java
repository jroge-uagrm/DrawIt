package com.jriz.drawit.Structure;
import java.util.LinkedList;

public class Polygon {

    public LinkedList<Point> pointList;

    Polygon() {
        this.pointList = new LinkedList<>();
    }
    void setClosed(){
        addPoint(this.getFirstPoint());
    }
    void addPoint(Point newPoint){
        this.pointList.add(newPoint);
    }
    public Point getFirstPoint(){
        return this.getPoint((byte)0);
    }
    public Point getPoint(int index){
        return this.pointList.get(index);
    }
    void removeLastPoint(){
        this.pointList.removeLast();
    }
}

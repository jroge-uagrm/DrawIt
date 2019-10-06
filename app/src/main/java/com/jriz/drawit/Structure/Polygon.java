package com.jriz.drawit.Structure;
import java.util.LinkedList;

public class Polygon {

    public LinkedList<Point> pointList;
    public boolean isClosedPolygon;

    public Polygon(boolean isClosedPolygon) {
        this.pointList = new LinkedList<>();
        this.isClosedPolygon = isClosedPolygon;
    }
    public Polygon(LinkedList<Point> pointList, boolean isClosedPolygon){
        this.pointList =pointList;
        this.isClosedPolygon =isClosedPolygon;
    }
    public void setClosed(){
        isClosedPolygon=true;
    }
    public void addPoint(Point newPoint){
        this.pointList.add(newPoint);
    }
    public Point getFirstPoint(){
        return this.getPoint((byte)0);
    }
    public Point getPoint(byte index){
        return this.pointList.get(index);
    }
    public Point getLastPoint(){
        return getPoint((byte)(this.pointList.size()-1));
    }
    public void removeFirstPoint(){
        this.pointList.removeFirst();
    }
    public void removeLastPoint(){
        this.pointList.removeLast();
    }
}

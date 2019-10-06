package com.jriz.drawit.Structure;

import com.jriz.drawit.Constants;

import java.util.LinkedList;

public class Object {

    public LinkedList<Polygon>polygonList;
    private boolean isFinishedPoligon;

    public Object(){
        this.polygonList=new LinkedList<>();
        this.isFinishedPoligon=true;
    }
    private void addPolygon(Polygon newPolygon){
        this.polygonList.add(newPolygon);
    }
    public void removeLastPolygon(){
        this.removePolygon((byte)(this.polygonList.size()-1));
    }
    public void removePolygon(byte index){
        this.polygonList.remove(index);
    }
    public void addPoint(Point newPoint){
        if(isFinishedPoligon){
            this.addPolygon(new Polygon(false));
            isFinishedPoligon=false;
            addPoint(newPoint);
        }else{
            this.polygonList.getLast().addPoint(newPoint);
        }
    }
    //Usado para borrar una linea
    public void removeLastPoint(){
        if(polygonList.size()>0) {
            polygonList.getLast().removeLastPoint();
            if (polygonList.getLast().pointList.size() <= 1) {
                polygonList.removeLast();
                isFinishedPoligon = true;
            } else {
                isFinishedPoligon=false;
            }
        }
    }
    public void finishPolygon(){
        isFinishedPoligon=true;
    }
    public void setClosedLastPolygon(){
        setClosedPolygon((byte)(polygonList.size()-1));
    }
    public void setClosedPolygon(byte index){
        this.polygonList.get(index).setClosed();
    }

    public boolean distanceBetweenLastTwoPointIsShort() {
        Polygon auxPolygon=polygonList.getLast();
        Point pointA=auxPolygon.getPoint((byte) (auxPolygon.pointList.size()-1));
        Point pointB=auxPolygon.getPoint((byte) (auxPolygon.pointList.size()-2));
        return Math.abs(pointA.x - pointB.x)< Constants.DISTANCE_TOUCHES&&
                Math.abs(pointA.y - pointB.y)< Constants.DISTANCE_TOUCHES;
    }
    public boolean isEmpty(){
        return polygonList.size()==0;
    }
}

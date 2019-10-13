package com.jriz.drawit.Structure;

import com.jriz.drawit.Constants;

import java.util.LinkedList;

public class Object {

    public LinkedList<Polygon>polygonList;
    private boolean isFinishedPolygon;

    public Object(){
        this.polygonList=new LinkedList<>();
        this.isFinishedPolygon =true;
    }
    private void addPolygon(Polygon newPolygon){
        this.polygonList.add(newPolygon);
    }
    public void addPoint(Point newPoint){
        if(isFinishedPolygon){
            this.addPolygon(new Polygon());
            isFinishedPolygon =false;
            addPoint(newPoint);
        }else{
            this.polygonList.getLast().addPoint(newPoint);
        }
    }

    //Used to delete a line
    public void removeLastPoint(){
        if(polygonList.size()>0) {
            polygonList.getLast().removeLastPoint();
            if (polygonList.getLast().pointList.size() <= 1) {
                polygonList.removeLast();
                isFinishedPolygon = true;
            } else {
                isFinishedPolygon =false;
            }
        }
    }
    public void finishPolygon(){
        isFinishedPolygon =true;
    }
    public void setClosedLastPolygon(){
        setClosedPolygon((byte)(polygonList.size()-1));
    }
    private void setClosedPolygon(byte index){
        this.polygonList.get(index).setClosed();
    }

    public boolean distanceBetweenLastTwoPointIsShort() {
        Polygon auxPolygon=polygonList.getLast();
        Point pointA=auxPolygon.getPoint((byte) (auxPolygon.pointList.size()-1));
        return pointA.x<Constants.DISTANCE_TOUCHES&&pointA.y<Constants.DISTANCE_TOUCHES;
    }
    public boolean isEmpty(){
        return polygonList.size()==0;
    }
}

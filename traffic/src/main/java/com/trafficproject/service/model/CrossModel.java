package com.trafficproject.service.model;

import java.util.ArrayList;

public class CrossModel {
    private String crossID;
    private RoadModel upRoad;
    private RoadModel rightRoad;
    private RoadModel downRoad;
    private RoadModel leftRoad;
    private String[] roadIDList=new String[4];
    //****以下属性为了找寻路径
    public boolean isKnown;
    public float cost;
    public CrossModel preCross;  // 找路径时的前一个Cross

    public CrossModel() {}
    public CrossModel(String crossID) {
        this.crossID=crossID;
    }

    public CrossModel(String crossID, RoadModel upRoad, RoadModel rightRoad, RoadModel downRoad, RoadModel leftRoad) {
        this.crossID=crossID;
        this.upRoad=upRoad;
        this.rightRoad=rightRoad;
        this.downRoad=downRoad;
        this.leftRoad=leftRoad;
        this.roadIDList[0]=upRoad.getRoadID();
        this.roadIDList[1]=rightRoad.getRoadID();
        this.roadIDList[2]=downRoad.getRoadID();
        this.roadIDList[3]=leftRoad.getRoadID();
        this.isKnown=false;
        this.cost = Float.MAX_VALUE;
        this.preCross=null;
    }



    public String getCrossID() {
        return crossID;
    }

    public void setCrossID(String crossID) {
        this.crossID = crossID;
    }

    public RoadModel getUpRoad() {
        return upRoad;
    }

    public void setUpRoad(RoadModel upRoad) {
        this.upRoad = upRoad;
    }

    public RoadModel getRightRoad() {
        return rightRoad;
    }

    public void setRightRoad(RoadModel rightRoad) {
        this.rightRoad = rightRoad;
    }

    public RoadModel getDownRoad() {
        return downRoad;
    }

    public void setDownRoad(RoadModel downRoad) {
        this.downRoad = downRoad;
    }

    public RoadModel getLeftRoad() {
        return leftRoad;
    }

    public void setLeftRoad(RoadModel leftRoad) {
        this.leftRoad = leftRoad;
    }

    public String[] getRoadIDList() {
        return roadIDList;
    }

    public void setRoadIDList(String[] roadIDList) {
        this.roadIDList = roadIDList;
    }

    public boolean isKnown() {
        return isKnown;
    }

    public void setKnown(boolean known) {
        isKnown = known;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public CrossModel getPreCross() {
        return preCross;
    }

    public void setPreCross(CrossModel preCross) {
        this.preCross = preCross;
    }


}

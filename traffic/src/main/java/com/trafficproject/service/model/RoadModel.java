package com.trafficproject.service.model;

import java.util.LinkedList;

public class RoadModel {
    private String roadID;         // 道路ID
    private int roadLength;        //道路长度，不小于6
    private int maxRoadVelocity;   //道路限速

    private LinkedList<LaneModel> forwardLane = new LinkedList<>();
    private LinkedList<LaneModel> backwardLane = new LinkedList<>();
    private int lanesNum;           //lane的数量
    private String fromCrossID;     //道路连接的起始路口
    private String toCrossID;       //道路连接的终点路口
    private boolean isDuplex;       //道路是否双向，1双向，0单向

    public  RoadModel(){}
    public  RoadModel(String roadID){
        this.roadID = roadID;
    }

    public String getRoadID() {
        return roadID;
    }

    public void setRoadID(String roadID) {
        this.roadID = roadID;
    }

    public int getRoadLength() {
        return roadLength;
    }

    public void setRoadLength(int roadLength) {
        this.roadLength = roadLength;
    }

    public int getMaxRoadVelocity() {
        return maxRoadVelocity;
    }

    public void setMaxRoadVelocity(int maxRoadVelocity) {
        this.maxRoadVelocity = maxRoadVelocity;
    }

    public LinkedList<LaneModel> getForwardLane() {
        return forwardLane;
    }

    public void setForwardLane(LinkedList<LaneModel> forwardLane) {
        this.forwardLane = forwardLane;
    }

    public LinkedList<LaneModel> getBackwardLane() {
        return backwardLane;
    }

    public void setBackwardLane(LinkedList<LaneModel> backwardLane) {
        this.backwardLane = backwardLane;
    }

    public int getLanesNum() {
        return lanesNum;
    }

    public void setLanesNum(int lanesNum) {
        this.lanesNum = lanesNum;
    }

    public String getFromCrossID() {
        return fromCrossID;
    }

    public void setFromCrossID(String fromCrossID) {
        this.fromCrossID = fromCrossID;
    }

    public String getToCrossID() {
        return toCrossID;
    }

    public void setToCrossID(String toCrossID) {
        this.toCrossID = toCrossID;
    }

    public boolean isDuplex() {
        return isDuplex;
    }

    public void setDuplex(boolean duplex) {
        isDuplex = duplex;
    }

    @Override
    public String toString() {
        return "Road{" +
                "roadID='" + roadID + '\'' +
                ", forwardLane=" + forwardLane +
                ", backwardLane=" + backwardLane +
                ", isDuplex=" + isDuplex +
                '}';
    }
}

package com.trafficproject.service.model;

import java.util.LinkedList;

public class LaneModel {
    private int laneIndex;              //lane的编号
    public LinkedList<CarModel> carsInLane;  //该lane上的车

    public LaneModel(int laneIndex){
        this.laneIndex = laneIndex;
        this.carsInLane = new LinkedList<>();
    }

    public LaneModel() {

    }

    public int getLaneIndex() {
        return laneIndex;
    }

    public void setLaneIndex(int laneIndex) {
        this.laneIndex = laneIndex;
    }

    public LinkedList<CarModel> getCarsInLane() {
        return carsInLane;
    }

    public void setCarsInLane(LinkedList<CarModel> carsInLane) {
        this.carsInLane = carsInLane;
    }
}

package vo;

import java.util.LinkedList;

public class Lane {
    
    private int laneIndex;              //lane�ı��
    public LinkedList<Car> carsInLane;  //��lane�ϵĳ�

    public Lane(int laneIndex) {
        this.laneIndex=laneIndex;
        this.carsInLane=new LinkedList<>();
    }

    public Lane() {
        // TODO Auto-generated constructor stub
    }


    public int getLaneIndex() {
        return laneIndex;
    }

    public void setLaneIndex(int laneIndex) {
        this.laneIndex = laneIndex;
    }
}

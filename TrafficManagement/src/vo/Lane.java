package vo;

import java.util.LinkedList;

public class Lane{
    private int laneIndex;
    public LinkedList<CarInOutPriority> cars;

    public int getLaneIndex() {
        return laneIndex;
    }

    public void setLaneIndex(int laneIndex) {
        this.laneIndex = laneIndex;
    }

}
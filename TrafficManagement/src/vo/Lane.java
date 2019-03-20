package vo;

import java.util.LinkedList;
/**
 * Lane:laneIndex、carsInLane
 * @author Tricia
 * @version 2010-03-19
 */
public class Lane{
    private int laneIndex; //lane的编号
    public LinkedList<Car> carsInLane; //该lane上的车

    public Lane(int laneIndex, LinkedList<Car> carsInLane) {
		this.laneIndex=laneIndex;
		this.carsInLane=carsInLane;
	}

	public int getLaneIndex() {
        return laneIndex;
    }

    public void setLaneIndex(int laneIndex) {
        this.laneIndex = laneIndex;
    }

}
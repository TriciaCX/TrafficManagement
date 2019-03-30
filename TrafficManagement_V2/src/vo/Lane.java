package vo;

import java.util.LinkedList;
/**
 * Lane:laneIndex、carsInLane
 * @author Tricia
 * @version 2010-03-21
 */
public class Lane{
    private int laneIndex; //lane的编号
    public LinkedList<Car> carsInLane; //该lane上的车

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
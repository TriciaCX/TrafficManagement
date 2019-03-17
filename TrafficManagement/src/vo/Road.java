package vo;

import java.util.LinkedList;
import java.util.Vector;

public class Road {
    private String roadID;
    private int roadLength;
    private int maxRoadVelocity;    
    private Vector<Lane> lanes;
    private  int  lanesNum;
    private  Cross fromCross;
    private  Cross toCross;
    private boolean isDuplex;
    public LinkedList<CarInOutPriority> cars;

    public Road(String roadID, int length, int speed, int channel, Cross from, Cross to, boolean isDuplex)
	{
		this.roadID=roadID;
		this.roadLength=length;
		this.maxRoadVelocity=speed;
		this.lanesNum=channel;
		this.fromCross=from;
		this.toCross=to;
		this.isDuplex=isDuplex;
	}

	public Road(String roadID)
	{
		this.roadID=roadID;
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

    public Vector<Lane> getLanes() {
        return lanes;
    }

    public void setLanes(Vector<Lane> lanes) {
        this.lanes = lanes;
    }

    public int getLanesNum() {
        return lanesNum;
    }

    public void setLanesNum(int lanesNum) {
        this.lanesNum = lanesNum;
    }

    public Cross getFromCross() {
        return fromCross;
    }

    public void setFromCross(Cross fromCross) {
        this.fromCross = fromCross;
    }

    public Cross getToCross() {
        return toCross;
    }

    public void setToCross(Cross toCross) {
        this.toCross = toCross;
    }

    public boolean isDuplex() {
        return isDuplex;
    }

    public void setDuplex(boolean duplex) {
        isDuplex = duplex;
    }
}

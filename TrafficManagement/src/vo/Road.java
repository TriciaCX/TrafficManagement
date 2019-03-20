package vo;

import java.util.LinkedList;

/**
 * Road:roadID、roadLength、maxRoadVelocity、lanes、lanesNum、fromCross、toCross、isDuplex
 * @author Tricia
 * @version 2019-3-19
 */
public class Road {
    private String roadID;     //道路ID
    private int roadLength;    //道路长度，不小于6
    private int maxRoadVelocity;  //道路限速   
    private LinkedList<Lane> lanes; //
    private  int  lanesNum;   //lane的数量
    private  Cross fromCross;  //道路连接的起始路口
    private  Cross toCross;    //道路连接的终点路口
    private boolean isDuplex;  //道路是否双向，1双向，0单向


    public Road(String roadID, int length, int speed, int channel, Cross from, Cross to, boolean isDuplex)
	{
		this.roadID = roadID;
		this.roadLength = length;
		this.maxRoadVelocity = speed;
		this.lanesNum = channel;
		this.fromCross = from;
		this.toCross = to;
		this.isDuplex = isDuplex;
		this.lanes =  new LinkedList<Lane>();
	}

	public Road(String roadID)
	{
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

	public LinkedList<Lane> getLanes() {
		return lanes;
	}

	public void setLanes(LinkedList<Lane> lanes) {
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

	public void setDuplex(boolean isDuplex) {
		this.isDuplex = isDuplex;
	}

	
}

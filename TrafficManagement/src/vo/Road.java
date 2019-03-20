package vo;

import java.util.LinkedList;

/**
 * Road:roadID��roadLength��maxRoadVelocity��lanes��lanesNum��fromCross��toCross��isDuplex
 * @author Tricia
 * @version 2019-3-19
 */
public class Road {
    private String roadID;     //��·ID
    private int roadLength;    //��·���ȣ���С��6
    private int maxRoadVelocity;  //��·����   
    private LinkedList<Lane> lanes; //
    private  int  lanesNum;   //lane������
    private  Cross fromCross;  //��·���ӵ���ʼ·��
    private  Cross toCross;    //��·���ӵ��յ�·��
    private boolean isDuplex;  //��·�Ƿ�˫��1˫��0����


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

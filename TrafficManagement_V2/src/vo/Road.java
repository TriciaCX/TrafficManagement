package vo;

import java.util.LinkedList;

public class Road {
     private String roadID;         // ��·ID
     private int roadLength;        //��·���ȣ���С��6
     private int maxRoadVelocity;   //��·����

    private LinkedList<Lane> forwardLane;
    private LinkedList<Lane> backwardLane;  
    private int lanesNum;           //lane������
    private String fromCrossID;     //��·���ӵ���ʼ·��
    private String toCrossID;       //��·���ӵ��յ�·��
    private boolean isDuplex;       //��·�Ƿ�˫��1˫��0����


    public Road(String roadID, int length, int speed, int channel, String from, String to, boolean isDuplex) {
        this.roadID = roadID;
        this.roadLength = length;
        this.maxRoadVelocity = speed;
        this.lanesNum = channel;
        this.fromCrossID = from;
        this.toCrossID = to;
        this.isDuplex = isDuplex;
        this.backwardLane = new LinkedList<Lane>();
        this.forwardLane = new LinkedList<Lane>();

    }


    /**
     * ��дequals����
     */
    public boolean equals(Road road) {
        if (this.roadID.equals(road.getRoadID())){
            return true;
        }
        else{
            return false;
        }
    }

    public Road(String roadID) {
        this.roadID = roadID;
    }

    public Road() {
        // TODO Auto-generated constructor stub
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

    public void setDuplex(boolean isDuplex) {
        this.isDuplex = isDuplex;
    }

    public LinkedList<Lane> getForwardLane() {
        return forwardLane;
    }

    public void setForwardLane(LinkedList<Lane> forwardLane) {
        this.forwardLane = forwardLane;
    }

    public LinkedList<Lane> getBackwardLane() {
        return backwardLane;
    }

    public void setBackwardLane(LinkedList<Lane> backwardLane) {
        this.backwardLane = backwardLane;
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

package vo;

import java.util.Vector;

public class Road {
    private String roadID;
    private String roadLength;
    private String maxRoadVelocity;
    private Vector<Lane> lanes;
    private  int  lanesNum;
    private  Cross fromCross;
    private  Cross toCross;
    private boolean isDuplex;

    public String getRoadID() {
        return roadID;
    }

    public void setRoadID(String roadID) {
        this.roadID = roadID;
    }

    public String getRoadLength() {
        return roadLength;
    }

    public void setRoadLength(String roadLength) {
        this.roadLength = roadLength;
    }

    public String getMaxRoadVelocity() {
        return maxRoadVelocity;
    }

    public void setMaxRoadVelocity(String maxRoadVelocity) {
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

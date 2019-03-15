package vo;

public class Cross {
    private String crossID;
    private Road upRoad;
    private Road downRoad;
    private Road leftRoad;
    private Road rightRoad;

    public String getCrossID() {
        return crossID;
    }

    public void setCrossID(String crossID) {
        this.crossID = crossID;
    }

    public Road getUpRoad() {
        return upRoad;
    }

    public void setUpRoad(Road upRoad) {
        this.upRoad = upRoad;
    }

    public Road getDownRoad() {
        return downRoad;
    }

    public void setDownRoad(Road downRoad) {
        this.downRoad = downRoad;
    }

    public Road getLeftRoad() {
        return leftRoad;
    }

    public void setLeftRoad(Road leftRoad) {
        this.leftRoad = leftRoad;
    }

    public Road getRightRoad() {
        return rightRoad;
    }

    public void setRightRoad(Road rightRoad) {
        this.rightRoad = rightRoad;
    }
}

package vo;

public class Cross {
    private String crossID;
    private Road upRoad;
    private Road downRoad;
    private Road leftRoad;
    private Road rightRoad;

    /**
     * ¹¹Ôìº¯Êý
     * @param crossID
     * @author Tricia
     * @version 2019-03-16
     */
    public Cross(String crossID)
	{
		this.crossID=crossID;
	}

	public Cross(String crossID,Road upRoad,Road rightRoad,Road downRoad,Road leftRoad)
	{
		this.crossID=crossID;
		this.upRoad=upRoad;
		this.rightRoad=rightRoad;
		this.downRoad=downRoad;
		this.leftRoad=leftRoad;
	}

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

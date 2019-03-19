package vo;

public class Cross {
    private String crossID;
    private Road upRoad;
    private Road downRoad;
    private Road leftRoad;
    private Road rightRoad;
    public boolean known;
    public float dist;
    public Cross path;

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

	public boolean isKnown()
	{
		return known;
	}

	public void setKnown(boolean known)
	{
		this.known = known;
	}

	public float getDist()
	{
		return dist;
	}

	public void setDist(float dist)
	{
		this.dist = dist;
	}

	public Cross getPath()
	{
		return path;
	}

	public void setPath(Cross path)
	{
		this.path = path;
	}
}

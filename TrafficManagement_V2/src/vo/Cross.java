package vo;

import java.util.ArrayList;

/**
 * Cross：crossID、upRoad、downRoad、leftRoad、rightRoad、roadIDList、known
 * @author Tricia
 * @version 2019-03-21
 */
public class Cross {
    private String crossID;
    private Road upRoad;
    private Road rightRoad;
    private Road downRoad;
    private Road leftRoad;
    private String[] roadIDList=new String[4];

    //以下属性为了找寻路径
    public boolean isKnown;
    public float cost;
    public Cross preCross;   //找路径时的前一个Cross 



	public Cross(String crossID) {
		this.crossID=crossID;
	}
	
		
	public Cross(String crossID, Road upRoad, Road rightRoad, Road downRoad, Road leftRoad,ArrayList<Road> roadIDlist) {
		this.crossID=crossID;
		this.upRoad=upRoad;
		this.rightRoad=rightRoad;
		this.downRoad=downRoad;
		this.leftRoad=leftRoad;
		this.roadIDList[0]=upRoad.getRoadID();
		this.roadIDList[1]=rightRoad.getRoadID();
		this.roadIDList[2]=downRoad.getRoadID();
		this.roadIDList[3]=leftRoad.getRoadID();
	    this.isKnown=false;
	    this.cost = Float.MAX_VALUE;
	    this.preCross=null;
	}


	public Cross() {
		// TODO Auto-generated constructor stub
	}

	public boolean equals(Cross cross) {
		if(this.crossID.equals(cross.getCrossID())) return true;
		else return false;
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
	public Road getRightRoad() {
		return rightRoad;
	}
	public void setRightRoad(Road rightRoad) {
		this.rightRoad = rightRoad;
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
	
	public String[] getRoadIDList() {
		return roadIDList;
	}


	public void setRoadIDList(String[] roadIDList) {
		this.roadIDList = roadIDList;
	}


	public boolean isKnown() {
		return isKnown;
	}
	public void setKnown(boolean isKnown) {
		this.isKnown = isKnown;
	}
	public float getCost() {
		return cost;
	}
	public void setCost(float cost) {
		this.cost = cost;
	}
	public Cross getPreCross() {
		return preCross;
	}
	public void setPreCross(Cross preCross) {
		this.preCross = preCross;
	}
}

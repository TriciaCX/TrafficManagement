package vo;

import java.util.ArrayList;

/**
 * Cross£∫crossID°¢upRoad°¢downRoad°¢leftRoad°¢rightRoad°¢roadIDList°¢known
 * @author Tricia
 * @version 2019-03-19
 */
public class Cross {
    private String crossID;
    private Road upRoad;
    private Road rightRoad;
    private Road downRoad;
    private Road leftRoad;
    private ArrayList<Road> roadIDList;  //¥Ê¥¢À≥–Ú£¨…œ”“œ¬◊Û
    public boolean known;
    public int weight;
    



	public Cross(String crossID) {
		this.crossID=crossID;
	}
	
	
	
	public Cross(String crossID, Road upRoad, Road rightRoad, Road downRoad, Road leftRoad,ArrayList<Road> roadIDlist) {
		this.crossID=crossID;
		this.upRoad=upRoad;
		this.rightRoad=rightRoad;
		this.downRoad=downRoad;
		this.leftRoad=leftRoad;
		for(Road roadID: roadIDlist) {
			this.roadIDList.add(roadID);
		}
	}



	public Cross() {
		// TODO Auto-generated constructor stub
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
	public ArrayList<Road> getRoadIDList() {
		return roadIDList;
	}
	public void setRoadIDList(ArrayList<Road> roadIDList) {
		this.roadIDList = roadIDList;
	}
	public boolean isKnown() {
		return known;
	}
	public void setKnown(boolean known) {
		this.known = known;
	}
    
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}



}

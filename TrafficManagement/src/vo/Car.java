package vo;

/**
 * Car
 * @author Tricia
 * @version 2019-3-19
 */
public class Car {
    private String carID;        //车辆ID
    private Cross fromCross;     //出发地，对于car.txt中的from
    private Cross toCross;       //目的地，对应car.txt中的to
    private Cross curFromCross;  //当前出发路口
    private Cross curToCross;    //当前目的路口
    private int maxVelocity;     //车辆自身的最大行驶速度，对应car.txt中的speed
    private int realVelocity;    //车辆在lane上的实际行驶速度
    private int planTime;        // 计划出发时间
    private int realStartTime;   //实际出发时间
    private int crossStartTime;  //到达cross的时间   
    private int realEndTime;     //实际达到时间
    private String RoadID;      //车位于的道路
    private int LaneID;         //车位于的车道
    private int Priority;       //优先级 0-未安排； 1-右转； 2-左转； 3-直行
    private int NextPos;        //[0,N]该车位于车道上，处于中止状态，-1表示将且可以离开路口，-2初始化，表示该车还在车库中，没有上路
    private int CurPos;         //当前位置
    public int rest;            //当前T内到达cross后还能够行驶的距离（以其在前一个道路的行驶速度计算）
    
    
    //读入Car.txt文件构造的car
	public Car(String carID, Cross fromCross, Cross toCross, int maxVelocity, int planTime, int NextPos ) {
		this.carID=carID;
		this.fromCross=fromCross;
		this.toCross=toCross;
		this.maxVelocity=maxVelocity;
		this.planTime=planTime;
		this.NextPos=-2;
	}
	
	public String getCarID() {
		return carID;
	}
	public void setCarID(String carID) {
		this.carID = carID;
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
	public Cross getCurFromCross() {
		return curFromCross;
	}
	public void setCurFromCross(Cross curFromCross) {
		this.curFromCross = curFromCross;
	}
	public Cross getCurToCross() {
		return curToCross;
	}
	public void setCurToCross(Cross curToCross) {
		this.curToCross = curToCross;
	}
	public int getMaxVelocity() {
		return maxVelocity;
	}
	public void setMaxVelocity(int maxVelocity) {
		this.maxVelocity = maxVelocity;
	}
	public int getRealVelocity() {
		return realVelocity;
	}
	public void setRealVelocity(int realVelocity) {
		this.realVelocity = realVelocity;
	}
	public int getPlanTime() {
		return planTime;
	}
	public void setPlanTime(int planTime) {
		this.planTime = planTime;
	}
	public int getRealStartTime() {
		return realStartTime;
	}
	public void setRealStartTime(int realStartTime) {
		this.realStartTime = realStartTime;
	}
	public int getCrossStartTime() {
		return crossStartTime;
	}
	public void setCrossStartTime(int crossStartTime) {
		this.crossStartTime = crossStartTime;
	}
	public int getRealEndTime() {
		return realEndTime;
	}
	public void setRealEndTime(int realEndTime) {
		this.realEndTime = realEndTime;
	}
	public String getRoadID() {
		return RoadID;
	}
	public void setRoadID(String roadID) {
		RoadID = roadID;
	}
	public int getLaneID() {
		return LaneID;
	}
	public void setLaneID(int laneID) {
		LaneID = laneID;
	}
	public int getPriority() {
		return Priority;
	}
	public void setPriority(int priority) {
		Priority = priority;
	}
	public int getNextPos() {
		return NextPos;
	}
	public void setNextPos(int nextPos) {
		NextPos = nextPos;
	}
	public int getCurPos() {
		return CurPos;
	}
	public void setCurPos(int curPos) {
		CurPos = curPos;
	}

	public int getRest() {
		return rest;
	}
	public void setRest(int rest) {
		this.rest = rest;
	}
    
   
    
    
}
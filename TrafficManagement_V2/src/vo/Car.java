package vo;

/**
 * Car
 * @version 2019-3-23
 */
public class Car {
	private String carID; // 车辆ID,读取初始化
	private String fromCrossID; // 出发地，对于car.txt中的from,读取初始化
	private String toCrossID; // 目的地，对应car.txt中的to，读取初始化
	private String curFromCrossID; // 当前出发路口，初始化时就是fromCrossID
	private String curToCrossID; // 当前目的路口，初始化：null
	private int maxVelocity; // 车辆自身的最大行驶速度，对应car.txt中的speed,读取初始化
	private int planTime; // 计划出发时间，读取初始化
	private int realStartTime; // 实际出发时间，初始化：-1
	private int realEndTime; // 实际达到时间，初始化：-1
	private String RoadID; // 车位于的道路，初始化：“-1”
	private int LaneID; // 车位于的车道，初始化：-1
	private int Priority; // 优先级 0-未安排； 1-右转； 2-左转； 3-直行
	private int CurPos; // 当前位置，初始化：0
	private int sheng; // 初始化：-1
	private boolean hasArrangedOrNot; // 等待状态是否安排过，初始化：false
	private int state; // 设置状态位,初始状态设为-1
	private String NextRoadID;// 初始状态为“-1"
	private boolean canThrough;// 初始状态为false;
	

	public boolean isCanThrough() {
		return canThrough;
	}

	public void setCanThrough(boolean canThrough) {
		this.canThrough = canThrough;
	}

	public String getNextRoadID() {
		return NextRoadID;
	}

	public void setNextRoadID(String nextRoadID) {
		NextRoadID = nextRoadID;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	// 读入Car.txt文件构造的car
	public Car(String carID, String fromCrossID, String toCrossID, int maxVelocity, int planTime) {
		this.carID = carID;
		this.fromCrossID = fromCrossID;
		this.toCrossID = toCrossID;
		this.maxVelocity = maxVelocity;
		this.planTime = planTime;
		this.realStartTime = -1;
		this.realEndTime = -1;
		this.curFromCrossID = fromCrossID;
		this.curToCrossID = fromCrossID;
		this.CurPos = 0;
		this.hasArrangedOrNot = false;
		this.Priority = 0;
		this.state = -1;
		this.sheng = 0;
		this.canThrough = false;
		this.NextRoadID = "-1";
		this.RoadID = "-1";
		this.LaneID =-1;
	}

	

	public Car() {
		// TODO Auto-generated constructor stub
	}

	public Car(Car car) {
		// TODO Auto-generated constructor stub
		this.carID = car.getCarID();
		this.state = car.getState();
		this.curFromCrossID = car.getCurFromCrossID();
		this.CurPos = car.getCurPos();
		this.curToCrossID = car.getCurToCrossID();
		this.fromCrossID = car.getFromCrossID();
		this.hasArrangedOrNot = car.isHasArrangedOrNot();
		this.LaneID = car.getLaneID();
		this.maxVelocity = car.getMaxVelocity();
		this.planTime = car.getPlanTime();
		this.Priority = car.getPriority();
		this.realEndTime = car.getRealEndTime();
		this.realStartTime = car.getRealStartTime();
		this.RoadID = car.getRoadID();
		this.sheng = car.getSheng();
		this.toCrossID = car.getToCrossID();
		this.NextRoadID = car.getNextRoadID();
		this.canThrough = car.isCanThrough();

	}

	// 重写equals方法
	public boolean equals(Car car) {
		if (this.carID.equals(car.getCarID()))
			return true;
		else
			return false;
	}

	public String getCarID() {
		return carID;
	}

	public void setCarID(String carID) {
		this.carID = carID;
	}

	public int getMaxVelocity() {
		return maxVelocity;
	}

	public void setMaxVelocity(int maxVelocity) {
		this.maxVelocity = maxVelocity;
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

	public int getCurPos() {
		return CurPos;
	}

	public void setCurPos(int curPos) {
		CurPos = curPos;
	}

	public int getSheng() {
		return sheng;
	}

	public void setSheng(int sheng) {
		this.sheng = sheng;
	}

	public boolean isHasArrangedOrNot() {
		return hasArrangedOrNot;
	}

	public void setHasArrangedOrNot(boolean hasArrangedOrNot) {
		this.hasArrangedOrNot = hasArrangedOrNot;
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

	public String getCurFromCrossID() {
		return curFromCrossID;
	}

	public void setCurFromCrossID(String curFromCrossID) {
		this.curFromCrossID = curFromCrossID;
	}

	public String getCurToCrossID() {
		return curToCrossID;
	}

	public void setCurToCrossID(String curToCrossID) {
		this.curToCrossID = curToCrossID;
	}

}
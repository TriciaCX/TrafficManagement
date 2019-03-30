package vo;

/**
 * Car
 * @version 2019-3-23
 */
public class Car {
	private String carID; // ����ID,��ȡ��ʼ��
	private String fromCrossID; // �����أ�����car.txt�е�from,��ȡ��ʼ��
	private String toCrossID; // Ŀ�ĵأ���Ӧcar.txt�е�to����ȡ��ʼ��
	private String curFromCrossID; // ��ǰ����·�ڣ���ʼ��ʱ����fromCrossID
	private String curToCrossID; // ��ǰĿ��·�ڣ���ʼ����null
	private int maxVelocity; // ��������������ʻ�ٶȣ���Ӧcar.txt�е�speed,��ȡ��ʼ��
	private int planTime; // �ƻ�����ʱ�䣬��ȡ��ʼ��
	private int realStartTime; // ʵ�ʳ���ʱ�䣬��ʼ����-1
	private int realEndTime; // ʵ�ʴﵽʱ�䣬��ʼ����-1
	private String RoadID; // ��λ�ڵĵ�·����ʼ������-1��
	private int LaneID; // ��λ�ڵĳ�������ʼ����-1
	private int Priority; // ���ȼ� 0-δ���ţ� 1-��ת�� 2-��ת�� 3-ֱ��
	private int CurPos; // ��ǰλ�ã���ʼ����0
	private int sheng; // ��ʼ����-1
	private boolean hasArrangedOrNot; // �ȴ�״̬�Ƿ��Ź�����ʼ����false
	private int state; // ����״̬λ,��ʼ״̬��Ϊ-1
	private String NextRoadID;// ��ʼ״̬Ϊ��-1"
	private boolean canThrough;// ��ʼ״̬Ϊfalse;
	

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

	// ����Car.txt�ļ������car
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

	// ��дequals����
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
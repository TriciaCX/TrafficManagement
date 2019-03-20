package vo;

/**
 * Car
 * @author Tricia
 * @version 2019-3-19
 */
public class Car {
    private String carID;        //����ID
    private Cross fromCross;     //�����أ�����car.txt�е�from
    private Cross toCross;       //Ŀ�ĵأ���Ӧcar.txt�е�to
    private Cross curFromCross;  //��ǰ����·��
    private Cross curToCross;    //��ǰĿ��·��
    private int maxVelocity;     //��������������ʻ�ٶȣ���Ӧcar.txt�е�speed
    private int realVelocity;    //������lane�ϵ�ʵ����ʻ�ٶ�
    private int planTime;        // �ƻ�����ʱ��
    private int realStartTime;   //ʵ�ʳ���ʱ��
    private int crossStartTime;  //����cross��ʱ��   
    private int realEndTime;     //ʵ�ʴﵽʱ��
    private String RoadID;      //��λ�ڵĵ�·
    private int LaneID;         //��λ�ڵĳ���
    private int Priority;       //���ȼ� 0-δ���ţ� 1-��ת�� 2-��ת�� 3-ֱ��
    private int NextPos;        //[0,N]�ó�λ�ڳ����ϣ�������ֹ״̬��-1��ʾ���ҿ����뿪·�ڣ�-2��ʼ������ʾ�ó����ڳ����У�û����·
    private int CurPos;         //��ǰλ��
    public int rest;            //��ǰT�ڵ���cross���ܹ���ʻ�ľ��루������ǰһ����·����ʻ�ٶȼ��㣩
    
    
    //����Car.txt�ļ������car
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
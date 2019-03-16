package vo;

public class Car {
    private String carID;//车辆ID
    private Cross originCross;
    private Cross destiCross;
    private int maxVelocity;
    private int realVelocity;
    private String startTime;
    private String realStartTime;
    private String currentTime;
    private String endTime;
    
    
    
    /**
     * 构造函数
     * @param carID
     * @param originCross
     * @param destiCross
     * @param maxVelocity
     * @param startTime
     * @author Tricia
     */
	public Car(String carID, Cross originCross, Cross destiCross, int maxVelocity, String startTime)
	{
		this.carID=carID;
		this.originCross = originCross;
		this.destiCross=destiCross;
		this.maxVelocity=maxVelocity;
		this.startTime=startTime;
		
	}
	public String getCarID() {
		return carID;
	}
	public void setCarID(String carID) {
		this.carID = carID;
	}
	public Cross getOriginCross() {
		return originCross;
	}
	public void setOriginCross(Cross originCross) {
		this.originCross = originCross;
	}
	public Cross getDestiCross() {
		return destiCross;
	}
	public void setDestiCross(Cross destiCross) {
		this.destiCross = destiCross;
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
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getRealStartTime() {
		return realStartTime;
	}
	public void setRealStartTime(String realStartTime) {
		this.realStartTime = realStartTime;
	}
	public String getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
    


  
}

package vo;

public class Car {
    private String carID;     //车辆ID
    private Cross originCross;
    private Cross destiCross;
    private int maxVelocity;
    private int realVelocity;
    private int startTime;  //planTime
    private int realStartTime; //实际出发时间
    private int currentTime; 
    private int endTime; //实际达到时间
    
       
    /**
     * 构造函数
     * @param carID
     * @param originCross
     * @param destiCross
     * @param maxVelocity
     * @param startTime
     * @author Tricia
     */
	public Car(String carID, Cross originCross, Cross destiCross, int maxVelocity, int startTime)
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
	public float getStartTime()
	{
		return startTime;
	}
	public void setStartTime(int startTime)
	{
		this.startTime = startTime;
	}
	public float getRealStartTime()
	{
		return realStartTime;
	}
	public void setRealStartTime(int realStartTime)
	{
		this.realStartTime = realStartTime;
	}
	public float getCurrentTime()
	{
		return currentTime;
	}
	public void setCurrentTime(int currentTime)
	{
		this.currentTime = currentTime;
	}
	public float getEndTime()
	{
		return endTime;
	}
	public void setEndTime(int endTime)
	{
		this.endTime = endTime;
	}

  
}

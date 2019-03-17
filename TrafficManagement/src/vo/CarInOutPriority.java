package vo;

public class CarInOutPriority {
	private int carVelocity;
	private float inTime;
	private float outTime;
	private String priority;
	public CarInOutPriority(int carVelocity,float inTime,float outTime,String priority) {
		this.setCarVelocity(carVelocity);
		this.inTime=inTime;
		this.outTime=outTime;
		this.priority=priority;
	}
	public float getInTime() {
		return inTime;
	}
	public void setInTime(float inTime) {
		this.inTime = inTime;
	}
	public float getOutTime() {
		return outTime;
	}
	public void setOutTime(float outTime) {
		this.outTime = outTime;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public int getCarVelocity()
	{
		return carVelocity;
	}
	public void setCarVelocity(int carVelocity)
	{
		this.carVelocity = carVelocity;
	}

}

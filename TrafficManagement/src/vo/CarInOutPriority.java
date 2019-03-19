package vo;

public class CarInOutPriority {

	private Car car;
	private int inTime;
	private int outTime;
	private int priority;//这个应该写成int
	private int CarVelocity;
	public CarInOutPriority(Car car,int inTime,int outTime,int priority,int CarVelocity) {
		this.setCar(car);
		this.inTime=inTime;
		this.outTime=outTime;
		this.priority=priority;
		this.setCarVelocity(CarVelocity);
	}
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	public int getInTime() {
		return inTime;
	}
	public void setInTime(int inTime) {
		this.inTime = inTime;
	}
	public int getOutTime() {
		return outTime;
	}
	public void setOutTime(int outTime) {
		this.outTime = outTime;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getCarVelocity() {
		return CarVelocity;
	}
	public void setCarVelocity(int carVelocity) {
		CarVelocity = carVelocity;
	}
	
	
}

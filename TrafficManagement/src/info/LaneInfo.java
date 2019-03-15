package info;

import java.util.Vector;

import vo.Car;
import vo.Lane;

public class LaneInfo {
    //返回在当前lane跑的车辆有多少辆
	public static int getCarsNum(Lane myLane) {
		return myLane.getCars().size();
	}
    //返回在当前lane还可以跑的车
	public static int getLeftCarsNum(Lane myLane) {
		
	}
	//获取在当前lane跑的车辆的最慢速度
	public static int  getMinVelocity(Lane myLane) {
		Vector<Car> cars =myLane.getCars();
		int minV=Integer.MAX_VALUE;
		for(Car c:cars) {
			minV=Math.min(c.getRealVelocity(), minV);
		}
		return minV;
		
	}
	//
	

}

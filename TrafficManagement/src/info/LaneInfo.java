package info;

import vo.CarInOutPriority;
import vo.Lane;
/**
 * 返回在当前lane有多少辆车
 * @author Tricia
 * @version 2019-03-17
 */

public class LaneInfo {

    //返回在当前lane有多少辆车
	//LinkedList<CarInOutPriority> cars
	public static int getLeftCarsNum(Lane myLane,float t) {
		int CurCarsNum = 0;
		for (CarInOutPriority tempCar:myLane.cars) 
		{
			if(t>tempCar.getInTime()&&t<tempCar.getOutTime()) {
				CurCarsNum++;
			}
		}
		return CurCarsNum;
	}
	//获取在当前lane跑的车辆的最慢速度

	public static int  getMinVelocity(Lane myLane,float t) {
      
		return 0;
		
	}
	//
	

}

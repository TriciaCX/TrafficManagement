package info;

import vo.CarInOutPriority;
import vo.Lane;
/**
 * �����ڵ�ǰlane�ж�������
 * @author Tricia
 * @version 2019-03-17
 */

public class LaneInfo {

    //�����ڵ�ǰlane�ж�������
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
	//��ȡ�ڵ�ǰlane�ܵĳ����������ٶ�

	public static int  getMinVelocity(Lane myLane,float t) {
      
		return 0;
		
	}
	//
	

}

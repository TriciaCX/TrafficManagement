package info;

import java.util.Vector;

import vo.Car;
import vo.Lane;

public class LaneInfo {
    //�����ڵ�ǰlane�ܵĳ����ж�����
	public static int getCarsNum(Lane myLane) {
		return myLane.getCars().size();
	}
    //�����ڵ�ǰlane�������ܵĳ�
	public static int getLeftCarsNum(Lane myLane) {
		
	}
	//��ȡ�ڵ�ǰlane�ܵĳ����������ٶ�
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

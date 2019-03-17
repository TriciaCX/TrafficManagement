package info;

import vo.CarInOutPriority;
import vo.Road;

/**
 * getCurCarsNum(Road myRoad,float t)--�����ڵ�ǰroad���ж�������
 * getLeftCarsNum(Road myRoad,float t)--���ص�ǰroad���ܽ����������
 * getMinVelocity(Road myRoad,float t)--���ص�ǰ��·����С�ٶ�
 * @author Tricia
 * @version 2019-03-17
 */
public class RoadInfo {
	   
		/**
		 * �����ڵ�ǰroad���ж�������
		 * ����LinkedList<CarInOutPriority> cars�г���InTime��OutTime�жϣ��Ѿ���������û���ߵģ����ǵ�ǰʱ���ڵ�·�ϵĳ�
		 * @param myRoad
		 * @param t
		 * @return CurCarsNum 
		 */
		public int getCurCarsNum(Road myRoad,float t) {
			int CurCarsNum = 0;
			for (CarInOutPriority tempCar:myRoad.cars) 
			{
				if(t>tempCar.getInTime() && t<tempCar.getOutTime()) {
					CurCarsNum++;
				}
			}
			return CurCarsNum;
		}
	
		
		
		/**
		 * ���ص�ǰroad���ܽ����������
		 * @param myRoad
		 * @param t
		 * @return LeftCarsNum
		 */
		public int getLeftCarsNum(Road myRoad,float t) {
			int LeftCarsNum =0;
			int CurCarsNum = getCurCarsNum(myRoad,t);
			int SumCarsNum = myRoad.getLanesNum()*myRoad.getRoadLength();
			LeftCarsNum = SumCarsNum-CurCarsNum;
			return LeftCarsNum;
		}
		
		
		/**
		 * ���ص�ǰ��·����С�ٶȣ���һ������ĳ������ʵ����ʻ�ٶȣ�
		 * @param myRoad
		 * @param t
		 * @return
		 */
		public int getMinVelocity(Road myRoad,float t) {
			int MinSpeed = myRoad.getMaxRoadVelocity();
			int tempSpeed = 0;
			for (CarInOutPriority tempCar:myRoad.cars) 
			{
				if(t>tempCar.getInTime() && t<tempCar.getOutTime()) {
                    tempSpeed=tempCar.getCarVelocity();
				}
				if(tempSpeed<MinSpeed) {
					MinSpeed = tempSpeed;
				}
			}
			return MinSpeed;
		}

	

}

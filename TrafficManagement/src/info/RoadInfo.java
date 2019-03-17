package info;

import vo.CarInOutPriority;
import vo.Road;

/**
 * getCurCarsNum(Road myRoad,float t)--返回在当前road上有多少辆车
 * getLeftCarsNum(Road myRoad,float t)--返回当前road还能进入多少辆车
 * getMinVelocity(Road myRoad,float t)--返回当前道路的最小速度
 * @author Tricia
 * @version 2019-03-17
 */
public class RoadInfo {
	   
		/**
		 * 返回在当前road上有多少辆车
		 * 利用LinkedList<CarInOutPriority> cars中车的InTime和OutTime判断，已经进来，还没有走的，就是当前时刻在道路上的车
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
		 * 返回当前road还能进入多少辆车
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
		 * 返回当前道路的最小速度（下一辆进入的车的最大实际行驶速度）
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

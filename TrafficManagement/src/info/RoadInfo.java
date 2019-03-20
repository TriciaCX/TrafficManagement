package info;

import java.util.ArrayList;

import vo.Car;
import vo.Cross;
import vo.Lane;
import vo.Road;

/**
 * getLeftCarsNum(Road myRoad,Cross fromCross, Cross toCross)--返回当前road的每一个lane还能进入多少辆车
 * @author Tricia
 * @version 2019-03-19
 */
public class RoadInfo {



	/**
	 *  返回下一时刻road的每一条lane还能进入多少辆车,即车位于NextPos时还剩多少位置
	 * LanesCarsList[0]=3,表示lane1的剩余可进入长度为3，还能进3辆车
	 * @param myRoad
	 * @param fromCross
	 * @param toCross
	 * @return LanesCarsList
	 */
	public static ArrayList<Integer> getLeftLanesLength(Road myRoad,Cross fromCross) {
		ArrayList<Integer> LeftLanesLengthList = new ArrayList<Integer>();
		boolean isDuplex  = myRoad.isDuplex();
		int LeftLength = myRoad.getRoadLength();
		int index=0;
		//单向道路且车行驶方向与道路方向一致
		if(!isDuplex && fromCross == myRoad.getFromCross()) {
			for(Lane lane : myRoad.getLanes()) { //从road上得到lane
				Car lastCar = lane.carsInLane.getLast();
				if(lastCar.getNextPos()<myRoad.getRoadLength()-1&&lastCar.getNextPos()>=0) {
					LeftLength = myRoad.getRoadLength()-1-lastCar.getNextPos();
				}else if(lastCar.getNextPos()==-1) { 
					LeftLength = myRoad.getRoadLength();
				}
				LeftLanesLengthList.add(index++, LeftLength);
			}
		}
		else if(isDuplex) {//双向道路
			if(fromCross == myRoad.getFromCross()) {//走1、2、3号lane(以lanesnum=3为例)
				for(Lane lane : myRoad.getLanes()) {
					Car lastCar = lane.carsInLane.getLast();
					if(lastCar.getNextPos()<myRoad.getRoadLength()-1&&lastCar.getNextPos()>=0) {
						LeftLength = myRoad.getRoadLength()-1-lastCar.getNextPos();
					}else if(lastCar.getNextPos()==-1) { 
						LeftLength = myRoad.getRoadLength();
					}
					LeftLanesLengthList.add(index++, LeftLength);
				}
			}
			else if(fromCross == myRoad.getToCross()) {//走4、5、6号lane(以lanesnum=3为例)
				for(int i=0;i< myRoad.getLanesNum();i++) {
					LeftLanesLengthList.add(i, 0);
				}
				index = myRoad.getLanesNum();
				for(Lane lane : myRoad.getLanes()) {
					Car lastCar = lane.carsInLane.getLast();
					if(lastCar.getNextPos()<myRoad.getRoadLength()-1&&lastCar.getNextPos()>=0) {
						LeftLength = myRoad.getRoadLength()-1-lastCar.getNextPos();
					}else if(lastCar.getNextPos()==-1) { 
						LeftLength = myRoad.getRoadLength();
					}
					LeftLanesLengthList.add(index++, LeftLength);
				}
			}
		}
		else {
			return null;
		}
		return LeftLanesLengthList;
	}
}		
		
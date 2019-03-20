package info;

import java.util.ArrayList;

import vo.Car;
import vo.Cross;
import vo.Lane;
import vo.Road;

/**
 * getLeftCarsNum(Road myRoad,Cross fromCross, Cross toCross)--���ص�ǰroad��ÿһ��lane���ܽ����������
 * @author Tricia
 * @version 2019-03-19
 */
public class RoadInfo {



	/**
	 *  ������һʱ��road��ÿһ��lane���ܽ����������,����λ��NextPosʱ��ʣ����λ��
	 * LanesCarsList[0]=3,��ʾlane1��ʣ��ɽ��볤��Ϊ3�����ܽ�3����
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
		//�����·�ҳ���ʻ�������·����һ��
		if(!isDuplex && fromCross == myRoad.getFromCross()) {
			for(Lane lane : myRoad.getLanes()) { //��road�ϵõ�lane
				Car lastCar = lane.carsInLane.getLast();
				if(lastCar.getNextPos()<myRoad.getRoadLength()-1&&lastCar.getNextPos()>=0) {
					LeftLength = myRoad.getRoadLength()-1-lastCar.getNextPos();
				}else if(lastCar.getNextPos()==-1) { 
					LeftLength = myRoad.getRoadLength();
				}
				LeftLanesLengthList.add(index++, LeftLength);
			}
		}
		else if(isDuplex) {//˫���·
			if(fromCross == myRoad.getFromCross()) {//��1��2��3��lane(��lanesnum=3Ϊ��)
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
			else if(fromCross == myRoad.getToCross()) {//��4��5��6��lane(��lanesnum=3Ϊ��)
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
		
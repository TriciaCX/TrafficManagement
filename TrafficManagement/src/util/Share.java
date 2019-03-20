package util;

import java.util.ArrayList;
import java.util.List;

import vo.Car;
import vo.Cross;
import vo.Road;

public class Share {
	/*
	 * 将车辆分为终止状态车辆和等待车辆两个类别，等待状态车辆按速度升序排列
	 * @param cars：所有车辆集合cars
	 * @return frozen：终止车辆；wait：等待车辆
	 */
	public static List<Car> classifyCars(){
		
	}
	
	/*
	 * 从等待状态车辆中返回速度最大的车
	 */
	public static Car getMaxVelocityCarFromwait() {
		
	}
	
	/*
	 * 对当前车辆规划下一路口（判断能进入的道路（根据车道剩余距离）；写D算法，根据能行驶的最远距离，选择一条road）
	 * 并更新车辆优先级，判断优先级是否冲突；
	 * 更新下个路口位置、出发道路（道路，车道，具体位置（距离下个路口的距离））、所在类别
	 * @param car：当前车辆（等待状态）
	 * @return 
	 */
	public static void findNextCross(Car car) {
	//计算该车辆的下一个可能进入道路的剩余容量，判断能进入的道路
    Cross curCross = car.getFromCross(); // 车当前路口
    ArrayList<Road> roads = new ArrayList<Road> ();
    roads = curCross.getRoadIDList();
    String curRoadID = car.getRoadID();  // 车当前道路
    int RoadLeftLength = 0;
    for(int i=0;i<3;i++) {
    	Road road = roads.get(i);
    	if(road==null) {
    		continue;
    	}
    	else {  //如果该road存在
    	   if(road.getRoadID().equals(curRoadID)){ //该road是当前车来的road
    		   continue;
    	   } 
    	   else{ //该road不是车当前位于的road，也就是车可能能去的road
    		   ArrayList<Integer> LeftLanesLength= getLeftLanesLength(roads.get(i),curCross);
    		   if(LeftLanesLength==null) {//单向，方向上不能通行
    			   continue;
    		   }
    		   else { //方向上能通行
    		   if(!road.isDuplex()) {//单向
    		       
    		    }                                                   
    	   }
    	}
    }
		
		
	}
	
	/*
	 * 判断优先级是否冲突，有冲突则看是否有回退的车（有冲突的车可能也不会回退，仍旧可以在当前道路）,并更新有变动的车辆的相关信息
	 * @param car：当前车；road：当前车所在道路
	 * @return List<Car> cars:回退车辆集合
	 */
	public static List<Car> checkPriority(Car car, Road road){
		
	}
}

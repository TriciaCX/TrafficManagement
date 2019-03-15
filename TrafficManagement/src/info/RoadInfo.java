package info;
// 获取道路信息

import java.util.Vector;

import vo.Lane;
import vo.Road;

public class RoadInfo {
	//获取实际的被占用的容量，也就是现在车道上所有车辆数目
	 public static int  getAllLanesCapacity (Road myRoad) {
		 Vector<Lane> lanes=myRoad.getLanes();
		 int ans=0;
		 for(Lane l:lanes) {
			 ans+=LaneInfo.getCarsNum(l);
		 }
		 return ans;
}
	 //返回在当前road还可以跑的车
		public static int getLeftCarsNum(Road myRoad) {

		}
	 
	

}

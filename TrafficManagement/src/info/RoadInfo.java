package info;
// ��ȡ��·��Ϣ

import java.util.Vector;

import vo.Lane;
import vo.Road;

public class RoadInfo {
	//��ȡʵ�ʵı�ռ�õ�������Ҳ�������ڳ��������г�����Ŀ
	 public static int  getAllLanesCapacity (Road myRoad) {
		 Vector<Lane> lanes=myRoad.getLanes();
		 int ans=0;
		 for(Lane l:lanes) {
			 ans+=LaneInfo.getCarsNum(l);
		 }
		 return ans;
}
	 //�����ڵ�ǰroad�������ܵĳ�
		public static int getLeftCarsNum(Road myRoad) {

		}
	 
	

}

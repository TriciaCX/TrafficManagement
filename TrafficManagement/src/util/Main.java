package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vo.Road;
import vo.Car;
import vo.Cross;

public class Main {

	public static void main(String[] args) {
		/** 
	     * @version 2019-3-19
	     */
		//读取road.txt文件，并将它进行预处理
		String[] roadString=IOUtil.read("D:\\eclipse-workspace\\Huawei\\src\\util\\1-map-training-1\\road.txt", null);
		ArrayList<Road> listRoad=PreprocUtil.PreRoadData(roadString);
		
		//读取cross.txt文件，并将它进行预处理
		String[] crossString=IOUtil.read("D:\\\\eclipse-workspace\\\\Huawei\\\\src\\\\util\\\\1-map-training-1\\\\cross.txt", null);
		ArrayList<Cross> listCross=PreprocUtil.PreCrossData(crossString);
		
		//读取car.txt文件，并将它进行预处理,其中ans为规划结果
		String[] carString=IOUtil.read("D:\\\\eclipse-workspace\\\\Huawei\\\\src\\\\util\\\\1-map-training-1\\\\car.txt", null);
		Map<String,String>ansMap=new HashMap<>();
		String[] ans =new String[carString.length];
		ans[0]="#carID, StartTime, RoadID...";
		ArrayList<Car> listCar=PreprocUtil.PreCarData(carString,ansMap,ans);
		
		//程序起始时间
		long t1=System.currentTimeMillis();	
		//跑起来。。。
		
		int t=0;
		while(true) {
			Share.classifyCars(listCar);
			while(!wait.isEmpty()) {
			Car car=Share.getMaxVelocityCarFromwait();
			//找最短路径（第一个节点可达）
			//
			Share.findNextCross(car);
			}
			if(frozen.isEmpty()) break;
			t++;
		}
		
		
		
		//跑完了，把ansMap更新到ans,同时计算所有车辆的运行时间carsRuntime和程序调度时间 scheduleTime
		int carsRuntime=0;
		int scheduleTime=0;
		for(String s:ans) {
			String carDriveInfo=ansMap.get(s);
			String carDriveRoads=carDriveInfo.substring(carDriveInfo.indexOf(','));
			String carDriveTime=carDriveInfo.substring(0,carDriveInfo.indexOf(','));
			carsRuntime+=Integer.valueOf(carDriveTime);
			scheduleTime=Math.max(scheduleTime, carsRuntime);
			s=s.concat(", "+carDriveRoads);
			s="("+s+")";
			}
		//将结果写出到文件
		IOUtil.write("D:\\eclipse-workspace\\Huawei\\src\\util\\1-map-training-1\\answer.txt", ans, false);
		//程序结束时间
		long t2=System.currentTimeMillis();
		//跑程序花费时间
		System.out.println("time:"+(t2-t1)+"ms");
		
		
		
		
	}
	/**
	 * ---规划数据更新到ansMap中----
	 * @param  传入的s为carID,CrossStartTime,Road如"300, 1, 501"
	 * @author Lulu
	 * @version 2019-3-17
	 */
	public static void updateAns(String s,Map<String,String>ansMap,String[] ans) {
		s= s.replaceAll(" ", "");    //去空格
		//分数据, tempStr[0]-carID, tempStr[1]-CrossStartTime, tempStr[2]-Road
		String[] tempStr = s.split(",");
		if(ansMap.get(tempStr[0])==null) {
			//如果是第一次给这辆 车进行规划，就要把这个时候的tempStr[1]-CrossStartTime也放到ans里面的， 格式是carID，CrossStartTime
			ansMap.put(tempStr[0],s.substring(s.indexOf(',')+1));
			for(String i:ans) {
				if(i==tempStr[0])
					i.concat(", "+tempStr[1]);
			}
			return;
		}
		String carDriveInfo=ansMap.get(tempStr[0]);	
		//把后面的规划加进去
		carDriveInfo.concat(", "+tempStr[2].toString());
		//更新时间
		String carDriveTime=carDriveInfo.substring(0,carDriveInfo.indexOf(','));
		int tem=Integer.valueOf(carDriveTime)+Integer.valueOf(tempStr[1]);
		//更新这辆车的规划信息
		ansMap.put(tempStr[0],"Integer.toString(tem)+carDriveInfo.substring(carDriveInfo.indexOf(','),carDriveInfo.length())");

	}

}

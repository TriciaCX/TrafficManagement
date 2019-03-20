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
		//��ȡroad.txt�ļ�������������Ԥ����
		String[] roadString=IOUtil.read("D:\\eclipse-workspace\\Huawei\\src\\util\\1-map-training-1\\road.txt", null);
		ArrayList<Road> listRoad=PreprocUtil.PreRoadData(roadString);
		
		//��ȡcross.txt�ļ�������������Ԥ����
		String[] crossString=IOUtil.read("D:\\\\eclipse-workspace\\\\Huawei\\\\src\\\\util\\\\1-map-training-1\\\\cross.txt", null);
		ArrayList<Cross> listCross=PreprocUtil.PreCrossData(crossString);
		
		//��ȡcar.txt�ļ�������������Ԥ����,����ansΪ�滮���
		String[] carString=IOUtil.read("D:\\\\eclipse-workspace\\\\Huawei\\\\src\\\\util\\\\1-map-training-1\\\\car.txt", null);
		Map<String,String>ansMap=new HashMap<>();
		String[] ans =new String[carString.length];
		ans[0]="#carID, StartTime, RoadID...";
		ArrayList<Car> listCar=PreprocUtil.PreCarData(carString,ansMap,ans);
		
		//������ʼʱ��
		long t1=System.currentTimeMillis();	
		//������������
		
		int t=0;
		while(true) {
			Share.classifyCars(listCar);
			while(!wait.isEmpty()) {
			Car car=Share.getMaxVelocityCarFromwait();
			//�����·������һ���ڵ�ɴ
			//
			Share.findNextCross(car);
			}
			if(frozen.isEmpty()) break;
			t++;
		}
		
		
		
		//�����ˣ���ansMap���µ�ans,ͬʱ�������г���������ʱ��carsRuntime�ͳ������ʱ�� scheduleTime
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
		//�����д�����ļ�
		IOUtil.write("D:\\eclipse-workspace\\Huawei\\src\\util\\1-map-training-1\\answer.txt", ans, false);
		//�������ʱ��
		long t2=System.currentTimeMillis();
		//�ܳ��򻨷�ʱ��
		System.out.println("time:"+(t2-t1)+"ms");
		
		
		
		
	}
	/**
	 * ---�滮���ݸ��µ�ansMap��----
	 * @param  �����sΪcarID,CrossStartTime,Road��"300, 1, 501"
	 * @author Lulu
	 * @version 2019-3-17
	 */
	public static void updateAns(String s,Map<String,String>ansMap,String[] ans) {
		s= s.replaceAll(" ", "");    //ȥ�ո�
		//������, tempStr[0]-carID, tempStr[1]-CrossStartTime, tempStr[2]-Road
		String[] tempStr = s.split(",");
		if(ansMap.get(tempStr[0])==null) {
			//����ǵ�һ�θ����� �����й滮����Ҫ�����ʱ���tempStr[1]-CrossStartTimeҲ�ŵ�ans����ģ� ��ʽ��carID��CrossStartTime
			ansMap.put(tempStr[0],s.substring(s.indexOf(',')+1));
			for(String i:ans) {
				if(i==tempStr[0])
					i.concat(", "+tempStr[1]);
			}
			return;
		}
		String carDriveInfo=ansMap.get(tempStr[0]);	
		//�Ѻ���Ĺ滮�ӽ�ȥ
		carDriveInfo.concat(", "+tempStr[2].toString());
		//����ʱ��
		String carDriveTime=carDriveInfo.substring(0,carDriveInfo.indexOf(','));
		int tem=Integer.valueOf(carDriveTime)+Integer.valueOf(tempStr[1]);
		//�����������Ĺ滮��Ϣ
		ansMap.put(tempStr[0],"Integer.toString(tem)+carDriveInfo.substring(carDriveInfo.indexOf(','),carDriveInfo.length())");

	}

}

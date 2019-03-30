package util;
import vo.Road;
import vo.Car;
import vo.Cross;
import vo.Lane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PreprocUtil {
	/**
	 * ---car����Ԥ����----
	 * @param  �����car.txt��ʱ�䡢˳�����
	 * car���ݸ�ʽ(id,from,to,speed,planTime)����(10000, 18, 50, 8, 3)����Ӧ���ݸ�ʽstring,string,string,int,string.���˴�Ĭ�϶���Ķ���string
	 * @version 2019-3-26
	 */
	public static ArrayList<Car> PreCarData(String[] strings,Map<String,String> ansMap,String[] ans){
		//����һ�����ڴ��car��Ϣ��carList.
		ArrayList<Car> carList = new ArrayList<Car>(); 
		int num = strings.length;   //�������ݵ�������
		for(int i=1;i<num;i++) {    //���ǵ���һ����#��ͷ��ע����Ϣ��Ӧ��i=1��ʼ��
			strings[i]= strings[i].replaceAll("\\(|\\)", "");    //ȥ����
			strings[i]= strings[i].replaceAll(" ", "");    //ȥ�ո�

			//������, tempStr[0]-id, tempStr[1]-from, tempStr[2]-to,tempStr[3]-speed,tempStr[4]-planTime,
			String[] tempStr = strings[i].split(",");
			//Ԥ����һЩ��Ϣ��ȥ
		
				
			ans[i]=tempStr[0];
			ansMap.put(tempStr[0],null);
			//����tempStr�ж��������ʵ����car
			int maxVelocity = Integer.valueOf(tempStr[3]);
			int planTime = Integer.valueOf(tempStr[4]);
			Car c = new Car(tempStr[0],tempStr[1],tempStr[2],maxVelocity,planTime);
			carList.add(c);
		}

		return carList;
	}

	/**
	 * ---car����Ԥ����----
	 * car���ݸ�ʽ(id,from,to,speed,planTime)����(10000, 18, 50, 8, 3)����Ӧ���ݸ�ʽstring,cross,cross,int,string.���˴�Ĭ�϶���Ķ���string
	 * @return carMap<carID,car>
	 * @version 2019-3-21
	 */
	public static HashMap<String,Car> PreCarDataMap(ArrayList<Car> carList){
		HashMap<String,Car> carMap = new HashMap<String,Car>();
		for(Car car:carList) {
			carMap.put(car.getCarID(),car);
		}
		return carMap;
	}

	//************************************Road data process************************************	
	
	/**
	 * ---road����Ԥ����---
	 * road���ݸ�ʽ--(id,length,speed,channel,from,to,isDuplex)������·id����·���ȣ�������٣�������Ŀ����ʼ��id���յ�id���Ƿ�˫��ע��1��˫��0������
	 * ��Ӧ�ĸ�ʽΪ---string,int,int,int,Cross,Cross,boolean
	 * @param strings
	 * @return roadList   
	 * @version 2019-3-16
	 */
	public static ArrayList<Road> PreRoadData(String[] strings){
		ArrayList<Road> roadList = new ArrayList<Road>(); 
		int num = strings.length;   //�������ݵ�������
		for(int i=1;i<num;i++) {    //���ǵ���һ����#��ͷ��ע����Ϣ��Ӧ��i=1��ʼ��
			strings[i]= strings[i].replaceAll("\\(|\\)", "");    //ȥ����
			strings[i]= strings[i].replaceAll(" ", "");    //ȥ�ո�
			//������, tempStr[0]-id, tempStr[1]-length, tempStr[2]-speed, tempStr[3]-channel,tempStr[4]-from,tempStr[5]-to,tempStr[6]-isDuplex
			String[] tempStr = strings[i].split(",");
	
			int length = Integer.valueOf(tempStr[1]);
			int speed = Integer.valueOf(tempStr[2]);
			int channel = Integer.valueOf(tempStr[3]);
			boolean isDuplex = false;
			if(tempStr[6].equals("1")) {
				isDuplex = true;
			}else {
				isDuplex = false;
			}
			Road road = new Road(tempStr[0],length,speed,channel,tempStr[4],tempStr[5],isDuplex);
			//��ô˵��Ҫ�ȼ�һ����·��
				for(int j=0;j<road.getLanesNum();j++) {
					Lane lane=new Lane(j);
					road.getForwardLane().add(lane);
			
				}
			if(isDuplex){  //�����˫���·
				for(int j=0;j<road.getLanesNum();j++) {
					Lane lane=new Lane(j);
					road.getBackwardLane().add(lane);
					
					
				}
				
			}
						
			roadList.add(road);
		}
		return roadList;
	}
	
	/**
	 * ---road����Ԥ����---
	 * road���ݸ�ʽ--(id,length,speed,channel,from,to,isDuplex)������·id����·���ȣ�������٣�������Ŀ����ʼ��id���յ�id���Ƿ�˫��ע��1��˫��0������
	 * ��Ӧ�ĸ�ʽΪ---string,int,int,int,Cross,Cross,boolean
	 * @param strings
	 * @return roadMap(roadID,road)  
	 * @version 2019-3-21
	 */
	public static HashMap<String,Road> PreRoadDataMap(ArrayList<Road> roadList){
		HashMap<String,Road> roadMap = new HashMap<String,Road>(); 
		for(Road road:roadList) {
			roadMap.put(road.getRoadID(),road);
		}
		Road jiaRoad = new Road("-1");
		roadMap.put("-1", jiaRoad);
		return roadMap;
	}

	
//************************************Cross data process************************************	
	
	/**
	 * ---cross���ݴ���
	 * cross���ݸ�ʽ--(id,roadId,roadId,roadId,roadId),(·��id,��·id,��·id,��·id,��·id)��-��-��-�� ע��-1��ʾû�и�����·
	 * @param strings
	 * @return crossList
	 * @version 2019-3-23
	 */

	public static ArrayList<Cross> PreCrossData(String[] strings,HashMap<String,Road> roadMap){
		ArrayList<Cross> crossList = new ArrayList<Cross>(); 
		int num = strings.length;   //�������ݵ�������
		for(int i=1;i<num;i++) {    //���ǵ���һ����#��ͷ��ע����Ϣ��Ӧ��i=1��ʼ��
			strings[i]= strings[i].replaceAll("\\(|\\)", "");    //ȥ����
			strings[i]= strings[i].replaceAll(" ", "");    //ȥ�ո�
			//������, tempStr[0]-crossId, tempStr[1]-roadId, tempStr[2]-roadId, tempStr[3]-roadId,tempStr[4]-roadId
			String[] tempStr = strings[i].split(","); 
			ArrayList<Road> roadIDList = new ArrayList<Road>();
			if(!tempStr[1].equals("-1")) {   //RoadID=-1��ʾû������·
				roadIDList.add(roadMap.get(tempStr[1]));
			}else {
				roadIDList.add(roadMap.get("-1"));
			}
			if(!tempStr[2].equals("-1")) {
				roadIDList.add(roadMap.get(tempStr[2]));
			}else {
				roadIDList.add(roadMap.get("-1"));
			}
			if(!tempStr[3].equals("-1")) {
				roadIDList.add(roadMap.get(tempStr[3]));
			}else {
				roadIDList.add(roadMap.get("-1"));
			}
			if(!tempStr[4].equals("-1")) {
				roadIDList.add(roadMap.get(tempStr[4]));
			}else {
				roadIDList.add(roadMap.get("-1"));
			}
			Cross cross = new Cross(tempStr[0],roadMap.get(tempStr[1]),roadMap.get(tempStr[2]),roadMap.get(tempStr[3]),roadMap.get(tempStr[4]),roadIDList);
			crossList.add(cross);
		}
		return crossList;
	}

	/**
	 * ---cross���ݴ���
	 * cross���ݸ�ʽ--(id,roadId,roadId,roadId,roadId),(·��id,��·id,��·id,��·id,��·id)��-��-��-�� ע��-1��ʾû�и�����·
	 * @param strings
	 * @return crossMap(crossID,cross)
	 * @version 2019-3-21
	 */
	public static HashMap<String,Cross> PreCrossDataMap(ArrayList<Cross> crossList){
		HashMap<String,Cross> crossMap = new HashMap<String,Cross>(); 
		for(Cross cross:crossList) {
			crossMap.put(cross.getCrossID(),cross);
		}
		return crossMap;
	}
}

package util;
import vo.Road;
import vo.Car;
import vo.Cross;
import vo.Lane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import util.MyComparator;


public class PreprocUtil {
	/**
	 * ---car����Ԥ����----
	 * @param  �����car.txt��ʱ�䡢˳�����
	 * car���ݸ�ʽ(id,from,to,speed,planTime)����(10000, 18, 50, 8, 3)����Ӧ���ݸ�ʽstring,cross,cross,int,string.���˴�Ĭ�϶���Ķ���string
	 * @author Tricia
	 * @version 2019-3-16
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
			Cross from = new Cross(tempStr[1]);
			Cross to = new Cross(tempStr[2]);
			int planTime = Integer.valueOf(tempStr[4]);
			int NextPos = -2;
			Car c = new Car(tempStr[0],from,to,maxVelocity,planTime,NextPos);
			carList.add(c);
		}

		//��carList����maxVelocity����,����
		MyComparator mc = new MyComparator();
		Collections.sort(carList, mc);

		return carList;
	}

	/**
	 * ---road����Ԥ����---
	 * road���ݸ�ʽ--(id,length,speed,channel,from,to,isDuplex)������·id����·���ȣ�������٣�������Ŀ����ʼ��id���յ�id���Ƿ�˫��ע��1��˫��0������
	 * ��Ӧ�ĸ�ʽΪ---string,int,int,int,Cross,Cross,boolean
	 * @param strings
	 * @return roadList   
	 * @author Tricia
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
			//for(String s:tempStr)
			//	s.trim();
			int length = Integer.valueOf(tempStr[1]);
			int speed = Integer.valueOf(tempStr[2]);
			int channel = Integer.valueOf(tempStr[3]);
			Cross from = new Cross(tempStr[4]);
			Cross to = new Cross(tempStr[5]);
			boolean isDuplex = false;
			if(tempStr[6].equals("1")) {
				isDuplex = true;
			}else {
				isDuplex = false;
			}
			Road road = new Road(tempStr[0],length,speed,channel,from,to,isDuplex);
			if(isDuplex==false) {   //����ǵ����·
				for(int j=0;j<road.getLanesNum();j++) {
					Lane lane=new Lane(j,null);
					road.getLanes().add(lane);
				}
			}else {  //�����˫���·
				for(int j=road.getLanesNum();j<2*road.getLanesNum();j++) {
					Lane lane=new Lane(j,null);
					road.getLanes().add(lane);
				}
			}
			roadList.add(road);
		}
		return roadList;
	}

	/**
	 * ---cross���ݴ���
	 * cross���ݸ�ʽ--(id,roadId,roadId,roadId,roadId),(·��id,��·id,��·id,��·id,��·id)��-��-��-�� ע��-1��ʾû�и�����·
	 * @param strings
	 * @return crossList
	 * @author Tricia
	 * @version 2019-3-19
	 */

	public static ArrayList<Cross> PreCrossData(String[] strings){
		ArrayList<Cross> crossList = new ArrayList<Cross>(); 
		int num = strings.length;   //�������ݵ�������
		for(int i=1;i<num;i++) {    //���ǵ���һ����#��ͷ��ע����Ϣ��Ӧ��i=1��ʼ��
			strings[i]= strings[i].replaceAll("\\(|\\)", "");    //ȥ����
			strings[i]= strings[i].replaceAll(" ", "");    //ȥ�ո�
			//������, tempStr[0]-crossId, tempStr[1]-roadId, tempStr[2]-roadId, tempStr[3]-roadId,tempStr[4]-roadId
			String[] tempStr = strings[i].split(","); 
			ArrayList<Road> roadIDList = new ArrayList<Road>();
			Road upRoad =null;
			Road rightRoad =null;
			Road downRoad =null;
			Road leftRoad =null;
			if(tempStr[1]!="-1") {   //RoadID=-1��ʾû������·
				upRoad = new Road(tempStr[1]);
				roadIDList.add(upRoad);
			}else {
				roadIDList.add(upRoad);
			}
			if(tempStr[2]!="-1") {
				rightRoad = new Road(tempStr[2]);
				roadIDList.add(rightRoad);
			}else {
				roadIDList.add(rightRoad);
			}
			if(tempStr[3]!="-1") {
				downRoad = new Road(tempStr[3]);
				roadIDList.add(downRoad);
			}else {
				roadIDList.add(downRoad);
			}
			if(tempStr[4]!="-1") {
				leftRoad = new Road(tempStr[4]);
				roadIDList.add(leftRoad);
			}else {
				roadIDList.add(leftRoad);
			}
			Cross cross = new Cross(tempStr[0],upRoad,rightRoad,downRoad,leftRoad,roadIDList);
			crossList.add(cross);
		}
		return crossList;
	}

}

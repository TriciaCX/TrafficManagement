package util;

import java.util.ArrayList;
import java.util.Collections;


import vo.Car;
import vo.Cross;
import vo.Road;

public class ProDataUtil
{
	/**
	 * ---road����Ԥ����---
	 * road���ݸ�ʽ--(id,length,speed,channel,from,to,isDuplex)������·id����·���ȣ�������٣�������Ŀ����ʼ��id���յ�id���Ƿ�˫��ע��1��˫��0������
	 * ��Ӧ�ĸ�ʽΪ---string,int,int,int,Cross,Cross,boolean
	 * @param strings
	 * @return roadList   
	 * @author Tricia
	 * @version 2019-3-16
	 */
	public ArrayList<Road> PreRoadData(String[] strings){
		ArrayList<Road> roadList = new ArrayList<Road>(); 
		int num = strings.length;   //�������ݵ�������
		for(int i=1;i<num;i++) {    //���ǵ���һ����#��ͷ��ע����Ϣ��Ӧ��i=1��ʼ��
			strings[i]= strings[i].replaceAll("\\(|\\)", "");    //ȥ����
		    //������, tempStr[0]-id, tempStr[1]-length, tempStr[2]-speed, tempStr[3]-channel,tempStr[4]-from,tempStr[5]-to,tempStr[6]-isDuplex
			String[] tempStr = strings[i].split(","); 
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
	 * @version 2019-3-16
	 */
	
	public ArrayList<Cross> PreCrossData(String[] strings){
		ArrayList<Cross> crossList = new ArrayList<Cross>(); 
		int num = strings.length;   //�������ݵ�������
		for(int i=1;i<num;i++) {    //���ǵ���һ����#��ͷ��ע����Ϣ��Ӧ��i=1��ʼ��
			strings[i]= strings[i].replaceAll("\\(|\\)", "");    //ȥ����
		    //������, tempStr[0]-crossId, tempStr[1]-roadId, tempStr[2]-roadId, tempStr[3]-roadId,tempStr[4]-roadId
			String[] tempStr = strings[i].split(","); 
			Road upRoad = new Road(tempStr[1]);
			Road rightRoad = new Road(tempStr[2]);
			Road downRoad = new Road(tempStr[3]);
			Road leftRoad = new Road(tempStr[4]);
			Cross cross = new Cross(tempStr[0],upRoad, rightRoad,downRoad,leftRoad);
			crossList.add(cross);
			}
		return crossList;
	}
	
	
	/**
	 * ---car����Ԥ����----
	 * @param  �����car.txt��ʱ�䡢˳�����
	 * car���ݸ�ʽ(id,from,to,speed,planTime)����(10000, 18, 50, 8, 3)����Ӧ���ݸ�ʽstring,cross,cross,int,string.���˴�Ĭ�϶���Ķ���string
	 * @author Tricia
	 * @version 2019-3-16
	 */
	public ArrayList<Car> PreCarData(String[] strings){
		//����һ�����ڴ��car��Ϣ��carList.
		ArrayList<Car> carList = new ArrayList<Car>(); 
		int num = strings.length;   //�������ݵ�������
		for(int i=1;i<num;i++) {    //���ǵ���һ����#��ͷ��ע����Ϣ��Ӧ��i=1��ʼ��
			strings[i]= strings[i].replaceAll("\\(|\\)", "");    //ȥ����
		    //������, tempStr[0]-id, tempStr[1]-from, tempStr[2]-to,tempStr[3]-speed,tempStr[4]-planTime,
			String[] tempStr = strings[i].split(","); 
			//����tempStr�ж��������ʵ����car
			int maxVelocity = Integer.valueOf(tempStr[3]);
			Cross from = new Cross(tempStr[1]);
			Cross to = new Cross(tempStr[2]);
			Car c = new Car(tempStr[0],from,to,maxVelocity,tempStr[4]);
			carList.add(c);
		}
		
		//��carList����maxVelocity����,����
		MyComparator mc = new MyComparator();
        Collections.sort(carList, mc);
        
        return carList;
	}
}

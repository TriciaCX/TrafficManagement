package util;

import java.util.ArrayList;
import java.util.List;

import vo.Car;
import vo.Cross;
import vo.Road;

public class Share {
	/*
	 * ��������Ϊ��ֹ״̬�����͵ȴ�����������𣬵ȴ�״̬�������ٶ���������
	 * @param cars�����г�������cars
	 * @return frozen����ֹ������wait���ȴ�����
	 */
	public static List<Car> classifyCars(){
		
	}
	
	/*
	 * �ӵȴ�״̬�����з����ٶ����ĳ�
	 */
	public static Car getMaxVelocityCarFromwait() {
		
	}
	
	/*
	 * �Ե�ǰ�����滮��һ·�ڣ��ж��ܽ���ĵ�·�����ݳ���ʣ����룩��дD�㷨����������ʻ����Զ���룬ѡ��һ��road��
	 * �����³������ȼ����ж����ȼ��Ƿ��ͻ��
	 * �����¸�·��λ�á�������·����·������������λ�ã������¸�·�ڵľ��룩�����������
	 * @param car����ǰ�������ȴ�״̬��
	 * @return 
	 */
	public static void findNextCross(Car car) {
	//����ó�������һ�����ܽ����·��ʣ���������ж��ܽ���ĵ�·
    Cross curCross = car.getFromCross(); // ����ǰ·��
    ArrayList<Road> roads = new ArrayList<Road> ();
    roads = curCross.getRoadIDList();
    String curRoadID = car.getRoadID();  // ����ǰ��·
    int RoadLeftLength = 0;
    for(int i=0;i<3;i++) {
    	Road road = roads.get(i);
    	if(road==null) {
    		continue;
    	}
    	else {  //�����road����
    	   if(road.getRoadID().equals(curRoadID)){ //��road�ǵ�ǰ������road
    		   continue;
    	   } 
    	   else{ //��road���ǳ���ǰλ�ڵ�road��Ҳ���ǳ�������ȥ��road
    		   ArrayList<Integer> LeftLanesLength= getLeftLanesLength(roads.get(i),curCross);
    		   if(LeftLanesLength==null) {//���򣬷����ϲ���ͨ��
    			   continue;
    		   }
    		   else { //��������ͨ��
    		   if(!road.isDuplex()) {//����
    		       
    		    }                                                   
    	   }
    	}
    }
		
		
	}
	
	/*
	 * �ж����ȼ��Ƿ��ͻ���г�ͻ���Ƿ��л��˵ĳ����г�ͻ�ĳ�����Ҳ������ˣ��Ծɿ����ڵ�ǰ��·��,�������б䶯�ĳ����������Ϣ
	 * @param car����ǰ����road����ǰ�����ڵ�·
	 * @return List<Car> cars:���˳�������
	 */
	public static List<Car> checkPriority(Car car, Road road){
		
	}
}

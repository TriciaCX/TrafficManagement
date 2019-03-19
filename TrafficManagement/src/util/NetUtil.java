package util;
import java.util.*;

import vo.*;
import info.*;

public class NetUtil {
	//  ������״̬�Լ����� �������ƣ�����Դ���� 
	
	//��������״̬
	//����cross���
	
	/**
	 * @param s��Դ�ڵ�,t�ǻ�ڵ㣬crosses�����нڵ�ļ��ϣ�carVelocity�ǵ�ǰ��������ٶ�
	 * //Ѱ�ҷ�ʱ����·��
	 */
	public static void findMinTimePath(Cross t,Cross s,List<Cross> crosses,Car car) {
		List<Cross> known=new ArrayList<Cross>();
		List<Cross> unknown=new ArrayList<Cross>();
		int carVelocity=car.getMaxVelocity();
		for(Cross cross:crosses) {
			if(s.getCrossID()==cross.getCrossID()) {
				cross.dist=0;
				cross.known=false;
				cross.path=null;
			}
				
			else {
				cross.dist=Integer.MAX_VALUE;
				cross.known=false;
			}	
		}
		while(t.known==false) {
			Collections.sort(unknown,new Comparator<Cross>() {
	            @Override
	            public int compare(Cross o1, Cross o2) {
	                if(o2.dist > o1.dist) {
	                	return 1;
	                }
	                else if(o2.dist < o1.dist) {
	                	return -1;
	                }else {
	                	return 0;
	                }
	            }
	        });
			Cross v=unknown.get(unknown.size()-1);
			v.known=true;
			markNextCross(v.getLeftRoad(),v,carVelocity);
			markNextCross(v.getRightRoad(),v,carVelocity);
			markNextCross(v.getUpRoad(),v,carVelocity);
			markNextCross(v.getDownRoad(),v,carVelocity);
		}
	}
	
	
	/*
	 * @param Rode r ��·��rͨ����һ·��w��carVelocity�ǵ�ǰ��������ٶ�
	 * ���Rode rͨ�����һ�ڵ㣬����dist��path,v�ǵ�ǰ�ڵ�
	 */
	private static void markNextCross(Road r,Cross v,int carVelocity) {
		if(r!=null&&(r.isDuplex()||r.getFromCross()==v)&&RoadInfo.getLeftCarsNum(r,v.dist)>0) {
			Cross w=r.getFromCross().equals(v)?r.getToCross():r.getFromCross();
			if(!w.known) {
				int cvw=(r.getRoadLength())/(Math.min(carVelocity,RoadInfo.getMinVelocity(r,v.dist)));
				if(v.dist+cvw<w.dist) {
					w.dist=v.dist+cvw;
					w.path=v;
				}
			}
		}
	}
	
	/**
	 * @param s��Դ�ڵ㣬t�ǻ�ڵ�,T�Ǵ�ʱ��Ƭ
	 * @return String path (carID��crossStartTime,RoadID1,...,RoadIDn)
	 * //��ӡ��ǰʱ��T��������ʻ��·�������ѳ�����Ϣ������ʱ�䣬����ʱ�䣬�������ȼ����ڵ�ǰ��·����ʻ�ٶȣ���ӵ���·�ĳ�����
	 */
	public static String printPath(Cross s,Cross t,Car car,float T){
		float startTime=car.getStartTime();
		StringBuilder path=new StringBuilder();
		path.append(car.getCarID());
		path.append(car.getStartTime());
		addCross(s,t,startTime,T,path,car.getMaxVelocity());

		return path.toString();
	}
	private static void addCross(Cross s,Cross t,float startTime,float T,StringBuilder path,int carVelocity) {
		if(t.path!=null) {
			addCross(s,t.path,startTime,T,path,carVelocity);
			if(t.dist+startTime>T+1) return;
			Road r=findRoad(t.path,t);
			path.append(r.getRoadID());
			int indexOfLane=(RoadInfo.getCurCarsNum(r,t.dist+startTime)%r.getLanesNum())+1;
			Lane lane=r.getLanes().get(indexOfLane);
			String priority=getPriority(t);
			lane.cars.addLast(new CarInOutPriority(Math.min(carVelocity,RoadInfo.getMinVelocity(r,t.dist)),t.path.dist+startTime, t.dist+startTime, priority));
		}
		

	}
	
	/**
	 * @param ͨ��·��t�����ȼ�
	 * @return ֱ�У���3������ת����2������ת����1�����ȴ���������0��
	 */
	private static String getPriority(Cross t) {
		StringBuilder r1=new StringBuilder("");
		StringBuilder r2=new StringBuilder("");
		if(t.path.path!=null) {
			r1=findRodeNum(t.path.path, t.path);
			r2=findRodeNum(t.path, t);
		}
		else return "0";
		switch(r1.append(r2).toString()) {
		case "11": return "3";
		case "13": return "2";
		case "12": return "1";

		case "22": return "3";
		case "21": return "2";
		case "23": return "1";
		
		case "33": return "3";
		case "32": return "2";
		case "34": return "1";
		
		case "44": return "3";
		case "41": return "2";
		case "43": return "1";
		default: return null;
		}
	}
	
	/**
	 * @param �ҵ���·��s��·��t�ĵ�·
	 * @return Roadʵ��
	 */
	public static Road findRoad(Cross s,Cross t) {
		if(s.getDownRoad()!=null&&s.getDownRoad().getToCross().equals(t)) return s.getDownRoad();
		if(s.getLeftRoad()!=null&&s.getLeftRoad().getToCross().equals(t)) return s.getLeftRoad();
		if(s.getRightRoad()!=null&&s.getRightRoad().getToCross().equals(t)) return s.getRightRoad();
		if(s.getUpRoad()!=null&&s.getUpRoad().getToCross().equals(t)) return s.getUpRoad();
		return null;
	}
	
	/*
	 * @param�ҵ���·��s��·��t�ĵ�·���
	 * @return Up����1����Right����2����Down����3����Left����4��
	 */
	public static StringBuilder findRodeNum(Cross s,Cross t) {
		if(s.getDownRoad()!=null && s.getDownRoad().getToCross().equals(t)) return new StringBuilder("3");
		if(s.getLeftRoad()!=null && s.getLeftRoad().getToCross().equals(t)) return new StringBuilder("4");
		if(s.getRightRoad()!=null && s.getRightRoad().getToCross().equals(t)) return new StringBuilder("2");
		if(s.getUpRoad()!=null && s.getUpRoad().getToCross().equals(t)) return new StringBuilder("1");
		return null;
	}
	
	//��������״̬
	public static void updateNet() {
		
	}

}

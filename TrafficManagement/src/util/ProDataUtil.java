package util;

import java.util.ArrayList;
import java.util.Collections;


import vo.Car;
import vo.Cross;
import vo.Road;

public class ProDataUtil
{
	/**
	 * ---road数据预处理---
	 * road数据格式--(id,length,speed,channel,from,to,isDuplex)，（道路id，道路长度，最高限速，车道数目，起始点id，终点id，是否双向）注：1：双向；0：单向
	 * 相应的格式为---string,int,int,int,Cross,Cross,boolean
	 * @param strings
	 * @return roadList   
	 * @author Tricia
	 * @version 2019-3-16
	 */
	public ArrayList<Road> PreRoadData(String[] strings){
		ArrayList<Road> roadList = new ArrayList<Road>(); 
		int num = strings.length;   //读入数据的总组数
		for(int i=1;i<num;i++) {    //考虑到第一个是#开头的注释信息，应从i=1开始读
			strings[i]= strings[i].replaceAll("\\(|\\)", "");    //去括号
		    //分数据, tempStr[0]-id, tempStr[1]-length, tempStr[2]-speed, tempStr[3]-channel,tempStr[4]-from,tempStr[5]-to,tempStr[6]-isDuplex
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
	 * ---cross数据处理
	 * cross数据格式--(id,roadId,roadId,roadId,roadId),(路口id,道路id,道路id,道路id,道路id)上-右-下-左 注：-1表示没有该条道路
	 * @param strings
	 * @return crossList
	 * @author Tricia
	 * @version 2019-3-16
	 */
	
	public ArrayList<Cross> PreCrossData(String[] strings){
		ArrayList<Cross> crossList = new ArrayList<Cross>(); 
		int num = strings.length;   //读入数据的总组数
		for(int i=1;i<num;i++) {    //考虑到第一个是#开头的注释信息，应从i=1开始读
			strings[i]= strings[i].replaceAll("\\(|\\)", "");    //去括号
		    //分数据, tempStr[0]-crossId, tempStr[1]-roadId, tempStr[2]-roadId, tempStr[3]-roadId,tempStr[4]-roadId
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
	 * ---car数据预处理----
	 * @param  传入的car.txt按时间、顺序分类
	 * car数据格式(id,from,to,speed,planTime)，如(10000, 18, 50, 8, 3)，对应数据格式string,cross,cross,int,string.但此处默认读入的都是string
	 * @author Tricia
	 * @version 2019-3-16
	 */
	public ArrayList<Car> PreCarData(String[] strings){
		//构建一个用于存放car信息的carList.
		ArrayList<Car> carList = new ArrayList<Car>(); 
		int num = strings.length;   //读入数据的总组数
		for(int i=1;i<num;i++) {    //考虑到第一个是#开头的注释信息，应从i=1开始读
			strings[i]= strings[i].replaceAll("\\(|\\)", "");    //去括号
		    //分数据, tempStr[0]-id, tempStr[1]-from, tempStr[2]-to,tempStr[3]-speed,tempStr[4]-planTime,
			String[] tempStr = strings[i].split(","); 
			//根据tempStr中读入的数据实例化car
			int maxVelocity = Integer.valueOf(tempStr[3]);
			Cross from = new Cross(tempStr[1]);
			Cross to = new Cross(tempStr[2]);
			Car c = new Car(tempStr[0],from,to,maxVelocity,tempStr[4]);
			carList.add(c);
		}
		
		//对carList按照maxVelocity排序,降序
		MyComparator mc = new MyComparator();
        Collections.sort(carList, mc);
        
        return carList;
	}
}

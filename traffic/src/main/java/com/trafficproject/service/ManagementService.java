package com.trafficproject.service;

import com.trafficproject.service.model.CarModel;
import com.trafficproject.service.model.CrossModel;
import com.trafficproject.service.model.RoadModel;

import java.util.*;

public interface ManagementService {
    public ArrayList<CarModel> getListCar();

    public ArrayList<CrossModel> getListCross();

    public ArrayList<RoadModel> getListRoad();

    public LinkedList<CarModel> getGarageFrozen();

    public HashSet<CarModel> getNowInRoadCar();

    public HashSet<String> getArrivedCar();

    /**
     * 返回roadList中最长的道路
     *
     * @param roadList
     * @return 最大道路长度
     */
    public int getMaxRoadLength(ArrayList<RoadModel> roadList);

    /**
     * 对车遍历，是不是都是真实的位置了
     * <p>
     * *@param hasArrag=true,sheng=o
     *
     * @return
     * @author Lulu
     * @version 2019-3-26
     */
    public boolean isAllReal();

    /**
     * 对车遍历，是不是都是已经到达终点了
     * <p>
     * *@param
     *
     * @return
     * @author Lulu
     * @version 2019-3-26
     */
    public boolean isAllArrived();



    /**
     * 安排过本来就在路上走的车之后，要安排从车库来的车了，安排以后，统一把它的标志位设置成true,同时要把从车库来的车加到在路上的车里去
     */
    public void setNowInRoadCarFromGarageWait();

    /**
     * 一个时间片的末尾，将所有在路上行走的车的是否安排过都要置true或者false
     *
     * @param flag
     */
    public void setNowInRoadCarState(Boolean flag);


    public void carsFromGarageInsertToRoad(int t);



    /**
     * 对车辆进行所有操作
     *
     * @param carsFour
     * @param t
     */
    public void FourCarStateUnionProcess(LinkedList<CarModel> carsFour, int t);
}

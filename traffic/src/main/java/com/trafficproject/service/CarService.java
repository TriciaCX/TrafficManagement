package com.trafficproject.service;

import com.trafficproject.service.model.CarModel;
import com.trafficproject.service.model.LaneModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface CarService {
    CarModel getCarModelById (String carID);
    List<CarModel> listCar();
    Map<String,CarModel> mapCar();

    /**
     * 找出车辆的CurPos最小的车辆
     *
     * @param carList 车辆集合
     * @return CurPos最小的车辆
     * @author Dalton
     * @version 2019.3.26
     */
    public CarModel minCarCurPos(CarModel[] carList);

    /**
     * 查询当前搜索的道路和车的方向是否冲突
     *
     * @param roadID    当前搜索道路
     * @param crossID 出发节点ID
     * @return true：方向一致；false：方向相反
     */
    public boolean isDirectionRight(String roadID, String crossID);

    /**
     * 在一个车辆集合中选出能过路口的车
     *
     * @param roadID     当前的道路
     * @param carList  需要判断是否通过路口的车集合
     * @param laneList 车集合所在的lane集合
     * @param carIndex 车集合中各个车处于各自lane上的第几个位置
     * @return 需要过路口的车的集合，大小和输入的carList相同，只是将不能过路口的车的位置放置null；
     * @author Dalton
     * @version 2019.3.28
     */
    public CarModel[] ThroughCar(String roadID, CarModel[] carList, LinkedList<LaneModel> laneList, int[] carIndex);

    /**
     * 判断该车能不能通过路口，如果一条lane上前车不能通过路口，该车也不能； 如果没有前车或者前车可以通过路口，则根据行驶速度和所在位置判断是否可以通过路口
     *
     * @param roadID     当前道路
     * @param carID      当前车辆
     * @param laneList 车所在的lane集合
     * @param carIndex 车处于各自lane上的第几个位置
     * @author Dalton
     * @version 2019.3.28
     */
    public void canThrough(String roadID, String carID, LinkedList<LaneModel> laneList, int[] carIndex);
}

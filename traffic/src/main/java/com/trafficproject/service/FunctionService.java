package com.trafficproject.service;

import com.trafficproject.service.model.CarModel;
import com.trafficproject.service.model.CrossModel;
import com.trafficproject.service.model.LaneModel;
import com.trafficproject.service.model.RoadModel;

import java.util.ArrayList;
import java.util.LinkedList;

public interface FunctionService {
    /**
     * sigmoid函数
     * @param x
     * @return
     */
    float sigmoid(int x);

    float swish(int x);

    void adjustW();

    /**
     * 查询当前搜索的道路和车的方向是否冲突
     * @param roadID    当前搜索道路
     * @param crossID 出发节点ID
     * @return true：方向一致；false：方向相反
     */
    boolean isDirectionRight(String roadID, String crossID);

    /**
     * 在一个车辆集合中选出能过路口的车
     *
     * @param roadID     当前的道路
     * @param carList  需要判断是否通过路口的车集合
     * @param laneList 车集合所在的lane集合
     * @param carIndex 车集合中各个车处于各自lane上的第几个位置
     * @return 需要过路口的车的集合，大小和输入的carList相同，只是将不能过路口的车的位置放置null；
     */
    CarModel[] ThroughCar(String roadID, CarModel[] carList, LinkedList<LaneModel> laneList, int[] carIndex);

    /**
     * 判断该车能不能通过路口，如果一条lane上前车不能通过路口，该车也不能； 如果没有前车或者前车可以通过路口，则根据行驶速度和所在位置判断是否可以通过路口
     *
     * @param roadID     当前道路
     * @param carID      当前车辆
     * @param laneList 车所在的lane集合
     * @param carIndex 车处于各自lane上的第几个位置
     */
    void canThrough(String roadID, String carID, LinkedList<LaneModel> laneList, int[] carIndex);

    /**
     * 对这个路口取出这时候安排的那4辆车
     * *@param carsFour最多只有四个车
     */
    LinkedList<CarModel> extractFourCar(CrossModel s);

    /**
     * cost3,sigmoid(carInCrossNum)
     * @param carToCrossID
     * @return
     */
    int getCrossCarNum(String carToCrossID);

    void markNextCross(String roadID, String sID);

    /**
     * 根据一条路上,车是否出路口，lane的顺序和lane上车辆距离目的路口的距离，构建车的链表，不考虑hasArrangeOrNot为true的车
     * （首先考虑是否出路口，然后考虑距离，最后考虑车道顺序）
     * @param roadID：当前道路;crossID:车从哪个路口到这个路;
     * @return 获得当前道路和车辆行驶方向相同的所有lane上车辆的发车顺序链表。getfirst是先出发的车（头头）
     */
    LinkedList<String> getCarInRoad(String roadID, String crossID);

    /**
     * 根据一条路上,车是否出路口，lane的顺序和lane上车辆距离目的路口的距离，给出排在第一个的车ID，不考虑hasArrangeOrNot为true的车
     * （首先考虑是否出路口，然后考虑距离，最后考虑车道顺序）
     * @param roadID：当前道路;crossID:车从哪个路口到这个路;
     * @return 获得当前道路和车辆行驶方向相同的所有lane上的第一辆车。getfirst是先出发的车（头头）
     */
    String getFirstCarInRoad(String roadID, String crossID);

    /**
     * 根据一条路上,车是否出路口，lane的顺序和lane上车辆距离目的路口的距离，给出排在第一个的车ID，该路上不可能有false车
     * （首先考虑是否出路口，然后考虑距离，最后考虑车道顺序）
     * @param roadID    当前道路
     * @param crossID 车从哪个路口到这个路;
     * @return 获得当前道路和车辆行驶方向相同的所有lane上的第一辆车。getfirst是先出发的车（头头）
     */
    String getFirstTrueCarInRoad(String roadID, String crossID);

    /**
     * 更新头车所在方向lane的所有车的相关属性
     * @param toRoad 车下一个时刻要去的路
     * @param car 当前车
     * @param r 车当前所在的路
     * @param state 要将车更改为的数字状态，如果是-2则不更改此状态
     * @param setHasArrangedOrNot true则要更改此属性
     * @param setSheng true则要更改此属性，变为0
     * @param setCurPos true则要更新车的位置，并动起来
     * @param setNextRoadID true则要更新车的此属性
     * @param setPriority true则要更新车的此属性
     */
    void setCarInRoad(RoadModel toRoad, CarModel car, RoadModel r, int state, boolean setHasArrangedOrNot, boolean setSheng, boolean setCurPos, boolean setNextRoadID, boolean setPriority);


    /**
     * 返回roadList中最长的道路
     * @param roadList
     * @return 最大道路长度
     */
    int getMaxRoadLength(ArrayList<RoadModel> roadList);

    /**
     * 更新从车库出发的车的信息，判断车是否能够插入到规划的道路中，如果能，查看ID优先级是否冲突，有冲突则看
     * 是否有回退的车（存入reArrangeCars中），如果不能，直接加到存入reArrangeCars中
     * @param car：当前车；road：要走的道路；virtualCarsHashMap：存好的车的原始状态；reArrangeCars：回退车辆集合，就是可能会有ID小但是速度快的车被先从车库里面取出来，在同一时刻，起始路口相同，要选择的道路也是一条，这时候就要回退了；MapRoad：用来返回对象的
     * @return 这里返回两个元素，false:第一个插不进去，第二个：有没有回滚？没有
     */
    boolean[] checkIDPriority(CarModel car, RoadModel road, LinkedList<CarModel> reArrangeCars, int t);

    /**
     * 对当前车辆规划下一道路,2019.3.28 保证第一条路可走（考虑rest），从car.getCurToCross开始找路径,可以进一步优化：此时第一条路不能走不一定真的在该时刻不能走，需要安排完一些车后可能会有空间
     *
     * @param car           当前车辆（两个等待状态）
     * @return 下一条道路
     */
    RoadModel findNextCross(CarModel car);
}
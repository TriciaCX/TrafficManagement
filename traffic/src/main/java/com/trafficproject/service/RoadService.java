package com.trafficproject.service;

import com.trafficproject.service.model.CrossModel;
import com.trafficproject.service.model.RoadModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface RoadService {
    RoadModel getRoadModelById(String roadID);
    List<RoadModel> listRoad();
    Map<String, RoadModel> mapRoad();

    /**
     * 根据一条路上,车是否出路口，lane的顺序和lane上车辆距离目的路口的距离，构建车的链表，不考虑hasArrangeOrNot为true的车
     * （首先考虑是否出路口，然后考虑距离，最后考虑车道顺序）
     *
     * @param roadID：当前道路;crossID:车从哪个路口到这个路;
     * @return 获得当前道路和车辆行驶方向相同的所有lane上车辆的发车顺序链表。getfirst是先出发的车（头头）
     * @author Dalton
     * @version 2019.3.28
     */
    public LinkedList<String> getCarInRoad(String roadID, String crossID);

    /**
     * 根据一条路上,车是否出路口，lane的顺序和lane上车辆距离目的路口的距离，给出排在第一个的车ID，不考虑hasArrangeOrNot为true的车
     * （首先考虑是否出路口，然后考虑距离，最后考虑车道顺序）
     *
     * @param roadID：当前道路;crossID:车从哪个路口到这个路;
     * @return 获得当前道路和车辆行驶方向相同的所有lane上的第一辆车。getfirst是先出发的车（头头）
     * @author Dalton
     * @version 2019.3.30
     */
    public String getFirstCarInRoad(String roadID, String crossID);

    /**
     * 根据一条路上,车是否出路口，lane的顺序和lane上车辆距离目的路口的距离，给出排在第一个的车ID，该路上不可能有false车
     * （首先考虑是否出路口，然后考虑距离，最后考虑车道顺序）
     *
     * @param roadID    当前道路
     * @param crossID 车从哪个路口到这个路;
     * @return 获得当前道路和车辆行驶方向相同的所有lane上的第一辆车。getfirst是先出发的车（头头）
     * @author Dalton
     * @version 2019.4.13
     */
    public String getFirstTrueCarInRoad(String roadID, String crossID);

    /**
     * 查询当前道路是否是真正有可走的空间，因为需要先走车道号小的车道。 小车道没空间，但车没有更新过直接判为没路； 没更新有空间则有路；更新过没空间继续查找大的lane； 更新过有空间为有路；
     *
     * @param roadID    当前搜索的道路 ;
     * @param CrossID crossID:出发节点ID
     * @autour Dalton
     * @return -2:所有lane都更新过了,也没有空间；-1：未更新过而没有空间；1：未更新过而有空间；2：更新过而有空间；
     */
    public int hasLeftLength(String roadID, String CrossID);

    /**
     * 根据道路road以及当前路口找到道路通向的下一路口
     *
     * @param roadID
     * @param sID
     * @return
     */
    public CrossModel getCross(String roadID, String sID);

    /**
     * cost1
     *
     * @param roadID
     * @return NormalizedRoadLength
     * @author Tricia
     * @version 2019-3-21
     */
    public float getNormalizedRoadLength(String roadID, int maxRoadLength);

    /**
     * 返回下一时刻road的每一条lane还能进入多少辆车,即车位于NextPos时还剩多少位置
     *
     * @param roadID
     * @param crossID
     * @return
     */
    public ArrayList<Integer> getLeftLanesLength(String roadID, String crossID);
}

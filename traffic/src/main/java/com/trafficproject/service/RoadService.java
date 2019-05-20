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
     * 查询当前道路是否是真正有可走的空间，因为需要先走车道号小的车道。 小车道没空间，但车没有更新过直接判为没路； 没更新有空间则有路；更新过没空间继续查找大的lane； 更新过有空间为有路；
     *
     * @param roadID    当前搜索的道路 ;
     * @param CrossID crossID:出发节点ID
     * @autour Dalton
     * @return -2:所有lane都更新过了,也没有空间；-1：未更新过而没有空间；1：未更新过而有空间；2：更新过而有空间；
     */
    int hasLeftLength(String roadID, String CrossID);

    /**
     * 根据道路road以及当前路口找到道路通向的下一路口
     *
     * @param roadID
     * @param sID
     * @return
     */
    String getCross(String roadID, String sID);

    /**
     * cost1
     *
     * @param roadID
     * @return NormalizedRoadLength
     * @author Tricia
     * @version 2019-3-21
     */
    float getNormalizedRoadLength(String roadID, int maxRoadLength);

    /**
     * 返回下一时刻road的每一条lane还能进入多少辆车,即车位于NextPos时还剩多少位置
     *
     * @param roadID
     * @param crossID
     * @return
     */
    ArrayList<Integer> getLeftLanesLength(String roadID, String crossID);
    /**
     *
     * @param roadID
     * @param crossID
     * @return NormalizedRoadLeftLength
     */
    float getNormalizedRoadLeftLength(String roadID, String crossID);

}

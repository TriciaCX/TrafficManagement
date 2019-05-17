package com.trafficproject.service;

import com.trafficproject.service.model.CarModel;
import com.trafficproject.service.model.CrossModel;
import com.trafficproject.service.model.RoadModel;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public interface CrossService {
    CrossModel getCrossModelById (String crossID);
    List<CrossModel> listCross();
    Map<String,CrossModel> mapCross();

    /**
     * 对这个路口取出这时候安排的那4辆车
     * <p>
     * *@param carsFour最多只有四个车
     *
     * @return
     * @author Lulu
     * @version 2019-3-26
     */
    public LinkedList<CarModel> extractFourCar(CrossModel s);

    /**
     * 找到从路口s到路口t的道路
     *
     * @param crossSID
     * @param crossTID
     * @return
     */
    public RoadModel findRoad(String crossSID, String crossTID);

    public void markNextCross(String roadID, String sID);

    /**
     * @param sID:当前出发路口ID
     * @param tID：目的路口ID
     * @return 从当前出发路口出发的第一条道路
     * @author Dalton
     * @version 2019.3.22
     */
    public RoadModel findFirstRoad(String sID, String tID);

    /**
     * @param fromRoadID
     * @param toRoadID
     * @param crossID
     * @return 找到从路from通过路口c到路to的这个转弯的优先级
     */
    public int setPriority(String fromRoadID, String toRoadID, String crossID);
}

package com.trafficproject.service;
import com.trafficproject.service.model.CrossModel;
import java.util.List;
import java.util.Map;


public interface CrossService {
    CrossModel getCrossModelById (String crossID);
    List<CrossModel> listCross();
    Map<String,CrossModel> mapCross();



    /**
     * 找到从路口s到路口t的道路
     *
     * @param crossSID
     * @param crossTID
     * @return
     */
    String findRoad(String crossSID, String crossTID);



    /**
     * @param sID:当前出发路口ID
     * @param tID：目的路口ID
     * @return 从当前出发路口出发的第一条道路
     * @author Dalton
     * @version 2019.3.22
     */
    String findFirstRoad(String sID, String tID);

    /**
     * @param fromRoadID
     * @param toRoadID
     * @param crossID
     * @return 找到从路from通过路口c到路to的这个转弯的优先级
     */
   int setPriority(String fromRoadID, String toRoadID, String crossID);
}

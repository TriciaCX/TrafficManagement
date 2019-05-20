package com.trafficproject.service.impl;

import com.trafficproject.dao.CrossDOMapper;
import com.trafficproject.dataobject.CrossDO;
import com.trafficproject.service.BaseService;
import com.trafficproject.service.CrossService;
import com.trafficproject.service.RoadService;
import com.trafficproject.service.model.CarModel;
import com.trafficproject.service.model.CrossModel;
import com.trafficproject.service.model.LaneModel;
import com.trafficproject.service.model.RoadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
//import java.util.stream.Collectors;

@Service
public class CrossServiceImpl extends BaseService implements CrossService {

    @Autowired
    private CrossDOMapper crossDOMapper;

    @Autowired
    private RoadService roadService;

    public CrossServiceImpl() {
        System.out.println("");
    }

    @Override
    public CrossModel getCrossModelById(String crossID) {
        if(crossID == null){
            return null;
        }
        CrossDO crossDO = crossDOMapper.selectByCrossId(crossID);
        return convertFromDataObject(crossDO);
    }

    private CrossModel convertFromDataObject(CrossDO crossDO) {
        if(crossDO == null){
            return null;
        }
        ArrayList<RoadModel> crossList = new ArrayList<RoadModel>();

        if(!crossDO.getUpRoadId().equals("-1")){
            //crossModel.setUpRoad(roadService.mapRoad().get(crossDO.getUpRoadId()));
           crossList.add(roadService.mapRoad().get(crossDO.getUpRoadId()));
        }else{
            crossList.add(new RoadModel("-1"));
        }

        if(!crossDO.getRightRoadId().equals("-1")){
           // crossModel.setRightRoad(roadService.mapRoad().get(crossDO.getRightRoadId()));
            crossList.add(roadService.mapRoad().get(crossDO.getRightRoadId()));
        }else {
            crossList.add(new RoadModel("-1"));
        }

        if(!crossDO.getDownRoadId().equals("-1")){
           // crossModel.setDownRoad(roadService.mapRoad().get(crossDO.getDownRoadId()));
            crossList.add(roadService.mapRoad().get(crossDO.getDownRoadId()));
        }else {
            crossList.add(new RoadModel("-1"));
        }
        if(!crossDO.getLeftRoadId().equals("-1")){
           // crossModel.setDownRoad(roadService.mapRoad().get(crossDO.getDownRoadId()));
            crossList.add(roadService.mapRoad().get(crossDO.getLeftRoadId()));
        }else {
            crossList.add(new RoadModel("-1"));
        }
        //CrossModel crossModel = new CrossModel(crossDO.getCrossId(),roadService.mapRoad().get(crossDO.getUpRoadId()),
         //       roadService.mapRoad().get(crossDO.getRightRoadId()),roadService.mapRoad().get(crossDO.getDownRoadId()),
           //     roadService.mapRoad().get(crossDO.getLeftRoadId()));
        CrossModel crossModel = new CrossModel(crossDO.getCrossId(),crossList.get(0),crossList.get(1),
                crossList.get(2),crossList.get(3));
        return  crossModel;
    }

    @Override
    public List<CrossModel> listCross() {
        List<CrossDO> crossDOList = crossDOMapper.listCross();
        List<CrossModel> crossModelList = crossDOList.stream().map(crossDO -> {
            CrossModel crossModel = this.convertFromDataObject(crossDO);
            return crossModel;
        }).collect(Collectors.toList());
        return crossModelList;
    }

    @Override
    public Map<String, CrossModel> mapCross() {
        ArrayList<CrossModel> listCross  = (ArrayList<CrossModel>) this.listCross();
        Map<String,CrossModel> mapCross = new HashMap<>();
        for (CrossModel crossModel:listCross
             ) {
            mapCross.put(crossModel.getCrossID(),crossModel);
        }
        return mapCross;
    }



    /**
     * 找到从路口s到路口t的道路
     */
    public String findRoad(String crossSID, String crossTID) {
        if (crossSID.equals(crossTID)) {

            return null;
        }
        CrossModel s = getCrossModelById(crossSID);
        CrossModel t = getCrossModelById(crossTID);
        String[] roadss = s.getRoadIDList();
        String[] roadst = t.getRoadIDList();
        HashSet<String> set = new HashSet<>();
        String roadID = new String();
        for (String ss : roadss) {
            set.add(ss);
        }
        for (String ss : roadst) {
            if (!ss.equals("-1") && !set.add(ss)) {

                roadID = ss;
            }
        }
        return roadID;
    }




    /**
     * @param sID:当前出发路口ID
     * @param tID：目的路口ID
     * @return 从当前出发路口出发的第一条道路
     */
    public String findFirstRoad(String sID, String tID) {
        CrossModel s = getCrossModelById(sID);
        CrossModel t = getCrossModelById(tID);
        CrossModel temp = t;
        while (!temp.preCross.equals(s)) {
            temp = temp.preCross;
        }
        return findRoad(sID, temp.getCrossID());
    }

    /**
     * @param fromRoadID
     * @param toRoadID
     * @param crossID
     * @return 找到从路from通过路口c到路to的这个转弯的优先级
     */
    public int setPriority(String fromRoadID, String toRoadID, String crossID) {
        CrossModel c = getCrossModelById(crossID);
        int ans = 0;
        String[] roadlist = c.getRoadIDList();
        int i = -1, j = -1;
        int size = roadlist.length;
        while (i != -1 && j != -1) {
            for (int k = 0; k < size; k++) {
                if (roadlist[k] == null) {

                    continue;
                }
                if (roadlist[k].equals(fromRoadID)) {

                    i = k;
                } else if (roadlist[k].equals(toRoadID)) {
                    j = k;

                } else {
                }

            }
        }
        if (Math.abs(i - j) == 2) {

            ans = 3;
        } else if (i - j == -1 || (i == 3 && j == 0)) {
            ans = 2;
        } else {
            ans = 1;
        }

        return ans;

    }


}

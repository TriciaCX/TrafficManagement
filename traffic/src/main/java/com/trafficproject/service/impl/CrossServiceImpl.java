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
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Component
public class CrossServiceImpl extends BaseService implements CrossService {

    @Autowired
    private CrossDOMapper crossDOMapper;

    @Autowired
    private RoadService roadService;

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


    @Autowired
    private RoadServiceImpl roadServiceImpl;

    @Autowired
    private CarServiceImpl carServiceImpl;

    @Autowired
    private FunctionServiceImpl functionServiceImpl;


    /**
     * 对这个路口取出这时候安排的那4辆车
     * *@param carsFour最多只有四个车
     */
    public LinkedList<CarModel> extractFourCar(CrossModel s) {
        LinkedList<CarModel> carsFour = new LinkedList<>();
        if (!s.getDownRoad().getRoadID().equals("-1")) {
            String carID = roadServiceImpl.getFirstCarInRoad(s.getDownRoad().getRoadID(), s.getCrossID());
            if (carID != null) {
                carsFour.add(carServiceImpl.getCarModelById(carID));
            }
        }
        if (!s.getUpRoad().getRoadID().equals("-1")) {
            String carID = roadServiceImpl.getFirstCarInRoad(s.getUpRoad().getRoadID(), s.getCrossID());
            if (carID != null) {

                carsFour.add(carServiceImpl.getCarModelById(carID));
            }
        }
        if (!s.getLeftRoad().getRoadID().equals("-1")) {
            String carID = roadServiceImpl.getFirstCarInRoad(s.getLeftRoad().getRoadID(), s.getCrossID());
            if (carID != null) {

                carsFour.add(carServiceImpl.getCarModelById(carID));
            }
        }
        if (!s.getRightRoad().getRoadID().equals("-1")) {
            String carID = roadServiceImpl.getFirstCarInRoad(s.getRightRoad().getRoadID(), s.getCrossID());
            if (carID != null) {

                carsFour.add(carServiceImpl.getCarModelById(carID));
            }
        }

        return carsFour;

    }



    /**
     * 找到从路口s到路口t的道路
     *
     * @param crossSID
     * @param crossTID
     * @return
     */
    public RoadModel findRoad(String crossSID, String crossTID) {
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
        return roadServiceImpl.getRoadModelById(roadID);
    }


    public void markNextCross(String roadID, String sID) {
        RoadModel road=roadServiceImpl.getRoadModelById(roadID);
        CrossModel s=getCrossModelById(sID);
        CrossModel t = roadServiceImpl.getCross(roadID, sID);
        if (!t.isKnown) {
            // 自适应调整w2（和遇到前方）和w3（和回滚相关）
            // cost1:要去的那条路和最长的那条路的比值
            float NormalizedRoadLength = roadServiceImpl.getNormalizedRoadLength(roadID, maxRoadLength);
            // cost2：要去的那条路的剩余空间
            float NormalizedRoadLeftLength = roadServiceImpl.getNormalizedRoadLeftLength(roadID, s.getCrossID());
            // cost3：拥挤系数，其中CrossInfo.getCrossCarNum(s.getCrossID(), t.getCrossID())表示有多少辆车会通过这个路口
            float sigmoidCrossCarNum = functionServiceImpl.swish(getCrossCarNum(t.getCrossID()));
            float cost = w[0] * NormalizedRoadLength + w[1] * (1 - NormalizedRoadLeftLength) + w[2] * sigmoidCrossCarNum;
            if (s.cost + cost < t.cost) {
                t.cost = s.cost + cost;
                t.preCross = s;
            }
        }
//        try {
//            jp.proceed();
//        }catch (Throwable e){
//            System.out.println("AOP_adjustW wrong!");
//        }
        // functionServiceImpl.adjustW(Main.w);
    }

    /**
     * @param sID:当前出发路口ID
     * @param tID：目的路口ID
     * @return 从当前出发路口出发的第一条道路
     */
    public RoadModel findFirstRoad(String sID, String tID) {
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

    /**
     * cost3,sigmoid(carInCrossNum)
     * @param carToCrossID
     * @return
     */
    public int getCrossCarNum(String carToCrossID) {
        //Cross相连的道路上的所有车
        int carInCrossNum =0;
        int carInCrossSumNum =0;
        int carInReverseLaneNum =0;
        String[] roadIDList = getCrossModelById(carToCrossID).getRoadIDList();
        ArrayList<RoadModel> roadList = new ArrayList<RoadModel>();
        for(String s:roadIDList) {
            if (!s.equals("-1")) {
                roadList.add(roadServiceImpl.getRoadModelById(s));
            }
        }
        for(RoadModel road:roadList) {
            //road的toCross=car.curFromCross
            //****************寻找不能算的道路
            if(road.getFromCrossID().equals(carToCrossID)) {
                //计算这辆车的同一road的逆向lane上的路 （如果存在的话）
                for(LaneModel lane:road.getBackwardLane()) {
                    //如果lane上没有车
                    if(lane.carsInLane==null) {
                        carInReverseLaneNum = 0;
                    }else {
                        carInReverseLaneNum += lane.carsInLane.size();
                    }
                }
            }
            //forward道路
            LinkedList<LaneModel> forwardLanes = road.getForwardLane();
            for(LaneModel lane:forwardLanes) {
                if(lane.carsInLane==null) {
                    carInCrossSumNum +=0;
                }else {
                    carInCrossSumNum += lane.carsInLane.size();
                }
            }

            //如果存在反向道路,也就是有反向的lane
            if(road.getBackwardLane()!=null) {
                LinkedList<LaneModel> backwardLanes = road.getBackwardLane();
                for(LaneModel lane:backwardLanes) {
                    if(lane.carsInLane==null) {
                        carInCrossSumNum += 0;
                    }else {
                        carInCrossSumNum += lane.carsInLane.size();}
                }
            }
        }

        //减去这辆车的同一road的逆向lane上的路 （如果存在的话）
        carInCrossNum =carInCrossSumNum-carInReverseLaneNum;
        return carInCrossNum;

    }
}

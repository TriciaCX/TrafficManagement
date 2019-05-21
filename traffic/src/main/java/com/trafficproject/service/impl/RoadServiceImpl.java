package com.trafficproject.service.impl;

import com.trafficproject.dao.RoadDOMapper;
import com.trafficproject.dataobject.RoadDO;
import com.trafficproject.service.BaseService;
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

@Service
public class RoadServiceImpl extends BaseService implements RoadService {

    @Autowired
    private RoadDOMapper roadDOMapper;

    public RoadServiceImpl() {
        System.out.println("");
    }

    @Override
    public RoadModel getRoadModelById(String roadID) {
        if(roadID==null||roadID==""){
            return null;
        }
        RoadDO roadDO = roadDOMapper.selectByRoadID(roadID);
        return convertFromDataObject(roadDO);
    }

    private RoadModel convertFromDataObject(RoadDO roadDO) {
        if(roadDO == null){
            return null;
        }
        RoadModel roadModel = new RoadModel(roadDO.getRoadid());
        roadModel.setRoadID(roadDO.getRoadid());
        roadModel.setRoadLength(roadDO.getRoadlength());
        roadModel.setMaxRoadVelocity(roadDO.getMaxroadvelocity());
        roadModel.setLanesNum(roadDO.getLanesnum());
        roadModel.setFromCrossID(roadDO.getFromcrossid());
        roadModel.setToCrossID(roadDO.getTocrossid());
        //在road上加Lane
        for(int i = 0;i<roadDO.getLanesnum();i++){
            LaneModel laneModel = new LaneModel(i);
            roadModel.getForwardLane().add(laneModel);
        }
        if(roadDO.getIsduplex()==0) {
            roadModel.setDuplex(false);
        }else {
            roadModel.setDuplex(true); //如果还有反向道路
            for(int i = 0;i<roadDO.getLanesnum();i++){
                LaneModel laneModel = new LaneModel(i);
                roadModel.getBackwardLane().add(laneModel);
            }
        }

        return roadModel;

    }

    @Override
    public List<RoadModel> listRoad() {
        List<RoadDO> roadDOList = roadDOMapper.listRoad();
        List<RoadModel> roadModelList = roadDOList.stream().map(roadDO -> {
            RoadModel roadModel = this.convertFromDataObject(roadDO);
            return roadModel;
        }).collect(Collectors.toList());
        return roadModelList;
    }

    @Override
    public Map<String, RoadModel> mapRoad() {
//        ArrayList<RoadModel> listRoad = (ArrayList<RoadModel>) this.listRoad();
        Map<String,RoadModel> mapRoad = new HashMap<>();
        for (RoadModel roadModel:listRoad) {
            mapRoad.put(roadModel.getRoadID(),roadModel);
        }
        return mapRoad;

    }


    /**
     * 查询当前道路是否是真正有可走的空间，因为需要先走车道号小的车道。 小车道没空间，但车没有更新过直接判为没路； 没更新有空间则有路；更新过没空间继续查找大的lane； 更新过有空间为有路；
     * @param road    当前搜索的道路 ;
     * @param CrossID crossID:出发节点ID
     * @return -2:所有lane都更新过了,也没有空间；-1：未更新过而没有空间；1：未更新过而有空间；2：更新过而有空间；
     */
    public int hasLeftLength(RoadModel road, String CrossID) {
//        RoadModel road=getRoadModelById(roadID);
        LinkedList<LaneModel> laneList;
        // 找到和车辆方向一致的车道集合
        if (road.isDuplex()) {
            laneList = road.getFromCrossID().equals(CrossID) ? road.getForwardLane() : road.getBackwardLane();
        } else {
            laneList = road.getForwardLane();
        }
        List<Integer> leftLength = getLeftLanesLength(road, CrossID);
        int i = 0;
        int laneNum = leftLength.size();
        for (; i < laneNum; i++) {
            LaneModel lane = laneList.get(i);
            LinkedList<CarModel> carsInLane = lane.carsInLane;
            if (!carsInLane.isEmpty()) {
                boolean hasArranged = carsInLane.getLast().isHasArrangedOrNot();
                if (hasArranged) {
                    if (leftLength.get(i) > 0) {
                        // 更新过而有空间
                        return 2;
                    } else {
                        continue;
                    }
                } else {
                    if (leftLength.get(i) > 0) {
                        // 未更新过而有空间
                        return 1;
                    } else {
                        // 未更新过而没有空间
                        return -1;
                    }
                }
            } else {
                return 3;
            }
        }
        // 所有lane都更新过了,也没有空间
        return -2;
    }

    /**
     * 根据道路road以及当前路口找到道路通向的下一路口
     * @param road
     * @param sID
     * @return
     */
    public String getCross(RoadModel road, String sID) {
//        RoadModel road=getRoadModelById(roadID);
        if (!road.isDuplex()) {
            if (road.getFromCrossID().equals(sID)) {
                return road.getToCrossID();
            } else {
                return null;
            }
        } else {

            return road.getFromCrossID().equals(sID) ? road.getToCrossID()
                    : road.getFromCrossID();
        }
    }

    /**
     * cost1
     * @param road
     * @return NormalizedRoadLength
     */
    public float getNormalizedRoadLength(RoadModel road, int maxRoadLength) {
//        RoadModel road=getRoadModelById(roadID);
        float NormalizedRoadLength = 0;
        // Main.maxRoadLength最大路长度
        NormalizedRoadLength = road.getRoadLength() / maxRoadLength;
        return NormalizedRoadLength;
    }

    /**
     * 返回下一时刻road的每一条lane还能进入多少辆车,即车位于NextPos时还剩多少位置
     * @param myRoad
     * @param crossID
     * @return
     */
    public ArrayList<Integer> getLeftLanesLength(RoadModel myRoad, String crossID) {
//        RoadModel myRoad=getRoadModelById(myRoadID);
        if (myRoad == null || myRoad.getRoadID().equals("-1")) {
            System.out.println("road");
        }

        if (crossID == null) {
            System.out.println("crossID");
        }

        ArrayList<Integer> LeftLanesLengthList = new ArrayList<Integer>();
        int LeftLength;
        int index = 0;
        //单向道路且车行驶方向与道路方向一致
        if (crossID.equals((myRoad.getFromCrossID()))) {
            for (LaneModel lane : myRoad.getForwardLane()) {
                //从road上得到lane
                //如果初始路上没有车
                CarModel lastCar;
                if (lane.carsInLane.size() == 0) {
                    //lastCar就不存在
                    //leftLength就是roadlength
                    LeftLength = myRoad.getRoadLength();
                } else {
                    lastCar = lane.carsInLane.getLast();
                    LeftLength = myRoad.getRoadLength() - 1 - lastCar.getCurPos();
                }
                LeftLanesLengthList.add(index++, LeftLength);

            }
        } else {//走backword
            for (LaneModel lane : myRoad.getBackwardLane()) {
                CarModel lastCar;
                if (lane.carsInLane.size() == 0) {
                    //lastCar就不存在
                    //leftLength就是roadlength
                    LeftLength = myRoad.getRoadLength();
                } else {
                    lastCar = lane.carsInLane.getLast();
                    LeftLength = myRoad.getRoadLength() - 1 - lastCar.getCurPos();
                }
                LeftLanesLengthList.add(index++, LeftLength);
            }

        }

        return LeftLanesLengthList;
    }

    /**
     * @param road
     * @param crossID
     * @return NormalizedRoadLeftLength
     */
    public float getNormalizedRoadLeftLength(RoadModel road, String crossID) {
//        RoadModel road=getRoadModelById(roadID);
        float NormalizedRoadLeftLength = 0;
        ArrayList<Integer> leftLength = getLeftLanesLength(road,crossID);
        int ans=0;
        Iterator<Integer> i=leftLength.iterator();
        while(i.hasNext()) {
            ans+=i.next();
        }
        /**
         * 修改于4.9
         */
        NormalizedRoadLeftLength=ans/(road.getRoadLength()*road.getLanesNum());
        return NormalizedRoadLeftLength;
    }
}

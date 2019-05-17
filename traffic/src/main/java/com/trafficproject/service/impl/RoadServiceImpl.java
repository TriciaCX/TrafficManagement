package com.trafficproject.service.impl;

import com.trafficproject.dao.RoadDOMapper;
import com.trafficproject.dataobject.RoadDO;
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
public class RoadServiceImpl implements RoadService {

    @Autowired
    private RoadDOMapper roadDOMapper;

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
        if(roadDO.getIsduplex()==1) {
            roadModel.setDuplex(true);
        }else {
            roadModel.setDuplex(false); //如果还有反向道路
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
        ArrayList<RoadModel> listRoad = (ArrayList<RoadModel>) this.listRoad();
        Map<String,RoadModel> mapRoad = new HashMap<>();
        for (RoadModel roadModel:listRoad) {
            mapRoad.put(roadModel.getRoadID(),roadModel);
        }
        return mapRoad;

    }


    @Autowired
    private CarServiceImpl carServiceImpl;

    @Autowired
    private CrossServiceImpl crossServiceImpl;


    /**
     * 根据一条路上,车是否出路口，lane的顺序和lane上车辆距离目的路口的距离，构建车的链表，不考虑hasArrangeOrNot为true的车
     * （首先考虑是否出路口，然后考虑距离，最后考虑车道顺序）
     * @param roadID：当前道路;crossID:车从哪个路口到这个路;
     * @return 获得当前道路和车辆行驶方向相同的所有lane上车辆的发车顺序链表。getfirst是先出发的车（头头）
     */
    public LinkedList<String> getCarInRoad(String roadID, String crossID) {
        RoadModel road=getRoadModelById(roadID);
        LinkedList<LaneModel> laneList;
        LinkedList<String> out = new LinkedList<>();
        // 找到和车辆方向一致的车道集合
        if (road.isDuplex()) {

            laneList = road.getFromCrossID().equals(crossID) ? road.getForwardLane() : road.getBackwardLane();
        } else {
            laneList = road.getForwardLane();
        }
        int laneNum = laneList.size();
        CarModel[] carList = new CarModel[laneNum];// 每次取排在最前面的几个车道的车进行比较,下标对应所在车道，没车的车道或遍历完车的车道放null
        int[] carIndex = new int[laneNum];// 每个车道取到第几辆车，下标对应所在车道，没车的车道或者已经遍历完的车道放置-1
        for (int i = 0; i < laneNum; i++) {// 初始化,放头几辆车
            carIndex[i] = 0;
            LaneModel lane = laneList.get(i);
            if (!lane.carsInLane.isEmpty()) {// 该车道有车
                // 跳过已安排的车辆
                while (carIndex[i] < lane.carsInLane.size() && lane.carsInLane.get(carIndex[i]).isHasArrangedOrNot()) {
                    carIndex[i]++;
                }
                // 该条车道都是已安排的车
                if (carIndex[i] == lane.carsInLane.size()) {
                    carList[i] = null;
                    carIndex[i] = -1;
                }
                // 还有未安排的车
                else {
                    carList[i] = lane.carsInLane.get(carIndex[i]);
                }
            } else {// 该车道无车
                carList[i] = null;
                carIndex[i] = -1;
            }
        }
        int laneIndex;
        int t = 0, tt = 0;
        CarModel[] throughCar = carServiceImpl.ThroughCar(roadID, carList, laneList, carIndex);// 选出出路口的车
        while (true) {
            t = 0;
            tt = 0;
            for (int i = 0; i < throughCar.length; i++) {
                if (throughCar[i] == null) {
                    tt++;
                }
            }
            if (tt < throughCar.length) {// 先按距离和车道号给出路口的车排序
                for (int i = 0; i < laneNum; i++) {
                    if (carIndex[i] < 0) {
                        t++;
                    }
                }
                if (t == laneNum) {
                    break;// 遍历完所有车，所有车道的carIndex为-1，退出循环
                }
                CarModel car = carServiceImpl.minCarCurPos(throughCar);// 取出当前carList中curPos最小的车
                laneIndex = car.getLaneID();
                out.add(car.getCarID());
                // 不考虑已经安排的车
                while ((++carIndex[laneIndex]) < laneList.get(laneIndex).carsInLane.size()
                        && laneList.get(laneIndex).carsInLane.get(carIndex[laneIndex]).isHasArrangedOrNot())
                    ;
                // 当前车道已经遍历完车了
                if (carIndex[laneIndex] >= laneList.get(laneIndex).carsInLane.size()) {
                    carIndex[laneIndex] = -1;
                    carList[laneIndex] = null;
                    throughCar[laneIndex] = null;
                } else {// 将该车道对应的比较集合carList位置的car指向该车道的下一辆车
                    CarModel c = laneList.get(laneIndex).carsInLane.get(carIndex[laneIndex]);
                    carServiceImpl.canThrough(roadID, c.getCarID(), laneList, carIndex);
                    if (c.isCanThrough()) {

                        throughCar[laneIndex] = c;
                    } else {
                        carList[laneIndex] = c;
                        throughCar[laneIndex] = null;
                    }
                    // throughCar = ThroughCar(road, carList, laneList, carIndex);
                }
            } else {// 处理完出路口的车之后，为不出路口的车按距离和车道号排序
                t = 0;
                for (int i = 0; i < laneNum; i++) {
                    if (carIndex[i] < 0) {
                        t++;
                    }
                }
                if (t == laneNum)
                    break;// 遍历完所有车，所有车道的carIndex为-1，退出循环
                CarModel car = carServiceImpl.minCarCurPos(carList);// 取出当前carList中curPos最小的车
                laneIndex = car.getLaneID();
                out.add(car.getCarID());
                // 不考虑已经安排的车
                while ((++carIndex[laneIndex]) < laneList.get(laneIndex).carsInLane.size()
                        && laneList.get(laneIndex).carsInLane.get(carIndex[laneIndex]).isHasArrangedOrNot())
                    ;
                // 当前车道已经遍历完车了
                if (carIndex[laneIndex] >= laneList.get(laneIndex).carsInLane.size()) {
                    carIndex[laneIndex] = -1;
                    carList[laneIndex] = null;
                } else {// 将该车道对应的比较集合carList位置的car指向该车道的下一辆车
                    carList[laneIndex] = laneList.get(laneIndex).carsInLane.get(carIndex[laneIndex]);
                }
            }
        }
        return out;
    }

    /**
     * 根据一条路上,车是否出路口，lane的顺序和lane上车辆距离目的路口的距离，给出排在第一个的车ID，不考虑hasArrangeOrNot为true的车
     * （首先考虑是否出路口，然后考虑距离，最后考虑车道顺序）
     * @param roadID：当前道路;crossID:车从哪个路口到这个路;
     * @return 获得当前道路和车辆行驶方向相同的所有lane上的第一辆车。getfirst是先出发的车（头头）
     */
    public String getFirstCarInRoad(String roadID, String crossID) {
        RoadModel road=getRoadModelById(roadID);
        LinkedList<LaneModel> laneList;
        // 找到和车辆方向一致的车道集合
        if (road.isDuplex()) {

            laneList = road.getFromCrossID().equals(crossID) ? road.getForwardLane() : road.getBackwardLane();
        } else {
            laneList = road.getForwardLane();
        }
        int laneNum = laneList.size();
        // 每次取排在最前面的几个车道的车进行比较,下标对应所在车道，没车的车道或遍历完车的车道放null
        CarModel[] carList = new CarModel[laneNum];
        // 每个车道取到第几辆车，下标对应所在车道，没车的车道或者已经遍历完的车道放置-1
        int[] carIndex = new int[laneNum];
        for (int i = 0; i < laneNum; i++) {
            // 初始化,放头几辆车
            carIndex[i] = 0;
            LaneModel lane = laneList.get(i);
            if (!lane.carsInLane.isEmpty()) {
                // 该车道有车
                // 跳过已安排的车辆
                while (carIndex[i] < lane.carsInLane.size() && lane.carsInLane.get(carIndex[i]).isHasArrangedOrNot()) {
                    carIndex[i]++;
                }
                // 该条车道都是已安排的车
                if (carIndex[i] == lane.carsInLane.size()) {
                    carList[i] = null;
                    carIndex[i] = -1;
                }
                // 还有未安排的车
                else {
                    carList[i] = lane.carsInLane.get(carIndex[i]);
                }
            } else {// 该车道无车
                carList[i] = null;
                carIndex[i] = -1;
            }
        }
        //选出出路口的车
        CarModel[] throughCar = carServiceImpl.ThroughCar(roadID, carList, laneList, carIndex);
        CarModel out;
        out = carServiceImpl.minCarCurPos(throughCar);
        if (out != null) {

            return out.getCarID();
        } else {
            out = carServiceImpl.minCarCurPos(carList);
        }
        if (out != null) {

            return out.getCarID();
        } else {
            return null;
        }
    }

    /**
     * 根据一条路上,车是否出路口，lane的顺序和lane上车辆距离目的路口的距离，给出排在第一个的车ID，该路上不可能有false车
     * （首先考虑是否出路口，然后考虑距离，最后考虑车道顺序）
     * @param roadID    当前道路
     * @param crossID 车从哪个路口到这个路;
     * @return 获得当前道路和车辆行驶方向相同的所有lane上的第一辆车。getfirst是先出发的车（头头）
     */
    public String getFirstTrueCarInRoad(String roadID, String crossID) {
        RoadModel road=getRoadModelById(roadID);
        LinkedList<LaneModel> laneList;
        // 找到和车辆方向一致的车道集合
        if (road.isDuplex())
            laneList = road.getFromCrossID().equals(crossID) ? road.getForwardLane() : road.getBackwardLane();
        else
            laneList = road.getForwardLane();
        int laneNum = laneList.size();
        CarModel[] carList = new CarModel[laneNum];// 每次取排在最前面的几个车道的车进行比较,下标对应所在车道，没车的车道或遍历完车的车道放null
        int[] carIndex = new int[laneNum];// 每个车道取到第几辆车，下标对应所在车道，没车的车道或者已经遍历完的车道放置-1
        for (int i = 0; i < laneNum; i++) {// 初始化,放头几辆车
            carIndex[i] = 0;
            LaneModel lane = laneList.get(i);
            if (!lane.carsInLane.isEmpty()) {// 该车道有车
                // 跳过已安排的车辆
                while (carIndex[i] < lane.carsInLane.size() && !lane.carsInLane.get(carIndex[i]).isHasArrangedOrNot()) {
                    carIndex[i]++;
                    System.out.println("有false车，谁在调用getFirstTrueCarInRoad");
                }
                // 该条车道都是未安排的车，不应该来这里
                if (carIndex[i] == lane.carsInLane.size()) {
                    carList[i] = null;
                    carIndex[i] = -1;
                }
                //
                else
                    carList[i] = lane.carsInLane.get(carIndex[i]);
            } else {// 该车道无车
                carList[i] = null;
                carIndex[i] = -1;
            }
        }
        CarModel[] throughCar = carServiceImpl.ThroughCar(roadID, carList, laneList, carIndex);//选出出路口的车
        CarModel out;
        out = carServiceImpl.minCarCurPos(throughCar);
        if (out != null)
            return out.getCarID();
        else {
            out = carServiceImpl.minCarCurPos(carList);
        }
        if (out != null)
            return out.getCarID();
        else return null;
    }

    /**
     * 查询当前道路是否是真正有可走的空间，因为需要先走车道号小的车道。
     * 小车道没空间，但车没有更新过直接判为没路； 没更新有空间则有路；更新过没空间继续查找大的lane；更新过有空间为有路；
     * @param roadID    当前搜索的道路 ;
     * @param CrossID crossID:出发节点ID
     * @return -2:所有lane都更新过了,也没有空间；-1：未更新过而没有空间；1：未更新过而有空间；2：更新过而有空间；
     */
    public int hasLeftLength(String roadID, String CrossID) {
        RoadModel road=getRoadModelById(roadID);
        LinkedList<LaneModel> laneList;
        // 找到和车辆方向一致的车道集合
        if (road.isDuplex()) {
            laneList = road.getFromCrossID().equals(CrossID) ? road.getForwardLane() : road.getBackwardLane();
        } else {
            laneList = road.getForwardLane();
        }
        List<Integer> leftLength = getLeftLanesLength(roadID, CrossID);
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
     * @param roadID
     * @param sID
     * @return
     */
    public CrossModel getCross(String roadID, String sID) {
        RoadModel road=getRoadModelById(roadID);
        if (!road.isDuplex()) {
            if (road.getFromCrossID().equals(sID)) {
                return crossServiceImpl.getCrossModelById(road.getToCrossID());
            } else {
                return null;
            }
        } else {

            return road.getFromCrossID().equals(sID) ? crossServiceImpl.getCrossModelById(road.getToCrossID())
                    : crossServiceImpl.getCrossModelById(road.getFromCrossID());
        }
    }

    /**
     * cost1
     * @param roadID
     * @return NormalizedRoadLength
     */
    public float getNormalizedRoadLength(String roadID, int maxRoadLength) {
        RoadModel road=getRoadModelById(roadID);
        float NormalizedRoadLength = 0;
        // Main.maxRoadLength最大路长度
        NormalizedRoadLength = road.getRoadLength() / maxRoadLength;
        return NormalizedRoadLength;
    }

    /**
     * 返回下一时刻road的每一条lane还能进入多少辆车,即车位于NextPos时还剩多少位置
     * @param myRoadID
     * @param crossID
     * @return
     */
    public ArrayList<Integer> getLeftLanesLength(String myRoadID, String crossID) {
        RoadModel myRoad=getRoadModelById(myRoadID);
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
     *
     * @param roadID
     * @param crossID
     * @return NormalizedRoadLeftLength
     */
    public float getNormalizedRoadLeftLength(String roadID, String crossID) {
        RoadModel road=getRoadModelById(roadID);
        float NormalizedRoadLeftLength = 0;
        ArrayList<Integer> leftLength = getLeftLanesLength(roadID,crossID);
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

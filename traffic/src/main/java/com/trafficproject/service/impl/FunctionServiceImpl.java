package com.trafficproject.service.impl;

import com.trafficproject.service.*;
import com.trafficproject.service.model.CarModel;
import com.trafficproject.service.model.CrossModel;
import com.trafficproject.service.model.LaneModel;
import com.trafficproject.service.model.RoadModel;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class FunctionServiceImpl extends BaseService implements FunctionService {

//    @Autowired
//    private RoadService roadService;
//    @Autowired
//    private CarService carService;
//    @Autowired
//    private CrossService crossService;
//
//    private ArrayList<CrossModel> listCross;






    /**
     * 查询当前搜索的道路和车的方向是否冲突
     *
     * @param roadID    当前搜索道路
     * @param crossID 出发节点ID
     * @return true：方向一致；false：方向相反
     */
    public boolean isDirectionRight(String roadID, String crossID) {
        RoadModel road=mapRoad.get(roadID);
        if (!road.isDuplex()) {
            if (road.getFromCrossID().equals(crossID)) {

                return true;
            } else {
                return false;
            }
        } else {

            return true;
        }
    }

    /**
     * 在一个车辆集合中选出能过路口的车
     *
     * @param roadID     当前的道路
     * @param carList  需要判断是否通过路口的车集合
     * @param laneList 车集合所在的lane集合
     * @param carIndex 车集合中各个车处于各自lane上的第几个位置
     * @return 需要过路口的车的集合，大小和输入的carList相同，只是将不能过路口的车的位置放置null；
     */
    public CarModel[] ThroughCar(String roadID, CarModel[] carList, LinkedList<LaneModel> laneList, int[] carIndex) {
        CarModel[] c = new CarModel[carList.length];
        int j = 0;
        for (; j < carList.length; j++) {
            // 先取第一个不为null的车
            CarModel car = carList[j];
            if (car != null) {
                canThrough(roadID, car.getCarID(), laneList, carIndex);
                if (car.isCanThrough()) {
                    c[j] = carList[j];
                    carList[j] = null;
                }
            } else {

                c[j] = null;
            }
        }
        return c;
    }

    /**
     * 判断该车能不能通过路口，如果一条lane上前车不能通过路口，该车也不能； 如果没有前车或者前车可以通过路口，则根据行驶速度和所在位置判断是否可以通过路口
     *
     * @param roadID     当前道路
     * @param carID      当前车辆
     * @param laneList 车所在的lane集合
     * @param carIndex 车处于各自lane上的第几个位置
     */
    public void canThrough(String roadID, String carID, LinkedList<LaneModel> laneList, int[] carIndex) {
        RoadModel road=mapRoad.get(roadID);
        CarModel car= mapCar.get(carID);
        int laneID = car.getLaneID();
        int carIn = carIndex[laneID];
        LinkedList<CarModel> carsInLane = laneList.get(laneID).carsInLane;
        if (!carsInLane.isEmpty()) {
            if (carIn > 0 && !carsInLane.get(carIn - 1).isCanThrough()) {
                // 有前车且前车不能通过路口
                car.setCanThrough(false);
            } else {// 没前车或前车能通过路口，根据行驶速度和所在位置判断是否可以通过路口
                if (car.getCurPos() < Math.min(car.getMaxVelocity(), road.getMaxRoadVelocity())) {
                    car.setCanThrough(true);
                } else {
                    car.setCanThrough(false);
                }
            }
        }
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
        String[] roadIDList = mapCross.get(carToCrossID).getRoadIDList();
        ArrayList<RoadModel> roadList = new ArrayList<RoadModel>();
        for(String s:roadIDList) {
            if (!s.equals("-1")) {
                roadList.add(mapRoad.get(s));
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

    /**
     * 对这个路口取出这时候安排的那4辆车
     * *@param carsFour最多只有四个车
     */
    public LinkedList<CarModel> extractFourCar(CrossModel s) {
        LinkedList<CarModel> carsFour = new LinkedList<>();
        if (!s.getDownRoad().getRoadID().equals("-1")) {
            String carID = getFirstCarInRoad(s.getDownRoad().getRoadID(), s.getCrossID());
            if (carID != null) {
                carsFour.add(mapCar.get(carID));
            }
        }
        if (!s.getUpRoad().getRoadID().equals("-1")) {
            String carID = getFirstCarInRoad(s.getUpRoad().getRoadID(), s.getCrossID());
            if (carID != null) {

                carsFour.add(mapCar.get(carID));
            }
        }
        if (!s.getLeftRoad().getRoadID().equals("-1")) {
            String carID = getFirstCarInRoad(s.getLeftRoad().getRoadID(), s.getCrossID());
            if (carID != null) {

                carsFour.add(mapCar.get(carID));
            }
        }
        if (!s.getRightRoad().getRoadID().equals("-1")) {
            String carID = getFirstCarInRoad(s.getRightRoad().getRoadID(), s.getCrossID());
            if (carID != null) {

                carsFour.add(mapCar.get(carID));
            }
        }

        return carsFour;
    }

    public void markNextCross(String roadID, String sID) {
//        if(maxRoadLength==0){
//            maxRoadLength=getMaxRoadLength((ArrayList<RoadModel>) roadService.listRoad());
//        }
        RoadModel road=roadService.getRoadModelById(roadID);
        CrossModel s=mapCross.get(sID);
        CrossModel t = mapCross.get(roadService.getCross(road, sID));
        if (!t.isKnown) {
            // 自适应调整w2（和遇到前方）和w3（和回滚相关）
            // cost1:要去的那条路和最长的那条路的比值
            float NormalizedRoadLength = roadService.getNormalizedRoadLength(road, maxRoadLength);
            // cost2：要去的那条路的剩余空间
            float NormalizedRoadLeftLength = roadService.getNormalizedRoadLeftLength(road, s.getCrossID());
            // cost3：拥挤系数，其中CrossInfo.getCrossCarNum(s.getCrossID(), t.getCrossID())表示有多少辆车会通过这个路口
            float sigmoidCrossCarNum = swish(getCrossCarNum(t.getCrossID()));
            float cost = w[0] * NormalizedRoadLength + w[1] * (1 - NormalizedRoadLeftLength) + w[2] * sigmoidCrossCarNum;
            if (s.cost + cost < t.cost) {
                t.cost = s.cost + cost;
                t.preCross = s;
            }
        }

    }


    /**
     * 根据一条路上,车是否出路口，lane的顺序和lane上车辆距离目的路口的距离，构建车的链表，不考虑hasArrangeOrNot为true的车
     * （首先考虑是否出路口，然后考虑距离，最后考虑车道顺序）
     * @param roadID：当前道路;crossID:车从哪个路口到这个路;
     * @return 获得当前道路和车辆行驶方向相同的所有lane上车辆的发车顺序链表。getfirst是先出发的车（头头）
     */
    public LinkedList<String> getCarInRoad(String roadID, String crossID) {
        RoadModel road=mapRoad.get(roadID);
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
        CarModel[] throughCar = ThroughCar(roadID, carList, laneList, carIndex);// 选出出路口的车
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
                CarModel car = carService.minCarCurPos(throughCar);// 取出当前carList中curPos最小的车
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
                    canThrough(roadID, c.getCarID(), laneList, carIndex);
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
                CarModel car = carService.minCarCurPos(carList);// 取出当前carList中curPos最小的车
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
        RoadModel road=mapRoad.get(roadID);
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
        CarModel[] throughCar = ThroughCar(roadID, carList, laneList, carIndex);
        CarModel out;
        out = carService.minCarCurPos(throughCar);
        if (out != null) {

            return out.getCarID();
        } else {
            out = carService.minCarCurPos(carList);
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
        RoadModel road=mapRoad.get(roadID);
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
        CarModel[] throughCar = ThroughCar(roadID, carList, laneList, carIndex);//选出出路口的车
        CarModel out;
        out = carService.minCarCurPos(throughCar);
        if (out != null)
            return out.getCarID();
        else {
            out = carService.minCarCurPos(carList);
        }
        if (out != null)
            return out.getCarID();
        else return null;
    }

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
    public void setCarInRoad(RoadModel toRoad, CarModel car, RoadModel r, int state, boolean setHasArrangedOrNot, boolean setSheng, boolean setCurPos, boolean setNextRoadID, boolean setPriority) {
        if (car.getCarID().equals("10789") && r.getRoadID().equals("5007"))
            System.out.println();
        LinkedList<LaneModel> lanes;
        LinkedList<CarModel> carsInLane;
        CarModel shengCar;
        if (car.getCurFromCrossID().equals(r.getFromCrossID())) {

            lanes = r.getForwardLane();
        } else {
            lanes = r.getBackwardLane();
        }
        for (int j = 0; j < lanes.size(); j++) {
            carsInLane = lanes.get(j).carsInLane;
            //按lane逐个取车
            for (int y = 0; y < carsInLane.size(); y++) {
                shengCar = carsInLane.get(y);
                //设置curPos
                if (setCurPos) {
                    //有sheng
                    if (shengCar.getSheng() != 0) {
                        if (y == 0) {
                            shengCar.setCurPos(shengCar.getCurPos() + shengCar.getSheng());
                        } else {
                            int curPos = shengCar.getCurPos()
                                    - Math.min(shengCar.getCurPos() - carsInLane.get(y - 1).getCurPos() - 1,
                                    Math.abs(shengCar.getSheng()));
                            shengCar.setCurPos(curPos);
                        }
                    } else if (shengCar.equals(car)) {
                        //
                        numOf5 += 2;
                    } else if (!shengCar.isHasArrangedOrNot()) {
                        //没有sheng，但没被安排过
                        int cha = shengCar.getCurPos() - Math.min(shengCar.getMaxVelocity(), r.getMaxRoadVelocity());
                        if (y == 0) {
                            if (cha < 0) {
                                ;
                            } else {
                                shengCar.setCurPos(cha);
                            }
                        } else {
                            if (cha < 0) {
                                ;
                            } else {
                                shengCar.setCurPos(shengCar.getCurPos()
                                        - Math.min(Math.min(shengCar.getMaxVelocity(), r.getMaxRoadVelocity()),
                                        shengCar.getCurPos() - carsInLane.get(y - 1).getCurPos() - 1));
                            }
                        }
                    }
                }
                //设置state
                if (state != -2) {
                    if (state == 4) {
                        numOf2++;
                    }

                    if (state == 5 && setHasArrangedOrNot) {
                        numOf5 += 2;
                    }

                    if (state == 5 && !setHasArrangedOrNot) {
                        numOf5++;
                    }
                    if(shengCar.getState()==5 && !setHasArrangedOrNot && state!=5) {
                        numOf5--;
                    }
                    if(shengCar.getState()==5 && setHasArrangedOrNot && state!=5) {
                        numOf5-=2;
                    }
                    if((shengCar.getState()==4|| shengCar.getState()==2) && state!=4) {
                        numOf2--;
                    }
                    shengCar.setState(state);
                }
                if (setHasArrangedOrNot) {

                    shengCar.setHasArrangedOrNot(true);
                }
                if (setSheng) {

                    shengCar.setSheng(0);
                }
                if (setNextRoadID) {

                    shengCar.setNextRoadID(toRoad.getRoadID());
                }
                if (setPriority) {

                    shengCar.setPriority(crossService.setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
                }
            }
        }
    }

//    /**
//     * 返回roadList中最长的道路
//     * @param roadList
//     * @return 最大道路长度
//     */
//    public int getMaxRoadLength(ArrayList<RoadModel> roadList) {
//        RoadComparator myComparator = new RoadComparator();
//        return (Collections.max(roadList, myComparator).getRoadLength());
//    }
//
//    private static class RoadComparator implements Comparator<RoadModel> {
//        @Override
//        public int compare(RoadModel r1, RoadModel r2) {
//            //根据路的长度对路排序
//            if (r1.getRoadLength() > r2.getRoadLength()) {
//
//                return 1;
//            } else if (r1.getRoadLength() < r2.getRoadLength()) {
//                return -1;
//
//            } else {
//
//            }
//            return 0;
//        }
//    }

    /**
     * 更新从车库出发的车的信息，判断车是否能够插入到规划的道路中，如果能，查看ID优先级是否冲突，有冲突则看
     * 是否有回退的车（存入reArrangeCars中），如果不能，直接加到存入reArrangeCars中
     * @param car：当前车；road：要走的道路；virtualCarsHashMap：存好的车的原始状态；reArrangeCars：回退车辆集合，就是可能会有ID小但是速度快的车被先从车库里面取出来，在同一时刻，起始路口相同，要选择的道路也是一条，这时候就要回退了；MapRoad：用来返回对象的
     * @return 这里返回两个元素，false:第一个插不进去，第二个：有没有回滚？没有
     */
    public boolean[] checkIDPriority(CarModel car, RoadModel road, LinkedList<CarModel> reArrangeCars, int t) {
        /**
         * [可以插进去,没有回滚]
         */
        boolean flag1 = true;
        boolean flag2 = true;

        /**
         * 更新一些信息
         */

        car.setCurFromCrossID(car.getFromCrossID());
        car.setPriority(0);

        /**
         * 首先设定这辆车的getCurToCross()是从自己的始发路口出发的要开上这个道路
         */

        if (road.getFromCrossID().equals(car.getCurFromCrossID())) {
            car.setCurToCrossID(road.getToCrossID());

        } else {
            car.setCurToCrossID(road.getFromCrossID());
        }

        /**
         * 得到了这个车要插入的道路的lane们
         */

        LinkedList<LaneModel> myLanes;// 首先要判断方向
        if (!car.getCurToCrossID().equals(road.getFromCrossID())) {
            myLanes = road.getForwardLane();
        } else {
            myLanes = road.getBackwardLane();
        }
        int size = myLanes.size();
        int i = 0;
        for (i = size; i > 0; i--) {
            /**
             * 先拿出来的在lane4上的一系列车
             */
            LinkedList<CarModel> cars = myLanes.get(i - 1).carsInLane;
            // 找到要插入的位置了//priority还是要设置为0
            if (cars.size() > 0 && cars.getLast().getPriority() == car.getPriority() && Integer.valueOf(cars.getLast().getCarID()) > Integer.valueOf(car.getCarID())) {
                // 这个车也是刚从车库v提出来的而且，这个车的ID比现在安排的车大
                while (cars.size() > 0 && cars.getLast().getPriority() == car.getPriority()
                        && Integer.valueOf(cars.getLast().getCarID()) > Integer.valueOf(car.getCarID())) {
                    // 那这个车要回退的
                    // 先把这些要退回的车辆的信息更新一下
                    // 提取这辆车
                    CarModel virtualCar = mapCar.get(cars.getLast().getCarID());
                    /**
                     * 4.13.新发现，如果直接删掉它可能会导致这条路上车辆的状态都是4，这可不行
                     */
                    if (virtualCar.getState() == 2)
                        setCarInRoad(null, virtualCar, road, 3, false, false, false, false, false);
                    //更新mapCar里它原来的信息
                    innitial(virtualCar.getCarID());
                    // 删除真实网络的信息
                    cars.removeLast();
                    // 把这些车辆返回
                    reArrangeCars.addLast(virtualCar);
                    // 回滚了
                    flag2 = false;
                }
            }
        }

        // 插进去这个车，并且更新相关信息
        flag1 = carIDInsertToRoad(car, road, t);
        if (!flag1)
        // 如果这个车插不进去的,也放到重新安排的车的集合里,外面会再安排一次,再不行就放到garageFrozon里
        // 因为是false,所以没有在实际网络添加过它，所以只要返回原来的就好了
        {
            CarModel carVir = mapCar.get(car.getCarID());
            innitial(carVir.getCarID());
            reArrangeCars.add(car);
        }

        // 返回被退回的车或者可能是本身这辆车,再插一次！
        while (!reArrangeCars.isEmpty()) {
            CarModel c = reArrangeCars.getLast();
            if (c != null) {
                if (reArrangeCarsIDInsertToRoad(c, road, t)) {
                    reArrangeCars.removeLast();
                } else {
                    // 插不进去了
                    break;
                }
            } else {
                //取出来是null就说明应该时没有了
                break;
            }
        }
        // 两个元素，false:第一个插不进去，第二个：没有回滚
        boolean[] flags = new boolean[2];
        flags[0] = flag1;
        flags[1] = flag2;
        return flags;
    }

    /**
     * 已经判断这辆从车库来的车想要到这个路，该怎么选择车道呢？还要更新相关信息哦，比如说准备回滚，要存一个影分身之类的，前面的道路上已经没有优先级比它差的车了
     * @param car：当前车；road：要走的道路；virtualCarsHashMap；MapRoad：用来返回对象的；
     * @return 这里返回一个元素，false:插不进去
     */
    private boolean carIDInsertToRoad(CarModel car, RoadModel road, int t) {
        // LanesCarsList[0]=3,表示lane1的剩余可进入长度为3，还能进3辆车
        boolean flag = InsertFreshCarToRoad(car, road, t);
        // false:插不进去
        return flag;
    }

    /**
     * 这辆从车库出来的车能不能到这个路？调用这个函数的时候，
     * 前面是不可能出现回滚的，因为已经滚过了！能的话就插进去！
     */
    private boolean InsertFreshCarToRoad(CarModel car, RoadModel road, int t) {
        ArrayList<Integer> LeftLanesLengthList = roadService.getLeftLanesLength(road, car.getCurFromCrossID());
        int size = road.getLanesNum();
        int i = 0;
        // true:可以插入；false:插不进去
        boolean flag1 = true;
        if (LeftLanesLengthList.size() == 0) {
            flag1 = false;
            // false:插不进去

            return flag1;

        } else {
            while (i < size && LeftLanesLengthList.get(i) == 0) {
                i++;
            }
            if (i == size) {
                flag1 = false;
                innitial(car.getCarID());
                // false:插不进去
                return flag1;

            }

        }


        /**
         * 可以插进去！
         */
        // 要插入的lane可以插入的空间
        int nextLaneOfRoadLeftSize = LeftLanesLengthList.get(i);

        // 以这个道路的最大速度以及车辆自己的最大速度
        // 在这个lane的最大速度
        int nextLaneVel = Math.min(road.getMaxRoadVelocity(), car.getMaxVelocity());

        /**
         * 之前已经更新过的信息 car.setCurFromCross(car.getFromCross()); car.setPriority(0);
         * car.setCurToCross(road.getToCross());
         * 不用更新的信息 car.setFromCross(fromCross); car.setToCross(toCross);
         *          * car.setMaxVelocity(maxVelocity);
         */
        if (car.getRealStartTime() == -1) {

            car.setRealStartTime(t);
        }
        /**
         * 找到下一个去往的路口
         */
        if (car.getFromCrossID().equals(road.getFromCrossID())) {

            car.setCurToCrossID(road.getToCrossID());
        } else if (road.isDuplex()) {
            car.setCurToCrossID(road.getFromCrossID());

        } else {
            System.out.println("没有下一个路口了？奇奇怪怪的");
        }

        car.setCurPos(road.getRoadLength() - Math.min(nextLaneOfRoadLeftSize, nextLaneVel));
        car.setLaneID(i);
        car.setRoadID(road.getRoadID());
        car.setSheng(0);
        // 通过路的对象和laneID找到Lane的对象thisLane
        LaneModel thisLane = new LaneModel();
        LinkedList<LaneModel> l;
        if (road.getFromCrossID().equals(car.getCurFromCrossID())) {

            l = road.getForwardLane();
        } else {
            l = road.getBackwardLane();
        }

        for (LaneModel l1 : l) {
            if (l1.getLaneIndex() == car.getLaneID()) {
                thisLane = l1;
                break;
            }
        }
        // 把这辆车的实例加到后来的lane
        thisLane.carsInLane.add(car);
        // 前面没有车,不一定是一个lane哦

        // preCar是这个路上的之字划开的前面的第一辆车，可能是刚插进去的那辆车，可能不是
        // 这里不用判断是不是为空，因为我刚插进去一辆车呢，而且car.setHasArrangedOrNot是false，是会被读到的

        CarModel preCar = mapCar.get(getFirstCarInRoad(road.getRoadID(), car.getFromCrossID()));


        if (preCar.equals(car)) {
            //
            if (car.getCurPos() - nextLaneVel < 0) {
                // 下一时刻是可以出去的车
                setCarInRoad(null, car, road, 3, false, false, false, false, false);
                car.setState(1);
            } else {
                // 下一时刻出不去
                setCarInRoad(null, car, road, 4, false, false, false, false, false);
                car.setState(2);
            }
        } else {
            int preState = preCar.getState();
            if (preState == 1) {

                car.setState(3);
            } else if (preState == 2) {
                car.setState(4);
            } else if (preState == 3) {
                car.setState(3);
            } else if (preState == 4) {
                car.setState(4);
            } else {// 前车是5
                car.setState(5);
            }
        }

        // 准备返回,false:插不进去
        return flag1;
    }

    /**
     * 被退回来的从车库出发的车看看还能不能按照原来的想法插入到这个道路中
     * @param car：被退回来的车；road：本来要走的道路；
     * @return 有没有成功插入
     */
    private boolean reArrangeCarsIDInsertToRoad(CarModel car, RoadModel road, int t) {
        boolean flag = InsertFreshCarToRoad(car, road, t);
        // false:插不进去
        return flag;

    }

    private void innitial(String cID) {
        CarModel mapc=mapCar.get(cID);
        mapc.setCurFromCrossID(mapc.getFromCrossID());
        mapc.setLaneID(-1);
        mapc.setRoadID("-1");
        mapc.setNextRoadID("-1");
        mapc.setCanThrough(false);
        mapc.setSheng(0);
        mapc.setState(-1);
        mapc.setPriority(0);
        mapc.setHasArrangedOrNot(false);
        mapc.setCurPos(0);
        mapc.setCurFromCrossID(mapc.getFromCrossID());
        mapc.setCurToCrossID(mapc.getFromCrossID());

    }

    /**
     * 对当前车辆规划下一道路,2019.3.28 保证第一条路可走（考虑rest），从car.getCurToCross开始找路径,可以进一步优化：
     * 此时第一条路不能走不一定真的在该时刻不能走，需要安排完一些车后可能会有空间
     * @param car 当前车辆（两个等待状态）
     * @return 下一条道路
     */
    public RoadModel findNextCross(CarModel car) {
        if(listCross==null){
            listCross= (ArrayList<CrossModel>) crossService.listCross();
        }
        // 车所在路的通向路口是终点，则返回这条路ID
        if (car.getCurToCrossID().equals(car.getToCrossID())) {
            return mapRoad.get(car.getRoadID());
        }
        // 未知节点集合
        List<CrossModel> unknown = new ArrayList<CrossModel>();
        // 当前出发节点路口，可能为null
        CrossModel s = mapCross.get(car.getCurToCrossID());
        // 目的地
        CrossModel t = mapCross.get(car.getToCrossID());
        Iterator<CrossModel> crossIter = listCross.iterator();
        while (crossIter.hasNext()) {
            CrossModel cross = crossIter.next();
            if (s.getCrossID().equals(cross.getCrossID())) {
                // 初始化出发节点,我重写了equals
                // 到达当前节点时间
                cross.cost = 0;
                cross.isKnown = true;
                cross.preCross = null;
            } else {// 初始化其余节点
                cross.cost = Float.MAX_VALUE;
                cross.isKnown = false;
                cross.preCross = null;
                unknown.add(cross);
            }
        }
        // 表示从出发节点来标记邻接节点，此时要保证搜索可行路径
        ArrayList<RoadModel> roads = new ArrayList<>();
        for (String roadID : s.getRoadIDList()) {
            if (!roadID.equals("-1")) {

                roads.add(mapRoad.get(roadID));
            }
        }
        // 找到车过来的路,初始情况返回null
        RoadModel preRoad = mapRoad.get(crossService.findRoad(car.getCurFromCrossID(), car.getCurToCrossID()));
        // 不标记第一条可选路中不能走的路
        boolean flag = deleteCrossFromUnknown(car, roads, preRoad);
        if (!flag) {
            // 第一条路无路可走
            return null;
        }
        while (!t.isKnown) {
            Collections.sort(unknown, new Comparator<CrossModel>() {
                @Override
                public int compare(CrossModel o1, CrossModel o2) {
                    if (o2.cost > o1.cost) {
                        return 1;
                    } else if (o2.cost < o1.cost) {
                        return -1;

                    } else {
                        return 0;
                    }

                }
            });
            if (unknown.isEmpty()) {
                System.out.println("找不到目标路口"+t.getCrossID());
            }
            // 找到当前从源节点出发，代价最小的节点
            CrossModel v = unknown.get(unknown.size() - 1);
            v.isKnown = true;
            unknown.remove(unknown.size() - 1);
            ArrayList<RoadModel> roadsList = new ArrayList<>();
            for (String roadID : v.getRoadIDList()) {
                RoadModel road = mapRoad.get(roadID);
                if (!roadID.equals("-1")) {

                    roadsList.add(road);
                }
            }
            for (RoadModel road : roadsList) {
                // 从该节点出发标记其相邻节点
                if (road != null && isDirectionRight(road.getRoadID(), v.getCrossID())) {
                    markNextCross(road.getRoadID(), v.getCrossID());
                }
            }
        }
        return mapRoad.get(crossService.findFirstRoad(s.getCrossID(), t.getCrossID()));
    }

    /**
     * @param car     当前安排车辆
     * @param roads   当前搜索道路集合
     * @param preRoad 车当前所在道路
     * @return 从unknown中标记第一跳可行节点 ，不标记没有的路，方向不对的路，过来的路 对于没空间的路，都标记cost，不管是更新过没空间还是没更新过没空间
     */
    private boolean deleteCrossFromUnknown( CarModel car, List<RoadModel> roads, RoadModel preRoad) {
        // 可行道路数量
        int numOfAbleRoads = 0;
        CrossModel s;
        if (car.getCurToCrossID() == null) {
            s = mapCross.get(car.getFromCrossID());
        } else {
            // 车辆出发节点
            s = mapCross.get(car.getCurToCrossID());
        }
        int[] flag = new int[roads.size()];
        for (int i = 0; i < roads.size(); i++) {
            // 当前搜索道路
            RoadModel road = roads.get(i);
            if (preRoad != null && (road.equals(preRoad))) {
                // 不走回头路
                flag[i] = -1;
            } else {
                if (road == null || road.getRoadID().equals("-1")) {
                    // 前面已经确定不会有“-1”
                    // 没有路标记为-2
                    flag[i] = -2;
                } else if (!isDirectionRight(road.getRoadID(), s.getCrossID())) {
                    // 有路但方向不对标记为-3
                    flag[i] = -3;
                } else if (roadService.hasLeftLength(road, s.getCrossID()) > 0) {
                    numOfAbleRoads++;
                    // 有空间
                    flag[i] = 1;
                } else if (roadService.hasLeftLength(road, s.getCrossID()) == -1) {
                    numOfAbleRoads++;
                    // 没更新过而没空间
                    flag[i] = 2;
                } else if (roadService.hasLeftLength(road, s.getCrossID()) == -2) {
                    numOfAbleRoads++;
                    // 更新过而没空间
                    flag[i] = 3;
                } else {
                    flag[i] = 0;
                }
            }
        }
        if (numOfAbleRoads == 0) {

            return false;
        } else {
            for (int i = 0; i < roads.size(); i++) {
                RoadModel road = roads.get(i);
                if (flag[i] > 0) {
                    markNextCross(road.getRoadID(), s.getCrossID());
                } else if (flag[i] < 0) {
                    continue;
                } else {
                    System.out.println("不知道啥路子");
                }

            }
        }
        return true;
    }

}

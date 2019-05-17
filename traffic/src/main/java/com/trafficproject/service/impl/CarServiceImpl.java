package com.trafficproject.service.impl;

import com.trafficproject.dao.CarDOMapper;
import com.trafficproject.dataobject.CarDO;
import com.trafficproject.service.CarService;
import com.trafficproject.service.model.CarModel;
import com.trafficproject.service.model.LaneModel;
import com.trafficproject.service.model.RoadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Component
public class CarServiceImpl implements CarService {

    @Autowired
    private CarDOMapper carDOMapper;


    @Override
    public CarModel getCarModelById(String carID) {
        CarDO carDO = carDOMapper.selectByCarId(carID);
        if(carDO == null){
            return null;
        }
        return convertFromDataObject(carDO);
    }

    @Override
    public List<CarModel> listCar() {
        List<CarDO> carDOList = carDOMapper.listCar();
        List<CarModel> carModelList = carDOList.stream().map(carDO -> {
            CarModel carModel = this.convertFromDataObject(carDO);
            return carModel;
        }).collect(Collectors.toList());
        return carModelList;
    }

    @Override
    public Map<String, CarModel> mapCar() {
        ArrayList<CarModel> listCar = (ArrayList<CarModel>) this.listCar();
        Map<String,CarModel> mapCar = new HashMap<>();
        for (CarModel carModel:listCar
             ) {
            mapCar.put(carModel.getCarID(),carModel);
        }
        return mapCar;
    }

    private CarModel convertFromDataObject(CarDO carDO) {
        if(carDO == null){
            return null;
        }
        CarModel carModel = new CarModel();
        //BeanUtils.copyProperties(carDO,carModel); //copy失败了？！ 貌似因为对应参数的名字不同
        carModel.setCarID(carDO.getCarid());
        carModel.setFromCrossID(carDO.getFromcrossid());
        carModel.setToCrossID(carDO.getTocrossid());
        carModel.setCurFromCrossID(carDO.getFromcrossid());
        carModel.setCurToCrossID(carDO.getTocrossid());
        carModel.setMaxVelocity(carDO.getMaxvelocity());
        carModel.setPlanTime(carDO.getPlantime());
        carModel.setLaneID(-1);
        carModel.setPriority(0);
        carModel.setCurPos(0);
        carModel.setSheng(-1);
        carModel.setHasArrangedOrNot(false);
        carModel.setState(-1);
        carModel.setNextRoadID("-1");
        carModel.setCanThrough(false);
        return carModel;
    }

    @Autowired
    private RoadServiceImpl roadServiceImpl;


    /**
     * 找出车辆的CurPos最小的车辆
     */
    public CarModel minCarCurPos(CarModel[] carList) {
        CarModel minCar = carList[0];
        int j = 0;
        for (; j < carList.length; j++) {
            // 先取一个不为null的车作为CurPos的最小车
            if (carList[j] != null) {
                minCar = carList[j];
                break;
            }
        }
        for (int i = j + 1; i < carList.length; i++) {
            // 比较得到curPos最小车
            if (carList[i] == null) {

                continue;
            }
            if (carList[i].getCurPos() < minCar.getCurPos()) {
                minCar = carList[i];
            }
        }
        return minCar;
    }

    /**
     * 查询当前搜索的道路和车的方向是否冲突
     * @param roadID    当前搜索道路
     * @param crossID 出发节点ID
     * @return true：方向一致；false：方向相反
     */
    public boolean isDirectionRight(String roadID, String crossID) {
        RoadModel road=roadServiceImpl.getRoadModelById(roadID);
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
     * @param roadID     当前的道路
     * @param carList  需要判断是否通过路口的车集合
     * @param laneList 车集合所在的lane集合
     * @param carIndex 车集合中各个车处于各自lane上的第几个位置
     * @return 需要过路口的车的集合，大小和输入的carList相同，只是将不能过路口的车的位置放置null；
     */
    public CarModel[] ThroughCar(String roadID, CarModel[] carList, LinkedList<LaneModel> laneList, int[] carIndex) {
        RoadModel road=roadServiceImpl.getRoadModelById(roadID);
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
     * @param roadID     当前道路
     * @param carID      当前车辆
     * @param laneList 车所在的lane集合
     * @param carIndex 车处于各自lane上的第几个位置
     */
    public void canThrough(String roadID, String carID, LinkedList<LaneModel> laneList, int[] carIndex) {
        RoadModel road=roadServiceImpl.getRoadModelById(roadID);
        CarModel car= getCarModelById(carID);
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
}

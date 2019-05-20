package com.trafficproject.service.impl;

import com.trafficproject.dao.CarDOMapper;
import com.trafficproject.dataobject.CarDO;
import com.trafficproject.service.CarService;
import com.trafficproject.service.model.CarModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
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
        carModel.setCurToCrossID(carDO.getFromcrossid());
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




    /**
     * 找出车辆的CurPos最小的车辆
     *
     * @param carList 车辆集合
     * @return CurPos最小的车辆
     * @author Dalton
     * @version 2019.3.26
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




}

package com.trafficproject.service;

import com.trafficproject.service.model.CarModel;
import java.util.List;
import java.util.Map;

public interface CarService {
    CarModel getCarModelById (String carID);
    List<CarModel> listCar();
    Map<String,CarModel> mapCar();

    /**
     * 找出车辆的CurPos最小的车辆
     * @param carList 车辆集合
     * @return CurPos最小的车辆
     */
    CarModel minCarCurPos(CarModel[] carList);


}

package com.trafficproject.service;

import com.trafficproject.service.model.CarModel;

import java.util.HashSet;
import java.util.LinkedList;

public interface DebugService {
    void testShowCarInfo(HashSet<CarModel> cs);
    void testShowCarInfo(LinkedList<CarModel> cs);
    void testShowRoadInfo(String s);
    void testShowMapInfo();

}

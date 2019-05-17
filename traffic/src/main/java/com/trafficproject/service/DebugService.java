package com.trafficproject.service;

import com.trafficproject.service.model.CarModel;

import java.util.HashSet;
import java.util.LinkedList;

public interface DebugService {
    /*
     * 用来输出车的状态，来debug
     *
     * @author lulu
     *
     * @version 2019-3-28
     */
    public void testShowCarInfo(HashSet<CarModel> cs);

    /*
     * 用来输出车的状态，来debug
     *
     * @author lulu
     *
     * @version 2019-3-28
     */
    public void testShowCarInfo(LinkedList<CarModel> cs);

    /*
     * 用来输出路的状态，来debug
     *
     * @author lulu
     *
     * @version 2019-3-28
     */
    public void testShowRoadInfo();

    /*
     * 用来输出路的状态，来debug
     *
     * @author lulu
     *
     * @version 2019-3-28
     */
    public void testShowRoadInfo(String s);

    public void testShowMapInfo();

}

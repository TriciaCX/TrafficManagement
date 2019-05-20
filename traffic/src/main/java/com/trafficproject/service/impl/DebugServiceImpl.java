package com.trafficproject.service.impl;

import com.trafficproject.service.DebugService;
import com.trafficproject.service.ManagementService;
import com.trafficproject.service.RoadService;
import com.trafficproject.service.model.CarModel;
import com.trafficproject.service.model.RoadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DebugServiceImpl implements DebugService {

    @Autowired
    private ManagementService managementService;

    @Autowired
    private RoadService roadService;

    private ArrayList<RoadModel> listRoad;

    private LinkedList<CarModel> garageFrozen;

    private HashSet<String> arrivedCar;

    private HashSet<CarModel> nowInRoadCar;


    /*
     * 用来输出车的状态，来debug
     *
     * @author lulu
     *
     * @version 2019-3-28
     */
    public void testShowCarInfo(HashSet<CarModel> cs) {
        Iterator<CarModel> it = cs.iterator();
        while (it.hasNext()) {
            CarModel c = it.next();
            System.out.println(c.toString());

        }

    }

    /*
     * 用来输出车的状态，来debug
     *
     * @author lulu
     *
     * @version 2019-3-28
     */
    public void testShowCarInfo(LinkedList<CarModel> cs) {
        Iterator<CarModel> it = cs.iterator();
        while (it.hasNext()) {
            CarModel c = it.next();
            System.out.println(c.toString());

        }

    }

    /*
     * 用来输出路的状态，来debug
     *
     * @author lulu
     *
     * @version 2019-3-28
     */
    public void testShowRoadInfo() {
        if(managementService.getListRoad()==null)
            managementService.setListRoad();
        listRoad=managementService.getListRoad();
        Iterator<RoadModel> it = listRoad.iterator();
        while (it.hasNext()) {

            String s = it.next().getRoadID();
            testShowRoadInfo(s);
        }

    }

    /*
     * 用来输出路的状态，来debug
     *
     * @author lulu
     *
     * @version 2019-3-28
     */
    public void testShowRoadInfo(String s) {
        RoadModel r = roadService.getRoadModelById(s);
        System.out.println("-----正向：");
        for (int i = 0; i < r.getLanesNum(); i++) {

            testShowCarInfo(r.getForwardLane().get(i).carsInLane);

        }
        System.out.println("-----反向：");
        for (int i = 0; i < r.getLanesNum(); i++) {

            testShowCarInfo(r.getBackwardLane().get(i).carsInLane);

        }

    }
    /**
     *
     */
    public void testShowMapInfo() {
        garageFrozen=managementService.getGarageFrozen();
        nowInRoadCar=managementService.getNowInRoadCar();
        arrivedCar=managementService.getArrivedCar();
        System.out.println("车库里还有："+garageFrozen.size()+"  在路上有："+ nowInRoadCar.size()+"  已经到家："+arrivedCar.size());
    }
}

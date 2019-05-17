package com.trafficproject.service.impl;

import com.trafficproject.service.ManagementService;
import com.trafficproject.service.RoadService;
import com.trafficproject.service.model.CarModel;
import com.trafficproject.service.model.RoadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
@Component
public class DebugServiceImpl {

    @Autowired
    private ManagementService managementService;

    @Autowired
    private RoadService roadService;

    private List<RoadModel> listRoad;

    private LinkedList<CarModel> garageFrozen;

    private HashSet<String> arrivedCar;

    private HashSet<CarModel> nowInRoadCar;

    public DebugServiceImpl() {
        this.listRoad = managementService.getListRoad();
        this.garageFrozen=managementService.getGarageFrozen();
        this.nowInRoadCar = managementService.getNowInRoadCar();
        this.arrivedCar = managementService.getArrivedCar();
    }

    /*
     * 用来输出车的状态，来debug
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
     */
    public void testShowRoadInfo() {
        Iterator<RoadModel> it = listRoad.iterator();
        while (it.hasNext()) {

            String s = it.next().getRoadID();
            testShowRoadInfo(s);
        }

    }

    /*
     * 用来输出路的状态，来debug
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

    public void testShowMapInfo() {
        System.out.println("车库里还有："+garageFrozen.size()+"  在路上有："+ nowInRoadCar.size()+"  已经到家："+arrivedCar.size());
    }
}

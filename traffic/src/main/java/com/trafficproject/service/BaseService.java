package com.trafficproject.service;

import com.trafficproject.service.model.CarModel;
import com.trafficproject.service.model.CrossModel;
import com.trafficproject.service.model.RoadModel;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Aspect
public class BaseService {

    @Autowired
    public CarService carService;

    @Autowired
    public RoadService roadService;

    @Autowired
    public CrossService crossService;

    public  static int maxRoadLength;
    public  static float[] w = new float[3];
    public  static int numOf2 = 0;
    public  static int numOf5 = 0;
    public  static ArrayList<CarModel> listCar;
    public  static ArrayList<CrossModel> listCross;
    public  static ArrayList<RoadModel> listRoad;
    public  static Map<String,CarModel> mapCar;
    public  static Map<String,CrossModel> mapCross;
    public  static Map<String,RoadModel> mapRoad;

    public  static LinkedList<CarModel> garageFrozen = new LinkedList<>();
    public  static LinkedList<CarModel> garageWait = new LinkedList<>();
    public  static HashSet<String> arrivedCar = new HashSet<>();
    public  static HashSet<CarModel> nowInRoadCar = new HashSet<>();
    public  static Map<String, String> ansMap;
    public  static String[] ans;

    public void initial() {
        if(this.listCar==null||this.listCar.isEmpty())
            this.listCar= (ArrayList<CarModel>) carService.listCar();
        if(this.listRoad==null||this.listRoad.isEmpty())
            this.listRoad= (ArrayList<RoadModel>) roadService.listRoad();
        if(this.listCross==null||this.listCross.isEmpty())
            this.listCross= (ArrayList<CrossModel>) crossService.listCross();
        if(this.mapCar==null||this.mapCar.isEmpty())
            this.mapCar=carService.mapCar();
        if(this.mapRoad==null||this.mapRoad.isEmpty())
            this.mapRoad= roadService.mapRoad();
        if(this.mapCross==null||this.mapCross.isEmpty())
            this.mapCross= crossService.mapCross();

        this.w[0]=1;
        this.w[1]=0;
        this.w[2]=0;
        this.maxRoadLength=(Collections.max(this.listRoad, new Comparator<RoadModel>() {
            @Override
            public int compare(RoadModel r1, RoadModel r2) {
                //根据路的长度对路排序
                if (r1.getRoadLength() > r2.getRoadLength()) {

                    return 1;
                } else if (r1.getRoadLength() < r2.getRoadLength()) {
                    return -1;

                } else {

                }
                return 0;
            }
        }).getRoadLength());
        ansMap = new HashMap<>(listCar.size()+1);
        ans = new String[listCar.size()+1];
        ans[0] = "#carID, StartTime, RoadID...";
        Iterator<CarModel> carIter=listCar.iterator();
        CarModel car;
        int i=1;
        while (carIter.hasNext()){
            car=carIter.next();
            ans[i++]=car.getCarID();
            ansMap.put(car.getCarID(),null);
        }
    }
    /**
     * sigmoid函数
     */
    public float sigmoid(int x) {
        return (float) (1 / (1 + Math.exp(-x)));
    }

    public float swish(int x) {
        return x * sigmoid(x);
    }

//    @Pointcut("execution(* com.trafficproject.service.FunctionService.findNextCross())")
//    public void find(){}

    @Before("execution(* com.trafficproject.service.FunctionService.findNextCross())")
    public void adjustW() {
        if (numOf2 != 0) {
            w[1] = sigmoid(numOf5 / numOf2);
        } else {

            w[1] = sigmoid(numOf5 / (numOf2 + 1));
        }
        w[2] = (float) ((float) w[1] * 0.5);
        w[0] = 1 - w[1] - w[2];
        w[1] *= 1;
        System.out.println("AOP works");
    }
}

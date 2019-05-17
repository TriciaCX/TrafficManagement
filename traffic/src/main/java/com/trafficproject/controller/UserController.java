package com.trafficproject.controller;

import com.trafficproject.service.*;
import com.trafficproject.service.model.CarModel;
import com.trafficproject.service.model.CrossModel;
import com.trafficproject.service.model.RoadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("/user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*", origins = "*")
public class UserController extends BaseController {

    public static int maxRoadLength;
    public static float[] w = new float[3];
    public static int numOf2 = 0;
    public static int numOf5 = 0;
    public static String filePath = "D:\\map";

//    @Autowired
//    private ManagementService managementService;
//
//    @Autowired
//    private RunService runService;
//
//    @Autowired
//    private AnswerService answerService;

    @Autowired
    private CarService carService;
    @Autowired
    private CrossService crossService;
    @Autowired
    private RoadService roadService;

    @RequestMapping(value = "/login",method = {RequestMethod.GET})
    @ResponseBody
    public String test(){
        Map<String,CarModel> mapCar = carService.mapCar();
        Map<String, RoadModel> mapRoad = roadService.mapRoad();
        CrossModel crossModel = crossService.getCrossModelById("1");
        System.out.println(crossModel.getCrossID());
        List<CrossModel> listCross = crossService.listCross();
        Map<String, CrossModel> mapCross = crossService.mapCross();

        return  mapCross.get("1").getCrossID();

//        w[0] = 1;w[1] = 0;w[2] = 0;
//        int n = managementService.getListCar().size();
//        Map<String, String> ansMap = new HashMap<>(n);
//        String[] ans = new String[managementService.getListCar().size()];
//        ans[0] = "#carID, StartTime, RoadID...";
//        managementService.getGarageFrozen().addAll(managementService.getListCar());
//        maxRoadLength=managementService.getMaxRoadLength(managementService.getListRoad());
//
//        long t1 = System.currentTimeMillis();
//        runService.run(ansMap, ans);
//        System.out.println("Successful routing!!!");
////        answerService.ansMapTOans(ansMap, ans);
////        answerService.write(filePath + "\\answer.txt", ans, false);
//        long t2 = System.currentTimeMillis();
//
//        System.out.println("time:" + (t2 - t1) + "ms");
//        return String.valueOf(t2-t1);
    }
}

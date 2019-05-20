package com.trafficproject.controller;

import com.trafficproject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller("/user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*", origins = "*")
public class UserController extends BaseController {

    public static String filePath = "D:\\config_10";

    @Autowired
    private RunService runService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private ManagementService managementService;


  //  public static float[] w = new float[3];

    @RequestMapping(value = "/login",method = {RequestMethod.GET})
    @ResponseBody
    public String test(){

        Map<String, String> ansMap = new HashMap<>(managementService.getListCar().size());
        String[] ans = new String[managementService.getListCar().size()];
        ans[0] = "#carID, StartTime, RoadID...";

        long t1 = System.currentTimeMillis();
        runService.run(ansMap, ans);
        System.out.println("Successful routing!!!");
        answerService.ansMapTOans(ansMap, ans);
        answerService.write(filePath + "\\answer.txt", ans, false);
        long t2 = System.currentTimeMillis();

        return ("time:" + (t2 - t1) + "ms");

    }
}

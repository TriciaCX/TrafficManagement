package com.trafficproject.controller;

import com.trafficproject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



@Controller("/user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*", origins = "*")
public class UserController extends BaseController {

    public static String filePath = "D:\\map";

    @Autowired
    private RunService runService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private BaseService baseService;


  //  public static float[] w = new float[3];

    @RequestMapping(value = "/login",method = {RequestMethod.GET})
    @ResponseBody
    public String test(){

        baseService.initial();
//        Map<String, String> ansMap = new HashMap<>(baseService.listCar.size()+1);
//        String[] ans = new String[baseService.listCar.size()+1];
//        ans[0] = "#carID, StartTime, RoadID...";

        long t1 = System.currentTimeMillis();
        runService.run(baseService.ansMap, baseService.ans);
        System.out.println("Successful routing!!!");
        answerService.ansMapTOans(baseService.ansMap, baseService.ans);
        answerService.write(filePath + "\\answer.txt", baseService.ans, false);
        long t2 = System.currentTimeMillis();

        return ("time:" + (t2 - t1) + "ms");

    }
}

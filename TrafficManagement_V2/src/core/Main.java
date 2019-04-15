package core;

import vo.Car;
import vo.Cross;
import vo.Road;
import util.PostprocUtil;
import util.PreprocUtil;
import util.IOUtil;
import info.*;

import java.util.*;

public class Main {
    public static LinkedList<Car> garageFrozen = new LinkedList<>();
    public static LinkedList<Car> garageWait = new LinkedList<>();
    public static int maxRoadLength = 0;
    public static HashMap<String, Road> MapRoad;
    public static HashMap<String, Cross> MapCross;
    public static HashMap<String, Car> MapCar;
    public static ArrayList<Cross> listCross;
    public static ArrayList<Car> listCar;
    public static ArrayList<Road> listRoad;
    public static HashSet<String> ArrivedCar = new HashSet<>();
    public static HashSet<Car> NowInRoadCar = new HashSet<>();
    public static String filePath = "D:\\map\\config_5";
    public static int numOf2 = 0;
    public static int numOf5 = 0;
    public static float[] w = new float[3];


    public static void main(String[] args) {
        //主程序入口

        //初始化权重
        w[0] = 1;w[1] = 0;w[2] = 0;

        //读取road.txt文件，并将它进行预处理
        String[] roadString = IOUtil.read(filePath + "\\road.txt", null);
        listRoad = PreprocUtil.PreRoadData(roadString);
        MapRoad = PreprocUtil.PreRoadDataMap(listRoad);

        //读取cross.txt文件，并将它进行预处理
        String[] crossString = IOUtil.read(filePath + "\\cross.txt", null);
        listCross = PreprocUtil.PreCrossData(crossString, MapRoad);
        MapCross = PreprocUtil.PreCrossDataMap(listCross);
        //读取car.txt文件，并将它进行预处理,其中ans为规划结果
        String[] carString = IOUtil.read(filePath + "\\car.txt", null);
        Map<String, String> ansMap = new HashMap<>(carString.length);
        String[] ans = new String[carString.length];
        ans[0] = "#carID, StartTime, RoadID...";
        listCar = PreprocUtil.PreCarData(carString, ansMap, ans);
       
        //所有车首先在车库准备着
        garageFrozen.addAll(listCar);
        MapCar = PreprocUtil.PreCarDataMap(listCar);
        System.out.println("Successful data accessing and object initializing!!!");
        maxRoadLength = RoadInfo.getMaxRoadLength(listRoad);
        
        //程序起始时间
        long t1 = System.currentTimeMillis();
        // 跑起来。。。
        Run2.run(ansMap, ans);
        System.out.println("Successful routing!!!");
        PostprocUtil.ansMapTOans(ansMap, ans);
        
        //将结果写出到文件
        IOUtil.write(filePath + "\\answer.txt", ans, false);
        
        //程序结束时间
        long t2 = System.currentTimeMillis();
        
        //跑程序花费时间
        System.out.println("time:" + (t2 - t1) + "ms");
    }
}

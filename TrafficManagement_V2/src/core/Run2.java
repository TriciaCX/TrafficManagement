package core;

import debug.ShowDetail;
import vo.Car;
import vo.Cross;


import java.util.LinkedList;
import java.util.Map;

import static debug.ShowDetail.testShowMapInfo;
import static util.PostprocUtil.updateAns;
import static util.RunUtil2.*;


public class Run2 {
    public static void run(Map<String, String> ansMap, String[] ans) {
        int t = 1;
        //在当前时刻从车库发车
        carsFromGarageInsertToRoad(t);
        //安排从车库来的车了，安排以后，统一把它的标志位设置成true,同时要把从车库来的车加到在路上的车里去
        setNowInRoadCarFromGarageWait();

        //保存车辆路径
        updateAns(ansMap, ans);
        System.out.println("车库始发车：");
        System.out.println("：");
        ShowDetail.testShowCarInfo(Main.NowInRoadCar);
        testShowMapInfo();


        while (true) {
            //把标志位都设成false
            setNowInRoadCarState(false);

            t++;
            if(t==2070)
                System.out.println();

            //判断是不是所有车都安排过了，sheng都是0了呀，也就是说位置都是真的了,其实是为了保证车库的车

            System.out.println("At time slot " + t);
            
            //先根据路口id升序，更新 路上车的状态
            while (!isAllReal()) {
                for (int i = 0; i < Main.listCross.size(); i++) {

                    //获得四个车，存进这个链表里，可能 不是4辆车 ，但最多四辆
                    Cross s = Main.listCross.get(i);
                    LinkedList<Car> carsFour = extractFourCar(s);
                    if (carsFour.isEmpty()) {
                        continue;
                    }
                    System.out.println("操作之前：");
                    ShowDetail.testShowCarInfo(carsFour);
                    //对取出来的“4”辆车进行一些列操作
                    FourCarStateUnionProcess(carsFour, t);
                    System.out.println("操作之后：");
                    ShowDetail.testShowCarInfo(carsFour);
                    System.out.println("_____");

                }
            }
            
            // 路上的车已经安排完了
            //把标志位都设成false,这样就能取出头车了
            setNowInRoadCarState(false);

            //保存车辆路径
            updateAns(ansMap, ans);

            //在当前时刻从车库发车
            carsFromGarageInsertToRoad(t);

            //安排从车库来的车了，安排以后，统一把它的标志位设置成true,同时要把从车库来的车加到在路上的车里去
            setNowInRoadCarFromGarageWait();

            //之前把原先在路上的车设成了false,所以这里要全设为true
            setNowInRoadCarState(true);

            //保存车辆路径
            updateAns(ansMap, ans);

            System.out.println("在" + t + "时刻”+“，车库发车：");
            ShowDetail.testShowCarInfo(Main.NowInRoadCar);
            testShowMapInfo();

            if (isAllArrived()) {
                break;
            }


        }

    }
}

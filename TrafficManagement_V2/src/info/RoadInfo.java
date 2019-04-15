package info;

import java.util.ArrayList;
import java.util.Collections;

import vo.Car;
import vo.Lane;
import vo.Road;

/**
 * getLeftCarsNum(Road myRoad,Cross fromCross, Cross toCross)--返回当前road的每一个lane还能进入多少辆车
 * @version 2019-03-20
 */
public class RoadInfo {


    /**
     * 返回下一时刻road的每一条lane还能进入多少辆车,即车位于NextPos时还剩多少位置
     *
     * @param myRoad
     * @param crossID
     * @return
     */
    public static ArrayList<Integer> getLeftLanesLength(Road myRoad, String crossID) {
        if (myRoad == null || myRoad.getRoadID().equals("-1")) {
            System.out.println("road");
        }

        if (crossID == null) {
            System.out.println("crossID");
        }

        ArrayList<Integer> LeftLanesLengthList = new ArrayList<Integer>();
        int LeftLength;
        int index = 0;
        //单向道路且车行驶方向与道路方向一致
        if (crossID.equals((myRoad.getFromCrossID()))) {
            for (Lane lane : myRoad.getForwardLane()) {
                //从road上得到lane
                //如果初始路上没有车
                Car lastCar;
                if (lane.carsInLane.size() == 0) {
                    //lastCar就不存在
                    //leftLength就是roadlength
                    LeftLength = myRoad.getRoadLength();
                } else {
                    lastCar = lane.carsInLane.getLast();
                    LeftLength = myRoad.getRoadLength() - 1 - lastCar.getCurPos();
                }
                LeftLanesLengthList.add(index++, LeftLength);

            }
        } else {//走backword
            for (Lane lane : myRoad.getBackwardLane()) {
                Car lastCar;
                if (lane.carsInLane.size() == 0) {
                    //lastCar就不存在
                    //leftLength就是roadlength
                    LeftLength = myRoad.getRoadLength();
                } else {
                    lastCar = lane.carsInLane.getLast();
                    LeftLength = myRoad.getRoadLength() - 1 - lastCar.getCurPos();
                }
                LeftLanesLengthList.add(index++, LeftLength);
            }

        }

        return LeftLanesLengthList;
    }


    /**
     * 返回roadList中最长的道路
     * @param roadList
     * @return 最大道路长度
     */
    public static int getMaxRoadLength(ArrayList<Road> roadList) {
        RoadComparator myComparator = new RoadComparator();
        return (Collections.max(roadList, myComparator).getRoadLength());
    }
}

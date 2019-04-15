package info;

import java.util.ArrayList;
import java.util.Collections;

import vo.Car;
import vo.Lane;
import vo.Road;

/**
 * getLeftCarsNum(Road myRoad,Cross fromCross, Cross toCross)--���ص�ǰroad��ÿһ��lane���ܽ����������
 * @version 2019-03-20
 */
public class RoadInfo {


    /**
     * ������һʱ��road��ÿһ��lane���ܽ����������,����λ��NextPosʱ��ʣ����λ��
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
        //�����·�ҳ���ʻ�������·����һ��
        if (crossID.equals((myRoad.getFromCrossID()))) {
            for (Lane lane : myRoad.getForwardLane()) {
                //��road�ϵõ�lane
                //�����ʼ·��û�г�
                Car lastCar;
                if (lane.carsInLane.size() == 0) {
                    //lastCar�Ͳ�����
                    //leftLength����roadlength
                    LeftLength = myRoad.getRoadLength();
                } else {
                    lastCar = lane.carsInLane.getLast();
                    LeftLength = myRoad.getRoadLength() - 1 - lastCar.getCurPos();
                }
                LeftLanesLengthList.add(index++, LeftLength);

            }
        } else {//��backword
            for (Lane lane : myRoad.getBackwardLane()) {
                Car lastCar;
                if (lane.carsInLane.size() == 0) {
                    //lastCar�Ͳ�����
                    //leftLength����roadlength
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
     * ����roadList����ĵ�·
     * @param roadList
     * @return ����·����
     */
    public static int getMaxRoadLength(ArrayList<Road> roadList) {
        RoadComparator myComparator = new RoadComparator();
        return (Collections.max(roadList, myComparator).getRoadLength());
    }
}

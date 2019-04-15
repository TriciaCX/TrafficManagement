package debug;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import core.Main;
import vo.Car;
import vo.Road;


public class ShowDetail {


    /**
     * �����������״̬����debug
     */
    public static void testShowCarInfo(LinkedList<Car> cs) {
        Iterator<Car> it = cs.iterator();
        while (it.hasNext()) {
            Car c = it.next();
            System.out.println(c.toString());

        }

    }
    
    /**
     * �����������״̬����debug
     */
    public static void testShowCarInfo(HashSet<Car> cs) {
        Iterator<Car> it = cs.iterator();
        while (it.hasNext()) {
            Car c = it.next();
            System.out.println(c.toString());

        }

    }

    /**
     * �������·��״̬����debug
     */
    public static void testShowRoadInfo() {
        Iterator<Road> it = Main.listRoad.iterator();
        while (it.hasNext()) {

            String s = it.next().getRoadID();
            testShowRoadInfo(s);
        }

    }

    /**
     * �������·��״̬����debug
     */
    public static void testShowRoadInfo(String s) {
        Road r = Main.MapRoad.get(s);
        System.out.println("-----����");
        for (int i = 0; i < r.getLanesNum(); i++) {

            testShowCarInfo(r.getForwardLane().get(i).carsInLane);

        }
        System.out.println("-----����");
        for (int i = 0; i < r.getLanesNum(); i++) {

            testShowCarInfo(r.getBackwardLane().get(i).carsInLane);

        }

    }

    public static void testShowMapInfo() {
        System.out.println("�����ﻹ�У�"+Main.garageFrozen.size()+"  ��·���У�"+ Main.NowInRoadCar.size()+"  �Ѿ����ң�"+Main.ArrivedCar.size());
    }

}

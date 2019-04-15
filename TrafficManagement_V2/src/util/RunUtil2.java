package util;

import core.Main;
import vo.Car;
import vo.Cross;
import vo.Lane;
import vo.Road;
import info.CrossInfo;
import info.RoadInfo;
//import com.sun.xml.internal.ws.runtime.config.TubelineFeatureReader;

import java.util.*;

import static info.CrossInfo.getNormalizedRoadLeftLength;


public class RunUtil2 {


    /**
     * ���´ӳ�������ĳ�����Ϣ���жϳ��Ƿ��ܹ����뵽�滮�ĵ�·�У�����ܣ��鿴ID���ȼ��Ƿ��ͻ���г�ͻ��
     * �Ƿ��л��˵ĳ�������reArrangeCars�У���������ܣ�ֱ�Ӽӵ�����reArrangeCars��
     * @param car����ǰ����road��Ҫ�ߵĵ�·��virtualCarsHashMap����õĳ���ԭʼ״̬��reArrangeCars�����˳������ϣ����ǿ��ܻ���IDС�����ٶȿ�ĳ����ȴӳ�������ȡ��������ͬһʱ�̣���ʼ·����ͬ��Ҫѡ��ĵ�·Ҳ��һ������ʱ���Ҫ�����ˣ�MapRoad���������ض����
     * @return ���ﷵ������Ԫ�أ�false:��һ���岻��ȥ���ڶ�������û�лع���û��
     * @version 2019-3-28
     */
    protected static boolean[] checkIDPriority(Car car, Road road, LinkedList<Car> reArrangeCars, HashMap<String, Road> MapRoad, int t) {
        //���Բ��ȥ,û�лع�
        boolean flag1 = true;
        boolean flag2 = true;

        //����һЩ��Ϣ
        car.setCurFromCrossID(car.getFromCrossID());
        car.setPriority(0);

        //�����趨��������getCurToCross()�Ǵ��Լ���ʼ��·�ڳ�����Ҫ���������·
        if (road.getFromCrossID().equals(car.getCurFromCrossID())) {
            car.setCurToCrossID(road.getToCrossID());

        } else {
            car.setCurToCrossID(road.getFromCrossID());
        }

        //�õ��������Ҫ����ĵ�·��lane��
        LinkedList<Lane> myLanes;// ����Ҫ�жϷ���
        if (!car.getCurToCrossID().equals(road.getFromCrossID())) {
            myLanes = road.getForwardLane();
        } else {
            myLanes = road.getBackwardLane();
        }
        int size = myLanes.size();
        int i = 0;
        for (i = size; i > 0; i--) {
            //���ó�������lane4�ϵ�һϵ�г�
            LinkedList<Car> cars = myLanes.get(i - 1).carsInLane;
            // �ҵ�Ҫ�����λ����//priority����Ҫ����Ϊ0
            if (cars.size() > 0 && cars.getLast().getPriority() == car.getPriority() && Integer.valueOf(cars.getLast().getCarID()) > Integer.valueOf(car.getCarID())) {
                // �����Ҳ�Ǹմӳ���v������Ķ��ң��������ID�����ڰ��ŵĳ���
                while (cars.size() > 0 && cars.getLast().getPriority() == car.getPriority()
                        && Integer.valueOf(cars.getLast().getCarID()) > Integer.valueOf(car.getCarID())) {
                    // �������Ҫ���˵�
                    // �Ȱ���ЩҪ�˻صĳ�������Ϣ����һ��
                    // ��ȡ������
                    Car virtualCar = Main.MapCar.get(cars.getLast().getCarID());
                    
                    //4.13 ���ֱ��ɾ�������ܻᵼ������·�ϳ�����״̬����4����ɲ���
                    if (virtualCar.getState() == 2)
                        setCarInRoad(null, virtualCar, road, 3, false, false, false, false, false);
                    //����mapCar����ԭ������Ϣ
                    innitial(virtualCar);
                    // ɾ����ʵ�������Ϣ
                    cars.removeLast();
                    // ����Щ��������
                    reArrangeCars.addLast(virtualCar);
                    // �ع���
                    flag2 = false;
                }
            }
            //������������ѭ����������Ļ�����˵��Ҫôȡ���ˣ�Ҫô���ǲ���ȡ��
        }
        
        //ÿ��lane����ȡ����
        // ����һ���������ã�3.28�汾��Ҫ�Լ��ж�һ�¿ɲ����Խ�ȥ��
        // ���ȥ����������Ҹ��������Ϣ
        flag1 = carIDInsertToRoad(car, road, MapRoad, t);
        if (!flag1)
        // ���������岻��ȥ��,Ҳ�ŵ����°��ŵĳ��ļ�����,������ٰ���һ��,�ٲ��оͷŵ�garageFrozon��
        // ��Ϊ��false,����û����ʵ��������ӹ���������ֻҪ����ԭ���ľͺ���
        {
            Car carVir = Main.MapCar.get(car.getCarID());
            innitial(carVir);
            reArrangeCars.add(car);
        }

        // ���ر��˻صĳ����߿����Ǳ���������,�ٲ�һ�Σ�
        while (!reArrangeCars.isEmpty()) {
            Car c = reArrangeCars.getLast();
            if (c != null) {
                if (reArrangeCarsIDInsertToRoad(c, road, MapRoad, t)) {
                    reArrangeCars.removeLast();
                } else {
                    // �岻��ȥ��
                    break;
                }
            } else {
                //ȡ������null��˵��Ӧ��ʱû����
                break;
            }
        }
        // ����Ԫ�أ�false:��һ���岻��ȥ���ڶ�����û�лع�
        boolean[] flags = new boolean[2];
        flags[0] = flag1;
        flags[1] = flag2;
        return flags;

    }

    /**
     * �Ѿ��ж������ӳ������ĳ���Ҫ�����·������ôѡ�񳵵��أ���Ҫ���������ϢŶ������˵׼���ع���Ҫ��һ��Ӱ����֮��ģ�ǰ��ĵ�·���Ѿ�û�����ȼ�������ĳ���
     * @param car����ǰ����road��Ҫ�ߵĵ�·��virtualCarsHashMap��MapRoad���������ض���ģ�
     * @return ���ﷵ��һ��Ԫ�أ�false:�岻��ȥ
     * @version 2019-3-28
     */
    protected static boolean carIDInsertToRoad(Car car, Road road, HashMap<String, Road> MapRoad, int t) {
        // LanesCarsList[0]=3,��ʾlane1��ʣ��ɽ��볤��Ϊ3�����ܽ�3����
        boolean flag = InsertFreshCarToRoad(car, road, t);
        // false:�岻��ȥ
        return flag;
    }

    /**
     * �����ӳ�������ĳ��ܲ��ܵ����·���������������ʱ��ǰ���ǲ����ܳ��ֻع��ģ���Ϊ�Ѿ������ˣ��ܵĻ��Ͳ��ȥ��
     * @param car
     * @param road
     * @param t
     * @return
     */
    protected static boolean InsertFreshCarToRoad(Car car, Road road, int t) {
        ArrayList<Integer> LeftLanesLengthList = RoadInfo.getLeftLanesLength(road, car.getCurFromCrossID());
        int size = road.getLanesNum();
        int i = 0;
        // true:���Բ��룻false:�岻��ȥ
        boolean flag1 = true;
        if (LeftLanesLengthList.size() == 0) {
            flag1 = false;
            // false:�岻��ȥ

            return flag1;

        } else {
            while (i < size && LeftLanesLengthList.get(i) == 0) {
                i++;
            }
            if (i == size) {
                flag1 = false;
                innitial(car);
                // false:�岻��ȥ
                return flag1;

            }

        }


        // ���Բ��ȥ��

        // Ҫ�����lane���Բ���Ŀռ�
        int nextLaneOfRoadLeftSize = LeftLanesLengthList.get(i);

        // �������·������ٶ��Լ������Լ�������ٶ�
        // �����lane������ٶ�
        int nextLaneVel = Math.min(road.getMaxRoadVelocity(), car.getMaxVelocity());

        /**
         * ֮ǰ�Ѿ����¹�����Ϣ car.setCurFromCross(car.getFromCross()); car.setPriority(0);
         * car.setCurToCross(road.getToCross());
         * ���ø��µ���Ϣ car.setFromCross(fromCross); car.setToCross(toCross);
         *          * car.setMaxVelocity(maxVelocity);
         */
        if (car.getRealStartTime() == -1) {

            car.setRealStartTime(t);
        }
        
        // �ҵ���һ��ȥ����·��
        if (car.getFromCrossID().equals(road.getFromCrossID())) {

            car.setCurToCrossID(road.getToCrossID());
        } else if (road.isDuplex()) {
            car.setCurToCrossID(road.getFromCrossID());

        } else {
            System.out.println("û����һ��·���ˣ�����ֵֹ�");
        }

        // �򵥵���Ϣ����
        // ������Ը���page10�õ���
        car.setCurPos(road.getRoadLength() - Math.min(nextLaneOfRoadLeftSize, nextLaneVel));
        car.setLaneID(i);
        car.setRoadID(road.getRoadID());
        car.setSheng(0);
        // ͨ��·�Ķ����laneID�ҵ�Lane�Ķ���thisLane
        Lane thisLane = new Lane();
        LinkedList<Lane> l;
        if (road.getFromCrossID().equals(car.getCurFromCrossID())) {

            l = road.getForwardLane();
        } else {
            l = road.getBackwardLane();
        }

        for (Lane l1 : l) {
            if (l1.getLaneIndex() == car.getLaneID()) {
                thisLane = l1;
                break;
            }
        }
        // ����������ʵ���ӵ�������lane
        thisLane.carsInLane.add(car);
        // ǰ��û�г�,��һ����һ��laneŶ

        // preCar�����·�ϵ�֮�ֻ�����ǰ��ĵ�һ�����������Ǹղ��ȥ�������������ܲ���
        // ���ﲻ���ж��ǲ���Ϊ�գ���Ϊ�Ҹղ��ȥһ�����أ�����car.setHasArrangedOrNot��false���ǻᱻ������

        Car preCar = Main.MapCar.get(RunUtil2.getFirstCarInRoad(road, car.getFromCrossID()));


        if (preCar.equals(car)) {
            //
            if (car.getCurPos() - nextLaneVel < 0) {
                // ��һʱ���ǿ��Գ�ȥ�ĳ�
                setCarInRoad(null, car, road, 3, false, false, false, false, false);
                car.setState(1);
            } else {
                // ��һʱ�̳���ȥ
                setCarInRoad(null, car, road, 4, false, false, false, false, false);
                car.setState(2);
            }
        } else {
            int preState = preCar.getState();
            if (preState == 1) {

                car.setState(3);
            } else if (preState == 2) {
                car.setState(4);
            } else if (preState == 3) {
                car.setState(3);
            } else if (preState == 4) {
                car.setState(4);
            } else {// ǰ����5
                car.setState(5);
            }
        }

        // ׼������,false:�岻��ȥ
        return flag1;

    }

    /**
     * ���˻����Ĵӳ�������ĳ��������ܲ��ܰ���ԭ�����뷨���뵽�����·��
     * @param car�����˻����ĳ���road������Ҫ�ߵĵ�·��
     * @return ��û�гɹ�����
     * @version 2019-3-26
     */
    protected static boolean reArrangeCarsIDInsertToRoad(Car car, Road road, HashMap<String, Road> MapRoad, int t) {
        boolean flag = InsertFreshCarToRoad(car, road, t);
        // false:�岻��ȥ
        return flag;

    }

    /**
     * �Գ����������в���
     * @param carsFour
     * @param t
     */
    public static void FourCarStateUnionProcess(LinkedList<Car> carsFour, int t) {
        ArrayList<Car> cars1 = new ArrayList<>();
        ArrayList<Car> cars2 = new ArrayList<>();
        for (Car c : carsFour) {
            switch (c.getState()) {
                case (3): {
                    /**
                     * �ж�һ����,��Ҫô���1��Ҫô���2��,��������еĳ�������״̬ҲҪ���Ÿ���һ��
                     */
                    UpdateRoadForCarsAtState3(c.getCarID());
                    break;

                }
                case (4): {
                    /**
                     * �������ǰ�汾���ǵڶ��೵,�����϶�����������ǰ������,���ù��ˣ����϶��Ǹ��¹��˵ĳ���
                     */
                    System.out.println("û�б�������ǰ�ܣ����ˣ�");
                    break;
                }
                case (5): {
                    /**
                     * �����뿴�ʼ�
                     */
                    updateLaneForCarsAtState5(c.getCarID());
                    break;

                }
                case (1): {
                    /**
                     * �����뿴�ʼǣ������ӵ�cars1�н������ȼ��Ƚϣ��Լ���ز���
                     */
                    cars1.add(c);
                    break;
                }
                case (2): {
                    /**
                     * ������ʲô����������Ϊ����cars1�ĳ�����ܻ�������µ�2false
                     */
                    break;
                }
                default:
                    System.out.println("��������������" + "\t");
            }


        }
        if (!cars1.isEmpty()) {
            UpdateLaneForCarsAtState1(cars1, t);
            // ������һ���µ�2false������
        }
        for (Car cc : carsFour) {
            if (cc.getState() == 2 && !cc.isHasArrangedOrNot()) {

                cars2.add(cc);
            }
        }

        /**
         * ����state2�ĳ�
         */
        UpdateLaneForCarsAtState2(cars2);


    }


    /**
     * ���ĸ�������һϵ�в�����
     * <p>
     * @param carsFour���ֻ���ĸ���
     * @return
     * @version 2019-3-26
     */
    public static void FourCarStatePreProcess(LinkedList<Car> carsFour) {
        for (Car c : carsFour) {
            if (c.getCarID().equals("10285") && c.getRoadID().equals("5002"))
                System.out.println();
            switch (c.getState()) {
                case (3): {
                    // �ж�һ����,��Ҫô���1��Ҫô���2��,��������еĳ���״̬ҲҪ���Ÿ���һ��
                    UpdateRoadForCarsAtState3(c.getCarID());
                    break;

                }
                case (4): {
                    // �������ǰ�汾���ǵڶ��೵,�����϶�����������ǰ������,���ù��ˣ����϶��Ǹ��¹��˵ĳ���
                    if (!c.isHasArrangedOrNot())
                        System.out.println("û�б�������ǰ�ܣ����ˣ�");
                    break;
                }
                case (5): {
                    /// �����������·�������ˣ����ͱ��1(false)�ˣ��������·�������߲���ȥ�����ͱ��5(true),����ĳ�����״̬5(false)
                    // ��Ȼ������5(false)

                    updateLaneForCarsAtState5(c.getCarID());
                    break;

                }
                case (1): {
                    // ǰ��û�赲,���ҳ�·��,���ʱ������������϶����Ǹտ�ʼ��ʱ��,����һʱ�̱����Ŵӳ��������

                    break;
                }
                case (2): {
                    // ǰ��û�赲,�����·��,ͬ��
                    break;
                }
                default:
                    System.out.println("��������������" + "\t");
            }

        }

    }

    /**
     * ���ĸ�������һϵ�в�����
     * @param carsFour���ֻ���ĸ���������ֻ������״̬��Ҫô����1Ҫô����2.
     * @return
     * @version 2019-3-26
     */
    public static void FourCarStateProcess(LinkedList<Car> carsFour, int t) {
        ArrayList<Car> cars1 = new ArrayList<>();
        ArrayList<Car> cars2 = new ArrayList<>();

        for (Car c : carsFour) {
            if (c.getState() == 1 && !c.isHasArrangedOrNot()) {
                cars1.add(c);
            }

        }
        UpdateLaneForCarsAtState1(cars1, t);
        // ������һ���µ�2false������
        for (Car c : carsFour) {
            if (c.getState() == 2 && !c.isHasArrangedOrNot()) {
                cars2.add(c);
            }

        }
        UpdateLaneForCarsAtState2(cars2);

    }

    /**
     * �����·��ȡ����ʱ���ŵ���4����
     * @param carsFour���ֻ���ĸ���
     * @return
     * @version 2019-3-26
     */
    public static LinkedList<Car> extractFourCar(Cross s) {
        LinkedList<Car> carsFour = new LinkedList<>();
        if (!s.getDownRoad().getRoadID().equals("-1")) {
            String carID = RunUtil2.getFirstCarInRoad(s.getDownRoad(), s.getCrossID());
            if (carID != null) {

                carsFour.add(Main.MapCar.get(carID));
            }
        }
        if (!s.getUpRoad().getRoadID().equals("-1")) {
            String carID = RunUtil2.getFirstCarInRoad(s.getUpRoad(), s.getCrossID());
            if (carID != null) {

                carsFour.add(Main.MapCar.get(carID));
            }
        }
        if (!s.getLeftRoad().getRoadID().equals("-1")) {
            String carID = RunUtil2.getFirstCarInRoad(s.getLeftRoad(), s.getCrossID());
            if (carID != null) {

                carsFour.add(Main.MapCar.get(carID));
            }
        }
        if (!s.getRightRoad().getRoadID().equals("-1")) {
            String carID = RunUtil2.getFirstCarInRoad(s.getRightRoad(), s.getCrossID());
            if (carID != null) {

                carsFour.add(Main.MapCar.get(carID));
            }
        }

        return carsFour;

    }

    /**
     * ����һ��·��,���Ƿ��·�ڣ�lane��˳���lane�ϳ�������Ŀ��·�ڵľ��룬������������������hasArrangeOrNotΪtrue�ĳ�
     * �����ȿ����Ƿ��·�ڣ�Ȼ���Ǿ��룬����ǳ���˳��
     * @param road����ǰ��·;crossID:�����ĸ�·�ڵ����·;
     * @return ��õ�ǰ��·�ͳ�����ʻ������ͬ������lane�ϳ����ķ���˳������getfirst���ȳ����ĳ�(ͷͷ)
     * @version 2019.3.28
     */
    public static LinkedList<String> getCarInRoad(Road road, String crossID) {
        LinkedList<Lane> laneList;
        LinkedList<String> out = new LinkedList<>();
        // �ҵ��ͳ�������һ�µĳ�������
        if (road.isDuplex()) {

            laneList = road.getFromCrossID().equals(crossID) ? road.getForwardLane() : road.getBackwardLane();
        } else {
            laneList = road.getForwardLane();
        }
        int laneNum = laneList.size();
        Car[] carList = new Car[laneNum];// ÿ��ȡ������ǰ��ļ��������ĳ����бȽ�,�±��Ӧ���ڳ�����û���ĳ���������공�ĳ�����null
        int[] carIndex = new int[laneNum];// ÿ������ȡ���ڼ��������±��Ӧ���ڳ�����û���ĳ��������Ѿ�������ĳ�������-1
        for (int i = 0; i < laneNum; i++) {// ��ʼ��,��ͷ������
            carIndex[i] = 0;
            Lane lane = laneList.get(i);
            if (!lane.carsInLane.isEmpty()) {// �ó����г�
                // �����Ѱ��ŵĳ���
                while (carIndex[i] < lane.carsInLane.size() && lane.carsInLane.get(carIndex[i]).isHasArrangedOrNot()) {
                    carIndex[i]++;
                }
                // �������������Ѱ��ŵĳ�
                if (carIndex[i] == lane.carsInLane.size()) {
                    carList[i] = null;
                    carIndex[i] = -1;
                }
                // ����δ���ŵĳ�
                else {
                    carList[i] = lane.carsInLane.get(carIndex[i]);
                }
            } else {// �ó����޳�
                carList[i] = null;
                carIndex[i] = -1;
            }
        }
        int laneIndex;
        int t = 0, tt = 0;
        Car[] throughCar = ThroughCar(road, carList, laneList, carIndex);// ѡ����·�ڵĳ�
        while (true) {
            t = 0;
            tt = 0;
            for (int i = 0; i < throughCar.length; i++) {
                if (throughCar[i] == null) {
                    tt++;
                }
            }
            if (tt < throughCar.length) {// �Ȱ�����ͳ����Ÿ���·�ڵĳ�����
                for (int i = 0; i < laneNum; i++) {
                    if (carIndex[i] < 0) {
                        t++;
                    }
                }
                if (t == laneNum) {
                    break;// ���������г������г�����carIndexΪ-1���˳�ѭ��
                }
                Car car = minCarCurPos(throughCar);// ȡ����ǰcarList��curPos��С�ĳ�
                laneIndex = car.getLaneID();
                out.add(car.getCarID());
                // �������Ѿ����ŵĳ�
                while ((++carIndex[laneIndex]) < laneList.get(laneIndex).carsInLane.size()
                        && laneList.get(laneIndex).carsInLane.get(carIndex[laneIndex]).isHasArrangedOrNot())
                    ;
                // ��ǰ�����Ѿ������공��
                if (carIndex[laneIndex] >= laneList.get(laneIndex).carsInLane.size()) {
                    carIndex[laneIndex] = -1;
                    carList[laneIndex] = null;
                    throughCar[laneIndex] = null;
                } else {// ���ó�����Ӧ�ıȽϼ���carListλ�õ�carָ��ó�������һ����
                    Car c = laneList.get(laneIndex).carsInLane.get(carIndex[laneIndex]);
                    canThrough(road, c, laneList, carIndex);
                    if (c.isCanThrough()) {

                        throughCar[laneIndex] = c;
                    } else {
                        carList[laneIndex] = c;
                        throughCar[laneIndex] = null;
                    }
                    // throughCar = ThroughCar(road, carList, laneList, carIndex);
                }
            } else {// �������·�ڵĳ�֮��Ϊ����·�ڵĳ�������ͳ���������
                t = 0;
                for (int i = 0; i < laneNum; i++) {
                    if (carIndex[i] < 0) {
                        t++;
                    }
                }
                if (t == laneNum)
                    break;// ���������г������г�����carIndexΪ-1���˳�ѭ��
                Car car = minCarCurPos(carList);// ȡ����ǰcarList��curPos��С�ĳ�
                laneIndex = car.getLaneID();
                out.add(car.getCarID());
                // �������Ѿ����ŵĳ�
                while ((++carIndex[laneIndex]) < laneList.get(laneIndex).carsInLane.size()
                        && laneList.get(laneIndex).carsInLane.get(carIndex[laneIndex]).isHasArrangedOrNot())
                    ;
                // ��ǰ�����Ѿ������공��
                if (carIndex[laneIndex] >= laneList.get(laneIndex).carsInLane.size()) {
                    carIndex[laneIndex] = -1;
                    carList[laneIndex] = null;
                } else {// ���ó�����Ӧ�ıȽϼ���carListλ�õ�carָ��ó�������һ����
                    carList[laneIndex] = laneList.get(laneIndex).carsInLane.get(carIndex[laneIndex]);
                }
            }
        }
        return out;
    }

    /**
     * �ҳ�������CurPos��С�ĳ���
     * @param carList ��������
     * @return CurPos��С�ĳ���
     * @version 2019.3.26
     */
    protected static Car minCarCurPos(Car[] carList) {
        Car minCar = carList[0];
        int j = 0;
        for (; j < carList.length; j++) {
            // ��ȡһ����Ϊnull�ĳ���ΪCurPos����С��
            if (carList[j] != null) {
                minCar = carList[j];
                break;
            }
        }
        for (int i = j + 1; i < carList.length; i++) {
            // �Ƚϵõ�curPos��С��
            if (carList[i] == null) {

                continue;
            }
            if (carList[i].getCurPos() < minCar.getCurPos()) {
                minCar = carList[i];
            }
        }
        return minCar;
    }


    /**
     * �Գ��������ǲ��Ƕ�����ʵ��λ����
     * <p>
     * @param hasArrag=true,sheng=0
     * @return
     * @version 2019-3-26
     */
    public static boolean isAllReal() {
        if (Main.NowInRoadCar.isEmpty()) {

            return true;
        }

        Iterator<Car> carIt = Main.NowInRoadCar.iterator();
        boolean ans = true;
        while (carIt.hasNext()) {
            Car c = carIt.next();
            if (c.getSheng() != 0 || !c.isHasArrangedOrNot()) {

                ans = false;

                break;

            }

        }
        return ans;
    }

    /**
     * �Գ��������ǲ��Ƕ����Ѿ������յ���
     * @version 2019-3-26
     */
    public static boolean isAllArrived() {
        Iterator<String> carIt = Main.ArrivedCar.iterator();
        int sum = 0;

        while (carIt.hasNext()) {
            carIt.next();
            sum++;

        }
        return sum == Main.listCar.size();
    }

    /**
     * ��ѯ��ǰ��·�Ƿ��������п��ߵĿռ䣬��Ϊ��Ҫ���߳�����С�ĳ����� С����û�ռ䣬����û�и��¹�ֱ����Ϊû·�� û�����пռ�����·�����¹�û�ռ�������Ҵ��lane�� ���¹��пռ�Ϊ��·��
     * @param road  ��ǰ�����ĵ�· ;
     * @param CrossID crossID:�����ڵ�ID
     * @return -2:����lane�����¹���,Ҳû�пռ䣻-1��δ���¹���û�пռ䣻1��δ���¹����пռ䣻2�����¹����пռ䣻
     */
    protected static int hasLeftLength(Road road, String CrossID) {
        LinkedList<Lane> laneList;
        // �ҵ��ͳ�������һ�µĳ�������
        if (road.isDuplex()) {
            laneList = road.getFromCrossID().equals(CrossID) ? road.getForwardLane() : road.getBackwardLane();
        } else {
            laneList = road.getForwardLane();
        }
        List<Integer> leftLength = RoadInfo.getLeftLanesLength(road, CrossID);
        int i = 0;
        int laneNum = leftLength.size();
        for (; i < laneNum; i++) {
            Lane lane = laneList.get(i);
            LinkedList<Car> carsInLane = lane.carsInLane;
            if (!carsInLane.isEmpty()) {
                boolean hasArranged = carsInLane.getLast().isHasArrangedOrNot();
                if (hasArranged) {
                    if (leftLength.get(i) > 0) {
                        // ���¹����пռ�
                        return 2;
                    } else {
                        continue;
                    }
                } else {
                    if (leftLength.get(i) > 0) {
                        // δ���¹����пռ�
                        return 1;
                    } else {
                        // δ���¹���û�пռ�
                        return -1;
                    }
                }
            } else {
                return 3;
            }
        }
        // ����lane�����¹���,Ҳû�пռ�
        return -2;
    }


    /**
     * ��ѯ��ǰ�����ĵ�·�ͳ��ķ����Ƿ��ͻ
     * @param road    ��ǰ������·
     * @param crossID �����ڵ�ID
     * @return true������һ�£�false�������෴
     */
    protected static boolean isDirectionRight(Road road, String crossID) {
        if (!road.isDuplex()) {
            if (road.getFromCrossID().equals(crossID)) {

                return true;
            } else {
                return false;
            }
        } else {

            return true;
        }
    }


    /**
     * �ӳ�����ֹ��ȡ�����Գ����ĳ�����garageWait
     *
     * @param cars ������ֹ��������
     * @param t    ��ǰʱ�䣨t-t+1��
     */
    protected static void classifyCars(LinkedList<Car> cars, int t) {

        LinkedList<Car> garageWait = Main.garageWait;

        garageWait.clear();

        for (int i = cars.size() - 1; i >= 0; i--) {
            Car car = cars.get(i);
            car.setHasArrangedOrNot(false);
            if (car.getPlanTime() <= t) {
                // �����ƻ�����ʱ����ڵ�ǰʱ�䣬������ֹ
                garageWait.add(car);
                cars.remove(i);
            }
        }
        // �������ٶ���������
        Collections.sort(garageWait, new Comparator<Car>() {
            @Override
            public int compare(Car o1, Car o2) {
                return o1.getMaxVelocity() - o2.getMaxVelocity();
            }
        });
    }


    /**
     * �Ե�ǰ�����滮��һ��·,2019.3.28 ��֤��һ��·���ߣ�����rest������car.getCurToCross��ʼ��·��,���Խ�һ���Ż�����ʱ��һ��·�����߲�һ������ڸ�ʱ�̲����ߣ���Ҫ������һЩ������ܻ��пռ�
     * @param car  ��ǰ�����������ȴ�״̬��
     * @param maxRoadLength
     * @return ��һ����·
     */
    protected static Road findNextCross(Car car, int maxRoadLength) {
        // ������·��ͨ��·�����յ㣬�򷵻�����·ID
        if (car.getCurToCrossID().equals(car.getToCrossID())) {

            return Main.MapRoad.get(car.getRoadID());
        }
        // δ֪�ڵ㼯��
        List<Cross> unknown = new ArrayList<Cross>();
        // ��ǰ�����ڵ�·�ڣ�����Ϊnull
        Cross s = Main.MapCross.get(car.getCurToCrossID());
        // Ŀ�ĵ�
        Cross t = Main.MapCross.get(car.getToCrossID());
        Iterator<Cross> crossIter = Main.listCross.iterator();
        while (crossIter.hasNext()) {
            Cross cross = crossIter.next();
            if (s.getCrossID().equals(cross.getCrossID())) {
                // ��ʼ�������ڵ�,����д��equals
                // ���ﵱǰ�ڵ�ʱ��
                cross.cost = 0;
                cross.isKnown = true;
                cross.preCross = null;
            } else {// ��ʼ������ڵ�
                cross.cost = Float.MAX_VALUE;
                cross.isKnown = false;
                cross.preCross = null;
                unknown.add(cross);
            }
        }
        // ��ʾ�ӳ����ڵ�������ڽӽڵ㣬��ʱҪ��֤��������·��
        ArrayList<Road> roads = new ArrayList<>();
        for (String roadID : s.getRoadIDList()) {
            if (!roadID.equals("-1")) {

                roads.add(Main.MapRoad.get(roadID));
            }
        }
        // �ҵ���������·,��ʼ�������null
        Road preRoad = findRoad(car.getCurFromCrossID(), car.getCurToCrossID());
        // ����ǵ�һ����ѡ·�в����ߵ�·
        boolean flag = deleteCrossFromUnknown(unknown, car, roads, preRoad);
        if (!flag) {
            // ��һ��·��·����
            return null;
        }
        while (!t.isKnown) {
            Collections.sort(unknown, new Comparator<Cross>() {
                @Override
                public int compare(Cross o1, Cross o2) {
                    if (o2.cost > o1.cost) {
                        return 1;
                    } else if (o2.cost < o1.cost) {
                        return -1;

                    } else {
                        return 0;
                    }

                }
            });
            if (unknown.isEmpty()) {
                System.out.println("�Ҳ���Ŀ��·��");
            }
            // �ҵ���ǰ��Դ�ڵ������������С�Ľڵ�
            Cross v = unknown.get(unknown.size() - 1);
            v.isKnown = true;
            unknown.remove(unknown.size() - 1);
            ArrayList<Road> roadsList = new ArrayList<>();
            for (String roadID : v.getRoadIDList()) {
                Road road = Main.MapRoad.get(roadID);
                if (!roadID.equals("-1")) {

                    roadsList.add(road);
                }
            }
            for (Road road : roadsList) {
                // �Ӹýڵ������������ڽڵ�
                if (road != null && isDirectionRight(road, v.getCrossID())) {
                    markNextCross(road, v);
                }
            }
        }
        return findFirstRoad(s.getCrossID(), t.getCrossID());
    }


    /**
     * @param unknown ����·����δ֪�ڵ㼯��
     * @param car     ��ǰ���ų���
     * @param roads   ��ǰ������·����
     * @param preRoad ����ǰ���ڵ�·
     * @return ��unknown�б�ǵ�һ�����нڵ� �������û�е�·�����򲻶Ե�·��������· ����û�ռ��·�������cost�������Ǹ��¹�û�ռ仹��û���¹�û�ռ�
     */
    protected static boolean deleteCrossFromUnknown(List<Cross> unknown, Car car, List<Road> roads, Road preRoad) {
        // ���е�·����
        int numOfAbleRoads = 0;
        Cross s;
        if (car.getCurToCrossID() == null) {
            s = Main.MapCross.get(car.getFromCrossID());
        } else {
            // ���������ڵ�
            s = Main.MapCross.get(car.getCurToCrossID());
        }
        int[] flag = new int[roads.size()];
        for (int i = 0; i < roads.size(); i++) {
            // ��ǰ������·
            Road road = roads.get(i);
            if (preRoad != null && (road.equals(preRoad))) {
                // ���߻�ͷ·
                flag[i] = -1;
            } else {
                if (road == null || road.getRoadID().equals("-1")) {
                    // ǰ���Ѿ�ȷ�������С�-1��
                    // û��·���Ϊ-2
                    flag[i] = -2;
                } else if (!isDirectionRight(road, s.getCrossID())) {
                    // ��·�����򲻶Ա��Ϊ-3
                    flag[i] = -3;
                } else if (hasLeftLength(road, s.getCrossID()) > 0) {
                    numOfAbleRoads++;
                    // �пռ�
                    flag[i] = 1;
                } else if (hasLeftLength(road, s.getCrossID()) == -1) {
                    numOfAbleRoads++;
                    // û���¹���û�ռ�
                    flag[i] = 2;
                } else if (hasLeftLength(road, s.getCrossID()) == -2) {
                    numOfAbleRoads++;
                    // ���¹���û�ռ�
                    flag[i] = 3;
                } else {
                    flag[i] = 0;
                }
            }
        }
        if (numOfAbleRoads == 0) {

            return false;
        } else {
            for (int i = 0; i < roads.size(); i++) {
                Road road = roads.get(i);
                if (flag[i] > 0) {

                    markNextCross(road, s);
                } else if (flag[i] < 0) {
                    continue;
                } else {
                    System.out.println("��֪��ɶ·��");
                }

            }
        }
        return true;
    }

    /**
     * �ҵ���·��s��·��t�ĵ�·
     * @param crossSID
     * @param crossTID
     * @return
     */
    protected static Road findRoad(String crossSID, String crossTID) {
        if (crossSID.equals(crossTID)) {

            return null;
        }
        Cross s = Main.MapCross.get(crossSID);
        Cross t = Main.MapCross.get(crossTID);
        String[] roadss = s.getRoadIDList();
        String[] roadst = t.getRoadIDList();
        HashSet<String> set = new HashSet<>();
        String roadID = new String();
        for (String ss : roadss) {
            set.add(ss);
        }
        for (String ss : roadst) {
            if (!ss.equals("-1") && !set.add(ss)) {

                roadID = ss;
            }
        }
        return Main.MapRoad.get(roadID);
    }

    protected static void markNextCross(Road road, Cross s) {
        Cross t = getCross(road, s.getCrossID());
        if (!t.isKnown) {
            // ����Ӧ����w2��������ǰ������w3���ͻع���أ�
            // cost1:Ҫȥ������·���������·�ı�ֵ
            float NormalizedRoadLength = RunUtil2.getNormalizedRoadLength(road, Main.maxRoadLength);
            // cost2��Ҫȥ������·��ʣ��ռ�
            float NormalizedRoadLeftLength = getNormalizedRoadLeftLength(road, s.getCrossID());
            // cost3��ӵ��ϵ��������CrossInfo.getCrossCarNum(s.getCrossID(), t.getCrossID())��ʾ�ж���������ͨ�����·��
            float sigmoidCrossCarNum = swish(CrossInfo.getCrossCarNum(s.getCrossID(), t.getCrossID()));
            float cost = Main.w[0] * NormalizedRoadLength + Main.w[1] * (1 - NormalizedRoadLeftLength) + Main.w[2] * sigmoidCrossCarNum;
            if (s.cost + cost < t.cost) {
                t.cost = s.cost + cost;
                t.preCross = s;
            }
        }
        adjustW(Main.w);
    }


    /**
     * ���ݵ�·road�Լ���ǰ·���ҵ���·ͨ�����һ·��
     * @param road
     * @param sID
     * @return
     */
    protected static Cross getCross(Road road, String sID) {
        if (!road.isDuplex()) {
            if (road.getFromCrossID().equals(sID)) {

                return Main.MapCross.get(road.getToCrossID());
            } else {
                return null;
            }
        } else {

            return road.getFromCrossID().equals(sID) ? Main.MapCross.get(road.getToCrossID())
                    : Main.MapCross.get(road.getFromCrossID());
        }
    }

    /**
     * @param sID:��ǰ����·��ID
     * @param tID��Ŀ��·��ID
     * @return �ӵ�ǰ����·�ڳ����ĵ�һ����·
     * @version 2019.3.22
     */
    protected static Road findFirstRoad(String sID, String tID) {
        Cross s = Main.MapCross.get(sID);
        Cross t = Main.MapCross.get(tID);
        Cross temp = t;
        while (!temp.preCross.equals(s)) {
            temp = temp.preCross;
        }
        return findRoad(sID, temp.getCrossID());
    }


    /**
     * sigmoid����
     * @param x
     * @return
     */
    protected static float sigmoid(int x) {
        return (float) (1 / (1 + Math.exp(-x)));

    }

    protected static float swish(int x) {
        return x * sigmoid(x);
    }

    protected static void adjustW(float[] w) {
        if (Main.numOf2 != 0) {
            w[1] = sigmoid(Main.numOf5 / Main.numOf2);
        } else {

            w[1] = sigmoid(Main.numOf5 / (Main.numOf2 + 1));
        }
        w[2] = (float) ((float) w[1] * 0.5);
        w[0] = 1 - w[1] - w[2];
        w[1] *= 1;
    }


    /**
     * cost1
     * @param road
     * @return NormalizedRoadLength
     * @version 2019-3-21
     */
    protected static float getNormalizedRoadLength(Road road, int maxRoadLength) {
        float NormalizedRoadLength = 0;
        // Main.maxRoadLength���·����
        NormalizedRoadLength = road.getRoadLength() / maxRoadLength;
        return NormalizedRoadLength;
    }

    /**
     * ��State3:firstCar������road�ϵĳ���״̬���� ��Ҫ����״̬��Ϣ��������λ����Ϣ�����firstCar�ܹ�·�ڣ�state=1,����ĳ�state=3;firstCar���ܹ�·�ڣ�state=2,����ĳ�state=4
     *
     * @param firstCarID
     */

    protected static void UpdateRoadForCarsAtState3(String firstCarID) {
        Car firstCar = Main.MapCar.get(firstCarID);
        // car��ʻ��·
        Road carInRoad = Main.MapRoad.get(firstCar.getRoadID());
       // String crossID = firstCar.getCurFromCrossID();
        // firstCar��ʱ����ʻ�ٶ�
        int maxSpeed = Math.min(firstCar.getMaxVelocity(), carInRoad.getMaxRoadVelocity());
        // �ж�firstCar�Ƿ��·��
        // ***********����firstCar��״̬
        // firstCar.setCurPos(firstCar.getCurPos()-maxSpeed);//State3�ĳ�������t3ʱ�̵�curPos
        if (firstCar.getCurPos() - maxSpeed < 0) { // ����ܹ���·��
            firstCar.setState(1);
        } else { // ������ܹ�·��
            firstCar.setState(2);
        }
        int firstCarState = firstCar.getState();
        // **********����Road����������״̬�������ֹ��һ���Ļ���
        // carInRoadList��洢����car��ID

//        LinkedList<String> carInRoadList = getCarInRoad(carInRoad, crossID);
//        int carInRoadNum = carInRoadList.size();
        int state = 0;
        if (firstCar.getState() == 1) {
            state = 3;
        } else {
            state = 4;
        }
        setCarInRoad(null, firstCar, carInRoad, state, false, false, false, false, false);
        firstCar.setState(firstCarState);
        // �Ż�Ϊ����������
        // �����ֹ��һ����
//        if (carInRoadNum > 1) {
//            Iterator<String> it = carInRoadList.iterator();
//            while (it.hasNext()) {
//                // �������г���state
//                Main.MapCar.get(it.next()).setState(state);
//            }
//            // ������������ʱ��������ģ���Ҫ��������firstCar��״̬
//            firstCar.setState(firstCarState);
//        }
    }

    /**
     * ��firstCar������lane�ϵĳ���t2ʱ�̸��µ�t3ʱ��
     * @param firstCar
     * @version 2019.03.26
     */

    protected static void UpdateLaneForCars(Car firstCar) {
        // firstCar��t3ʱ��һ���ǲ��ܹ�·�ڵģ�
        // ��t4ʱ�̵�״̬�ǲ�һ���ģ�����ܹ�·����Ϊ1������ĳ�����3�����ܹ�·�ڵĻ�����Ϊ2�����涼��4

        // *************�ҳ�firstCar���ڵ�lane
        // car��ʻ��·
        Road carInRoad = Main.MapRoad.get(firstCar.getRoadID());
        LinkedList<Lane> carInLane = new LinkedList<>();
        if (firstCar.getCurFromCrossID().equalsIgnoreCase(carInRoad.getFromCrossID())) {
            carInLane = carInRoad.getForwardLane();
        } else {
            // car��ʻ��·������Щlane
            carInLane = carInRoad.getBackwardLane();
        }

        int carInLaneID = firstCar.getLaneID();
        // fisrtCar���ڵ�lane
        Lane laneInvlovesCar = carInLane.get(carInLaneID);
        int maxSpeed = Math.min(firstCar.getMaxVelocity(), carInRoad.getMaxRoadVelocity());
        // ***********����firstCar��״̬
        // ����t3ʱ��
        firstCar.setCurPos(firstCar.getCurPos() - maxSpeed);
        // �ж�t4ʱ�̵�״̬
        if (firstCar.getCurPos() - maxSpeed < 0) {
            // ����ܹ���·��
            firstCar.setState(1);
        } else {
            // ������ܹ�·��
            firstCar.setState(2);
        }

        // **********����lane���������������ֹ��һ���Ļ���
        LinkedList<Car> carsInLane = laneInvlovesCar.carsInLane;
        int carInLaneNum = carsInLane.size();
        int state = 0;
        if (firstCar.getState() == 1) {
            state = 3;
        } else {
            state = 4;
        }
        if (carInLaneNum > 1) {
            for (int i = 1; i < carInLaneNum; i++) {
                // ------case1:û�а��Ź��ĳ�
                if (carsInLane.get(i).isHasArrangedOrNot() == false) {
                    if (carsInLane.get(i).getCurPos() - maxSpeed <= carsInLane.get(i - 1).getCurPos()) {
                        // ��׷��ǰ����ǰ���Ѿ����µ�t3ʱ���ˣ�
                        carsInLane.get(i).setCurPos(carsInLane.get(i - 1).getCurPos() + 1);
                    } else {
                        // �������׷��ǰ�������ܶ�Զ�ܶ�Զ
                        carsInLane.get(i).setCurPos(carsInLane.get(i).getCurPos() - maxSpeed);
                    }
                }
                // -----case2:���Ź��ĳ�������һ��·�ڹ�����)
                else if (carsInLane.get(i).isHasArrangedOrNot() == true && carsInLane.get(i).getSheng() != 0) {
                    if (carsInLane.get(i).getCurPos() - carsInLane.get(i - 1).getCurPos() > carsInLane.get(i)
                            .getSheng()) { // ׷����ǰ��
                        carsInLane.get(i).setCurPos(carsInLane.get(i).getCurPos() - carsInLane.get(i).getSheng());
                    } else {// ��׷��
                        carsInLane.get(i).setCurPos(carsInLane.get(i - 1).getCurPos() + 1);
                    }
                }
                // -----case3:�������һ���Ѿ����µ��ˣ���������϶�Ҳ�Ѿ������ˣ������������ע����Σ�
                else {
                    if (carsInLane.get(i).isHasArrangedOrNot() == true && carsInLane.get(i).getSheng() == 0) {
                        break;
                    }
                }
                // ͳһ��������
                carsInLane.get(i).setSheng(0);
                carsInLane.get(i).setState(state);
                carsInLane.get(i).setHasArrangedOrNot(true);
            }
        }

    }


    /**
     * ��firstCar������lane�ϵĳ���t2ʱ�̸��µ�t3ʱ��
     * @param car1s
     * @param t
     */
    protected static void UpdateLaneForCarsAtState1(ArrayList<Car> car1s, int t) {

        sortCarsOfState1(car1s);
        for (int i = 0; i < car1s.size(); i++)
            UpdateCarsAtState1(car1s.get(i).getCarID(), t);
    }

    /**
     * ��t2ʱ�̸��µ�t3ʱ��
     * @param firstCarID
     * @param t
     * @version 2019-04-14
     */

    public static void UpdateCarsAtState1(String firstCarID, int t) {
        Car firstCar = Main.MapCar.get(firstCarID); // firstCarʵ��
        Road carInRoad = Main.MapRoad.get(firstCar.getRoadID()); // firstCar���ڵ�Road
        LinkedList<Lane> carInLanes = new LinkedList<Lane>(); // firstCar��ʻ��·������Щlane

        if (firstCar.getCurFromCrossID().equals(carInRoad.getFromCrossID())) {// �жϷ���
            carInLanes = carInRoad.getForwardLane();
        } else {
            carInLanes = carInRoad.getBackwardLane(); // car��ʻ��·������Щlane
        }

        int carInLaneID = firstCar.getLaneID();
        Lane laneInvlovesCar = carInLanes.get(carInLaneID);// fisrtCar���ڵ�lane


        // **********���1��Ҫ������
        //
        if (firstCar.getCurToCrossID().equals(firstCar.getToCrossID())) {
            firstCar.setRealEndTime(t + 1); // ���õ���ʱ��
            Main.ArrivedCar.add(firstCarID); // �Ž�ArrivalCars����
            Main.NowInRoadCar.remove(Main.MapCar.get(firstCarID));// ������·�ϵĳ���������ɾ��

            laneInvlovesCar.carsInLane.removeFirst(); // $$$$$$�����ҵ�Car��lane��ȥ��$$$$$$

            //�ж�firstCar�ǲ�������·�ϵ�Ψһһ��
            String nextCarID = getFirstCarInRoad(carInRoad, firstCar.getCurFromCrossID());
            if (nextCarID == null) {
                setCarInRoad(null, firstCar, carInRoad, 3, false, true, true, false, false);
            }
        }
        // **********���2���ܹ�·�ڣ������·��
        else if (!firstCar.getCurToCrossID().equals(firstCar.getToCrossID())) {

//			laneInvlovesCar.carsInLane.removeFirst(); // $$$$���뿪����·��Car��lane��ȥ��$$$$$
//			String nextCarID = getFirstCarInRoad(carInRoad,firstCar.getCurFromCrossID());
//			if(nextCarID==null) {
//				setCarInRoad(null, firstCar, carInRoad, 3, false, true, true, false, false);
//			}
            // ��������ܵ����·��
            String nextRoadID = firstCar.getNextRoadID();
            Road nextRoad = Main.MapRoad.get(nextRoadID);
            int firstCarSpeed = Math.min(firstCar.getMaxVelocity(), nextRoad.getMaxRoadVelocity()); // firstCar����ʻ�ٶ�

            LinkedList<Lane> carInNextLanes = new LinkedList<Lane>(); // firstCarȥ����·������Щlane
            if (firstCar.getCurToCrossID().equals(nextRoad.getFromCrossID())) {// �жϷ���
                carInNextLanes = nextRoad.getForwardLane();
            } else {
                carInNextLanes = nextRoad.getBackwardLane(); // firstCar��Ҫǰ����·������Щlane
            }
            int carInNextLanesNum = carInNextLanes.size();
            ArrayList<Integer> lanesLeftLength = RoadInfo.getLeftLanesLength(nextRoad, firstCar.getCurToCrossID());

            int laneiLeftLength = 0;
            int templaneID = -1;
            for (int i = 0; i < carInNextLanesNum; i++) {
                laneiLeftLength = lanesLeftLength.get(i);
                if (laneiLeftLength > 0) {// ��0��ʼ�����ˣ��ҵ�Ҫ�����lane��
                    //firstCar.setLaneID(i);
                    templaneID = i;
                    break;
                }
            }

            //ȥ����
            if (templaneID == -1) {
                firstCar.setState(5);
                firstCar.setPriority(3);
                firstCar.setNextRoadID("-1");
                setCarInRoad(null, firstCar, carInRoad, 5, false, false, false, false, false);
            } else {
                firstCar.setLaneID(templaneID);
                // $$$$$����laneID$$$$$
                // case1.1����lane��car��ǰ���г������Ǹ��¹��ĳ�
                // case1.2����lane��car��ǰ���г�������û�и��¹��ĳ�
                // case2.1����lane��car��ǰ��û�г����Ҹ�road��Ҳû�г�
                // case2.2����lane��car��ǰ��û�г�������road���г�

                // case1�� ��lane��car��ǰ���г�
                // case1.1����lane��car��ǰ���г������Ǹ��¹��ĳ�
                // case1.2����lane��car��ǰ���г�������û�и��¹��ĳ�
                if (!carInNextLanes.get(firstCar.getLaneID()).carsInLane.isEmpty()) {
                    Car preCar = carInNextLanes.get(firstCar.getLaneID()).carsInLane.getLast();
                    if (preCar.isHasArrangedOrNot() && preCar.getSheng() == 0) {// case1.1����lane��car��ǰ���г������Ǹ��¹��ĳ�
                        // 1������״̬
                        if (preCar.getState() == 1) {
                            firstCar.setState(3);
                        } else if (preCar.getState() == 2) {
                            firstCar.setState(4);
                        } else {
                            firstCar.setState(preCar.getState());
                        }

                        // 2������λ��curPos
                        // firstCar��nextRoad���߶�Զ
                        int hasJumpDis = firstCar.getCurPos();
                        // �ж�firstCar�ܲ���׷��ǰ��
                        if (nextRoad.getRoadLength() - preCar.getCurPos() - 1 >= firstCarSpeed - hasJumpDis) {// ����׷��
                            firstCar.setCurPos(nextRoad.getRoadLength() - (firstCarSpeed - hasJumpDis));
                        } else {
                            firstCar.setCurPos(preCar.getCurPos() + 1);
                        }
                    } else {// case1.2:����lane��car��ǰ���г�������û�и��¹��ĳ�
                        // 1������״̬
                        if (preCar.getState() == 1) {
                            firstCar.setState(3);
                        } else if (preCar.getState() == 2) {
                            firstCar.setState(4);
                        } else {
                            firstCar.setState(preCar.getState());
                        }

                        // 2������λ��curPos
                        // firstCar��nextRoad���߶�Զ
                        int hasJumpDis = firstCar.getCurPos();
                        // �ж�firstCar�ܲ���׷��ǰ��
                        if (nextRoad.getRoadLength() - preCar.getCurPos() - 1 >= firstCarSpeed - hasJumpDis) {// ����׷��
                            firstCar.setCurPos(nextRoad.getRoadLength() - (firstCarSpeed - hasJumpDis));
                        } else {
                            firstCar.setCurPos(preCar.getCurPos() + 1);
                            firstCar.setSheng(
                                    -(preCar.getCurPos() + 1 - (nextRoad.getRoadLength() - (firstCarSpeed - hasJumpDis))));
                        }

                    }
                }
                // ��lane��car��ǰ��û�г���case2
                // case2.1����lane��car��ǰ��û�г����Ҹ�road��Ҳû�г�
                // case2.2����lane��car��ǰ��û�г�������road���г�

                else {
                    //LinkedList<String> carInNextRoadID = getCarInRoad(nextRoad, firstCar.getCurToCrossID());//4.12-���ǲ���ֻ��û����״̬�ĳ���������

                    //��ʵ����ֻ��Ҫ��ÿ��lane�ϵĵ�һ��������
                    //0412���£�����Ҫ��������·�����г���״̬����ֻ��û���¹��ĳ���
                    ArrayList<String> carInNextRoadID = new ArrayList<String>();
                    LinkedList<Car> carInNextLanel = new LinkedList<Car>();
                    for (Lane l : carInNextLanes) {
                        carInNextLanel = l.carsInLane;
                        if (carInNextLanel.size() != 0) {
                            carInNextRoadID.add(carInNextLanel.getFirst().getCarID());
                        }
                    } //����true������false������������nextRoad���ϵĳ�


                    //	 * @param road ��ǰ��·, @param crossID �����ĸ�·�ڵ����·;


                    if (carInNextRoadID.isEmpty()) {// case2.1:��road��Ҳû�г�   ԭ��д����carInNextRoadID.isEmpty()��ע�����ǵ�firstcar���ڻ���������·�ϣ���������
                        // 1������λ�� curPos
                        int hasJumpDis = firstCar.getCurPos();
                        firstCar.setCurPos(nextRoad.getRoadLength() - (firstCarSpeed - hasJumpDis));
                        // 2������״̬
                        // �жϸó�t3ʱ���Ƿ��ܹ�·��
                        if (firstCar.getCurPos() < firstCarSpeed) {// �ܹ�·����1
                            firstCar.setState(1);
                        } else {// ���ܹ�·����2
                            firstCar.setState(2);
                        }

                    } else { // case2.2:��road���г�
                        // 1������λ�� curPos
                        int hasJumpDis = firstCar.getCurPos();
                        firstCar.setCurPos(nextRoad.getRoadLength() - (firstCarSpeed - hasJumpDis));
                        // 2������״̬


                        // Ҫ�Ҹ�road����firstcar֮ǰ�ĳ�
                        //LinkedList<String> carInNextRoadID = getCarInRoad(nextRoad, firstCar.getCurToCrossID());
                        String preRoadCarID;
                        if (getFirstCarInRoad(nextRoad, firstCar.getCurToCrossID()) != null) {
                            preRoadCarID = getFirstCarInRoad(nextRoad, firstCar.getCurToCrossID()); //��false��
                        } else { //ֻ��true��
                            preRoadCarID = getFirstTrueCarInRoad(nextRoad, firstCar.getCurToCrossID()); //��������  road ��ǰ��·, @param crossID �����ĸ�·�ڵ����·;
                        }

                        Car preRoadCar = Main.MapCar.get(preRoadCarID); //����ȡ����������·�ϵĵ�һ��car��������trueҲ������false
                        //0412������Ҫȡ������·��������ͷ������ʱ��û�����ǵ�firstcar�ģ�����û�ӽ����أ���������firstcar���ڵ�lane��ľ�б��car�ˣ�
//						if(carInNextRoadID.size()>1) {
//						  for(int i=1;i<carInNextRoadID.size();i++) {
//						    //���ж��������ܲ��ܹ�·��
//							Car car1 = Main.MapCar.get(carInNextRoadID.get(i));
//							int car1Speed = Math.min(car1.getMaxVelocity(), nextRoad.getMaxRoadVelocity());
//							int preRoadCarSpeed = Math.min(preRoadCar.getMaxVelocity(), nextRoad.getMaxRoadVelocity());
//                            //�ܹ�·�ڣ���λ����preRoadCarǰ�棬��preRoadCar���ܹ�·��,��car1��ΪpreCar
//							if(car1.getCurPos()<car1Speed && car1.getCurPos()<preRoadCar.getCurPos()&&preRoadCar.getCurPos()>=preRoadCarSpeed) {
//								preRoadCar = car1;
//							}
//						  }
//						}
                        int preRoadCarSpeed = Math.min(preRoadCar.getMaxVelocity(), nextRoad.getMaxRoadVelocity());

                        //4.14����
                        //���preRoadCar��false��������һ����ͷ��
                        if (preRoadCar.isHasArrangedOrNot() == false) {
                            if (preRoadCar.getState() == 1) {
                                firstCar.setState(3);
                            } else if (preRoadCar.getState() == 2) {
                                firstCar.setState(4);
                            } else { //3-3,4-4,5-5
                                firstCar.setState(preRoadCar.getState());
                            }
                        } else { //���preRoadCar��true������Ҫ��һ���ж��ܷ��·�ڣ�curPos��laneID
                            //�õ�preCar֮�����ȿ�firstCar�ܲ��ܹ�·��
                            if (firstCar.getCurPos() < firstCarSpeed) { //firstCar�ܹ�·��
                                if (preRoadCar.getCurPos() < preRoadCarSpeed) { //preRoadCar�ܹ�·��
                                    //�Ƚ����ߵ�curPos
                                    if (firstCar.getCurPos() < firstCar.getCurPos()) {//firstCar��Ϊ����·��ͷ��
                                        firstCar.setState(1);
                                    } else if (firstCar.getCurPos() == firstCar.getCurPos()) { //λ����ͬ�Ƚ�laneID
                                        if (firstCar.getLaneID() < firstCar.getLaneID()) { //firstCar��Ϊͷ��
                                            firstCar.setState(1);
                                        } else { //preRoadCar������·��ͷ��
                                            firstCar.setState(3);
                                        }
                                    } else {//firstCar��curPos��preRoadCar֮��preRoadCar������·��ͷ��
                                        firstCar.setState(3);
                                    }
                                } else {//preRoadCar���ܹ�·��
                                    //firstCar��Ϊ����·��ͷ��
                                    firstCar.setState(1);
                                }
                            } else {//firstCar���ܹ�·��
                                if (preRoadCar.getCurPos() < preRoadCarSpeed) {//preRoadCar�ܹ�·��
                                    //preRoadCar������·��ͷ��
                                    firstCar.setState(3);
                                } else {//preRoadCar���ܹ�·��
                                    if (firstCar.getCurPos() < preRoadCar.getCurPos()) { //firstCar��ǰ��
                                        //firstCar��Ϊͷ��
                                        firstCar.setState(2);
                                    } else if (firstCar.getCurPos() == preRoadCar.getCurPos()) { //λ����ͬ���Ƚ�laneID
                                        if (firstCar.getLaneID() < firstCar.getLaneID()) { //firstCar��Ϊͷ��
                                            firstCar.setState(2);
                                        } else { //preRoadCar������·��ͷ��
                                            firstCar.setState(4);
                                        }
                                    } else {//firstCar��curPospre��RoadCar֮��preRoadCar������·��ͷ��
                                        firstCar.setState(4);
                                    }
                                }
                            }
                        }


//						if (preRoadCar.getCurPos() < firstCar.getCurPos()) { // firstcar��roadԭ�����ĺ���
//							if (preRoadCar.getState() == 1) {
//								firstCar.setState(3);
//							} else if (preRoadCar.getState() == 2) {
//								firstCar.setState(4);
//							} else {
//								firstCar.setState(preRoadCar.getState());
//							}
//						} else if (preRoadCar.getCurPos() == firstCar.getCurPos()
//								&& firstCar.getLaneID() > preRoadCar.getLaneID()) {// firstcar��roadԭ����һ����ͬһˮƽ�ߣ���Ҫ�Ƚ�laneID
//
//							if (preRoadCar.getState() == 1) {
//								firstCar.setState(3);
//							} else if (preRoadCar.getState() == 2) {
//								firstCar.setState(4);
//							} else {
//								firstCar.setState(preRoadCar.getState());
//							}
//						} else {// firstCar������Road����ǰ��
//								// �жϸó�t3ʱ���Ƿ��ܹ�·��
//							if (firstCar.getCurPos() < firstCarSpeed) {// �ܹ�·����1
//								firstCar.setState(1);
//							} else {// ���ܹ�·����2
//								firstCar.setState(2);
//							}
//						}

                    }

                }

                laneInvlovesCar.carsInLane.removeFirst(); // $$$$���뿪����·��Car��lane��ȥ��$$$$$
                String nextCarID = getFirstCarInRoad(carInRoad, firstCar.getCurFromCrossID());
                if (nextCarID == null) {
                    setCarInRoad(null, firstCar, carInRoad, 3, false, true, true, false, false);
                }

                // ------ͳһ������Ϣ
                carInNextLanes.get(firstCar.getLaneID()).carsInLane.add(firstCar); // $$$$$��car�ӵ����lane��$$$$$

                firstCar.setHasArrangedOrNot(true); // ���±�־λ
                firstCar.setRoadID(nextRoadID);
                firstCar.setCurFromCrossID(firstCar.getCurToCrossID());
                firstCar.setCurToCrossID(getCross(nextRoad, firstCar.getCurFromCrossID()).getCrossID());

                firstCar.setNextRoadID("-1");

            }
        }


    }


    /**
     * ��firstCar������lane�ϵĳ���t2ʱ�̸��µ�t3ʱ��
     *
     * @param car2s
     */

    public static void UpdateLaneForCarsAtState2(ArrayList<Car> car2s) {

        for (Car c : car2s) {
            UpdateRoadForCarsAtState2(c.getCarID());

        }

    }

    /**
     * ����״̬2�ĳ����ڵ�·
     * @param firstCarID
     * @version 2019-04-15
     */
    protected static void UpdateRoadForCarsAtState2(String firstCarID) {
        Car firstCar = Main.MapCar.get(firstCarID);

        if (!firstCar.getNextRoadID().equals("-1")) {
            UpdateRoadForCarsAtState2Super(firstCarID); // ��ʵ�ҵ�·�ˣ�ֻ�ǵ�·���ٹ���ȥ
        } else {
            UpdateRoadForCarsAtState2Nomal(firstCarID); // ������û�ܱ�ɵȴ���
        }
    }

    /**
     * firstCar State2 nextRoadID!=-1 ��ȥ���˵�1������ ��firstCar������Road�ϵĳ���t2ʱ�̸��µ�t3ʱ��
     * ����State��curPos��sheng��hasArrangedOrNot �ҳ�road������lane�ϵġ�firstCar��,����ÿһ��lane
     * @param firstCarID
     * @version 2019-4-15
     */
    protected static void UpdateRoadForCarsAtState2Super(String firstCarID) {
        Car firstCar = Main.MapCar.get(firstCarID);
        Road road = Main.MapRoad.get(firstCar.getRoadID());
        LinkedList<Lane> carInLane = new LinkedList<Lane>();
        if (firstCar.getCurFromCrossID().equals(road.getFromCrossID())) {
            carInLane = road.getForwardLane();
        } else {
            carInLane = road.getBackwardLane(); // car��ʻ��·������Щlane
        }

        // **********����λ��
        for (int i = 0; i < road.getLanesNum(); i++) {
            if (carInLane.get(i).carsInLane.size() > 0) {
                // ������lane���г����������lane�ϵĳ���λ��
                UpdateLanePosForCarsAtState2(carInLane.get(i).carsInLane.getFirst().getCarID(), carInLane);// �ҳ�ÿ��lane�ϵĵ�һ����
            } else {// ���Lane��û�г�������
                continue;
            }
        }

        // ����λ�ú�Ѱ��Ŀǰ������road�ĵ�һ����

        String newFirstCarID;
        // �ȿ�false�ĵ�һ����
        if (getFirstCarInRoad(road, firstCar.getCurFromCrossID()) != null) {
            newFirstCarID = getFirstCarInRoad(road, firstCar.getCurFromCrossID());
        } else {
            //���û��false�����ǾͶ���true���ˣ�
            newFirstCarID = getFirstTrueCarInRoad(road, firstCar.getCurFromCrossID());
        }
        Car newFirstCar = Main.MapCar.get(newFirstCarID);

        newFirstCar.setState(1); // firstCar����1��������ģ�curpos�����0�ˣ�״̬һ����1

        newFirstCar.setHasArrangedOrNot(true);// ����λ��֮��������hasArrangedOrNot

        // *********����State
        int followingState = 3;

        for (int i = 0; i < road.getLanesNum(); i++) {
            if (carInLane.get(i).carsInLane.size() > 0) {
                // ������lane���г����������lane�ϵĳ�״̬
                UpdateLaneStateForCarsAtState2(carInLane.get(i).carsInLane.getFirst().getCarID(), newFirstCarID, followingState, carInLane);// �ҳ�ÿ��lane�ϵĵ�һ����
            } else {// ���Lane��û�г�������
                continue;
            }
        }

    }


    /**
     * firstCar State2 nextRoadID==-1 ��ͨ��� ��firstCar������Road�ϵĳ���t2ʱ�̸��µ�t3ʱ��
     * ����State��curPos��sheng��hasArrangedOrNot �ҳ�road������lane�ϵġ�firstCar��,����ÿһ��lane
     * @param firstCarID
     * @version 2019-4-15
     */

    protected static void UpdateRoadForCarsAtState2Nomal(String firstCarID) {
        // Note:��firstCar������Road�ϵĳ���t2ʱ�̸��µ�t3ʱ��
        // �ҳ�road������lane�ϵġ�firstCar��,����ÿһ��lane
        // firstCar��t3ʱ��һ���ǲ��ܹ�·�ڵģ�
        // ��t4ʱ�̵�״̬�ǲ�һ���ģ�����ܹ���·����Ϊ1������ĳ�����3�����ܹ�·�ڵĻ�����Ϊ2�����涼��4
        Car firstCar = Main.MapCar.get(firstCarID);
        Road road = Main.MapRoad.get(firstCar.getRoadID());
        LinkedList<Lane> carInLane = new LinkedList<Lane>();
        if (firstCar.getCurFromCrossID().equals(road.getFromCrossID())) {
            carInLane = road.getForwardLane();
        } else {
            carInLane = road.getBackwardLane(); // car��ʻ��·������Щlane
        }
        // **********����λ��
        for (int i = 0; i < road.getLanesNum(); i++) {
            if (carInLane.get(i).carsInLane.size() > 0) {
                // ������lane���г����������lane�ϵĳ���λ��
                UpdateLanePosForCarsAtState2(carInLane.get(i).carsInLane.getFirst().getCarID(), carInLane);// �ҳ�ÿ��lane�ϵĵ�һ����
            } else {// ���Lane��û�г�������
                continue;
            }
        }


        // ����λ�ú�Ѱ��Ŀǰ������road�ĵ�һ����
        String newFirstCarID;
        // �ȿ�false�ĵ�һ����
        if (getFirstCarInRoad(road, firstCar.getCurFromCrossID()) != null) {
            newFirstCarID = getFirstCarInRoad(road, firstCar.getCurFromCrossID());
        } else {
            //���û��false�����ǾͶ���true���ˣ�
            newFirstCarID = getFirstTrueCarInRoad(road, firstCar.getCurFromCrossID());
        }
        Car newFirstCar = Main.MapCar.get(newFirstCarID);

        int newmaxSpeed = Math.min(newFirstCar.getMaxVelocity(), road.getMaxRoadVelocity()); // firstCar��ʱ����ʻ�ٶ�

        if (newFirstCar.getCurPos() - newmaxSpeed < 0) { // ����ܹ���·��
            newFirstCar.setState(1);
        } else { // ������ܹ�·��
            newFirstCar.setState(2);
        }
        newFirstCar.setHasArrangedOrNot(true);

        // *********����State
        int followingState = 0;
        if (newFirstCar.getState() == 1) {
            followingState = 3;
        } else if (newFirstCar.getState() == 2) {
            followingState = 4;
        } else {
            System.out.print("UpdateRoadForCarsAtState2---��һ������״̬����");
        }

        for (int i = 0; i < road.getLanesNum(); i++) {
            if (carInLane.get(i).carsInLane.size() > 0) {
                // ������lane���г����������lane�ϵĳ�״̬
                UpdateLaneStateForCarsAtState2(carInLane.get(i).carsInLane.getFirst().getCarID(), newFirstCarID, followingState, carInLane);// �ҳ�ÿ��lane�ϵĵ�һ����
            } else {// ���Lane��û�г�������
                continue;
            }
        }

    }

    /**
     * ��firstCar������lane�ϵĳ���λ�ô�t2ʱ�̸��µ�t3ʱ�� ����curPos��sheng
     * @param firstCarID
     * @version 2019-04-15
     */

    protected static void UpdateLanePosForCarsAtState2(String firstCarID, LinkedList<Lane> carInLane) {
        // ����curPos��sheng
        //
        // Note:��firstCar������lane�ϵĳ���λ�ô�t2ʱ�̸��µ�t3ʱ��
        // firstCar��t3ʱ��һ���ǲ��ܹ�·�ڵģ�
        // ��t4ʱ�̵�״̬�ǲ�һ���ģ�����ܹ���·����Ϊ1������ĳ�����3�����ܹ�·�ڵĻ�����Ϊ2�����涼��4

        // *************�ҳ�firstCar���ڵ�lane
        Car firstCar = Main.MapCar.get(firstCarID);
        Road carInRoad = Main.MapRoad.get(firstCar.getRoadID()); // car��ʻ��·

        int carInLaneID = firstCar.getLaneID();
        Lane laneInvlovesCar = carInLane.get(carInLaneID);// fisrtCar���ڵ�lane
        int maxSpeed = Math.min(firstCar.getMaxVelocity(), carInRoad.getMaxRoadVelocity()); // firstCar��ʱ����ʻ�ٶ�

        // ***********����firstCar��λ��
        // firstCar��nextRoadID�Ƿ�Ϊ-1
        if (firstCar.getNextRoadID().equals("-1")) {
            firstCar.setCurPos(firstCar.getCurPos() - maxSpeed);// ����t3ʱ��
            firstCar.setSheng(0);
            firstCar.setHasArrangedOrNot(true);
        } else {
            firstCar.setCurPos(0); // �������ٹ���ȥnextRoad�ĳ���curPos����Ϊ0
            firstCar.setSheng(0);
            firstCar.setHasArrangedOrNot(true);
        }

        // **********����lane���������������ֹ��һ���Ļ���
        LinkedList<Car> carsInLane = laneInvlovesCar.carsInLane;
        int cariSpeed = 0;
        int carInLaneNum = carsInLane.size();
        if (carInLaneNum > 1) {
            for (int i = 1; i < carInLaneNum; i++) {
                cariSpeed = Math.min(carsInLane.get(i).getMaxVelocity(), carInRoad.getMaxRoadVelocity());
                // ------case1:û�а��Ź��ĳ�
                if (carsInLane.get(i).isHasArrangedOrNot() == false) {
                    if (carsInLane.get(i).getCurPos() - cariSpeed <= carsInLane.get(i - 1).getCurPos()) {// ��׷��ǰ����ǰ���Ѿ����µ�t3ʱ���ˣ�
                        carsInLane.get(i).setCurPos(carsInLane.get(i - 1).getCurPos() + 1);
                    } else {// �������׷��ǰ�������ܶ�Զ�ܶ�Զ
                        carsInLane.get(i).setCurPos(carsInLane.get(i).getCurPos() - cariSpeed);
                    }
                }
                // -----case2:���Ź��ĳ�������һ��·�ڹ�����)
                else if (carsInLane.get(i).isHasArrangedOrNot() == true && carsInLane.get(i).getSheng() != 0) {
                    if (carsInLane.get(i).getCurPos() - carsInLane.get(i - 1).getCurPos() > Math
                            .abs(carsInLane.get(i).getSheng())) { // ׷����ǰ��
                        carsInLane.get(i).setCurPos(carsInLane.get(i).getCurPos() + carsInLane.get(i).getSheng()); // sheng�Ǹ�ֵ��
                    } else {// ��׷��
                        carsInLane.get(i).setCurPos(carsInLane.get(i - 1).getCurPos() + 1);
                    }
                }
                //				// -----case3:�������һ���Ѿ����µ��ˣ���������϶�Ҳ�Ѿ������ˣ������������ע����Σ�
                //				else if (carsInLane.get(i).isHasArrangedOrNot() == true && carsInLane.get(i).getSheng() == 0) {
                //					break;
                //				}
                // ͳһ��������
                carsInLane.get(i).setSheng(0);
                carsInLane.get(i).setHasArrangedOrNot(true);
            }
        }

    }

    /**
     * ��lanefirstCar������lane�ϵĳ���״̬��t2ʱ�̸��µ�t3ʱ�� ��Ҫ���µ���Ϣ�У�hasArrangedOrNot,State
     * @param laneFirstCarID
     * @param newFirstCarID
     * @param followingState
     * @version 2019.04.15
     */
    protected static void UpdateLaneStateForCarsAtState2(String laneFirstCarID, String newFirstCarID, int followingState, LinkedList<Lane> carInLane) {
        // Note:��firstCar������lane�ϵĳ���t2ʱ�̸��µ�t3ʱ��
        // firstCar��curPos�Ѿ����µ�t3ʱ�̣�����·�����г���curpos���Ѿ����µ�t3ʱ���ˣ�

        // *************�ҳ�firstCar���ڵ�lane
        Car firstCar = Main.MapCar.get(laneFirstCarID);
        //Road carInRoad = Main.MapRoad.get(firstCar.getRoadID()); // car��ʻ��·
        //		LinkedList<Lane> carInLane = new LinkedList<Lane>();
        //		if (firstCar.getCurFromCrossID().equals(carInRoad.getFromCrossID())) {
        //			carInLane = carInRoad.getForwardLane();
        //		} else {
        //			carInLane = carInRoad.getBackwardLane();   // car��ʻ��·������Щlane
        //		}

        int carInLaneID = firstCar.getLaneID();
        Lane laneInvlovesCar = carInLane.get(carInLaneID);   // fisrtCar���ڵ�lane

        // **********����lane���������������ֹ��һ���Ļ���
        LinkedList<Car> carsInLane = laneInvlovesCar.carsInLane;
        if (!laneFirstCarID.equals(newFirstCarID)) {// ����ҵ��Ĳ�������road�����ڡ���ǰ���ĳ���Ҳ�����Ѿ����ù�״̬�ĳ�
            carsInLane.get(0).setState(followingState);
            carsInLane.get(0).setHasArrangedOrNot(true);
        }
        // �����newfirstCar����Ҫ������state�ˡ�
        int carInLaneNum = carsInLane.size();
        if (carInLaneNum > 1) {
            for (int i = 1; i < carInLaneNum; i++) {
                // ͳһ��������
                carsInLane.get(i).setState(followingState);
                carsInLane.get(i).setHasArrangedOrNot(true);
            }
        }

    }


    /**
     * @param fromRoadID
     * @param toRoadID
     * @param crossID
     * @return �ҵ���·fromͨ��·��c��·to�����ת������ȼ�
     */
    protected static int setPriority(String fromRoadID, String toRoadID, String crossID) {
        Cross c = Main.MapCross.get(crossID);
        int ans = 0;
        String[] roadlist = c.getRoadIDList();
        int i = -1, j = -1;
        int size = roadlist.length;
        while (i != -1 && j != -1) {
            for (int k = 0; k < size; k++) {
                if (roadlist[k] == null) {

                    continue;
                }
                if (roadlist[k].equals(fromRoadID)) {

                    i = k;
                } else if (roadlist[k].equals(toRoadID)) {
                    j = k;

                } else {
                }

            }
        }
        if (Math.abs(i - j) == 2) {

            ans = 3;
        } else if (i - j == -1 || (i == 3 && j == 0)) {
            ans = 2;
        } else {
            ans = 1;
        }

        return ans;

    }


    /**
     * ���Ź���������·���ߵĳ�֮��Ҫ���Ŵӳ������ĳ��ˣ������Ժ�ͳһ�����ı�־λ���ó�true,ͬʱҪ�Ѵӳ������ĳ��ӵ���·�ϵĳ���ȥ
     */
    public static void setNowInRoadCarFromGarageWait() {
        Iterator<Car> carIt = Main.garageWait.iterator();
        while (carIt.hasNext()) {
            Car c = carIt.next();
            c.setHasArrangedOrNot(true);
            c.setPriority(3);
            Main.NowInRoadCar.add(c);


        }

    }


    /**
     * һ��ʱ��Ƭ��ĩβ����������·�����ߵĳ����Ƿ��Ź���Ҫ��true����false
     *
     * @param flag
     */
    public static void setNowInRoadCarState(Boolean flag) {
        Iterator<Car> it = Main.NowInRoadCar.iterator();
        while (it.hasNext()) {
            Car c = it.next();
            c.setHasArrangedOrNot(flag);
        }

    }


    /**
     * @param firstCarID
     * @return mapFirstCarInLanes(laneID, carID)
     * @version 2019-3-28
     */

  /*  protected static HashMap<Integer, String> findFirstCarInLanes(String firstCarID) {

        // mapFirstCarInLanes(laneID,carID)
        HashMap<Integer, String> mapFirstCarInLanes = new HashMap<Integer, String>();

        Car firstCar = Main.MapCar.get(firstCarID);
        Road road = Main.MapRoad.get(firstCar.getRoadID());
        String crossID = firstCar.getCurFromCrossID();
        LinkedList<String> carsInRoad = getCarInRoad(road, crossID);
        // �ж�·����û�г�
        if (carsInRoad.isEmpty() || carsInRoad == null || carsInRoad.size() < 1) {
            System.out.println("carsInRoadΪ�գ������ˣ����ܵ���findFirstCarInLanes");
            return null;
        }
        // ����·�ϵ�car��ȷ��ÿlane�ϵĵ�һ��car
        int cariLaneID = 0;
        Iterator<String> carIt = carsInRoad.iterator();
        while (carIt.hasNext()) {
            String s = carIt.next();
            cariLaneID = Main.MapCar.get(s).getLaneID();
            if (!mapFirstCarInLanes.containsKey(cariLaneID)) {
                // ������lane�ǵ�һ�α�������
                // �����carID�Ž�map
                mapFirstCarInLanes.put(cariLaneID, s);
            } else {
                continue;
            }
            if (mapFirstCarInLanes.size() == road.getLanesNum()) {
                // ÿ��lane���ҵ��˾�����ѭ��
                break;
            }
        }
        return mapFirstCarInLanes;
    }
*/

    /**
     * ��һ������������ѡ���ܹ�·�ڵĳ�
     *
     * @param road     ��ǰ�ĵ�·
     * @param carList  ��Ҫ�ж��Ƿ�ͨ��·�ڵĳ�����
     * @param laneList ���������ڵ�lane����
     * @param carIndex �������и��������ڸ���lane�ϵĵڼ���λ��
     * @return ��Ҫ��·�ڵĳ��ļ��ϣ���С�������carList��ͬ��ֻ�ǽ����ܹ�·�ڵĳ���λ�÷���null��
     * @version 2019.3.28
     */

    protected static Car[] ThroughCar(Road road, Car[] carList, LinkedList<Lane> laneList, int[] carIndex) {
        Car[] c = new Car[carList.length];
        int j = 0;
        for (; j < carList.length; j++) {
            // ��ȡ��һ����Ϊnull�ĳ�
            Car car = carList[j];
            if (car != null) {
                canThrough(road, car, laneList, carIndex);
                if (car.isCanThrough()) {
                    c[j] = carList[j];
                    carList[j] = null;
                }
            } else {

                c[j] = null;
            }
        }
        return c;
    }

    /**
     * �жϸó��ܲ���ͨ��·�ڣ����һ��lane��ǰ������ͨ��·�ڣ��ó�Ҳ���ܣ� ���û��ǰ������ǰ������ͨ��·�ڣ��������ʻ�ٶȺ�����λ���ж��Ƿ����ͨ��·��
     *
     * @param road     ��ǰ��·
     * @param car      ��ǰ����
     * @param laneList �����ڵ�lane����
     * @param carIndex �����ڸ���lane�ϵĵڼ���λ��
     * @version 2019.3.28
     */
    protected static void canThrough(Road road, Car car, LinkedList<Lane> laneList, int[] carIndex) {
        int laneID = car.getLaneID();
        int carIn = carIndex[laneID];
        LinkedList<Car> carsInLane = laneList.get(laneID).carsInLane;
        if (!carsInLane.isEmpty()) {
            if (carIn > 0 && !carsInLane.get(carIn - 1).isCanThrough()) {
                // ��ǰ����ǰ������ͨ��·��
                car.setCanThrough(false);
            } else {// ûǰ����ǰ����ͨ��·�ڣ�������ʻ�ٶȺ�����λ���ж��Ƿ����ͨ��·��
                if (car.getCurPos() < Math.min(car.getMaxVelocity(), road.getMaxRoadVelocity())) {
                    car.setCanThrough(true);
                } else {
                    car.setCanThrough(false);
                }
            }
        }
    }


    /**
     * �������CarID���գ����ȼ���carID�������������ȼ����Ӵ�С�������ȼ���ͬ������carID����С����
     * * ����ĳ�������ʱ�Ҳ���·����ʱ���øó���״̬Ϊ5���󳵵�״̬Ҳ������Ϊ5���Ҹó�����������
     * * ������ҵ�·�Ѿ�������Ҳ����ͨ�У�������Ϊ5true���ó��������󳵣���������ܹ�·���򲻶����������ܹ�·������ǰ����������Ϊ4true����ʵ������Ϊ5����4true����
     * * Ҫ��Ŀ�ĵصĳ����ȼ���Ϊ3���˳���������
     * * �����������nextRoadID����·��ֱ���ж�����Ϊ����������״̬
     * * �����ѡ��·�ϵ���ʻ�ٶȼ�ȥ�ڱ���·�Ͽ��о���С�ڵ���0�����ܹ�·�ڣ��ó���Ϊ2false���󳵱�Ϊ4false
     *
     * @param cars
     */
    public static void sortCarsOfState1(ArrayList<Car> cars) {
        ArrayList<Car> carsList = new ArrayList<>();
        if (cars != null && cars.size() != 0) {
            for (int i = 0; i < cars.size(); i++) {
                Car car = cars.get(i);
                Road r = Main.MapRoad.get((car.getRoadID()));
                if (car.getCurToCrossID().equals(car.getToCrossID())) {
                    //Ҫ���ҵĳ�
                    car.setPriority(3);
                    //��������
                    carsList.add(car);
                } else if (!car.getNextRoadID().equals("-1")) {
                    //��·�ڵ�����Ҫ������·
                    Road toRoad = Main.MapRoad.get(car.getNextRoadID());
                    //��·��Ϊû������û�ռ䣬���Ϊ5false����Ҳ��5false
                    if (hasLeftLength(toRoad, car.getCurToCrossID()) == -1) {
                        setCarInRoad(toRoad, car, r, 5, false, false, false, false, false);
                    }
                    //��·��Ϊ������û�ռ䣬���Ϊ5true������ǰ������4true,��ʵ������Ϊ5����4true����
                    else if (hasLeftLength(toRoad, car.getCurToCrossID()) == -2) {
                        setCarInRoad(toRoad, car, r, 5, true, true, true, false, false);
                    } else if (Math.min(car.getMaxVelocity(), toRoad.getMaxRoadVelocity()) - car.getCurPos() <= 0) {
                        setCarInRoad(toRoad, car, r, 4, false, false, false, false, false);
                        car.setState(2);
                    } else if (Math.min(car.getMaxVelocity(), toRoad.getMaxRoadVelocity()) - car.getCurPos() > 0) {
                        carsList.add(car);
                    }
                } else {//��Ҫ������·����ʻ����Ҫ��·�ĳ�
                    Road toRoad = findNextCross(car, Main.maxRoadLength);
                    if (toRoad == null) {
                        //�Ҳ���·��ó�״̬��Ϊ5��δ���ŵĺ�״̬����Ϊ5
                        System.out.println("������������");
                    }
                    //�ҵ���·��Ϊû������û�ռ䣬���Ϊ5false����Ҳ��5false
                    else if (hasLeftLength(toRoad, car.getCurToCrossID()) == -1) {
                        setCarInRoad(toRoad, car, r, 5, false, false, false, false, false);
                    }
                    //�ҵ���·��Ϊ������û�ռ䣬���Ϊ5true������ǰ������4true��,��ʵ������Ϊ5����4true����
                    else if (hasLeftLength(toRoad, car.getCurToCrossID()) == -2) {
                        setCarInRoad(toRoad, car, r, 5, true, true, true, false, false);
                        car.setPriority(setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
                        car.setNextRoadID(toRoad.getRoadID());
                    } else if (Math.min(car.getMaxVelocity(), toRoad.getMaxRoadVelocity()) - car.getCurPos() <= 0) {
                        setCarInRoad(toRoad, car, r, 4, false, false, false, false, false);
                        car.setState(2);
                        car.setNextRoadID(toRoad.getRoadID());
                        car.setPriority(setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
                    } else {
                        //�ҵ�·�ĳ�����������
                        car.setPriority(setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
                        car.setNextRoadID(toRoad.getRoadID());
                        carsList.add(car);
                    }
                }
            }
            Collections.sort(carsList, new Comparator<Car>() {
                // ���������ȼ���������
                @Override
                public int compare(Car o1, Car o2) {
                    return o2.getPriority() - o1.getPriority();
                }
            });
            for (int i = 0; i < carsList.size(); i++) {
                //���ȼ���ͬ�ĳ�����ID��������
                int j = i + 1;
                while (j < carsList.size() && carsList.get(j - 1).getPriority() == carsList.get(j).getPriority()) {
                    j++;
                }
                Collections.sort(carsList.subList(i, j), new Comparator<Car>() {
                    // ���������ȼ���������
                    @Override
                    public int compare(Car o1, Car o2) {
                        return o1.getCarID().compareTo(o2.getCarID());
                    }
                });
                i = j - 1;
            }
            cars.clear();
            for (int i = 0; i < carsList.size(); i++) {
                cars.add(carsList.get(i));
            }
        }
    }

    public static void setCarInRoad(Road toRoad, Car car, Road r, int state, boolean setHasArrangedOrNot, boolean setSheng, boolean setCurPos, boolean setNextRoadID, boolean setPriority) {
        if (car.getCarID().equals("10789") && r.getRoadID().equals("5007"))
            System.out.println();
        LinkedList<Lane> lanes;
        LinkedList<Car> carsInLane;
        Car shengCar;
        if (car.getCurFromCrossID().equals(r.getFromCrossID())) {

            lanes = r.getForwardLane();
        } else {
            lanes = r.getBackwardLane();
        }
        for (int j = 0; j < lanes.size(); j++) {
            carsInLane = lanes.get(j).carsInLane;
            //��lane���ȡ��
            for (int y = 0; y < carsInLane.size(); y++) {
                shengCar = carsInLane.get(y);
                //����curPos
                if (setCurPos) {
                    //��sheng
                    if (shengCar.getSheng() != 0) {
                        if (y == 0) {
                            shengCar.setCurPos(shengCar.getCurPos() + shengCar.getSheng());
                        } else {
                            int curPos = shengCar.getCurPos()
                                    - Math.min(shengCar.getCurPos() - carsInLane.get(y - 1).getCurPos() - 1,
                                    Math.abs(shengCar.getSheng()));
                            shengCar.setCurPos(curPos);
                        }
                    } else if (shengCar.equals(car)) {
                        //
                        Main.numOf5 += 2;
                    } else if (!shengCar.isHasArrangedOrNot()) {
                        //û��sheng����û�����Ź�
                        int cha = shengCar.getCurPos() - Math.min(shengCar.getMaxVelocity(), r.getMaxRoadVelocity());
                        if (y == 0) {
                            if (cha < 0) {
                                ;
                            } else {
                                shengCar.setCurPos(cha);
                            }
                        } else {
                            if (cha < 0) {
                                ;
                            } else {
                                shengCar.setCurPos(shengCar.getCurPos()
                                        - Math.min(Math.min(shengCar.getMaxVelocity(), r.getMaxRoadVelocity()),
                                        shengCar.getCurPos() - carsInLane.get(y - 1).getCurPos() - 1));
                            }
                        }
                    }
                }
                //����state
                if (state != -2) {
                    shengCar.setState(state);
                    if (state == 4) {
                        Main.numOf2++;
                    }

                    if (state == 5 && setHasArrangedOrNot) {
                        Main.numOf5 += 2;
                    }

                    if (state == 5 && !setHasArrangedOrNot) {
                        Main.numOf5++;
                    }

                }
                if (setHasArrangedOrNot) {

                    shengCar.setHasArrangedOrNot(true);
                }
                if (setSheng) {

                    shengCar.setSheng(0);
                }
                if (setNextRoadID) {

                    shengCar.setNextRoadID(toRoad.getRoadID());
                }
                if (setPriority) {

                    shengCar.setPriority(setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
                }
            }
        }
    }

    /**
     * �����������·�������ˣ����ͱ��1false�ˣ����泵���3false��
     * ������û�ռ䣬���ͱ��true5,����ĳ�����״̬���4true,��ʵ������Ϊ5����4true�������泵����������ܹ�·���򲻶����������ܹ�·������ǰ����;
     * ��Ȼ������flase5
     * @param firstCarID:״̬Ϊ5false�ĳ�������carInRoad��ͷͷ
     * @version 2019.3.30
     */
    protected static void updateLaneForCarsAtState5(String firstCarID) {
        Car firstCar = Main.MapCar.get(firstCarID);
        Road curRoad = Main.MapRoad.get(firstCar.getRoadID());
        Road road;
        /**
         * �����ж��ǲ��ǵ��ҳ��������ֱ�ӱ��1false, ���ǾͰ��������߼�
         */
        if (firstCar.getCurToCrossID().equals(firstCar.getToCrossID())) {
            setCarInRoad(null, firstCar, curRoad, 3, false, false, false, false, false);
            firstCar.setState(1);
            return;
        }
        road = findNextCross(firstCar, Main.maxRoadLength);
        if (hasLeftLength(road, firstCar.getCurToCrossID()) > 0) {
            // �ɹ�ѡ���·���пռ���ߣ��ó���Ϊ1���󳵱�Ϊ3
            setCarInRoad(road, firstCar, curRoad, 3, false, false, false, false, false);
            firstCar.setState(1);
            firstCar.setNextRoadID(road.getRoadID());
            firstCar.setPriority(setPriority(firstCar.getRoadID(), firstCar.getNextRoadID(), firstCar.getCurToCrossID()));
        }
        // ������Ҳû�ռ�,���Ϊ5true������ǰ������4true,��ʵ������Ϊ5����4true����
        else if (hasLeftLength(road, firstCar.getCurToCrossID()) == -2) {
            setCarInRoad(road, firstCar, curRoad, 5, true, true, true, false, false);
        } else if (hasLeftLength(road, firstCar.getCurToCrossID()) == -1) {
            Main.numOf5++;
        }
        // ��Ϊδ���¶�û�пռ䣬�ó�״̬����
    }

    /**
     * @param
     * @version 2019.3.29
     */
    protected static void innitial(Car c) {
        Car mapc = Main.MapCar.get(c.getCarID());
        mapc.setCurFromCrossID(mapc.getFromCrossID());
        mapc.setLaneID(-1);
        mapc.setRoadID("-1");
        mapc.setNextRoadID("-1");
        mapc.setCanThrough(false);
        mapc.setSheng(0);
        mapc.setState(-1);
        mapc.setPriority(0);
        mapc.setHasArrangedOrNot(false);
        mapc.setCurPos(0);
        mapc.setCurFromCrossID(mapc.getFromCrossID());
        mapc.setCurToCrossID(mapc.getFromCrossID());

    }

    /**
     * ����һ��·��,���Ƿ��·�ڣ�lane��˳���lane�ϳ�������Ŀ��·�ڵľ��룬�������ڵ�һ���ĳ�ID��������hasArrangeOrNotΪtrue�ĳ�
     * �����ȿ����Ƿ��·�ڣ�Ȼ���Ǿ��룬����ǳ���˳��
     * @param road����ǰ��·;crossID:�����ĸ�·�ڵ����·;
     * @return ��õ�ǰ��·�ͳ�����ʻ������ͬ������lane�ϵĵ�һ������getfirst���ȳ����ĳ���ͷͷ��
     * @version 2019.3.30
     */
    public static String getFirstCarInRoad(Road road, String crossID) {
        LinkedList<Lane> laneList;
        // �ҵ��ͳ�������һ�µĳ�������
        if (road.isDuplex()) {

            laneList = road.getFromCrossID().equals(crossID) ? road.getForwardLane() : road.getBackwardLane();
        } else {
            laneList = road.getForwardLane();
        }
        int laneNum = laneList.size();
        // ÿ��ȡ������ǰ��ļ��������ĳ����бȽ�,�±��Ӧ���ڳ�����û���ĳ���������공�ĳ�����null
        Car[] carList = new Car[laneNum];
        // ÿ������ȡ���ڼ��������±��Ӧ���ڳ�����û���ĳ��������Ѿ�������ĳ�������-1
        int[] carIndex = new int[laneNum];
        for (int i = 0; i < laneNum; i++) {
            // ��ʼ��,��ͷ������
            carIndex[i] = 0;
            Lane lane = laneList.get(i);
            if (!lane.carsInLane.isEmpty()) {
                // �ó����г�
                // �����Ѱ��ŵĳ���
                while (carIndex[i] < lane.carsInLane.size() && lane.carsInLane.get(carIndex[i]).isHasArrangedOrNot()) {
                    carIndex[i]++;
                }
                // �������������Ѱ��ŵĳ�
                if (carIndex[i] == lane.carsInLane.size()) {
                    carList[i] = null;
                    carIndex[i] = -1;
                }
                // ����δ���ŵĳ�
                else {
                    carList[i] = lane.carsInLane.get(carIndex[i]);
                }
            } else {// �ó����޳�
                carList[i] = null;
                carIndex[i] = -1;
            }
        }
        //ѡ����·�ڵĳ�
        Car[] throughCar = ThroughCar(road, carList, laneList, carIndex);
        Car out;
        out = minCarCurPos(throughCar);
        if (out != null) {

            return out.getCarID();
        } else {
            out = minCarCurPos(carList);
        }
        if (out != null) {

            return out.getCarID();
        } else {
            return null;
        }
    }


    public static void carsFromGarageInsertToRoad(int t) {
        LinkedList<Car> reArrangeCars = new LinkedList<>();
        RunUtil2.classifyCars(Main.garageFrozen, t);
        int garageWaitSize = Main.garageWait.size();
        // ���ų���ȴ��ĳ��������ܻع�
        // true[���ȥ�ˣ��޻ع�]
        boolean[] garageflag = new boolean[2];
        while (garageWaitSize > 0) {

            Car car = Main.garageWait.get(--garageWaitSize);
            if (car.getCarID().equals("11260"))
                System.out.println();
            Road road = RunUtil2.findNextCross(car, Main.maxRoadLength);
            if (road.getRoadID().equals("5007"))
                System.out.println();
            /**����ӵ������*/
            float normalizedRoadInsertLeftLength = getNormalizedRoadLeftLength(road, car.getCurFromCrossID());

            if (normalizedRoadInsertLeftLength >= 0.5) {
                //ÿ�ζ�Ҫ�����
                reArrangeCars.clear();
                if (road != null) {
                    garageflag = RunUtil2.checkIDPriority(car, road, reArrangeCars, Main.MapRoad, t);
                    if (!garageflag[0]) {
                        // �������Լ�Ҳ����ȥ���·��������wait����ŵ�frozon
                        Main.garageFrozen.add(car);
                        // �����ֿ���remove����
                        Main.garageWait.remove(car);
                    } else if (garageflag[0] && !garageflag[1]) {
                        // �������Լ����Խ�ȥ�ˣ��лع�
                        while (!reArrangeCars.isEmpty()) {
                            // �������˳����ĳ��ӵ�frozen��ȥ
                            Car c = reArrangeCars.remove(reArrangeCars.size() - 1);
                            Main.garageFrozen.add(c);
                            // �����ֿ���remove����
                            Main.garageWait.remove(c);
                        }
                    } else {
                        // ��������ȥ�ˣ�����û�ع�

                    }

                }

            } else {
                Main.garageFrozen.add(car);
                Main.garageWait.remove(car);

            }
        }


    }

    /**
     * ����һ��·��,���Ƿ��·�ڣ�lane��˳���lane�ϳ�������Ŀ��·�ڵľ��룬�������ڵ�һ���ĳ�ID����·�ϲ�������false��
     * �����ȿ����Ƿ��·�ڣ�Ȼ���Ǿ��룬����ǳ���˳��
     * @param road    ��ǰ��·
     * @param crossID �����ĸ�·�ڵ����·;
     * @return ��õ�ǰ��·�ͳ�����ʻ������ͬ������lane�ϵĵ�һ������getfirst���ȳ����ĳ���ͷͷ��
     * @version 2019.4.13
     */
    public static String getFirstTrueCarInRoad(Road road, String crossID) {
        LinkedList<Lane> laneList;
        // �ҵ��ͳ�������һ�µĳ�������
        if (road.isDuplex())
            laneList = road.getFromCrossID().equals(crossID) ? road.getForwardLane() : road.getBackwardLane();
        else
            laneList = road.getForwardLane();
        int laneNum = laneList.size();
        Car[] carList = new Car[laneNum];// ÿ��ȡ������ǰ��ļ��������ĳ����бȽ�,�±��Ӧ���ڳ�����û���ĳ���������공�ĳ�����null
        int[] carIndex = new int[laneNum];// ÿ������ȡ���ڼ��������±��Ӧ���ڳ�����û���ĳ��������Ѿ�������ĳ�������-1
        for (int i = 0; i < laneNum; i++) {// ��ʼ��,��ͷ������
            carIndex[i] = 0;
            Lane lane = laneList.get(i);
            if (!lane.carsInLane.isEmpty()) {// �ó����г�
                // �����Ѱ��ŵĳ���
                while (carIndex[i] < lane.carsInLane.size() && !lane.carsInLane.get(carIndex[i]).isHasArrangedOrNot()) {
                    carIndex[i]++;
                    System.out.println("��false����˭�ڵ���getFirstTrueCarInRoad");
                }
                // ������������δ���ŵĳ�����Ӧ��������
                if (carIndex[i] == lane.carsInLane.size()) {
                    carList[i] = null;
                    carIndex[i] = -1;
                }
                //
                else
                    carList[i] = lane.carsInLane.get(carIndex[i]);
            } else {// �ó����޳�
                carList[i] = null;
                carIndex[i] = -1;
            }
        }
        Car[] throughCar = ThroughCar(road, carList, laneList, carIndex);//ѡ����·�ڵĳ�
        Car out;
        out = minCarCurPos(throughCar);
        if (out != null)
            return out.getCarID();
        else {
            out = minCarCurPos(carList);
        }
        if (out != null)
            return out.getCarID();
        else return null;
    }

}


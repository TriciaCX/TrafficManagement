package com.trafficproject.service.impl;

import com.trafficproject.service.BaseService;
import com.trafficproject.service.ManagementService;
import com.trafficproject.service.model.CarModel;
import com.trafficproject.service.model.CrossModel;
import com.trafficproject.service.model.LaneModel;
import com.trafficproject.service.model.RoadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Component
public class ManagementServiceImpl extends BaseService implements ManagementService {

    private LinkedList<CarModel> garageFrozen = new LinkedList<>();
    private LinkedList<CarModel> garageWait = new LinkedList<>();
    private HashSet<String> ArrivedCar = new HashSet<>();
    private HashSet<CarModel> NowInRoadCar = new HashSet<>();


    @Autowired
    private CarServiceImpl carServiceImpl;

    @Autowired
    private RoadServiceImpl roadServiceImpl;

    @Autowired
    private CrossServiceImpl crossServiceImpl;

    private ArrayList<CarModel> listCar= (ArrayList<CarModel>) carServiceImpl.listCar();
    private ArrayList<CrossModel> listCross= (ArrayList<CrossModel>) crossServiceImpl.listCross();
    private ArrayList<RoadModel> listRoad= (ArrayList<RoadModel>) roadServiceImpl.listRoad();

    public ArrayList<CarModel> getListCar() {
        return listCar;
    }

    public ArrayList<CrossModel> getListCross() {
        return listCross;
    }

    public ArrayList<RoadModel> getListRoad() {
        return listRoad;
    }

    public LinkedList<CarModel> getGarageFrozen() {
        return garageFrozen;
    }

    public HashSet<CarModel> getNowInRoadCar() {
        return NowInRoadCar;
    }

    public HashSet<String> getArrivedCar() {
        return ArrivedCar;
    }

    public void setListCar() {
        this.listCar = (ArrayList<CarModel>) carServiceImpl.listCar();
    }

    public void setListCross() {
        this.listCross = (ArrayList<CrossModel>) crossServiceImpl.listCross();
    }

    public void setListRoad() {
        this.listRoad = (ArrayList<RoadModel>) roadServiceImpl.listRoad();
    }


    /**
     * 对车遍历，是不是都是真实的位置了
     * *@param hasArrag=true,sheng=o
     */
    public boolean isAllReal() {
        if (NowInRoadCar.isEmpty()) {

            return true;
        }

        Iterator<CarModel> carIt = NowInRoadCar.iterator();
        boolean ans = true;
        while (carIt.hasNext()) {
            CarModel c = carIt.next();
            if (c.getSheng() != 0 || !c.isHasArrangedOrNot()) {

                ans = false;

                break;

            }

        }
        return ans;
    }

    /**
     * 对车遍历，是不是都是已经到达终点了
     */
    public boolean isAllArrived() {
        Iterator<String> carIt = ArrivedCar.iterator();
        int sum = 0;

        while (carIt.hasNext()) {
            carIt.next();
            sum++;

        }
        return sum == listCar.size();
    }




    /**
     * 安排过本来就在路上走的车之后，要安排从车库来的车了，安排以后，统一把它的标志位设置成true,同时要把从车库来的车加到在路上的车里去
     */
    public void setNowInRoadCarFromGarageWait() {
        Iterator<CarModel> carIt = garageWait.iterator();
        while (carIt.hasNext()) {
            CarModel c = carIt.next();
            c.setHasArrangedOrNot(true);
            c.setPriority(3);
            NowInRoadCar.add(c);
        }
    }

    /**
     * 一个时间片的末尾，将所有在路上行走的车的是否安排过都要置true或者false
     */
    public void setNowInRoadCarState(Boolean flag) {
        Iterator<CarModel> it = NowInRoadCar.iterator();
        while (it.hasNext()) {
            CarModel c = it.next();
            c.setHasArrangedOrNot(flag);
        }

    }


    private void innitial(String cID) {
        CarModel mapc=carServiceImpl.getCarModelById(cID);
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

    public void carsFromGarageInsertToRoad(int t) {
        LinkedList<CarModel> reArrangeCars = new LinkedList<>();
        classifyCars(garageFrozen, t);
        int garageWaitSize = garageWait.size();
        // 安排车库等待的车辆，可能回滚
        // true[插进去了，无回滚]
        boolean[] garageflag = new boolean[2];
        while (garageWaitSize > 0) {

            CarModel car = garageWait.get(--garageWaitSize);
            if (car.getCarID().equals("11260"))
                System.out.println();
            RoadModel road = findNextCross(car);
            if (road.getRoadID().equals("5007"))
                System.out.println();
            /**用于拥塞控制*/
            float normalizedRoadInsertLeftLength = roadServiceImpl.getNormalizedRoadLeftLength(road.getRoadID(), car.getCurFromCrossID());

            if (normalizedRoadInsertLeftLength >= 0.5) {
                //每次都要清除的
                reArrangeCars.clear();
                if (road != null) {
                    garageflag = checkIDPriority(car, road, reArrangeCars, t);
                    if (!garageflag[0]) {
                        // 这辆车自己也进不去这个路，把他从wait里面放到frozon
                        garageFrozen.add(car);
                        // 这里又可以remove掉了
                        garageWait.remove(car);
                    } else if (garageflag[0] && !garageflag[1]) {
                        // 这辆车自己可以进去了！有回滚
                        while (!reArrangeCars.isEmpty()) {
                            // 把所有退出来的车加到frozen里去
                            CarModel c = reArrangeCars.remove(reArrangeCars.size() - 1);
                            garageFrozen.add(c);
                            // 这里又可以remove掉了
                            garageWait.remove(c);
                        }
                    } else {
                        // 这辆车进去了，而且没回滚

                    }

                }

            } else {
                garageFrozen.add(car);
                garageWait.remove(car);

            }
        }


    }

    /**
     * 更新从车库出发的车的信息，判断车是否能够插入到规划的道路中，如果能，查看ID优先级是否冲突，有冲突则看
     * 是否有回退的车（存入reArrangeCars中），如果不能，直接加到存入reArrangeCars中
     * @param car：当前车；road：要走的道路；virtualCarsHashMap：存好的车的原始状态；reArrangeCars：回退车辆集合，就是可能会有ID小但是速度快的车被先从车库里面取出来，在同一时刻，起始路口相同，要选择的道路也是一条，这时候就要回退了；MapRoad：用来返回对象的
     * @return 这里返回两个元素，false:第一个插不进去，第二个：有没有回滚？没有
     */
    private boolean[] checkIDPriority(CarModel car, RoadModel road, LinkedList<CarModel> reArrangeCars, int t) {
        /**
         * [可以插进去,没有回滚]
         */
        boolean flag1 = true;
        boolean flag2 = true;

        /**
         * 更新一些信息
         */

        car.setCurFromCrossID(car.getFromCrossID());
        car.setPriority(0);

        /**
         * 首先设定这辆车的getCurToCross()是从自己的始发路口出发的要开上这个道路
         */

        if (road.getFromCrossID().equals(car.getCurFromCrossID())) {
            car.setCurToCrossID(road.getToCrossID());

        } else {
            car.setCurToCrossID(road.getFromCrossID());
        }

        /**
         * 得到了这个车要插入的道路的lane们
         */

        LinkedList<LaneModel> myLanes;// 首先要判断方向
        if (!car.getCurToCrossID().equals(road.getFromCrossID())) {
            myLanes = road.getForwardLane();
        } else {
            myLanes = road.getBackwardLane();
        }
        int size = myLanes.size();
        int i = 0;
        for (i = size; i > 0; i--) {
            /**
             * 先拿出来的在lane4上的一系列车
             */
            LinkedList<CarModel> cars = myLanes.get(i - 1).carsInLane;
            // 找到要插入的位置了//priority还是要设置为0
            if (cars.size() > 0 && cars.getLast().getPriority() == car.getPriority() && Integer.valueOf(cars.getLast().getCarID()) > Integer.valueOf(car.getCarID())) {
                // 这个车也是刚从车库v提出来的而且，这个车的ID比现在安排的车大
                while (cars.size() > 0 && cars.getLast().getPriority() == car.getPriority()
                        && Integer.valueOf(cars.getLast().getCarID()) > Integer.valueOf(car.getCarID())) {
                    // 那这个车要回退的
                    // 先把这些要退回的车辆的信息更新一下
                    // 提取这辆车
                    CarModel virtualCar = carServiceImpl.getCarModelById(cars.getLast().getCarID());
                    /**
                     * 如果直接删掉它可能会导致这条路上车辆的状态都是4
                     */
                    if (virtualCar.getState() == 2)
                        setCarInRoad(null, virtualCar, road, 3, false, false, false, false, false);
                    //更新mapCar里它原来的信息
                    innitial(virtualCar.getCarID());
                    // 删除真实网络的信息
                    cars.removeLast();
                    // 把这些车辆返回
                    reArrangeCars.addLast(virtualCar);
                    // 回滚了
                    flag2 = false;
                }
            }

        }


        flag1 = carIDInsertToRoad(car, road, t);
        if (!flag1)
        // 如果这个车插不进去的,也放到重新安排的车的集合里,外面会再安排一次,再不行就放到garageFrozon里
        // 因为是false,所以没有在实际网络添加过它，所以只要返回原来的就好了
        {
            CarModel carVir = carServiceImpl.getCarModelById(car.getCarID());
            innitial(carVir.getCarID());
            reArrangeCars.add(car);
        }

        // 返回被退回的车或者可能是本身这辆车,再插一次！
        while (!reArrangeCars.isEmpty()) {
            CarModel c = reArrangeCars.getLast();
            if (c != null) {
                if (reArrangeCarsIDInsertToRoad(c, road, t)) {
                    reArrangeCars.removeLast();
                } else {
                    // 插不进去了
                    break;
                }
            } else {
                //取出来是null就说明应该时没有了
                break;
            }
        }
        // 两个元素，false:第一个插不进去，第二个：没有回滚
        boolean[] flags = new boolean[2];
        flags[0] = flag1;
        flags[1] = flag2;
        return flags;

    }

    /**
     * 已经判断这辆从车库来的车想要到这个路，该怎么选择车道呢？还要更新相关信息哦，比如说准备回滚，要存一个影分身之类的，前面的道路上已经没有优先级比它差的车了
     * @param car：当前车；road：要走的道路；virtualCarsHashMap；MapRoad：用来返回对象的；
     * @return 这里返回一个元素，false:插不进去
     */
    private boolean carIDInsertToRoad(CarModel car, RoadModel road, int t) {
        // LanesCarsList[0]=3,表示lane1的剩余可进入长度为3，还能进3辆车
        boolean flag = InsertFreshCarToRoad(car, road, t);
        // false:插不进去
        return flag;
    }

    /**
     * 这辆从车库出来的车能不能到这个路？调用这个函数的时候，前面是不可能出现回滚的，因为已经滚过了！能的话就插进去！
     */
    private boolean InsertFreshCarToRoad(CarModel car, RoadModel road, int t) {
        ArrayList<Integer> LeftLanesLengthList = roadServiceImpl.getLeftLanesLength(road.getRoadID(), car.getCurFromCrossID());
        int size = road.getLanesNum();
        int i = 0;
        // true:可以插入；false:插不进去
        boolean flag1 = true;
        if (LeftLanesLengthList.size() == 0) {
            flag1 = false;
            // false:插不进去

            return flag1;

        } else {
            while (i < size && LeftLanesLengthList.get(i) == 0) {
                i++;
            }
            if (i == size) {
                flag1 = false;
                innitial(car.getCarID());
                // false:插不进去
                return flag1;

            }

        }


        /**
         * 可以插进去！
         */
        // 要插入的lane可以插入的空间
        int nextLaneOfRoadLeftSize = LeftLanesLengthList.get(i);

        // 以这个道路的最大速度以及车辆自己的最大速度
        // 在这个lane的最大速度
        int nextLaneVel = Math.min(road.getMaxRoadVelocity(), car.getMaxVelocity());

        /**
         * 之前已经更新过的信息 car.setCurFromCross(car.getFromCross()); car.setPriority(0);
         * car.setCurToCross(road.getToCross());
         * 不用更新的信息 car.setFromCross(fromCross); car.setToCross(toCross);
         *          * car.setMaxVelocity(maxVelocity);
         */
        if (car.getRealStartTime() == -1) {

            car.setRealStartTime(t);
        }
        /**
         * 找到下一个去往的路口
         */
        if (car.getFromCrossID().equals(road.getFromCrossID())) {

            car.setCurToCrossID(road.getToCrossID());
        } else if (road.isDuplex()) {
            car.setCurToCrossID(road.getFromCrossID());

        } else {
            System.out.println("没有下一个路口了？奇奇怪怪的");
        }

        /**
         * 简单的信息更新
         */
        // 这个可以根据page10得到了
        car.setCurPos(road.getRoadLength() - Math.min(nextLaneOfRoadLeftSize, nextLaneVel));
        car.setLaneID(i);
        car.setRoadID(road.getRoadID());
        car.setSheng(0);
        // 通过路的对象和laneID找到Lane的对象thisLane
        LaneModel thisLane = new LaneModel();
        LinkedList<LaneModel> l;
        if (road.getFromCrossID().equals(car.getCurFromCrossID())) {

            l = road.getForwardLane();
        } else {
            l = road.getBackwardLane();
        }

        for (LaneModel l1 : l) {
            if (l1.getLaneIndex() == car.getLaneID()) {
                thisLane = l1;
                break;
            }
        }
        // 把这辆车的实例加到后来的lane
        thisLane.carsInLane.add(car);
        // 前面没有车,不一定是一个lane哦

        // preCar是这个路上的之字划开的前面的第一辆车，可能是刚插进去的那辆车，可能不是
        // 这里不用判断是不是为空，因为我刚插进去一辆车呢，而且car.setHasArrangedOrNot是false，是会被读到的

        CarModel preCar = carServiceImpl.getCarModelById(roadServiceImpl.getFirstCarInRoad(road.getRoadID(), car.getFromCrossID()));


        if (preCar.equals(car)) {
            //
            if (car.getCurPos() - nextLaneVel < 0) {
                // 下一时刻是可以出去的车
                setCarInRoad(null, car, road, 3, false, false, false, false, false);
                car.setState(1);
            } else {
                // 下一时刻出不去
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
            } else {// 前车是5
                car.setState(5);
            }
        }

        // 准备返回,false:插不进去
        return flag1;
    }

    /**
     * 被退回来的从车库出发的车看看还能不能按照原来的想法插入到这个道路中
     *
     * @param car：被退回来的车；road：本来要走的道路；
     * @return 有没有成功插入
     */
    private boolean reArrangeCarsIDInsertToRoad(CarModel car, RoadModel road, int t) {
        boolean flag = InsertFreshCarToRoad(car, road, t);
        // false:插不进去
        return flag;

    }

    /**
     * 对车辆进行所有操作
     */
    public void FourCarStateUnionProcess(LinkedList<CarModel> carsFour, int t) {
        ArrayList<CarModel> cars1 = new ArrayList<>();
        ArrayList<CarModel> cars2 = new ArrayList<>();
        for (CarModel c : carsFour) {
            switch (c.getState()) {
                case (3): {
                    /**
                     * 判断一下了,他要么变成1了要么变成2了,后面的所有的车的数字状态也要跟着更新一波
                     */
                    UpdateRoadForCarsAtState3(c.getCarID());
                    break;

                }
                case (4): {
                    /**
                     * 那这个车前面本来是第二类车,那他肯定被带领着往前跳过了,不用管了，他肯定是更新过了的车了
                     */
                    System.out.println("没有被带着往前跑！错了！");
                    break;
                }
                case (5): {
                    /**
                     * 详情请看笔记
                     */
                    updateCarsAtState5(c.getCarID());
                    break;

                }
                case (1): {
                    /**
                     * 详情请看笔记，将它加到cars1中进行优先级比较，以及相关操作
                     */
                    cars1.add(c);
                    break;
                }
                case (2): {
                    /**
                     * 这里先什么都不做，因为处理cars1的车后可能还会出现新的2false
                     */
                    break;
                }
                default:
                    System.out.println("出问题啦！！！" + "\t");
            }


        }
        if (!cars1.isEmpty()) {
            UpdateLaneForCarsAtState1(cars1, t);
            // 可能有一个新的2false出来的
        }
        for (CarModel cc : carsFour) {
            if (cc.getState() == 2 && !cc.isHasArrangedOrNot()) {

                cars2.add(cc);
            }
        }

        /**
         * 处理state2的车
         */
        UpdateLaneForCarsAtState2(cars2);


    }

    /**
     * 对当前车辆规划下一道路,2019.3.28 保证第一条路可走（考虑rest），从car.getCurToCross开始找路径,可以进一步优化：此时第一条路不能走不一定真的在该时刻不能走，需要安排完一些车后可能会有空间
     *
     * @param car           当前车辆（两个等待状态）
     * @return 下一条道路
     */
    protected RoadModel findNextCross(CarModel car) {

        // 车所在路的通向路口是终点，则返回这条路ID
        if (car.getCurToCrossID().equals(car.getToCrossID())) {

            return roadServiceImpl.getRoadModelById(car.getRoadID());
        }
        // 未知节点集合
        List<CrossModel> unknown = new ArrayList<CrossModel>();
        // 当前出发节点路口，可能为null
        CrossModel s = crossServiceImpl.getCrossModelById(car.getCurToCrossID());
        // 目的地
        CrossModel t = crossServiceImpl.getCrossModelById(car.getToCrossID());
        Iterator<CrossModel> crossIter = listCross.iterator();
        while (crossIter.hasNext()) {
            CrossModel cross = crossIter.next();
            if (s.getCrossID().equals(cross.getCrossID())) {
                // 初始化出发节点,我重写了equals
                // 到达当前节点时间
                cross.cost = 0;
                cross.isKnown = true;
                cross.preCross = null;
            } else {// 初始化其余节点
                cross.cost = Float.MAX_VALUE;
                cross.isKnown = false;
                cross.preCross = null;
                unknown.add(cross);
            }
        }
        // 表示从出发节点来标记邻接节点，此时要保证搜索可行路径
        ArrayList<RoadModel> roads = new ArrayList<>();
        for (String roadID : s.getRoadIDList()) {
            if (!roadID.equals("-1")) {

                roads.add(roadServiceImpl.getRoadModelById(roadID));
            }
        }
        // 找到车过来的路,初始情况返回null
        RoadModel preRoad = crossServiceImpl.findRoad(car.getCurFromCrossID(), car.getCurToCrossID());
        // 不标记第一条可选路中不能走的路
        boolean flag = deleteCrossFromUnknown(car, roads, preRoad);
        if (!flag) {
            // 第一条路无路可走
            return null;
        }
        while (!t.isKnown) {
            Collections.sort(unknown, new Comparator<CrossModel>() {
                @Override
                public int compare(CrossModel o1, CrossModel o2) {
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
                System.out.println("找不到目标路口");
            }
            // 找到当前从源节点出发，代价最小的节点
            CrossModel v = unknown.get(unknown.size() - 1);
            v.isKnown = true;
            unknown.remove(unknown.size() - 1);
            ArrayList<RoadModel> roadsList = new ArrayList<>();
            for (String roadID : v.getRoadIDList()) {
                RoadModel road = roadServiceImpl.getRoadModelById(roadID);
                if (!roadID.equals("-1")) {

                    roadsList.add(road);
                }
            }
            for (RoadModel road : roadsList) {
                // 从该节点出发标记其相邻节点
                if (road != null && carServiceImpl.isDirectionRight(road.getRoadID(), v.getCrossID())) {
                    crossServiceImpl.markNextCross(road.getRoadID(), v.getCrossID());
                }
            }
        }
        return crossServiceImpl.findFirstRoad(s.getCrossID(), t.getCrossID());
    }

    /**
     * @param car     当前安排车辆
     * @param roads   当前搜索道路集合
     * @param preRoad 车当前所在道路
     * @return 从unknown中标记第一跳可行节点 ，不标记没有的路，方向不对的路，过来的路 对于没空间的路，都标记cost，不管是更新过没空间还是没更新过没空间
     */
    private boolean deleteCrossFromUnknown( CarModel car, List<RoadModel> roads, RoadModel preRoad) {
        // 可行道路数量
        int numOfAbleRoads = 0;
        CrossModel s;
        if (car.getCurToCrossID() == null) {
            s = crossServiceImpl.getCrossModelById(car.getFromCrossID());
        } else {
            // 车辆出发节点
            s = crossServiceImpl.getCrossModelById(car.getCurToCrossID());
        }
        int[] flag = new int[roads.size()];
        for (int i = 0; i < roads.size(); i++) {
            // 当前搜索道路
            RoadModel road = roads.get(i);
            if (preRoad != null && (road.equals(preRoad))) {
                // 不走回头路
                flag[i] = -1;
            } else {
                if (road == null || road.getRoadID().equals("-1")) {
                    // 前面已经确定不会有“-1”
                    // 没有路标记为-2
                    flag[i] = -2;
                } else if (!carServiceImpl.isDirectionRight(road.getRoadID(), s.getCrossID())) {
                    // 有路但方向不对标记为-3
                    flag[i] = -3;
                } else if (roadServiceImpl.hasLeftLength(road.getRoadID(), s.getCrossID()) > 0) {
                    numOfAbleRoads++;
                    // 有空间
                    flag[i] = 1;
                } else if (roadServiceImpl.hasLeftLength(road.getRoadID(), s.getCrossID()) == -1) {
                    numOfAbleRoads++;
                    // 没更新过而没空间
                    flag[i] = 2;
                } else if (roadServiceImpl.hasLeftLength(road.getRoadID(), s.getCrossID()) == -2) {
                    numOfAbleRoads++;
                    // 更新过而没空间
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
                RoadModel road = roads.get(i);
                if (flag[i] > 0) {
                    crossServiceImpl.markNextCross(road.getRoadID(), s.getCrossID());
                } else if (flag[i] < 0) {
                    continue;
                } else {
                    System.out.println("不知道啥路子");
                }

            }
        }
        return true;
    }


    /**
     * 从车库中止中取出可以出发的车放入garageWait
     * @param cars 车库中止车辆集合
     * @param t    当前时间（t-t+1）
     */
    private void classifyCars(LinkedList<CarModel> cars, int t) {


        garageWait.clear();

        for (int i = cars.size() - 1; i >= 0; i--) {
            CarModel car = cars.get(i);
            car.setHasArrangedOrNot(false);
            if (car.getPlanTime() <= t) {
                // 车辆计划出发时间大于当前时间，车库中止
                garageWait.add(car);
                cars.remove(i);
            }
        }
        // 按车辆速度升序排列
        Collections.sort(garageWait, new Comparator<CarModel>() {
            @Override
            public int compare(CarModel o1, CarModel o2) {
                return o1.getMaxVelocity() - o2.getMaxVelocity();
            }
        });
    }

    /**
     * 将State3:firstCar所处的road上的车的状态更新 需要更新状态信息，不更新位置信息，
     * 如果firstCar能过路口，state=1,后面的车state=3;firstCar不能过路口，state=2,后面的车state=4
     * @param firstCarID
     */
    private void UpdateRoadForCarsAtState3(String firstCarID) {
        CarModel firstCar = carServiceImpl.getCarModelById(firstCarID);
        // car行驶的路
        RoadModel carInRoad = roadServiceImpl.getRoadModelById(firstCar.getRoadID());
        String crossID = firstCar.getCurFromCrossID();
        // firstCar此时的行驶速度
        int maxSpeed = Math.min(firstCar.getMaxVelocity(), carInRoad.getMaxRoadVelocity());
        // 判断firstCar是否过路口
        // ***********更新firstCar的状态
        // firstCar.setCurPos(firstCar.getCurPos()-maxSpeed);//State3的车不更新t3时刻的curPos
        if (firstCar.getCurPos() - maxSpeed < 0) { // 如果能够过路口
            firstCar.setState(1);
        } else { // 如果不能过路口
            firstCar.setState(2);
        }
        int firstCarState = firstCar.getState();
        // **********更新Road上其它车的状态（如果不止有一辆的话）
        // carInRoadList里存储的是car的ID

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
        // 优化为迭代器遍历
        // 如果不止有一辆车
//        if (carInRoadNum > 1) {
//            Iterator<String> it = carInRoadList.iterator();
//            while (it.hasNext()) {
//                // 设置所有车的state
//                Main.MapCar.get(it.next()).setState(state);
//            }
//            // 迭代器遍历的时候是无序的，需要重新设置firstCar的状态
//            firstCar.setState(firstCarState);
//        }
    }

    /**
     * 将firstCar所处的lane上的车从t2时刻更新到t3时刻
     */
    private void UpdateLaneForCarsAtState1(ArrayList<CarModel> car1s, int t) {
        sortCarsOfState1(car1s);
        for (int i = 0; i < car1s.size(); i++)
            UpdateCarsAtState1(car1s.get(i).getCarID(), t);
    }

    /**
     * 从t2时刻更新到t3时刻
     * @author Tricia
     * @version 2019-04-14
     */
    private void UpdateCarsAtState1(String firstCarID, int t) {
        CarModel firstCar = carServiceImpl.getCarModelById(firstCarID); // firstCar实例
        RoadModel carInRoad = roadServiceImpl.getRoadModelById(firstCar.getRoadID()); // firstCar所在的Road
        LinkedList<LaneModel> carInLanes = new LinkedList<LaneModel>(); // firstCar行驶的路上有哪些lane

        if (firstCar.getCurFromCrossID().equals(carInRoad.getFromCrossID())) {// 判断方向
            carInLanes = carInRoad.getForwardLane();
        } else {
            carInLanes = carInRoad.getBackwardLane(); // car行驶的路上有哪些lane
        }

        int carInLaneID = firstCar.getLaneID();
        LaneModel laneInvlovesCar = carInLanes.get(carInLaneID);// fisrtCar所在的lane


        // **********情况1：要到家了
        //
        if (firstCar.getCurToCrossID().equals(firstCar.getToCrossID())) {
            firstCar.setRealEndTime(t + 1); // 设置到家时间
            ArrivedCar.add(firstCarID); // 放进ArrivalCars集合
            NowInRoadCar.remove(carServiceImpl.getCarModelById(firstCarID));// 把它从路上的车集合里面删掉

            laneInvlovesCar.carsInLane.removeFirst(); // $$$$$$将到家的Car从lane上去掉$$$$$$

            //判断firstCar是不是这条路上的唯一一个
            String nextCarID = roadServiceImpl.getFirstCarInRoad(carInRoad.getRoadID(), firstCar.getCurFromCrossID());
            if (nextCarID == null) {
                setCarInRoad(null, firstCar, carInRoad, 3, false, true, true, false, false);
            }
        }
        // **********情况2：能过路口，到别的路上
        else if (!firstCar.getCurToCrossID().equals(firstCar.getToCrossID())) {

//			laneInvlovesCar.carsInLane.removeFirst(); // $$$$将离开这条路的Car从lane上去掉$$$$$
//			String nextCarID = getFirstCarInRoad(carInRoad,firstCar.getCurFromCrossID());
//			if(nextCarID==null) {
//				setCarInRoad(null, firstCar, carInRoad, 3, false, true, true, false, false);
//			}
            // 下面就是跑到别的路了
            String nextRoadID = firstCar.getNextRoadID();
            RoadModel nextRoad = roadServiceImpl.getRoadModelById(nextRoadID);
            int firstCarSpeed = Math.min(firstCar.getMaxVelocity(), nextRoad.getMaxRoadVelocity()); // firstCar的行驶速度

            LinkedList<LaneModel> carInNextLanes = new LinkedList<LaneModel>(); // firstCar去往的路上有哪些lane
            if (firstCar.getCurToCrossID().equals(nextRoad.getFromCrossID())) {// 判断方向
                carInNextLanes = nextRoad.getForwardLane();
            } else {
                carInNextLanes = nextRoad.getBackwardLane(); // firstCar将要前往的路上有哪些lane
            }
            int carInNextLanesNum = carInNextLanes.size();
            ArrayList<Integer> lanesLeftLength = roadServiceImpl.getLeftLanesLength(nextRoad.getRoadID(), firstCar.getCurToCrossID());

            int laneiLeftLength = 0;
            int templaneID = -1;
            for (int i = 0; i < carInNextLanesNum; i++) {
                laneiLeftLength = lanesLeftLength.get(i);
                if (laneiLeftLength > 0) {// 从0开始塞车了，找到要进入的lane了
                    //firstCar.setLaneID(i);
                    templaneID = i;
                    break;
                }
            }

            //去不了
            if (templaneID == -1) {
                firstCar.setState(5);
                firstCar.setPriority(3);
                firstCar.setNextRoadID("-1");
                setCarInRoad(null, firstCar, carInRoad, 5, false, false, false, false, false);
            } else {
                firstCar.setLaneID(templaneID);
                // $$$$$更新laneID$$$$$
                // case1.1：该lane上car的前方有车，且是更新过的车
                // case1.2：该lane上car的前方有车，且是没有更新过的车
                // case2.1：该lane上car的前方没有车，且该road上也没有车
                // case2.2：该lane上car的前方没有车，但该road上有车

                // case1： 该lane上car的前方有车
                // case1.1：该lane上car的前方有车，且是更新过的车
                // case1.2：该lane上car的前方有车，且是没有更新过的车
                if (!carInNextLanes.get(firstCar.getLaneID()).carsInLane.isEmpty()) {
                    CarModel preCar = carInNextLanes.get(firstCar.getLaneID()).carsInLane.getLast();
                    if (preCar.isHasArrangedOrNot() && preCar.getSheng() == 0) {// case1.1：该lane上car的前方有车，且是更新过的车
                        // 1、设置状态
                        if (preCar.getState() == 1) {
                            firstCar.setState(3);
                        } else if (preCar.getState() == 2) {
                            firstCar.setState(4);
                        } else {
                            firstCar.setState(preCar.getState());
                        }

                        // 2、设置位置curPos
                        // firstCar在nextRoad能走多远
                        int hasJumpDis = firstCar.getCurPos();
                        // 判断firstCar能不能追上前车
                        if (nextRoad.getRoadLength() - preCar.getCurPos() - 1 >= firstCarSpeed - hasJumpDis) {// 不能追上
                            firstCar.setCurPos(nextRoad.getRoadLength() - (firstCarSpeed - hasJumpDis));
                        } else {
                            firstCar.setCurPos(preCar.getCurPos() + 1);
                        }
                    } else {// case1.2:：该lane上car的前方有车，且是没有更新过的车
                        // 1、设置状态
                        if (preCar.getState() == 1) {
                            firstCar.setState(3);
                        } else if (preCar.getState() == 2) {
                            firstCar.setState(4);
                        } else {
                            firstCar.setState(preCar.getState());
                        }

                        // 2、设置位置curPos
                        // firstCar在nextRoad能走多远
                        int hasJumpDis = firstCar.getCurPos();
                        // 判断firstCar能不能追上前车
                        if (nextRoad.getRoadLength() - preCar.getCurPos() - 1 >= firstCarSpeed - hasJumpDis) {// 不能追上
                            firstCar.setCurPos(nextRoad.getRoadLength() - (firstCarSpeed - hasJumpDis));
                        } else {
                            firstCar.setCurPos(preCar.getCurPos() + 1);
                            firstCar.setSheng(
                                    -(preCar.getCurPos() + 1 - (nextRoad.getRoadLength() - (firstCarSpeed - hasJumpDis))));
                        }

                    }
                }
                // 该lane上car的前方没有车，case2
                // case2.1：该lane上car的前方没有车，且该road上也没有车
                // case2.2：该lane上car的前方没有车，但该road上有车

                else {
                    //LinkedList<String> carInNextRoadID = getCarInRoad(nextRoad, firstCar.getCurToCrossID());//4.12-我们不能只看没更新状态的车！！！！

                    //其实我们只需要看每个lane上的第一辆车就行
                    //0412更新，我们要更新这条路上所有车的状态，不只是没更新过的车。
                    ArrayList<String> carInNextRoadID = new ArrayList<String>();
                    LinkedList<CarModel> carInNextLanel = new LinkedList<CarModel>();
                    for (LaneModel l : carInNextLanes) {
                        carInNextLanel = l.carsInLane;
                        if (carInNextLanel.size() != 0) {
                            carInNextRoadID.add(carInNextLanel.getFirst().getCarID());
                        }
                    } //不管true车还是false车，都是这条nextRoad的上的车


                    //	 * @param road 当前道路, @param crossID 车从哪个路口到这个路;


                    if (carInNextRoadID.isEmpty()) {// case2.1:该road上也没有车   原来写的是carInNextRoadID.isEmpty()，注意我们的firstcar现在还不在这条路上！！！！！
                        // 1、设置位置 curPos
                        int hasJumpDis = firstCar.getCurPos();
                        firstCar.setCurPos(nextRoad.getRoadLength() - (firstCarSpeed - hasJumpDis));
                        // 2、设置状态
                        // 判断该车t3时刻是否能过路口
                        if (firstCar.getCurPos() < firstCarSpeed) {// 能过路口是1
                            firstCar.setState(1);
                        } else {// 不能过路口是2
                            firstCar.setState(2);
                        }

                    } else { // case2.2:该road上有车
                        // 1、设置位置 curPos
                        int hasJumpDis = firstCar.getCurPos();
                        firstCar.setCurPos(nextRoad.getRoadLength() - (firstCarSpeed - hasJumpDis));
                        // 2、设置状态


                        // 要找该road上在firstcar之前的车
                        //LinkedList<String> carInNextRoadID = getCarInRoad(nextRoad, firstCar.getCurToCrossID());
                        String preRoadCarID;
                        if (roadServiceImpl.getFirstCarInRoad(nextRoad.getRoadID(), firstCar.getCurToCrossID()) != null) {
                            preRoadCarID = roadServiceImpl.getFirstCarInRoad(nextRoad.getRoadID(), firstCar.getCurToCrossID()); //有false车
                        } else { //只有true车
                            preRoadCarID = roadServiceImpl.getFirstTrueCarInRoad(nextRoad.getRoadID(), firstCar.getCurToCrossID()); //方法参数  road 当前道路, @param crossID 车从哪个路口到这个路;
                        }

                        CarModel preRoadCar = carServiceImpl.getCarModelById(preRoadCarID); //我们取出的是整个路上的第一个car，可能是true也可能是false
                        //0412我们需要取得整个路上真正的头车，此时是没有我们的firstcar的，它还没加进来呢！！！（但firstcar所在的lane上木有别的car了）
//						if(carInNextRoadID.size()>1) {
//						  for(int i=1;i<carInNextRoadID.size();i++) {
//						    //先判断这辆车能不能过路口
//							Car car1 = Main.MapCar.get(carInNextRoadID.get(i));
//							int car1Speed = Math.min(car1.getMaxVelocity(), nextRoad.getMaxRoadVelocity());
//							int preRoadCarSpeed = Math.min(preRoadCar.getMaxVelocity(), nextRoad.getMaxRoadVelocity());
//                            //能过路口，且位置在preRoadCar前面，且preRoadCar不能过路口,将car1变为preCar
//							if(car1.getCurPos()<car1Speed && car1.getCurPos()<preRoadCar.getCurPos()&&preRoadCar.getCurPos()>=preRoadCarSpeed) {
//								preRoadCar = car1;
//							}
//						  }
//						}
                        int preRoadCarSpeed = Math.min(preRoadCar.getMaxVelocity(), nextRoad.getMaxRoadVelocity());

                        //4.14更新
                        //如果preRoadCar是false车，那它一定是头车
                        if (preRoadCar.isHasArrangedOrNot() == false) {
                            if (preRoadCar.getState() == 1) {
                                firstCar.setState(3);
                            } else if (preRoadCar.getState() == 2) {
                                firstCar.setState(4);
                            } else { //3-3,4-4,5-5
                                firstCar.setState(preRoadCar.getState());
                            }
                        } else { //如果preRoadCar是true车，需要进一步判断能否过路口，curPos，laneID
                            //得到preCar之后，首先看firstCar能不能过路口
                            if (firstCar.getCurPos() < firstCarSpeed) { //firstCar能过路口
                                if (preRoadCar.getCurPos() < preRoadCarSpeed) { //preRoadCar能过路口
                                    //比较两者的curPos
                                    if (firstCar.getCurPos() < firstCar.getCurPos()) {//firstCar称为这条路的头车
                                        firstCar.setState(1);
                                    } else if (firstCar.getCurPos() == firstCar.getCurPos()) { //位置相同比较laneID
                                        if (firstCar.getLaneID() < firstCar.getLaneID()) { //firstCar成为头车
                                            firstCar.setState(1);
                                        } else { //preRoadCar是这条路的头车
                                            firstCar.setState(3);
                                        }
                                    } else {//firstCar的curPos在preRoadCar之后，preRoadCar是这条路的头车
                                        firstCar.setState(3);
                                    }
                                } else {//preRoadCar不能过路口
                                    //firstCar成为这条路的头车
                                    firstCar.setState(1);
                                }
                            } else {//firstCar不能过路口
                                if (preRoadCar.getCurPos() < preRoadCarSpeed) {//preRoadCar能过路口
                                    //preRoadCar是这条路的头车
                                    firstCar.setState(3);
                                } else {//preRoadCar不能过路口
                                    if (firstCar.getCurPos() < preRoadCar.getCurPos()) { //firstCar在前面
                                        //firstCar成为头车
                                        firstCar.setState(2);
                                    } else if (firstCar.getCurPos() == preRoadCar.getCurPos()) { //位置相同，比较laneID
                                        if (firstCar.getLaneID() < firstCar.getLaneID()) { //firstCar成为头车
                                            firstCar.setState(2);
                                        } else { //preRoadCar是这条路的头车
                                            firstCar.setState(4);
                                        }
                                    } else {//firstCar的curPospre在RoadCar之后，preRoadCar是这条路的头车
                                        firstCar.setState(4);
                                    }
                                }
                            }
                        }


//						if (preRoadCar.getCurPos() < firstCar.getCurPos()) { // firstcar在road原来车的后面
//							if (preRoadCar.getState() == 1) {
//								firstCar.setState(3);
//							} else if (preRoadCar.getState() == 2) {
//								firstCar.setState(4);
//							} else {
//								firstCar.setState(preRoadCar.getState());
//							}
//						} else if (preRoadCar.getCurPos() == firstCar.getCurPos()
//								&& firstCar.getLaneID() > preRoadCar.getLaneID()) {// firstcar和road原来第一辆车同一水平线，需要比较laneID
//
//							if (preRoadCar.getState() == 1) {
//								firstCar.setState(3);
//							} else if (preRoadCar.getState() == 2) {
//								firstCar.setState(4);
//							} else {
//								firstCar.setState(preRoadCar.getState());
//							}
//						} else {// firstCar在这条Road的最前面
//								// 判断该车t3时刻是否能过路口
//							if (firstCar.getCurPos() < firstCarSpeed) {// 能过路口是1
//								firstCar.setState(1);
//							} else {// 不能过路口是2
//								firstCar.setState(2);
//							}
//						}

                    }

                }

                laneInvlovesCar.carsInLane.removeFirst(); // $$$$将离开这条路的Car从lane上去掉$$$$$
                String nextCarID = roadServiceImpl.getFirstCarInRoad(carInRoad.getRoadID(), firstCar.getCurFromCrossID());
                if (nextCarID == null) {
                    setCarInRoad(null, firstCar, carInRoad, 3, false, true, true, false, false);
                }

                // ------统一更新信息
                carInNextLanes.get(firstCar.getLaneID()).carsInLane.add(firstCar); // $$$$$把car加到这个lane上$$$$$

                firstCar.setHasArrangedOrNot(true); // 更新标志位
                firstCar.setRoadID(nextRoadID);
                firstCar.setCurFromCrossID(firstCar.getCurToCrossID());
                firstCar.setCurToCrossID(roadServiceImpl.getCross(nextRoad.getRoadID(), firstCar.getCurFromCrossID()).getCrossID());

                firstCar.setNextRoadID("-1");

            }
        }


    }

    /**
     * 将firstCar所处的lane上的车从t2时刻更新到t3时刻
     *
     * @param car2s
     */
    private void UpdateLaneForCarsAtState2(ArrayList<CarModel> car2s) {

        for (CarModel c : car2s) {
            UpdateRoadForCarsAtState2(c.getCarID());

        }

    }

    /**
     * 更新状态2的车所在的路
     *
     * @param firstCarID
     * @author Tricia
     * @version 2019-04-15
     */
    private void UpdateRoadForCarsAtState2(String firstCarID) {
        CarModel firstCar = carServiceImpl.getCarModelById(firstCarID);

        if (!firstCar.getNextRoadID().equals("-1")) {
            UpdateRoadForCarsAtState2Super(firstCarID); // 其实找到路了，只是道路限速过不去
        } else {
            UpdateRoadForCarsAtState2Nomal(firstCarID); // 本来就没能变成等待车
        }
    }

    /**
     * firstCar State2 nextRoadID!=-1 是去不了的1变来的 将firstCar所处的Road上的车从t2时刻更新到t3时刻
     * 更新State、curPos、sheng、hasArrangedOrNot 找出road上所有lane上的“firstCar”,更新每一个lane
     *
     * @param firstCarID
     * @author Tricia
     * @version 2019-4-15
     */
    private void UpdateRoadForCarsAtState2Super(String firstCarID) {
        CarModel firstCar = carServiceImpl.getCarModelById(firstCarID);
        RoadModel road = roadServiceImpl.getRoadModelById(firstCar.getRoadID());
        LinkedList<LaneModel> carInLane = new LinkedList<LaneModel>();
        if (firstCar.getCurFromCrossID().equals(road.getFromCrossID())) {
            carInLane = road.getForwardLane();
        } else {
            carInLane = road.getBackwardLane(); // car行驶的路上有哪些lane
        }

        // **********更新位置
        for (int i = 0; i < road.getLanesNum(); i++) {
            if (carInLane.get(i).carsInLane.size() > 0) {
                // 如果这个lane上有车，更新这个lane上的车的位置
                UpdateLanePosForCarsAtState2(carInLane.get(i).carsInLane.getFirst().getCarID(), carInLane);// 找出每个lane上的第一辆车
            } else {// 这个Lane上没有车，继续
                continue;
            }
        }

        // 更新位置后，寻找目前的整个road的第一辆车

        String newFirstCarID;
        // 先看false的第一辆车
        if (roadServiceImpl.getFirstCarInRoad(road.getRoadID(), firstCar.getCurFromCrossID()) != null) {
            newFirstCarID = roadServiceImpl.getFirstCarInRoad(road.getRoadID(), firstCar.getCurFromCrossID());
        } else {
            //如果没有false车，那就都是true车了？
            newFirstCarID = roadServiceImpl.getFirstTrueCarInRoad(road.getRoadID(), firstCar.getCurFromCrossID());
        }
        CarModel newFirstCar = carServiceImpl.getCarModelById(newFirstCarID);

        newFirstCar.setState(1); // firstCar是由1车变过来的，curpos都变成0了，状态一定是1

        newFirstCar.setHasArrangedOrNot(true);// 更新位置之后再设置hasArrangedOrNot

        // *********更新State
        int followingState = 3;

        for (int i = 0; i < road.getLanesNum(); i++) {
            if (carInLane.get(i).carsInLane.size() > 0) {
                // 如果这个lane上有车，更新这个lane上的车状态
                UpdateLaneStateForCarsAtState2(carInLane.get(i).carsInLane.getFirst().getCarID(), newFirstCarID, followingState, carInLane);// 找出每个lane上的第一辆车
            } else {// 这个Lane上没有车，继续
                continue;
            }
        }

    }

    /**
     * firstCar State2 nextRoadID==-1 普通情况 将firstCar所处的Road上的车从t2时刻更新到t3时刻
     * 更新State、curPos、sheng、hasArrangedOrNot 找出road上所有lane上的“firstCar”,更新每一个lane
     *
     * @param firstCarID
     * @author Tricia
     * @version 2019-4-15
     */
    private void UpdateRoadForCarsAtState2Nomal(String firstCarID) {
        // Note:将firstCar所处的Road上的车从t2时刻更新到t3时刻
        // 找出road上所有lane上的“firstCar”,更新每一个lane
        // firstCar的t3时刻一定是不能过路口的；
        // 但t4时刻的状态是不一定的，如果能够过路口设为1，后面的车都是3；不能过路口的话，设为2，后面都是4
        CarModel firstCar = carServiceImpl.getCarModelById(firstCarID);
        RoadModel road = roadServiceImpl.getRoadModelById(firstCar.getRoadID());
        LinkedList<LaneModel> carInLane = new LinkedList<LaneModel>();
        if (firstCar.getCurFromCrossID().equals(road.getFromCrossID())) {
            carInLane = road.getForwardLane();
        } else {
            carInLane = road.getBackwardLane(); // car行驶的路上有哪些lane
        }
        // **********更新位置
        for (int i = 0; i < road.getLanesNum(); i++) {
            if (carInLane.get(i).carsInLane.size() > 0) {
                // 如果这个lane上有车，更新这个lane上的车的位置
                UpdateLanePosForCarsAtState2(carInLane.get(i).carsInLane.getFirst().getCarID(), carInLane);// 找出每个lane上的第一辆车
            } else {// 这个Lane上没有车，继续
                continue;
            }
        }


        // 更新位置后，寻找目前的整个road的第一辆车
        String newFirstCarID;
        // 先看false的第一辆车
        if (roadServiceImpl.getFirstCarInRoad(road.getRoadID(), firstCar.getCurFromCrossID()) != null) {
            newFirstCarID = roadServiceImpl.getFirstCarInRoad(road.getRoadID(), firstCar.getCurFromCrossID());
        } else {
            //如果没有false车，那就都是true车了？
            newFirstCarID = roadServiceImpl.getFirstTrueCarInRoad(road.getRoadID(), firstCar.getCurFromCrossID());
        }
        CarModel newFirstCar = carServiceImpl.getCarModelById(newFirstCarID);

        int newmaxSpeed = Math.min(newFirstCar.getMaxVelocity(), road.getMaxRoadVelocity()); // firstCar此时的行驶速度

        if (newFirstCar.getCurPos() - newmaxSpeed < 0) { // 如果能够过路口
            newFirstCar.setState(1);
        } else { // 如果不能过路口
            newFirstCar.setState(2);
        }
        newFirstCar.setHasArrangedOrNot(true);

        // *********更新State
        int followingState = 0;
        if (newFirstCar.getState() == 1) {
            followingState = 3;
        } else if (newFirstCar.getState() == 2) {
            followingState = 4;
        } else {
            System.out.print("UpdateRoadForCarsAtState2---第一辆车的状态错了");
        }

        for (int i = 0; i < road.getLanesNum(); i++) {
            if (carInLane.get(i).carsInLane.size() > 0) {
                // 如果这个lane上有车，更新这个lane上的车状态
                UpdateLaneStateForCarsAtState2(carInLane.get(i).carsInLane.getFirst().getCarID(), newFirstCarID, followingState, carInLane);// 找出每个lane上的第一辆车
            } else {// 这个Lane上没有车，继续
                continue;
            }
        }

    }

    /**
     * 将firstCar所处的lane上的车的位置从t2时刻更新到t3时刻 更新curPos、sheng
     *
     * @param firstCarID
     * @version 2019-04-15
     */
    private void UpdateLanePosForCarsAtState2(String firstCarID, LinkedList<LaneModel> carInLane) {
        // 更新curPos、sheng
        //
        // Note:将firstCar所处的lane上的车的位置从t2时刻更新到t3时刻
        // firstCar的t3时刻一定是不能过路口的；
        // 但t4时刻的状态是不一定的，如果能够过路口设为1，后面的车都是3；不能过路口的话，设为2，后面都是4

        // *************找出firstCar所在的lane
        CarModel firstCar = carServiceImpl.getCarModelById(firstCarID);
        RoadModel carInRoad = roadServiceImpl.getRoadModelById(firstCar.getRoadID()); // car行驶的路

        int carInLaneID = firstCar.getLaneID();
        LaneModel laneInvlovesCar = carInLane.get(carInLaneID);// fisrtCar所在的lane
        int maxSpeed = Math.min(firstCar.getMaxVelocity(), carInRoad.getMaxRoadVelocity()); // firstCar此时的行驶速度

        // ***********更新firstCar的位置
        // firstCar的nextRoadID是否为-1
        if (firstCar.getNextRoadID().equals("-1")) {
            firstCar.setCurPos(firstCar.getCurPos() - maxSpeed);// 更新t3时刻
            firstCar.setSheng(0);
            firstCar.setHasArrangedOrNot(true);
        } else {
            firstCar.setCurPos(0); // 由于限速过不去nextRoad的车，curPos设置为0
            firstCar.setSheng(0);
            firstCar.setHasArrangedOrNot(true);
        }

        // **********更新lane上其它车（如果不止有一辆的话）
        LinkedList<CarModel> carsInLane = laneInvlovesCar.carsInLane;
        int cariSpeed = 0;
        int carInLaneNum = carsInLane.size();
        if (carInLaneNum > 1) {
            for (int i = 1; i < carInLaneNum; i++) {
                cariSpeed = Math.min(carsInLane.get(i).getMaxVelocity(), carInRoad.getMaxRoadVelocity());
                // ------case1:没有安排过的车
                if (carsInLane.get(i).isHasArrangedOrNot() == false) {
                    if (carsInLane.get(i).getCurPos() - cariSpeed <= carsInLane.get(i - 1).getCurPos()) {// 能追上前车（前车已经更新到t3时刻了）
                        carsInLane.get(i).setCurPos(carsInLane.get(i - 1).getCurPos() + 1);
                    } else {// 如果不能追上前车，能跑多远跑多远
                        carsInLane.get(i).setCurPos(carsInLane.get(i).getCurPos() - cariSpeed);
                    }
                }
                // -----case2:安排过的车（从另一个路口过来的)
                else if (carsInLane.get(i).isHasArrangedOrNot() == true && carsInLane.get(i).getSheng() != 0) {
                    if (carsInLane.get(i).getCurPos() - carsInLane.get(i - 1).getCurPos() > Math
                            .abs(carsInLane.get(i).getSheng())) { // 追不上前车
                        carsInLane.get(i).setCurPos(carsInLane.get(i).getCurPos() + carsInLane.get(i).getSheng()); // sheng是负值！
                    } else {// 能追上
                        carsInLane.get(i).setCurPos(carsInLane.get(i - 1).getCurPos() + 1);
                    }
                }
                //				// -----case3:如果遇到一辆已经更新的了，那它后面肯定也已经更新了（保险起见，先注释这段）
                //				else if (carsInLane.get(i).isHasArrangedOrNot() == true && carsInLane.get(i).getSheng() == 0) {
                //					break;
                //				}
                // 统一更新属性
                carsInLane.get(i).setSheng(0);
                carsInLane.get(i).setHasArrangedOrNot(true);
            }
        }

    }

    /**
     * 将lanefirstCar所处的lane上的车的状态从t2时刻更新到t3时刻 需要更新的信息有：hasArrangedOrNot,State
     *
     * @param laneFirstCarID
     * @param newFirstCarID
     * @param followingState
     * @author Tricia
     * @version 2019.04.15
     */
    private void UpdateLaneStateForCarsAtState2(String laneFirstCarID, String newFirstCarID, int followingState, LinkedList<LaneModel> carInLane) {
        // Note:将firstCar所处的lane上的车从t2时刻更新到t3时刻
        // firstCar的curPos已经更新到t3时刻！（该路上所有车的curpos都已经更新到t3时刻了）

        // *************找出firstCar所在的lane
        CarModel firstCar = carServiceImpl.getCarModelById(laneFirstCarID);
        //Road carInRoad = Main.MapRoad.get(firstCar.getRoadID()); // car行驶的路
        //		LinkedList<Lane> carInLane = new LinkedList<Lane>();
        //		if (firstCar.getCurFromCrossID().equals(carInRoad.getFromCrossID())) {
        //			carInLane = carInRoad.getForwardLane();
        //		} else {
        //			carInLane = carInRoad.getBackwardLane();   // car行驶的路上有哪些lane
        //		}

        int carInLaneID = firstCar.getLaneID();
        LaneModel laneInvlovesCar = carInLane.get(carInLaneID);   // fisrtCar所在的lane

        // **********更新lane上其它车（如果不止有一辆的话）
        LinkedList<CarModel> carsInLane = laneInvlovesCar.carsInLane;
        if (!laneFirstCarID.equals(newFirstCarID)) {// 如果找到的不是这辆road上排在“最前”的车，也就是已经设置过状态的车
            carsInLane.get(0).setState(followingState);
            carsInLane.get(0).setHasArrangedOrNot(true);
        }
        // 如果是newfirstCar，不要再设置state了。
        int carInLaneNum = carsInLane.size();
        if (carInLaneNum > 1) {
            for (int i = 1; i < carInLaneNum; i++) {
                // 统一更新属性
                carsInLane.get(i).setState(followingState);
                carsInLane.get(i).setHasArrangedOrNot(true);
            }
        }

    }

    /**
     * 将传入的CarID按照｛优先级，carID｝排序，先排优先级（从大到小），优先级相同的再排carID（从小到大）
     * * 传入的车可能暂时找不到路，此时设置该车的状态为5，后车的状态也都设置为5，且该车不参与排序
     * * 如果所找的路已经更新完也不能通行，则设置为5true，该车不动，后车（如果本来能过路口则不动，本来不能过路口则向前跳），设置为4true，但实际设置为5，按4true来跳
     * * 要到目的地的车优先级设为3，此车参与排序
     * * 如果车本来有nextRoadID则不找路，直接判断它变为上述的哪种状态
     * * 如果所选道路上的行驶速度减去在本道路上可行距离小于等于0，则不能过路口，该车变为2false，后车变为4false
     *
     * @param cars
     */
    private void sortCarsOfState1(ArrayList<CarModel> cars) {
        ArrayList<CarModel> carsList = new ArrayList<>();
        if (cars != null && cars.size() != 0) {
            for (int i = 0; i < cars.size(); i++) {
                CarModel car = cars.get(i);
                RoadModel r = roadServiceImpl.getRoadModelById((car.getRoadID()));
                if (car.getCurToCrossID().equals(car.getToCrossID())) {
                    //要到家的车
                    car.setPriority(3);
                    //参与排序
                    carsList.add(car);
                } else if (!car.getNextRoadID().equals("-1")) {
                    //过路口但不许要重新找路
                    RoadModel toRoad = roadServiceImpl.getRoadModelById(car.getNextRoadID());
                    //该路因为没更新完没空间，则变为5false，后车也是5false
                    if (roadServiceImpl.hasLeftLength(toRoad.getRoadID(), car.getCurToCrossID()) == -1) {
                        setCarInRoad(toRoad, car, r, 5, false, false, false, false, false);
                    }
                    //该路因为更新完没空间，则变为5true，后车往前跳，变4true,但实际设置为5，按4true来跳
                    else if (roadServiceImpl.hasLeftLength(toRoad.getRoadID(), car.getCurToCrossID()) == -2) {
                        setCarInRoad(toRoad, car, r, 5, true, true, true, false, false);
                    } else if (Math.min(car.getMaxVelocity(), toRoad.getMaxRoadVelocity()) - car.getCurPos() <= 0) {
                        setCarInRoad(toRoad, car, r, 4, false, false, false, false, false);
                        car.setState(2);
                    } else if (Math.min(car.getMaxVelocity(), toRoad.getMaxRoadVelocity()) - car.getCurPos() > 0) {
                        carsList.add(car);
                    }
                } else {//还要继续过路口行驶并需要找路的车
                    RoadModel toRoad = findNextCross(car);
                    if (toRoad == null) {
                        //找不到路则该车状态变为5，未安排的后车状态都变为5
                        System.out.println("我死在这里了");
                    }
                    //找到的路因为没更新完没空间，则变为5false，后车也是5false
                    else if (roadServiceImpl.hasLeftLength(toRoad.getRoadID(), car.getCurToCrossID()) == -1) {
                        setCarInRoad(toRoad, car, r, 5, false, false, false, false, false);
                    }
                    //找到的路因为更新完没空间，则变为5true，后车往前跳，变4true，,但实际设置为5，按4true来跳
                    else if (roadServiceImpl.hasLeftLength(toRoad.getRoadID(), car.getCurToCrossID()) == -2) {
                        setCarInRoad(toRoad, car, r, 5, true, true, true, false, false);
                        car.setPriority(crossServiceImpl.setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
                        car.setNextRoadID(toRoad.getRoadID());
                    } else if (Math.min(car.getMaxVelocity(), toRoad.getMaxRoadVelocity()) - car.getCurPos() <= 0) {
                        setCarInRoad(toRoad, car, r, 4, false, false, false, false, false);
                        car.setState(2);
                        car.setNextRoadID(toRoad.getRoadID());
                        car.setPriority(crossServiceImpl.setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
                    } else {
                        //找到路的车，参与排序
                        car.setPriority(crossServiceImpl.setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
                        car.setNextRoadID(toRoad.getRoadID());
                        carsList.add(car);
                    }
                }
            }
            Collections.sort(carsList, new Comparator<CarModel>() {
                // 按车辆优先级降序排列
                @Override
                public int compare(CarModel o1, CarModel o2) {
                    return o2.getPriority() - o1.getPriority();
                }
            });
            for (int i = 0; i < carsList.size(); i++) {
                //优先级相同的车辆按ID升序排列
                int j = i + 1;
                while (j < carsList.size() && carsList.get(j - 1).getPriority() == carsList.get(j).getPriority()) {
                    j++;
                }
                Collections.sort(carsList.subList(i, j), new Comparator<CarModel>() {
                    // 按车辆优先级降序排列
                    @Override
                    public int compare(CarModel o1, CarModel o2) {
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

    /**
     * 更新5false车的状态，首先判断该车能不能过路口，能过则找路，不能过则该车状态变为2，后车状态变为4
     * @param firstCarID 状态为5false的车，排在carInRoad的头头
     * @author Dalton
     * @version 2019.4.13
     */
    private void updateCarsAtState5(String firstCarID) {
        CarModel firstCar = carServiceImpl.getCarModelById(firstCarID);
        RoadModel curRoad = roadServiceImpl.getRoadModelById(firstCar.getRoadID());
        if (firstCar.getCurPos() < Math.min(firstCar.getMaxVelocity(), curRoad.getMaxRoadVelocity())) {
            updateLaneForCarsAtState5(firstCarID);
        } else {
            setCarInRoad(null, firstCar, curRoad, 4, false, false, false, false, false);
            firstCar.setState(2);
        }
    }

    /**
     * 如果他要到家，他就变回1false了，后面车变成3false；
     * 如果它发现有路可以走了，他就变回1false了，后面车变成3false；
     * 更新完没空间，他就变成true5,后面的车更新状态变成4true,但实际设置为5，按4true来跳后面车（如果本来能过路口则不动，本来不能过路口则向前跳）;
     * 不然他还是flase5
     * @param firstCarID:状态为5false的车，排在carInRoad的头头
     * @author Dalton
     * @version 2019.4.16
     */
    private void updateLaneForCarsAtState5(String firstCarID) {
        CarModel firstCar = carServiceImpl.getCarModelById(firstCarID);
        RoadModel curRoad = roadServiceImpl.getRoadModelById(firstCar.getRoadID());
        RoadModel road;
        /**
         * 首先判断是不是到家车，如果是直接变成1false, 不是就按照正常逻辑
         */
        if (firstCar.getCurToCrossID().equals(firstCar.getToCrossID())) {
            setCarInRoad(null, firstCar, curRoad, 3, false, false, false, false, false);
            firstCar.setState(1);
            return;
        }
        road = findNextCross(firstCar);
        if (roadServiceImpl.hasLeftLength(road.getRoadID(), firstCar.getCurToCrossID()) > 0) {
            // 可供选择的路都有空间可走，该车变为1，后车变为3
            setCarInRoad(road, firstCar, curRoad, 3, false, false, false, false, false);
            firstCar.setState(1);
            firstCar.setNextRoadID(road.getRoadID());
            firstCar.setPriority(crossServiceImpl.setPriority(firstCar.getRoadID(), firstCar.getNextRoadID(), firstCar.getCurToCrossID()));
        }
        // 更新完也没空间,则变为5true，后车往前跳，变4true,但实际设置为5，按4true来跳
        else if (roadServiceImpl.hasLeftLength(road.getRoadID(), firstCar.getCurToCrossID()) == -2) {
            setCarInRoad(road, firstCar, curRoad, 5, true, true, true, false, false);
        } else if (roadServiceImpl.hasLeftLength(road.getRoadID(), firstCar.getCurToCrossID()) == -1) {
            numOf5++;
        }
        // 因为未更新而没有空间，该车状态不变
    }

    /**
     * 更新头车所在方向lane的所有车的相关属性
     * @param toRoad 车下一个时刻要去的路
     * @param car 当前车
     * @param r 车当前所在的路
     * @param state 要将车更改为的数字状态，如果是-2则不更改此状态
     * @param setHasArrangedOrNot true则要更改此属性
     * @param setSheng true则要更改此属性，变为0
     * @param setCurPos true则要更新车的位置，并动起来
     * @param setNextRoadID true则要更新车的此属性
     * @param setPriority true则要更新车的此属性
     * @author Dalton
     * @version 2019.04.16
     */
    private void setCarInRoad(RoadModel toRoad, CarModel car, RoadModel r, int state, boolean setHasArrangedOrNot, boolean setSheng, boolean setCurPos, boolean setNextRoadID, boolean setPriority) {
        if (car.getCarID().equals("10789") && r.getRoadID().equals("5007"))
            System.out.println();
        LinkedList<LaneModel> lanes;
        LinkedList<CarModel> carsInLane;
        CarModel shengCar;
        if (car.getCurFromCrossID().equals(r.getFromCrossID())) {

            lanes = r.getForwardLane();
        } else {
            lanes = r.getBackwardLane();
        }
        for (int j = 0; j < lanes.size(); j++) {
            carsInLane = lanes.get(j).carsInLane;
            //按lane逐个取车
            for (int y = 0; y < carsInLane.size(); y++) {
                shengCar = carsInLane.get(y);
                //设置curPos
                if (setCurPos) {
                    //有sheng
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
                        numOf5 += 2;
                    } else if (!shengCar.isHasArrangedOrNot()) {
                        //没有sheng，但没被安排过
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
                //设置state
                if (state != -2) {
                    if (state == 4) {
                        numOf2++;
                    }

                    if (state == 5 && setHasArrangedOrNot) {
                        numOf5 += 2;
                    }

                    if (state == 5 && !setHasArrangedOrNot) {
                        numOf5++;
                    }
                    if(shengCar.getState()==5 && !setHasArrangedOrNot && state!=5) {
                        numOf5--;
                    }
                    if(shengCar.getState()==5 && setHasArrangedOrNot && state!=5) {
                        numOf5-=2;
                    }
                    if((shengCar.getState()==4|| shengCar.getState()==2) && state!=4) {
                        numOf2--;
                    }
                    shengCar.setState(state);
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

                    shengCar.setPriority(crossServiceImpl.setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
                }
            }
        }
    }

    /**
     * 返回roadList中最长的道路
     * @param roadList
     * @return 最大道路长度
     */
    public int getMaxRoadLength(ArrayList<RoadModel> roadList) {
        RoadComparator myComparator = new RoadComparator();
        return (Collections.max(roadList, myComparator).getRoadLength());
    }

    private static class RoadComparator implements Comparator<RoadModel> {
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
    }
}

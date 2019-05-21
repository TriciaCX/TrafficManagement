package com.trafficproject.service.impl;

import com.trafficproject.service.*;
import com.trafficproject.service.model.CarModel;
import com.trafficproject.service.model.CrossModel;
import com.trafficproject.service.model.LaneModel;
import com.trafficproject.service.model.RoadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ManagementServiceImpl extends BaseService implements ManagementService {

    @Autowired
    private FunctionService functionService;

//    public ArrayList<CrossModel> getListCross() {
////        if(listCross==null)
////            setListCross();
////        return listCross;
////    }
////
////    public void setListCross() {
////        this.listCross = (ArrayList<CrossModel>) crossService.listCross();
////    }
////
////    public ArrayList<RoadModel> getListRoad() {
////        if(listRoad==null)
////            setListRoad();
////        return listRoad;
////    }
////
////    public void setListRoad() {
////        this.listRoad = (ArrayList<RoadModel>) roadService.listRoad();
////    }
////
////    public ArrayList<CarModel> getListCar() {
////        if(listCar==null)
////            setListCar();
////
////        return listCar;
////    }

//    public void setListCar() {
//        this.listCar = (ArrayList<CarModel>) carService.listCar();
//    }
//
//    public LinkedList<CarModel> getGarageFrozen() {
//        return garageFrozen;
//    }
//
//    public HashSet<CarModel> getNowInRoadCar() {
//        return NowInRoadCar;
//    }
//
//    public HashSet<String> getArrivedCar() {
//        return ArrivedCar;
//    }




    /**
     * 从车库中止中取出可以出发的车放入garageWait
     *
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
     * 对车遍历，是不是都是真实的位置了
     * *@param hasArrag=true,sheng=o
     */
    public boolean isAllReal() {
        if (nowInRoadCar.isEmpty()) {

            return true;
        }

        Iterator<CarModel> carIt = nowInRoadCar.iterator();
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
//        if(this.listCar==null){
//            setListCar();
//        }
        Iterator<String> carIt = arrivedCar.iterator();
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
            nowInRoadCar.add(c);
        }
    }

    /**
     * 一个时间片的末尾，将所有在路上行走的车的是否安排过都要置true或者false
     */
    public void setNowInRoadCarState(Boolean flag) {
        Iterator<CarModel> it = nowInRoadCar.iterator();
        while (it.hasNext()) {
            CarModel c = it.next();
            c.setHasArrangedOrNot(flag);
        }

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
            RoadModel road = functionService.findNextCross(car);
            if (road.getRoadID().equals("5007"))
                System.out.println();
            /**用于拥塞控制*/
            float normalizedRoadInsertLeftLength = roadService.getNormalizedRoadLeftLength(road, car.getCurFromCrossID());

            if (normalizedRoadInsertLeftLength >= 0.5) {
                //每次都要清除的
                reArrangeCars.clear();
                if (road != null) {
                    garageflag = functionService.checkIDPriority(car, road, reArrangeCars, t);
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
     * 将State3:firstCar所处的road上的车的状态更新 需要更新状态信息，不更新位置信息，
     * 如果firstCar能过路口，state=1,后面的车state=3;firstCar不能过路口，state=2,后面的车state=4
     *
     * @param firstCarID
     */
    private void UpdateRoadForCarsAtState3(String firstCarID) {
        CarModel firstCar = mapCar.get(firstCarID);
        // car行驶的路
        RoadModel carInRoad = mapRoad.get(firstCar.getRoadID());
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
        functionService.setCarInRoad(null, firstCar, carInRoad, state, false, false, false, false, false);
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
     * @param firstCarID
     * @param t
     * @author Tricia
     * @version 2019-04-14
     */
    private void UpdateCarsAtState1(String firstCarID, int t) {
        CarModel firstCar = mapCar.get(firstCarID); // firstCar实例
        RoadModel carInRoad = mapRoad.get(firstCar.getRoadID()); // firstCar所在的Road
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
            arrivedCar.add(firstCarID); // 放进ArrivalCars集合
            nowInRoadCar.remove(mapCar.get(firstCarID));// 把它从路上的车集合里面删掉

            laneInvlovesCar.carsInLane.removeFirst(); // $$$$$$将到家的Car从lane上去掉$$$$$$

            //判断firstCar是不是这条路上的唯一一个
            String nextCarID = functionService.getFirstCarInRoad(carInRoad.getRoadID(), firstCar.getCurFromCrossID());
            if (nextCarID == null) {
                functionService.setCarInRoad(null, firstCar, carInRoad, 3, false, true, true, false, false);
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
            RoadModel nextRoad = mapRoad.get(nextRoadID);
            int firstCarSpeed = Math.min(firstCar.getMaxVelocity(), nextRoad.getMaxRoadVelocity()); // firstCar的行驶速度

            LinkedList<LaneModel> carInNextLanes = new LinkedList<LaneModel>(); // firstCar去往的路上有哪些lane
            if (firstCar.getCurToCrossID().equals(nextRoad.getFromCrossID())) {// 判断方向
                carInNextLanes = nextRoad.getForwardLane();
            } else {
                carInNextLanes = nextRoad.getBackwardLane(); // firstCar将要前往的路上有哪些lane
            }
            int carInNextLanesNum = carInNextLanes.size();
            ArrayList<Integer> lanesLeftLength = roadService.getLeftLanesLength(nextRoad, firstCar.getCurToCrossID());

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
                functionService.setCarInRoad(null, firstCar, carInRoad, 5, false, false, false, false, false);
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
                        if (functionService.getFirstCarInRoad(nextRoad.getRoadID(), firstCar.getCurToCrossID()) != null) {
                            preRoadCarID = functionService.getFirstCarInRoad(nextRoad.getRoadID(), firstCar.getCurToCrossID()); //有false车
                        } else { //只有true车
                            preRoadCarID = functionService.getFirstTrueCarInRoad(nextRoad.getRoadID(), firstCar.getCurToCrossID()); //方法参数  road 当前道路, @param crossID 车从哪个路口到这个路;
                        }

                        CarModel preRoadCar = mapCar.get(preRoadCarID); //我们取出的是整个路上的第一个car，可能是true也可能是false
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
                String nextCarID = functionService.getFirstCarInRoad(carInRoad.getRoadID(), firstCar.getCurFromCrossID());
                if (nextCarID == null) {
                    functionService.setCarInRoad(null, firstCar, carInRoad, 3, false, true, true, false, false);
                }

                // ------统一更新信息
                carInNextLanes.get(firstCar.getLaneID()).carsInLane.add(firstCar); // $$$$$把car加到这个lane上$$$$$

                firstCar.setHasArrangedOrNot(true); // 更新标志位
                firstCar.setRoadID(nextRoadID);
                firstCar.setCurFromCrossID(firstCar.getCurToCrossID());
                firstCar.setCurToCrossID(roadService.getCross(nextRoad, firstCar.getCurFromCrossID()));

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
     * @param firstCarID
     */
    private void UpdateRoadForCarsAtState2(String firstCarID) {
        CarModel firstCar = mapCar.get(firstCarID);

        if (!firstCar.getNextRoadID().equals("-1")) {
            UpdateRoadForCarsAtState2Super(firstCarID); // 其实找到路了，只是道路限速过不去
        } else {
            UpdateRoadForCarsAtState2Nomal(firstCarID); // 本来就没能变成等待车
        }
    }

    /**
     * firstCar State2 nextRoadID!=-1 是去不了的1变来的 将firstCar所处的Road上的车从t2时刻更新到t3时刻
     * 更新State、curPos、sheng、hasArrangedOrNot 找出road上所有lane上的“firstCar”,更新每一个lane
     * @param firstCarID
     */
    private void UpdateRoadForCarsAtState2Super(String firstCarID) {
        CarModel firstCar = mapCar.get(firstCarID);
        RoadModel road = mapRoad.get(firstCar.getRoadID());
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
        if (functionService.getFirstCarInRoad(road.getRoadID(), firstCar.getCurFromCrossID()) != null) {
            newFirstCarID = functionService.getFirstCarInRoad(road.getRoadID(), firstCar.getCurFromCrossID());
        } else {
            //如果没有false车，那就都是true车了？
            newFirstCarID = functionService.getFirstTrueCarInRoad(road.getRoadID(), firstCar.getCurFromCrossID());
        }
        CarModel newFirstCar = mapCar.get(newFirstCarID);

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
     * @param firstCarID
     */
    private void UpdateRoadForCarsAtState2Nomal(String firstCarID) {
        // Note:将firstCar所处的Road上的车从t2时刻更新到t3时刻
        // 找出road上所有lane上的“firstCar”,更新每一个lane
        // firstCar的t3时刻一定是不能过路口的；
        // 但t4时刻的状态是不一定的，如果能够过路口设为1，后面的车都是3；不能过路口的话，设为2，后面都是4
        CarModel firstCar = mapCar.get(firstCarID);
        RoadModel road = mapRoad.get(firstCar.getRoadID());
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
        if (functionService.getFirstCarInRoad(road.getRoadID(), firstCar.getCurFromCrossID()) != null) {
            newFirstCarID = functionService.getFirstCarInRoad(road.getRoadID(), firstCar.getCurFromCrossID());
        } else {
            //如果没有false车，那就都是true车了？
            newFirstCarID = functionService.getFirstTrueCarInRoad(road.getRoadID(), firstCar.getCurFromCrossID());
        }
        CarModel newFirstCar = mapCar.get(newFirstCarID);

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
     * @param firstCarID
     */
    private void UpdateLanePosForCarsAtState2(String firstCarID, LinkedList<LaneModel> carInLane) {
        // 更新curPos、sheng
        //
        // Note:将firstCar所处的lane上的车的位置从t2时刻更新到t3时刻
        // firstCar的t3时刻一定是不能过路口的；
        // 但t4时刻的状态是不一定的，如果能够过路口设为1，后面的车都是3；不能过路口的话，设为2，后面都是4

        // *************找出firstCar所在的lane
        CarModel firstCar = mapCar.get(firstCarID);
        RoadModel carInRoad = mapRoad.get(firstCar.getRoadID()); // car行驶的路

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
     * @param laneFirstCarID
     * @param newFirstCarID
     * @param followingState
     */
    private void UpdateLaneStateForCarsAtState2(String laneFirstCarID, String newFirstCarID, int followingState, LinkedList<LaneModel> carInLane) {
        // Note:将firstCar所处的lane上的车从t2时刻更新到t3时刻
        // firstCar的curPos已经更新到t3时刻！（该路上所有车的curpos都已经更新到t3时刻了）

        // *************找出firstCar所在的lane
        CarModel firstCar = mapCar.get(laneFirstCarID);
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
     * 传入的车可能暂时找不到路，此时设置该车的状态为5，后车的状态也都设置为5，且该车不参与排序
     * 如果所找的路已经更新完也不能通行，则设置为5true，该车不动，后车（如果本来能过路口则不动，本来不能过路口则向前跳），设置为4true，但实际设置为5，按4true来跳
     * 要到目的地的车优先级设为3，此车参与排序
     * 如果车本来有nextRoadID则不找路，直接判断它变为上述的哪种状态
     * 如果所选道路上的行驶速度减去在本道路上可行距离小于等于0，则不能过路口，该车变为2false，后车变为4false
     * @param cars
     */
    private void sortCarsOfState1(ArrayList<CarModel> cars) {
        ArrayList<CarModel> carsList = new ArrayList<>();
        if (cars != null && cars.size() != 0) {
            for (int i = 0; i < cars.size(); i++) {
                CarModel car = cars.get(i);
                RoadModel r = mapRoad.get((car.getRoadID()));
                if (car.getCurToCrossID().equals(car.getToCrossID())) {
                    //要到家的车
                    car.setPriority(3);
                    //参与排序
                    carsList.add(car);
                } else if (!car.getNextRoadID().equals("-1")) {
                    //过路口但不许要重新找路
                    RoadModel toRoad = mapRoad.get(car.getNextRoadID());
                    //该路因为没更新完没空间，则变为5false，后车也是5false
                    if (roadService.hasLeftLength(toRoad, car.getCurToCrossID()) == -1) {
                        functionService.setCarInRoad(toRoad, car, r, 5, false, false, false, false, false);
                    }
                    //该路因为更新完没空间，则变为5true，后车往前跳，变4true,但实际设置为5，按4true来跳
                    else if (roadService.hasLeftLength(toRoad, car.getCurToCrossID()) == -2) {
                        functionService.setCarInRoad(toRoad, car, r, 5, true, true, true, false, false);
                    } else if (Math.min(car.getMaxVelocity(), toRoad.getMaxRoadVelocity()) - car.getCurPos() <= 0) {
                        functionService.setCarInRoad(toRoad, car, r, 4, false, false, false, false, false);
                        car.setState(2);
                    } else if (Math.min(car.getMaxVelocity(), toRoad.getMaxRoadVelocity()) - car.getCurPos() > 0) {
                        carsList.add(car);
                    }
                } else {//还要继续过路口行驶并需要找路的车
                    RoadModel toRoad = functionService.findNextCross(car);
                    if (toRoad == null) {
                        //找不到路则该车状态变为5，未安排的后车状态都变为5
                        System.out.println("我死在这里了");
                    }
                    //找到的路因为没更新完没空间，则变为5false，后车也是5false
                    else if (roadService.hasLeftLength(toRoad, car.getCurToCrossID()) == -1) {
                        functionService.setCarInRoad(toRoad, car, r, 5, false, false, false, false, false);
                    }
                    //找到的路因为更新完没空间，则变为5true，后车往前跳，变4true，,但实际设置为5，按4true来跳
                    else if (roadService.hasLeftLength(toRoad, car.getCurToCrossID()) == -2) {
                        functionService.setCarInRoad(toRoad, car, r, 5, true, true, true, false, false);
                        car.setPriority(crossService.setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
                        car.setNextRoadID(toRoad.getRoadID());
                    } else if (Math.min(car.getMaxVelocity(), toRoad.getMaxRoadVelocity()) - car.getCurPos() <= 0) {
                        functionService.setCarInRoad(toRoad, car, r, 4, false, false, false, false, false);
                        car.setState(2);
                        car.setNextRoadID(toRoad.getRoadID());
                        car.setPriority(crossService.setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
                    } else {
                        //找到路的车，参与排序
                        car.setPriority(crossService.setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
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
     */
    private void updateCarsAtState5(String firstCarID) {
        CarModel firstCar = mapCar.get(firstCarID);
        RoadModel curRoad = mapRoad.get(firstCar.getRoadID());
        if (firstCar.getCurPos() < Math.min(firstCar.getMaxVelocity(), curRoad.getMaxRoadVelocity())) {
            updateLaneForCarsAtState5(firstCarID);
        } else {
            functionService.setCarInRoad(null, firstCar, curRoad, 4, false, false, false, false, false);
            firstCar.setState(2);
        }
    }

    /**
     * 如果他要到家，他就变回1false了，后面车变成3false；
     * 如果它发现有路可以走了，他就变回1false了，后面车变成3false；
     * 更新完没空间，他就变成true5,后面的车更新状态变成4true,但实际设置为5，按4true来跳后面车（如果本来能过路口则不动，本来不能过路口则向前跳）;
     * 不然他还是flase5
     * @param firstCarID:状态为5false的车，排在carInRoad的头头
     */
    private void updateLaneForCarsAtState5(String firstCarID) {
        CarModel firstCar = mapCar.get(firstCarID);
        RoadModel curRoad = mapRoad.get(firstCar.getRoadID());
        RoadModel road;
        /**
         * 首先判断是不是到家车，如果是直接变成1false, 不是就按照正常逻辑
         */
        if (firstCar.getCurToCrossID().equals(firstCar.getToCrossID())) {
            functionService.setCarInRoad(null, firstCar, curRoad, 3, false, false, false, false, false);
            firstCar.setState(1);
            return;
        }
        road = functionService.findNextCross(firstCar);
        if (roadService.hasLeftLength(road, firstCar.getCurToCrossID()) > 0) {
            // 可供选择的路都有空间可走，该车变为1，后车变为3
            functionService.setCarInRoad(road, firstCar, curRoad, 3, false, false, false, false, false);
            firstCar.setState(1);
            firstCar.setNextRoadID(road.getRoadID());
            firstCar.setPriority(crossService.setPriority(firstCar.getRoadID(), firstCar.getNextRoadID(), firstCar.getCurToCrossID()));
        }
        // 更新完也没空间,则变为5true，后车往前跳，变4true,但实际设置为5，按4true来跳
        else if (roadService.hasLeftLength(road, firstCar.getCurToCrossID()) == -2) {
            functionService.setCarInRoad(road, firstCar, curRoad, 5, true, true, true, false, false);
        } else if (roadService.hasLeftLength(road, firstCar.getCurToCrossID()) == -1) {
            numOf5++;
        }
        // 因为未更新而没有空间，该车状态不变
    }


}

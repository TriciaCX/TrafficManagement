package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import core.Main;
import info.CrossInfo;
import info.RoadInfo;
import vo.Car;
import vo.Cross;
import vo.Lane;
import vo.Road;

public class RunUtil2
{

	/**
	 * 更新从车库出发的车的信息，判断车是否能够插入到规划的道路中，如果能，查看ID优先级是否冲突，有冲突则看
	 * 是否有回退的车（存入reArrangeCars中），如果不能，直接加到存入reArrangeCars中
	 * @param car：当前车；road：要走的道路；virtualCarsHashMap：存好的车的原始状态；reArrangeCars：回退车辆集合，就是可能会有ID小但是速度快的车被先从车库里面取出来，在同一时刻，起始路口相同，要选择的道路也是一条，这时候就要回退了；MapRoad：用来返回对象的
	 * @return 这里返回两个元素，false:第一个插不进去，第二个：有没有回滚？没有
	 * @version 2019-3-28
	 */
	protected static boolean[] checkIDPriority(Car car, Road road, LinkedList<Car> reArrangeCars,
			HashMap<String, Road> MapRoad, int t)
	{

		boolean flag1 = true;// 可以插进去
		boolean flag2 = true;// 没有回滚

		// 更新一些信息
		car.setCurFromCrossID(car.getFromCrossID());
		car.setPriority(0);

		// 首先设定这辆车的getCurToCross()是从自己的始发路口出发的要开上这个道路
		if (road.getFromCrossID().equals(car.getCurFromCrossID()))
			car.setCurToCrossID(road.getToCrossID());
		else
			car.setCurToCrossID(road.getFromCrossID());

		// 得到了这个车要插入的道路的lane们
		LinkedList<Lane> myLanes;// 首先要判断方向
		if (!car.getCurToCrossID().equals(road.getFromCrossID()))
		{
			myLanes = road.getForwardLane();
		}

		else
		{
			myLanes = road.getBackwardLane();
		}
		int size = myLanes.size();
		int i = 0;
		for (i = size; i > 0; i--)
		{// 先拿出来的在lane4上的一系列车
			LinkedList<Car> cars = myLanes.get(i - 1).carsInLane;
			// 找到要插入的位置了//priority还是要设置为0
			if (cars.size() > 0 && cars.getLast().getPriority() == car.getPriority()
					&& Integer.valueOf(cars.getLast().getCarID()) > Integer.valueOf(car.getCarID()))

				while (cars.size() > 0 && cars.getLast().getPriority() == car.getPriority()// 这个车也是刚从车库v提出来的而且，这个车的ID比现在安排的车大
						&& Integer.valueOf(cars.getLast().getCarID()) > Integer.valueOf(car.getCarID()))
				{// 那这个车要回退的
					// 先把这些要退回的车辆的信息更新一下
					// 提取这辆车
					Car virtualCar = Main.MapCar.get(cars.getLast().getCarID());
			
					// 更新mapCar里它原来的信息
					innitial(virtualCar);
					// 删除真实网络的信息
					cars.removeLast();
					// 把这些车辆返回
					reArrangeCars.addLast(virtualCar);
					flag2 = false;// 回滚了
				}


		} 

	
		// 插进去这个车，并且更新相关信息
		flag1 = carIDInsertToRoad(car, road, MapRoad, t);
		if (!flag1)// 如果这个车插不进去的,也放到重新安排的车的集合里,外面会再安排一次,再不行就放到garageFrozon里
		// 因为是false,所以没有在实际网络添加过它，所以只要返回原来的就好了
		{
			Car carVir = Main.MapCar.get(car.getCarID());
			innitial(carVir);
			reArrangeCars.add(car);
		}

		// 返回被退回的车或者可能是本身这辆车,再插一次！
		while (!reArrangeCars.isEmpty())
		{
			Car c = reArrangeCars.getLast();
			if (c != null)
			{
				if (reArrangeCarsIDInsertToRoad(c, road, MapRoad, t))
					reArrangeCars.removeLast();
				else
					break;// 插不进去了
			} else
				break;// 取出来是null就说明应该时没有了
		}
		// 两个元素，false:第一个插不进去，第二个：没有回滚
		boolean[] flags = new boolean[2];
		flags[0] = flag1;
		flags[1] = flag2;
		return flags;

	}

	/**
	 * 已经判断这辆从车库来的车想要到这个路，该怎么选择车道呢？还要更新相关信息哦，比如说准备回滚，要存一个影分身之类的，前面的道路上已经没有优先级比它差的车了
	 * 
	 * @param car：当前车；road：要走的道路；virtualCarsHashMap；MapRoad：用来返回对象的；
	 * @return 这里返回一个元素，false:插不进去
	 * @version 2019-3-28
	 */
	protected static boolean carIDInsertToRoad(Car car, Road road, HashMap<String, Road> MapRoad, int t)
	{
		// LanesCarsList[0]=3,表示lane1的剩余可进入长度为3，还能进3辆车
		boolean flag = InsertFreshCarToRoad(car, road, MapRoad, t);

		return flag;// false:插不进去
	}

	/**
	 * 这辆从车库出来的车能不能到这个路？调用这个函数的时候，前面是不可能出现回滚的，因为已经滚过了！能的话就插进去！
	 * 
	 * @param car：当前车；road：要走的道路；virtualCarsHashMap：当前时刻的影分身们！MapRoad：用来返回对象的；reArrangeCars：回退车辆集合，其实就是之前村的初始影分身；
	 * @return 这里返回一个元素，false:插不进去
	 * @version 2019-3-26
	 */
	protected static boolean InsertFreshCarToRoad(Car c, Road road, HashMap<String, Road> MapRoad, int t)
	{
		ArrayList<Integer> LeftLanesLengthList = RoadInfo.getLeftLanesLength(road, c.getCurFromCrossID());
		int size = road.getLanesNum();
		int i = 0;
		boolean flag1 = true;// true:可以插入；false:插不进去
		if (LeftLanesLengthList.size() == 0)
		{
			flag1 = false;
			return flag1;// false:插不进去

		} else
		{
			while (i < size && LeftLanesLengthList.get(i) == 0)
			{
				i++;
			}
			if (i == size)
			{
				flag1 = false;
				innitial(c);
				return flag1;// false:插不进去

			}

		}

		// 要对真实网络进行改变！
		Car car = Main.MapCar.get(c.getCarID());

		// 可以插进去！
		int nextLaneOfRoadLeftSize = LeftLanesLengthList.get(i);// 要插入的lane可以插入的空间

		// 以这个道路的最大速度以及车辆自己的最大速度
		int nextLaneVel = Math.min(road.getMaxRoadVelocity(), car.getMaxVelocity());// 在这个lane的最大速度
	
		if (car.getRealStartTime() == -1)
			car.setRealStartTime(t);
		//找到下一个去往的路口
		
		if (car.getFromCrossID().equals(road.getFromCrossID()))
			car.setCurToCrossID(road.getToCrossID());
		else if (road.isDuplex())
		{
			car.setCurToCrossID(road.getFromCrossID());

		} else
		{
			System.out.println("没有下一个路口了？奇奇怪怪的");
		}

		//简单的信息更新

		car.setCurPos(road.getRoadLength() - Math.min(nextLaneOfRoadLeftSize, nextLaneVel));// 这个可以根据page10得到了
		car.setLaneID(i);
		car.setRoadID(road.getRoadID());

		car.setSheng(0);

		// 通过路的对象和laneID找到Lane的对象thisLane
		Lane thisLane = new Lane();
		LinkedList<Lane> l;
		if (road.getFromCrossID().equals(car.getCurFromCrossID()))
			l = road.getForwardLane();
		else
			l = road.getBackwardLane();

		for (Lane l1 : l)
		{
			if (l1.getLaneIndex() == car.getLaneID())
			{
				thisLane = l1;
				break;
			}
		}
		// 把这辆车的实例加到后来的lane
		thisLane.carsInLane.add(car);
		// 前面没有车,不一定是一个lane哦

		// preCar是这个路上的之字划开的前面的第一辆车，可能是刚插进去的那辆车，可能不是
		// 这里不用判断是不是为空，因为我刚插进去一辆车呢，而且car.setHasArrangedOrNot是false，是会被读到的
		Car preCar = Main.MapCar.get(RunUtil2.getCarInRoad(road, car.getCurFromCrossID()).getFirst());
		if (preCar.equals(car))
		{
			//
			if (car.getCurPos() - nextLaneVel < 0)
				car.setState(1);// 下一时刻是可以出去的车
			else
				car.setState(2);// 下一时刻出不去
		} else
		{
			int preState = preCar.getState();
			if (preState == 1)
				car.setState(3);
			else if (preState == 2)
				car.setState(4);
			else if (preState == 3)
				car.setState(3);
			else if (preState == 4)
				car.setState(4);
			else// 前车是5
				car.setState(5);
		}

		// 准备返回
		return flag1;// false:插不进去

	}

	/**
	 * 被退回来的从车库出发的车看看还能不能按照原来的想法插入到这个道路
	 * @param car：被退回来的车；road：本来要走的道路；
	 * @return 有没有成功插入
	 * @version 2019-3-26
	 */
	protected static boolean reArrangeCarsIDInsertToRoad(Car car, Road road, HashMap<String, Road> MapRoad, int t)
	{
		boolean flag = InsertFreshCarToRoad(car, road, MapRoad, t);
		return flag;// false:插不进去

	}

	/**
	 * 对四个车进行一系列操作， *@param carsFour最多只有四个车
	 * @return
	 * @version 2019-3-26
	 */
	public static void FourCarStatePreProcess(LinkedList<Car> carsFour)
	{
		for (Car c : carsFour)
		{
			switch (c.getState())
			{
			case (3):
			{
				// 判断一下了,他要么变成1了要么变成2了,后面的没有安排过的车的状态也要跟着更新一波
				UpdateRoadForCarsAtState3(c.getCarID());
				break;

			}
			case (4):
			{
				// 那这个车前面本来是第二类车,那他肯定被带领着往前跳过了,不用管了，他肯定是更新过了的车了
				break;
			}
			case (5):
			{
				/// 如果它发现有路可以走了，他就变回1(false)了，如果三条路都打死走不出去，他就变成5(true),后面的车更新状态5(false)
				// 不然他还是5(false)
				if (c.getCarID().equals(Main.problemCar))
					System.out.println(Main.problemCar + "coming!");
				updateLaneForCarsAtState5(c.getCarID());
				break;

			}
			case (1):
			{
				// 前方没阻挡,而且出路口,这个时候碰到这个车肯定就是刚开始的时候,它上一时刻被安排从车库出来了
				break;
			}
			case (2):
			{
				// 前方没阻挡,不会出路口,同上
				break;
			}
			default:
				System.out.println("出问题啦！！！" + "\t");
			}

		}

	}

	/**
	 * 对四个车进行一系列操作， *@param carsFour最多只有四个车，而且只有两种状态，要么就是1要么就是2.
	 * @version 2019-3-26
	 */
	public static void FourCarStateProcess(LinkedList<Car> carsFour, int t)
	{
		ArrayList<Car> cars1 = new ArrayList<>();
		ArrayList<Car> cars2 = new ArrayList<>();
		for (Car c : carsFour)
		{
			if (c.getCarID().equals(Main.problemCar))
				System.out.println(Main.problemCar + "coming!");
			if (c.getState() == 1 && !c.isHasArrangedOrNot())
			{
				if (c.getCarID().equals(Main.problemCar))
					System.out.println(Main.problemCar + "coming!");
				cars1.add(c);
			}

		}
		UpdateLaneForCarsAtState1(cars1, t);
		// 可能有一个新的2false出来的
		for (Car c : carsFour)
		{
			if (c.getCarID().equals(Main.problemCar))
				System.out.println(Main.problemCar + "coming!");
			if (c.getState() == 2 && !c.isHasArrangedOrNot())
			{
				if (c.getCarID().equals(Main.problemCar))
					System.out.println(Main.problemCar + "coming!");
				cars2.add(c);
			}

		}
		UpdateLaneForCarsAtState2(cars2);

	}

	/**
	 * 对这个路口取出这时候安排的那4辆车 *@param carsFour最多只有四个车
	 * @return
	 * @version 2019-3-26
	 */
	public static LinkedList<Car> extractFourCar(Cross s)
	{
		LinkedList<Car> carsFour = new LinkedList<>();
		if (!s.getDownRoad().getRoadID().equals("-1"))
		{
			String carID = RunUtil2.getFirstCarInRoad(s.getDownRoad(), s.getCrossID());
			if (carID != null)
				carsFour.add(Main.MapCar.get(carID));
		}
		if (!s.getUpRoad().getRoadID().equals("-1"))
		{
			String carID = RunUtil2.getFirstCarInRoad(s.getUpRoad(), s.getCrossID());
			if (carID != null)
				carsFour.add(Main.MapCar.get(carID));
		}
		if (!s.getLeftRoad().getRoadID().equals("-1"))
		{
			String carID = RunUtil2.getFirstCarInRoad(s.getLeftRoad(), s.getCrossID());
			if (carID != null)
				carsFour.add(Main.MapCar.get(carID));
		}
		if (!s.getRightRoad().getRoadID().equals("-1"))
		{
			String carID = RunUtil2.getFirstCarInRoad(s.getRightRoad(), s.getCrossID());
			if (carID != null)
				carsFour.add(Main.MapCar.get(carID));
		}

		return carsFour;

	}

	/**
	 * 根据一条路上,车是否出路口，lane的顺序和lane上车辆距离目的路口的距离，构建车的链表，不考虑hasArrangeOrNot为true的车
	 * （首先考虑是否出路口，然后考虑距离，最后考虑车道顺序）
	 * @param road：当前道路;crossID:车从哪个路口到这个路;
	 * @return 获得当前道路和车辆行驶方向相同的所有lane上车辆的发车顺序链表。getfirst是先出发的车（头头）
	 * @version 2019.3.28
	 */
	public static LinkedList<String> getCarInRoad(Road road, String crossID)
	{
		LinkedList<Lane> laneList;
		LinkedList<String> out = new LinkedList<>();
		// 找到和车辆方向一致的车道集合
		if (road.isDuplex())
			laneList = road.getFromCrossID().equals(crossID) ? road.getForwardLane() : road.getBackwardLane();
		else
			laneList = road.getForwardLane();
		int laneNum = laneList.size();
		Car[] carList = new Car[laneNum];// 每次取排在最前面的几个车道的车进行比较,下标对应所在车道，没车的车道或遍历完车的车道放null
		int[] carIndex = new int[laneNum];// 每个车道取到第几辆车，下标对应所在车道，没车的车道或者已经遍历完的车道放置-1
		for (int i = 0; i < laneNum; i++)
		{// 初始化,放头几辆车
			carIndex[i] = 0;
			Lane lane = laneList.get(i);
			if (!lane.carsInLane.isEmpty())
			{// 该车道有车
				// 跳过已安排的车辆
				while (carIndex[i] < lane.carsInLane.size() && lane.carsInLane.get(carIndex[i]).isHasArrangedOrNot())
				{
					carIndex[i]++;
				}
				// 该条车道都是已安排的车
				if (carIndex[i] == lane.carsInLane.size())
				{
					carList[i] = null;
					carIndex[i] = -1;
				}
				// 还有未安排的车
				else
					carList[i] = lane.carsInLane.get(carIndex[i]);
			} else
			{// 该车道无车
				carList[i] = null;
				carIndex[i] = -1;
			}
		}
		int laneIndex;
		int t = 0, tt = 0;
		Car[] throughCar = ThroughCar(road, carList, laneList, carIndex);// 选出出路口的车
		while (true)
		{
			t = 0;
			tt = 0;
			for (int i = 0; i < throughCar.length; i++)
			{
				if (throughCar[i] == null)
					tt++;
			}
			if (tt < throughCar.length)
			{// 先按距离和车道号给出路口的车排序
				for (int i = 0; i < laneNum; i++)
				{
					if (carIndex[i] < 0)
						t++;
				}
				if (t == laneNum)
					break;// 遍历完所有车，所有车道的carIndex为-1，退出循环
				Car car = minCarCurPos(throughCar);// 取出当前carList中curPos最小的车
				laneIndex = car.getLaneID();
				out.add(car.getCarID());
				// 不考虑已经安排的车
				while ((++carIndex[laneIndex]) < laneList.get(laneIndex).carsInLane.size()
						&& laneList.get(laneIndex).carsInLane.get(carIndex[laneIndex]).isHasArrangedOrNot())
					;
				// 当前车道已经遍历完车了
				if (carIndex[laneIndex] >= laneList.get(laneIndex).carsInLane.size())
				{
					carIndex[laneIndex] = -1;
					carList[laneIndex] = null;
					throughCar[laneIndex] = null;
				} else
				{// 将该车道对应的比较集合carList位置的car指向该车道的下一辆车
					Car c = laneList.get(laneIndex).carsInLane.get(carIndex[laneIndex]);
					canThrough(road, c, laneList, carIndex);
					if (c.isCanThrough())
						throughCar[laneIndex] = c;
					else
					{
						carList[laneIndex] = c;
						throughCar[laneIndex] = null;
					}
					// throughCar = ThroughCar(road, carList, laneList, carIndex);
				}
			} else
			{// 处理完出路口的车之后，为不出路口的车按距离和车道号排序
				t = 0;
				for (int i = 0; i < laneNum; i++)
				{
					if (carIndex[i] < 0)
						t++;
				}
				if (t == laneNum)
					break;// 遍历完所有车，所有车道的carIndex为-1，退出循环
				Car car = minCarCurPos(carList);// 取出当前carList中curPos最小的车
				laneIndex = car.getLaneID();
				out.add(car.getCarID());
				// 不考虑已经安排的车
				while ((++carIndex[laneIndex]) < laneList.get(laneIndex).carsInLane.size()
						&& laneList.get(laneIndex).carsInLane.get(carIndex[laneIndex]).isHasArrangedOrNot())
					;
				// 当前车道已经遍历完车了
				if (carIndex[laneIndex] >= laneList.get(laneIndex).carsInLane.size())
				{
					carIndex[laneIndex] = -1;
					carList[laneIndex] = null;
				} else
				{// 将该车道对应的比较集合carList位置的car指向该车道的下一辆车
					carList[laneIndex] = laneList.get(laneIndex).carsInLane.get(carIndex[laneIndex]);
				}
			}
		}
		return out;
	}

	/**
	 * 找出车辆的CurPos最小的车辆
	 * @param carList车辆集合
	 * @return CurPos最小的车辆
	 * @version 2019.3.26
	 */
	protected static Car minCarCurPos(Car[] carList)
	{
		Car minCar = carList[0];
		int j = 0;
		for (; j < carList.length; j++)
		{// 先取一个不为null的车作为CurPos的最小车
			if (carList[j] != null)
			{
				minCar = carList[j];
				break;
			}
		}
		for (int i = j + 1; i < carList.length; i++)
		{// 比较得到curPos最小车
			if (carList[i] == null)
				continue;
			if (carList[i].getCurPos() < minCar.getCurPos())
			{
				minCar = carList[i];
			}
		}
		return minCar;
	}

	/**
	 * 对车遍历，是不是都是真实的位置了
	 * @param hasArrag=true,sheng=o
	 * @return
	 * @version 2019-3-26
	 */
	public static boolean isAllReal()
	{
		if (Main.NowInRoadCar.isEmpty())
			return true;

		Iterator<Car> carIt = Main.NowInRoadCar.iterator();
		boolean ans = true;
		while (carIt.hasNext())
		{
			Car c = carIt.next();
			if (c.getSheng() != 0 || !c.isHasArrangedOrNot())
			{
				if (c.getRoadID().equals("-1"))
					System.out.println();
				ans = false;

				break;

			}

		}
		return ans;
	}

	/**
	 * 对车遍历，是不是都是已经到达终点了
	 * @version 2019-3-26
	 */
	public static boolean isAllArrived()
	{
		Iterator<String> carIt = Main.ArrivedCar.iterator();
		int sum = 0;

		while (carIt.hasNext())
		{
			carIt.next();
			sum++;

		}
		return sum == Main.listCar.size();
	}

	/**
	 * 查询当前道路是否是真正有可走的空间，因为需要先走车道号小的车道。 小车道没空间，但车没有更新过直接判为没路； 没更新有空间则有路；
	 * 更新过没空间继续查找大的lane； 更新过有空间为有路；
	 * @param road:当前搜索的道路;crossID:出发节点ID
	 * @return -2:所有lane都更新过了,也没有空间；-1：未更新过而没有空间；1：未更新过而有空间；2：更新过而有空间；
	 * @version 2019.3.27
	 */
	protected static int hasLeftLength(Road road, String CrossID)
	{
		LinkedList<Lane> laneList;
		// 找到和车辆方向一致的车道集合
		if (road.isDuplex())
			laneList = road.getFromCrossID().equals(CrossID) ? road.getForwardLane() : road.getBackwardLane();
		else
			laneList = road.getForwardLane();
		List<Integer> leftLength = RoadInfo.getLeftLanesLength(road, CrossID);
		int i = 0;
		int laneNum = leftLength.size();
		for (; i < laneNum; i++)
		{
			Lane lane = laneList.get(i);
			LinkedList<Car> carsInLane = lane.carsInLane;
			if (!carsInLane.isEmpty())
			{
				boolean hasArranged = carsInLane.getLast().isHasArrangedOrNot();
				if (hasArranged)
				{
					if (leftLength.get(i) > 0)
					{
						return 2;// 更新过而有空间
					} else
						continue;
				} else
				{
					if (leftLength.get(i) > 0)
					{
						return 1;// 未更新过而有空间
					} else
					{
						return -1;// 未更新过而没有空间
					}
				}
			} else
			{
				return 3;
			}
		}
		return -2;// 所有lane都更新过了,也没有空间
	}

	/**
	 * 查询当前搜索的道路和车的方向是否冲突
	 * @param road:当前搜索道路；crossID:出发节点ID
	 * @return true：方向一致；false：方向相反
	 * @version 2019.3.22
	 */
	protected static boolean isDirectionRight(Road road, String crossID)
	{
		if (!road.isDuplex())
		{
			if (road.getFromCrossID().equals(crossID))
				return true;
			else
				return false;
		} else
			return true;
	}

	/**
	 * 从车库中止中取出可以出发的车放入garageWait
	 * @param cars：车库中止车辆集合cars，t：当前时间（t-t+1）
	 * @version 2019.3.26
	 */
	protected static void classifyCars(LinkedList<Car> cars, int t)
	{

		LinkedList<Car> garageWait = Main.garageWait;

		garageWait.clear();

		for (int i = cars.size() - 1; i >= 0; i--)
		{
			Car car = cars.get(i);
			car.setHasArrangedOrNot(false);
			if (car.getPlanTime() <= t)
			{
				garageWait.add(car);// 车辆计划出发时间大于当前时间，车库中止
				cars.remove(i);
			}
		}

		Collections.sort(garageWait, new Comparator<Car>() {// 按车辆速度升序排列
			@Override
			public int compare(Car o1, Car o2)
			{
				return o1.getMaxVelocity() - o2.getMaxVelocity();
			}
		});
	}

	/**
	 * 对当前车辆规划下一道路 
	 * @param car：当前车辆（两个等待状态）
	 * @return 下一条道路
	 * @version 2019.3.28 保证第一条路可走（考虑rest），从car.getCurToCross开始找路径
	 * 可以进一步优化：此时第一条路不能走不一定真的在该时刻不能走，需要安排完一些车后可能会有空间
	 */
	protected static Road findNextCross(Car car, int maxRoadLength)
	{
		// 车所在路的通向路口是终点，则返回这条路ID
		if (car.getCurToCrossID().equals(car.getToCrossID()))
			return Main.MapRoad.get(car.getRoadID());
		List<Cross> unknown = new ArrayList<Cross>();// 未知节点集合
		Cross s = Main.MapCross.get(car.getCurToCrossID());// 当前出发节点路口，可能为null
		Cross t = Main.MapCross.get(car.getToCrossID());// 目的地
		Iterator<Cross> crossIter = Main.listCross.iterator();
		while (crossIter.hasNext())
		{
			Cross cross = crossIter.next();
			if (s.getCrossID().equals(cross.getCrossID()))
			{// 初始化出发节点,我重写了equals
				cross.cost = 0;// 到达当前节点时间
				cross.isKnown = true;
				cross.preCross = null;
			} else
			{// 初始化其余节点
				cross.cost = Float.MAX_VALUE;
				cross.isKnown = false;
				cross.preCross = null;
				unknown.add(cross);
			}
		}
		// 表示从出发节点来标记邻接节点，此时要保证搜索可行路径
		ArrayList<Road> roads = new ArrayList<>();
		for (String roadID : s.getRoadIDList())
		{
			if (!roadID.equals("-1"))
				roads.add(Main.MapRoad.get(roadID));
		}
		Road preRoad = findRoad(car.getCurFromCrossID(), car.getCurToCrossID());// 找到车过来的路,初始情况返回null
		boolean flag = deleteCrossFromUnknown(unknown, car, roads, preRoad);// 不标记第一条可选路中不能走的路
		if (!flag)
			return null;// 第一条路无路可走
		while (!t.isKnown)
		{
			Collections.sort(unknown, new Comparator<Cross>() {
				@Override
				public int compare(Cross o1, Cross o2)
				{
					if (o2.cost > o1.cost)
						return 1;
					else if (o2.cost < o1.cost)
						return -1;
					else
						return 0;
				}
			});
			if (unknown.isEmpty())
				System.out.println("找不到目标路口");
			Cross v = unknown.get(unknown.size() - 1);// 找到当前从源节点出发，代价最小的节点
			v.isKnown = true;
			unknown.remove(unknown.size() - 1);
			ArrayList<Road> roadsList = new ArrayList<>();
			for (String roadID : v.getRoadIDList())
			{
				Road road = Main.MapRoad.get(roadID);
				if (!roadID.equals("-1"))
					roadsList.add(road);
			}
			for (Road road : roadsList)
			{// 从该节点出发标记其相邻节点
				if (road != null && isDirectionRight(road, v.getCrossID()))
				{
					markNextCross(road, v);
				}
			}
		}
		return findFirstRoad(s.getCrossID(), t.getCrossID());
	}

	/**
	 * 从unknown中标记第一跳可行节点 ，不标记没有的路，方向不对的路，过来的路 对于没空间的路，都标记cost，不管是更新过没空间还是没更新过没空间
	 * @param unknown:搜索路径的未知节点集合；car：当前安排车辆；roads：当前搜索道路集合；preRoad：车当前所在道路
	 * @return true:有路可走（不一定当前可走，可能更新完后能走，也可能下个时间片才能走）
	 * @version 2019.3.28
	 */
	protected static boolean deleteCrossFromUnknown(List<Cross> unknown, Car car, List<Road> roads, Road preRoad)
	{
		int numOfAbleRoads = 0;// 可行道路数量
		Cross s;
		if (car.getCurToCrossID() == null)
			s = Main.MapCross.get(car.getFromCrossID());
		else
			s = Main.MapCross.get(car.getCurToCrossID());// 车辆出发节点
		int[] flag = new int[roads.size()];
		for (int i = 0; i < roads.size(); i++)
		{
			Road road = roads.get(i);// 当前搜索道路
			if (preRoad != null && (road.equals(preRoad)))
			{// 不走回头路
				flag[i] = -1;
			} else
			{
				if (road == null || road.getRoadID().equals("-1")) // 前面已经确定不会有“-1”
					flag[i] = -2;// 没有路标记为-2
				else if (!isDirectionRight(road, s.getCrossID()))
					flag[i] = -3;// 有路但方向不对标记为-3
				else if (hasLeftLength(road, s.getCrossID()) > 0)
				{
					numOfAbleRoads++;
					flag[i] = 1;// 有空间
				} else if (hasLeftLength(road, s.getCrossID()) == -1)
				{
					numOfAbleRoads++;
					flag[i] = 2;// 没更新过而没空间
				} else if (hasLeftLength(road, s.getCrossID()) == -2)
				{
					numOfAbleRoads++;
					flag[i] = 3;// 更新过而没空间
				} else
					flag[i] = 0;
			}
		}
		if (numOfAbleRoads == 0)
			return false;
		else
		{
			for (int i = 0; i < roads.size(); i++)
			{
				Road road = roads.get(i);
				if (flag[i] > 0)
					markNextCross(road, s);
				else if (flag[i] < 0)
					continue;
				else
					System.out.println("不知道啥路子");
			}
		}
		return true;
	}

	/**
	 * @param 找到从路口s到路口t的道
	 * @return Road实例
	 * @version 2019-3-22
	 */
	protected static Road findRoad(String crossSID, String crossTID)
	{
		if (crossSID.equals(crossTID))
			return null;
		Cross s = Main.MapCross.get(crossSID);
		Cross t = Main.MapCross.get(crossTID);
		String[] roadss = s.getRoadIDList();
		String[] roadst = t.getRoadIDList();
		HashSet<String> set = new HashSet<>();
		String roadID = new String();
		for (String ss : roadss)
		{
			set.add(ss);
		}
		for (String ss : roadst)
		{
			if (!ss.equals("-1") && !set.add(ss))// Dalton modified
				roadID = ss;
		}

		return Main.MapRoad.get(roadID);
	}

	protected static void markNextCross(Road road, Cross s)
	{
		Cross t = getCross(road, s.getCrossID());
		if (!t.isKnown)
		{
			// 自适应调整w2（和遇到前方）和w3（和回滚相关）
			float NormalizedRoadLength = RunUtil2.getNormalizedRoadLength(road, Main.maxRoadLength);// cost1
			float NormalizedRoadLeftLength = CrossInfo.getNormalizedRoadLeftLength(road, s.getCrossID());// cost2
			float sigmoidCrossCarNum = swish(CrossInfo.getCrossCarNum(s.getCrossID(), t.getCrossID()));// cost3
			float cost = Main.w[0] * NormalizedRoadLength + Main.w[1] * NormalizedRoadLeftLength
					+ Main.w[2] * sigmoidCrossCarNum;
			if (s.cost + cost < t.cost)
			{
				t.cost = s.cost + cost;
				t.preCross = s;
			}
		}
		adjustW(Main.w);
	}

	/**
	 * 根据道路road以及当前路口找到道路通向的下一路口
	 * @param road：当前道路；s:当前路口
	 * @return 通向的下一路口
	 * @version 2019.3.22
	 */
	protected static Cross getCross(Road road, String sID)
	{
		if (!road.isDuplex())
		{
			if (road.getFromCrossID().equals(sID))
				return Main.MapCross.get(road.getToCrossID());
			else
				return null;
		} else
			return road.getFromCrossID().equals(sID) ? Main.MapCross.get(road.getToCrossID())
					: Main.MapCross.get(road.getFromCrossID());
	}

	/**
	 * @param sID:当前出发路口ID
	 * @param tID：目的路口ID
	 * @return 从当前出发路口出发的第一条道
	 * @version 2019.3.22
	 */
	protected static Road findFirstRoad(String sID, String tID)
	{
		Cross s = Main.MapCross.get(sID);
		Cross t = Main.MapCross.get(tID);
		Cross temp = t;
		while (!temp.preCross.equals(s))
		{
			temp = temp.preCross;
		}
		return findRoad(sID, temp.getCrossID());
	}

	/**
	 * sigmoid函数
	 * @param x:自变量
	 * @version 2019.3.22
	 */
	protected static float sigmoid(int x)
	{
		return (float) (1 / (1 + Math.exp(-x)));

	}

	protected static float swish(int x)
	{
		return x * sigmoid(x);
	}

	protected static void adjustW(float[] w)
	{
		if (Main.numOf2 != 0)
		{
			w[2] = sigmoid(Main.numOf5 / Main.numOf2);
		} else
			w[2] = sigmoid(Main.numOf5 / (Main.numOf2 + 1));
		w[1] = (float) ((float) w[2] * 0.4);
		w[0] = 1 - w[1] - w[2];
	}

	/**
	 * cost1
	 * @param road
	 * @return NormalizedRoadLength
	 * @version 2019-3-21
	 */
	protected static float getNormalizedRoadLength(Road road, int maxRoadLength)
	{
		float NormalizedRoadLength = 0;

		NormalizedRoadLength = road.getRoadLength() / maxRoadLength; // Main.maxRoadLength最大路长度
		return NormalizedRoadLength;
	}

	/**
	 * 将State3:firstCar所处的road上的车的状态更新 需要更新状态信息，不更新位置信息
	 * 如果firstCar能过路口，state=1,后面的车state=3;firstCar不能过路口，state=2,后面的车state=4
	 * 
	 * @param firstCar
	 * @version 2019-3-27
	 */

	protected static void UpdateRoadForCarsAtState3(String firstCarID)
	{

		Car firstCar = Main.MapCar.get(firstCarID);
		Road carInRoad = Main.MapRoad.get(firstCar.getRoadID()); // car行驶的路
		String crossID = firstCar.getCurFromCrossID();
		int maxSpeed = Math.min(firstCar.getMaxVelocity(), carInRoad.getMaxRoadVelocity()); // firstCar此时的行驶速度
		// 判断firstCar是否过路口
		// ***********更新firstCar的状态
		// firstCar.setCurPos(firstCar.getCurPos()-maxSpeed);//State3的车不更新t3时刻的curPos
		if (firstCar.getCurPos() - maxSpeed < 0)
		{ // 如果能够过路口
			firstCar.setState(1);
		} else
		{ // 如果不能过路口
			firstCar.setState(2);
		}
		int firstCarState = firstCar.getState();
		// **********更新Road上其它车的状态（如果不止有一辆的话）
		LinkedList<String> carInRoadList = getCarInRoad(carInRoad, crossID); // carInRoadList里存储的是car的ID
		int carInRoadNum = carInRoadList.size();
		int state = 0;
		if (firstCar.getState() == 1)
		{
			state = 3;
		} else
		{
			state = 4;
		}

		// 优化为迭代器遍历
		if (carInRoadNum > 1)
		{// 如果不止有一辆车
			Iterator<String> it = carInRoadList.iterator();
			while (it.hasNext())
			{
				Main.MapCar.get(it.next()).setState(state);// 设置所有车的state
			}
			firstCar.setState(firstCarState); // 迭代器遍历的时候是无序的，需要重新设置firstCar的状态
		}
	}

	/**
	 * 将firstCar所处的lane上的车从t2时刻更新到t3时刻
	 * @param firstCar
	 * @version 2019.03.26
	 */

	protected static void UpdateLaneForCars(Car firstCar)
	{
		// firstCar的t3时刻一定是不能过路口的；
		// 但t4时刻的状态是不一定的，如果能够路口设为1，后面的车都是3；不能过路口的话，设为2，后面都是4

		// *************找出firstCar所在的lane
		Road carInRoad = Main.MapRoad.get(firstCar.getRoadID()); // car行驶的路
		LinkedList<Lane> carInLane = new LinkedList<Lane>();
		if (firstCar.getCurFromCrossID().equalsIgnoreCase(carInRoad.getFromCrossID()))
		{
			carInLane = carInRoad.getForwardLane();
		} else
		{
			carInLane = carInRoad.getBackwardLane(); // car行驶的路上有哪些lane
		}

		int carInLaneID = firstCar.getLaneID();
		Lane laneInvlovesCar = carInLane.get(carInLaneID);// fisrtCar所在的lane
		int maxSpeed = Math.min(firstCar.getMaxVelocity(), carInRoad.getMaxRoadVelocity());
		// ***********更新firstCar的状态
		firstCar.setCurPos(firstCar.getCurPos() - maxSpeed);// 更新t3时刻
		// 判断t4时刻的状态
		if (firstCar.getCurPos() - maxSpeed < 0)
		{ // 如果能够过路口
			firstCar.setState(1);
		} else
		{ // 如果不能过路口
			firstCar.setState(2);
		}

		// **********更新lane上其它车（如果不止有一辆的话）
		LinkedList<Car> carsInLane = laneInvlovesCar.carsInLane;
		int carInLaneNum = carsInLane.size();
		int state = 0;
		if (firstCar.getState() == 1)
		{
			state = 3;
		} else
		{
			state = 4;
		}
		if (carInLaneNum > 1)
		{
			for (int i = 1; i < carInLaneNum; i++)
			{
				// ------case1:没有安排过的车
				if (carsInLane.get(i).isHasArrangedOrNot() == false)
				{
					if (carsInLane.get(i).getCurPos() - maxSpeed <= carsInLane.get(i - 1).getCurPos())
					{// 能追上前车（前车已经更新到t3时刻了）
						carsInLane.get(i).setCurPos(carsInLane.get(i - 1).getCurPos() + 1);
					} else
					{// 如果不能追上前车，能跑多远跑多远
						carsInLane.get(i).setCurPos(carsInLane.get(i).getCurPos() - maxSpeed);
					}
				}
				// -----case2:安排过的车（从另一个路口过来的)
				else if (carsInLane.get(i).isHasArrangedOrNot() == true && carsInLane.get(i).getSheng() != 0)
				{
					if (carsInLane.get(i).getCurPos() - carsInLane.get(i - 1).getCurPos() > carsInLane.get(i)
							.getSheng())
					{ // 追不上前车
						carsInLane.get(i).setCurPos(carsInLane.get(i).getCurPos() - carsInLane.get(i).getSheng());
					} else
					{// 能追上
						carsInLane.get(i).setCurPos(carsInLane.get(i - 1).getCurPos() + 1);
					}
				}
				// -----case3:如果遇到一辆已经更新的了，那它后面肯定也已经更新了（保险起见，先注释这段）
				else
				{
					if (carsInLane.get(i).isHasArrangedOrNot() == true && carsInLane.get(i).getSheng() == 0)
					{
						break;
					}
				}
				// 统一更新属性
				carsInLane.get(i).setSheng(0);
				carsInLane.get(i).setState(state);
				carsInLane.get(i).setHasArrangedOrNot(true);
			}
		}

	}

	/**
	 * 将firstCar所处的lane上的车从t2时刻更新到t3时刻
	 * @param firstCar
	 * @version 2019.03.27
	 */

	protected static void UpdateLaneForCarsAtState1(ArrayList<Car> car1s, int t)
	{

		sortCarsOfState1(car1s);
		for (int i = 0; i < car1s.size(); i++)
			UpdateCarsAtState1(car1s.get(i).getCarID(), t);
	}

	/**
	 * 从t2时刻更新到t3时刻
	 * @param firstCarID
	 * @param t
	 * @version 2019-03-27
	 */

	public static void UpdateCarsAtState1(String firstCarID, int t)
	{
		Car firstCar = Main.MapCar.get(firstCarID); // firstCar实例
		Road carInRoad = Main.MapRoad.get(firstCar.getRoadID()); // firstCar所在的Road
		LinkedList<Lane> carInLanes = new LinkedList<Lane>(); // firstCar行驶的路上有哪些lane

		if (firstCar.getCurFromCrossID().equals(carInRoad.getFromCrossID()))
		{// 判断方向
			carInLanes = carInRoad.getForwardLane();
		} else
		{
			carInLanes = carInRoad.getBackwardLane(); // car行驶的路上有哪些lane
		}

		int carInLaneID = firstCar.getLaneID();
		Lane laneInvlovesCar = carInLanes.get(carInLaneID);// fisrtCar所在的lane

		// **********情况1：要到家了
		//
		if (firstCar.getCurToCrossID().equals(firstCar.getToCrossID()))
		{
			firstCar.setRealEndTime(t + 1); // 设置到家时间
			Main.ArrivedCar.add(firstCarID); // 放进ArrivalCars集合
			Main.NowInRoadCar.remove(Main.MapCar.get(firstCarID));// 把它从路上的车集合里面删掉

			laneInvlovesCar.carsInLane.removeFirst(); // $$$$$$将到家的Car从lane上去掉$$$$$$

			// 今天新加的
			// 判断firstCar是不是这条路上的唯一一个
			String nextCarID = getFirstCarInRoad(carInRoad, firstCar.getCurFromCrossID());
			if (nextCarID == null)
			{
				setCarInRoad(null, firstCar, carInRoad, 3, false, true, true, false, false);

			}
		}
		// **********情况2：能过路口，到别的路上
		else if (!firstCar.getCurToCrossID().equals(firstCar.getToCrossID()))
		{

			String nextRoadID = firstCar.getNextRoadID();
			Road nextRoad = Main.MapRoad.get(nextRoadID);
			int firstCarSpeed = Math.min(firstCar.getMaxVelocity(), nextRoad.getMaxRoadVelocity()); // firstCar的行驶速度

			LinkedList<Lane> carInNextLanes = new LinkedList<Lane>(); // firstCar去往的路上有哪些lane
			if (firstCar.getCurToCrossID().equals(nextRoad.getFromCrossID()))
			{// 判断方向
				carInNextLanes = nextRoad.getForwardLane();
			} else
			{
				carInNextLanes = nextRoad.getBackwardLane(); // firstCar将要前往的路上有哪些lane
			}
			int carInNextLanesNum = carInNextLanes.size();
			ArrayList<Integer> lanesLeftLength = RoadInfo.getLeftLanesLength(nextRoad, firstCar.getCurToCrossID());

			int laneiLeftLength = 0;
			int templaneID = -1;
			for (int i = 0; i < carInNextLanesNum; i++)
			{
				laneiLeftLength = lanesLeftLength.get(i);
				if (laneiLeftLength > 0)
				{// 从0开始塞车了，找到要进入的lane了
					templaneID = i;
					break;
				}
			}

			// 去不了
			if (templaneID == -1)
			{
				firstCar.setState(5);
				firstCar.setPriority(3);
				firstCar.setNextRoadID("-1");
				setCarInRoad(null, firstCar, carInRoad, 5, false, false, false, false, false);
			} else
			{
				firstCar.setLaneID(templaneID);
				// $$$$$更新laneID$$$$$
				// case1.1：该lane上car的前方有车，且是更新过的车
				// case1.2：该lane上car的前方有车，且是没有更新过的车
				// case2.1：该lane上car的前方没有车，且该road上也没有车
				// case2.2：该lane上car的前方没有车，但该road上有车

				// case1： 该lane上car的前方有车
				// case1.1：该lane上car的前方有车，且是更新过的车
				// case1.2：该lane上car的前方有车，且是没有更新过的车
				if (!carInNextLanes.get(firstCar.getLaneID()).carsInLane.isEmpty())
				{
					Car preCar = carInNextLanes.get(firstCar.getLaneID()).carsInLane.getLast();
					if (preCar.isHasArrangedOrNot() && preCar.getSheng() == 0)
					{// case1.1：该lane上car的前方有车，且是更新过的车
						// 1、设置状态
						if (preCar.getState() == 1)
						{
							firstCar.setState(3);
						} else if (preCar.getState() == 2)
						{
							firstCar.setState(4);
						} else
						{
							firstCar.setState(preCar.getState());
						}

						// 2、设置位置curPos
						// firstCar在nextRoad能走多远
						int hasJumpDis = firstCar.getCurPos();
						// 判断firstCar能不能追上前车
						if (nextRoad.getRoadLength() - preCar.getCurPos() - 1 >= firstCarSpeed - hasJumpDis)
						{// 不能追上
							firstCar.setCurPos(nextRoad.getRoadLength() - (firstCarSpeed - hasJumpDis));
						} else
						{
							firstCar.setCurPos(preCar.getCurPos() + 1);
						}
					} else
					{// case1.2:：该lane上car的前方有车，且是没有更新过的车
						// 1、设置状态
						if (preCar.getState() == 1)
						{
							firstCar.setState(3);
						} else if (preCar.getState() == 2)
						{
							firstCar.setState(4);
						} else
						{
							firstCar.setState(preCar.getState());
						}

						// 2、设置位置curPos
						// firstCar在nextRoad能走多远
						int hasJumpDis = firstCar.getCurPos();
						// 判断firstCar能不能追上前车
						if (nextRoad.getRoadLength() - preCar.getCurPos() - 1 >= firstCarSpeed - hasJumpDis)
						{// 不能追上
							firstCar.setCurPos(nextRoad.getRoadLength() - (firstCarSpeed - hasJumpDis));
						} else
						{
							firstCar.setCurPos(preCar.getCurPos() + 1);
							firstCar.setSheng(-(preCar.getCurPos() + 1
									- (nextRoad.getRoadLength() - (firstCarSpeed - hasJumpDis))));
						}

					}
				}
				// 该lane上car的前方没有车，case2
				// case2.1：该lane上car的前方没有车，且该road上也没有车
				// case2.2：该lane上car的前方没有车，但该road上有车

				else
				{
					LinkedList<String> carInNextRoadID = getCarInRoad(nextRoad, firstCar.getCurToCrossID());
					if (carInNextRoadID.isEmpty())
					{// case2.1:该road上也没有车
						// 1、设置位置 curPos
						int hasJumpDis = firstCar.getCurPos();
						firstCar.setCurPos(nextRoad.getRoadLength() - (firstCarSpeed - hasJumpDis));
						// 2、设置状态
						// 判断该车t3时刻是否能过路口
						if (firstCar.getCurPos() < firstCarSpeed)
						{// 能过路口是1
							firstCar.setState(1);
						} else
						{// 不能过路口是2
							firstCar.setState(2);
						}

					} else
					{ // case2.2:该road上有车
						// 1、设置位置 curPos
						int hasJumpDis = firstCar.getCurPos();
						firstCar.setCurPos(nextRoad.getRoadLength() - (firstCarSpeed - hasJumpDis));
						// 2、设置状态
						// 要找该road上在firstcar之前的车
						Car preRoadCar = Main.MapCar.get(carInNextRoadID.getFirst());
						if (preRoadCar.getCurPos() < firstCar.getCurPos())
						{ // firstcar在road原来车的后面
							if (preRoadCar.getState() == 1)
							{
								firstCar.setState(3);
							} else if (preRoadCar.getState() == 2)
							{
								firstCar.setState(4);
							} else
							{
								firstCar.setState(preRoadCar.getState());
							}
						} else if (preRoadCar.getCurPos() == firstCar.getCurPos()
								&& firstCar.getLaneID() > preRoadCar.getLaneID())
						{// firstcar和road原来第一辆车同一水平线，需要比较laneID

							if (preRoadCar.getState() == 1)
							{
								firstCar.setState(3);
							} else if (preRoadCar.getState() == 2)
							{
								firstCar.setState(4);
							} else
							{
								firstCar.setState(preRoadCar.getState());
							}
						} else
						{// firstCar在这条Road的最前面
							// 判断该车t3时刻是否能过路口
							if (firstCar.getCurPos() < firstCarSpeed)
							{// 能过路口是1
								firstCar.setState(1);
							} else
							{// 不能过路口是2
								firstCar.setState(2);
							}
						}

					}

				}

				laneInvlovesCar.carsInLane.removeFirst(); // $$$$将离开这条路的Car从lane上去掉$$$$$
				String nextCarID = getFirstCarInRoad(carInRoad, firstCar.getCurFromCrossID());
				if (nextCarID == null)
				{
					setCarInRoad(null, firstCar, carInRoad, 3, false, true, true, false, false);
				}

				// ------统一更新信息
				carInNextLanes.get(firstCar.getLaneID()).carsInLane.add(firstCar); // $$$$$把car加到这个lane上$$$$$
				firstCar.setHasArrangedOrNot(true); // 更新标志位
				firstCar.setRoadID(nextRoadID);
				firstCar.setCurFromCrossID(firstCar.getCurToCrossID());
				firstCar.setCurToCrossID(getCross(nextRoad, firstCar.getCurFromCrossID()).getCrossID());

				firstCar.setNextRoadID("-1");

			}
		}

	}

	/**
	 * 将firstCar所处的lane上的车从t2时刻更新到t3时刻
	 * 
	 * @param firstCar
	 * @version 2019.03.27
	 */

	public static void UpdateLaneForCarsAtState2(ArrayList<Car> car2s)
	{

		for (Car c : car2s)
			UpdateRoadForCarsAtState2(c.getCarID());
	}

	/**
	 * 更新状态2的车所在的路
	 * 
	 * @param firstCarID
	 * @version 2019-03-28
	 */
	protected static void UpdateRoadForCarsAtState2(String firstCarID)
	{
		Car firstCar = Main.MapCar.get(firstCarID);

		if (!firstCar.getNextRoadID().equals("-1"))
		{
			UpdateRoadForCarsAtState2Super(firstCarID); // 其实找到路了，只是道路限速过不去
		} else
		{
			UpdateRoadForCarsAtState2Nomal(firstCarID); // 本来就没能变成等待车
		}
	}

	/**
	 * firstCar State2 nextRoadID!=-1 是去不了的1变来的 将firstCar所处的Road上的车从t2时刻更新到t3时刻
	 * 更新State、curPos、sheng、hasArrangedOrNot 找出road上所有lane上的“firstCar”,更新每一个lane
	 * 
	 * @param firstCarID
	 * @version 2019-3-28
	 */
	protected static void UpdateRoadForCarsAtState2Super(String firstCarID)
	{
		Car firstCar = Main.MapCar.get(firstCarID);
		Road road = Main.MapRoad.get(firstCar.getRoadID());
		// 找出每个lane上的第一辆车
		HashMap<Integer, String> mapFirstCarInLanes = findFirstCarInLanes(firstCarID);
		if (mapFirstCarInLanes.isEmpty())
		{ // 如果这条路上没车
			System.out.println("map是空的");
		}
		// **********更新位置
		for (int i = 0; i < road.getLanesNum(); i++)
		{
			if (mapFirstCarInLanes.containsKey(i))
			{ // 如果这个lane上有车，更新这个lane上的车的位置
				UpdateLanePosForCarsAtState2(mapFirstCarInLanes.get(i));
			} else
			{// 这个Lane上没有车，继续
				continue;
			}
		}

		// 更新位置后，寻找目前的整个road的第一辆车

		String newFirstCarID = getFirstCarInRoad(road, firstCar.getCurFromCrossID());
		Car newFirstCar = Main.MapCar.get(newFirstCarID);

		newFirstCar.setState(1); // firstCar是由1车变过来的，curpos都变成0了，状态一定是1

		newFirstCar.setHasArrangedOrNot(true);// 更新位置之后再设置hasArrangedOrNot

		// *********更新State
		int followingState = 3;

		for (int i = 0; i < road.getLanesNum(); i++)
		{
			if (mapFirstCarInLanes.containsKey(i))
			{ // 如果这个lane上有车，更新这个lane上的车的位置
				UpdateLaneStateForCarsAtState2(mapFirstCarInLanes.get(i), newFirstCarID, followingState);
			} else
			{// 这个Lane上没有车，继续
				continue;
			}
		}

	}

	/**
	 * firstCar State2 nextRoadID==-1 普通情况 将firstCar所处的Road上的车从t2时刻更新到t3时刻
	 * 更新State、curPos、sheng、hasArrangedOrNot 找出road上所有lane上的“firstCar”,更新每一个lane
	 * 
	 * @param firstCarID
	 * @version 2019-3-28
	 */

	protected static void UpdateRoadForCarsAtState2Nomal(String firstCarID)
	{
		// Note:将firstCar所处的Road上的车从t2时刻更新到t3时刻
		// 找出road上所有lane上的“firstCar”,更新每一个lane
		// firstCar的t3时刻一定是不能过路口的；
		// 但t4时刻的状态是不一定的，如果能够过路口设为1，后面的车都是3；不能过路口的话，设为2，后面都是4
		Car firstCar = Main.MapCar.get(firstCarID);
		Road road = Main.MapRoad.get(firstCar.getRoadID());

		// 找出每个lane上的第一辆车
		HashMap<Integer, String> mapFirstCarInLanes = findFirstCarInLanes(firstCarID);
		// **********更新位置
		for (int i = 0; i < road.getLanesNum(); i++)
		{
			if (mapFirstCarInLanes.containsKey(i))
			{ // 如果这个lane上有车，更新这个lane上的车的位置
				UpdateLanePosForCarsAtState2(mapFirstCarInLanes.get(i));
			} else
			{// 这个Lane上没有车，继续
				continue;
			}
		}

		// 更新位置后，寻找目前的整个road的第一辆车
		// LinkedList<String> newCarsInRoad = getCarInRoad(road,
		// firstCar.getCurFromCrossID());
		// String newFirstCarID = newCarsInRoad.getFirst();
		String newFirstCarID = getFirstCarInRoad(road, firstCar.getCurFromCrossID());
		Car newFirstCar = Main.MapCar.get(newFirstCarID);

		int newmaxSpeed = Math.min(newFirstCar.getMaxVelocity(), road.getMaxRoadVelocity()); // firstCar此时的行驶速度

		if (newFirstCar.getCurPos() - newmaxSpeed < 0)
		{ // 如果能够过路口
			newFirstCar.setState(1);
		} else
		{ // 如果不能过路口
			newFirstCar.setState(2);
		}
		newFirstCar.setHasArrangedOrNot(true);

		// *********更新State
		int followingState = 0;
		if (newFirstCar.getState() == 1)
		{
			followingState = 3;
		} else if (newFirstCar.getState() == 2)
		{
			followingState = 4;
		} else
		{
			System.out.print("UpdateRoadForCarsAtState2---第一辆车的状态错了");
		}

		for (int i = 0; i < road.getLanesNum(); i++)
		{
			if (mapFirstCarInLanes.containsKey(i))
			{ // 如果这个lane上有车，更新这个lane上的车的位置
				UpdateLaneStateForCarsAtState2(mapFirstCarInLanes.get(i), newFirstCarID, followingState);
			} else
			{// 这个Lane上没有车，继续
				continue;
			}
		}

	}

	/**
	 * 将firstCar所处的lane上的车的位置从t2时刻更新到t3时刻 更新curPos、sheng
	 * 
	 * @param firstCarID
	 * @version 2019-3-28
	 */

	protected static void UpdateLanePosForCarsAtState2(String firstCarID)
	{
		// 更新carPos、sheng
		// Note:将firstCar所处的lane上的车的位置从t2时刻更新到t3时刻
		// firstCar的t3时刻一定是不能过路口的；
		// 但t4时刻的状态是不一定的，如果能够过路口设为1，后面的车都是3；不能过路口的话，设为2，后面都是4

		// *************找出firstCar所在的lane
		Car firstCar = Main.MapCar.get(firstCarID);
		Road carInRoad = Main.MapRoad.get(firstCar.getRoadID()); // car行驶的路
		LinkedList<Lane> carInLane = new LinkedList<Lane>();
		if (firstCar.getCurFromCrossID().equals(carInRoad.getFromCrossID()))
		{
			carInLane = carInRoad.getForwardLane();
		} else
		{
			carInLane = carInRoad.getBackwardLane(); // car行驶的路上有哪些lane
		}

		int carInLaneID = firstCar.getLaneID();
		Lane laneInvlovesCar = carInLane.get(carInLaneID);// fisrtCar所在的lane
		int maxSpeed = Math.min(firstCar.getMaxVelocity(), carInRoad.getMaxRoadVelocity()); // firstCar此时的行驶速度

		// ***********更新firstCar的位置
		// firstCar的nextRoadID是否为-1
		if (firstCar.getNextRoadID().equals("-1"))
		{
			firstCar.setCurPos(firstCar.getCurPos() - maxSpeed);// 更新t3时刻
			firstCar.setSheng(0);
		} else
		{
			firstCar.setCurPos(0); // 由于限速过不去nextRoad的车，curPos设置为0
			firstCar.setSheng(0);
		}

		// **********更新lane上其它车（如果不止有一辆的话）
		LinkedList<Car> carsInLane = laneInvlovesCar.carsInLane;
		int cariSpeed = 0;
		int carInLaneNum = carsInLane.size();
		if (carInLaneNum > 1)
		{
			for (int i = 1; i < carInLaneNum; i++)
			{
				cariSpeed = Math.min(carsInLane.get(i).getMaxVelocity(), carInRoad.getMaxRoadVelocity());
				// ------case1:没有安排过的车
				if (carsInLane.get(i).isHasArrangedOrNot() == false)
				{
					if (carsInLane.get(i).getCurPos() - cariSpeed <= carsInLane.get(i - 1).getCurPos())
					{// 能追上前车（前车已经更新到t3时刻了）
						carsInLane.get(i).setCurPos(carsInLane.get(i - 1).getCurPos() + 1);
					} else
					{// 如果不能追上前车，能跑多远跑多远
						carsInLane.get(i).setCurPos(carsInLane.get(i).getCurPos() - cariSpeed);
					}
				}
				// -----case2:安排过的车（从另一个路口过来的)
				else if (carsInLane.get(i).isHasArrangedOrNot() == true && carsInLane.get(i).getSheng() != 0)
				{
					if (carsInLane.get(i).getCurPos() - carsInLane.get(i - 1).getCurPos() > Math
							.abs(carsInLane.get(i).getSheng()))
					{ // 追不上前车
						carsInLane.get(i).setCurPos(carsInLane.get(i).getCurPos() + carsInLane.get(i).getSheng()); // sheng是负值！
					} else
					{// 能追上
						carsInLane.get(i).setCurPos(carsInLane.get(i - 1).getCurPos() + 1);
					}
				}
				// // -----case3:如果遇到一辆已经更新的了，那它后面肯定也已经更新了（保险起见，先注释这段）
				// else if (carsInLane.get(i).isHasArrangedOrNot() == true &&
				// carsInLane.get(i).getSheng() == 0) {
				// break;
				// }
				// 统一更新属性
				carsInLane.get(i).setSheng(0);
			}
		}

	}

	/**
	 * 将lanefirstCar所处的lane上的车的状态从t2时刻更新到t3时刻 需要更新的信息有：hasArrangedOrNot,State
	 * 
	 * @param laneFirstCarID
	 * @param newFirstCarID
	 * @param followingState
	 * @version 2019.03.28
	 */
	protected static void UpdateLaneStateForCarsAtState2(String laneFirstCarID, String newFirstCarID,
			int followingState)
	{
		// Note:将firstCar所处的lane上的车从t2时刻更新到t3时刻
		// firstCar的curPos已经更新到t3时刻！（该路上所有车的curpos都已经更新到t3时刻了）

		// *************找出firstCar所在的lane
		Car firstCar = Main.MapCar.get(laneFirstCarID);
		Road carInRoad = Main.MapRoad.get(firstCar.getRoadID()); // car行驶的路
		LinkedList<Lane> carInLane = new LinkedList<Lane>();
		if (firstCar.getCurFromCrossID().equals(carInRoad.getFromCrossID()))
		{
			carInLane = carInRoad.getForwardLane();
		} else
		{
			carInLane = carInRoad.getBackwardLane(); // car行驶的路上有哪些lane
		}

		int carInLaneID = firstCar.getLaneID();
		Lane laneInvlovesCar = carInLane.get(carInLaneID); // fisrtCar所在的lane

		// **********更新lane上其它车（如果不止有一辆的话）
		LinkedList<Car> carsInLane = laneInvlovesCar.carsInLane;
		if (!laneFirstCarID.equals(newFirstCarID))
		{// 如果找到的不是这辆road上排在“最前”的车，也就是已经设置过状态的车
			carsInLane.get(0).setState(followingState);
			carsInLane.get(0).setHasArrangedOrNot(true);
		}
		// 如果是newfirstCar，不要再设置state了。
		int carInLaneNum = carsInLane.size();
		if (carInLaneNum > 1)
		{
			for (int i = 1; i < carInLaneNum; i++)
			{
				// 统一更新属性
				carsInLane.get(i).setState(followingState);
				carsInLane.get(i).setHasArrangedOrNot(true);
			}
		}

	}

	/**
	 * @param 找到从路from通过路口c到路to
	 * @return 这个转弯的优先级
	 * @version 2019-3-20
	 */
	protected static int setPriority(String fromRoadID, String toRoadID, String crossID)
	{
		Cross c = Main.MapCross.get(crossID);
		int ans = 0;
		String[] roadlist = c.getRoadIDList();
		int i = -1, j = -1;
		int size = roadlist.length;
		while (i != -1 && j != -1)
		{
			for (int k = 0; k < size; k++)
			{
				if (roadlist[k] == null)
					continue;
				if (roadlist[k].equals(fromRoadID))
					i = k;
				else if (roadlist[k].equals(toRoadID))
					j = k;
				else
					;
			}
		}
		if (Math.abs(i - j) == 2)
			ans = 3;
		else if (i - j == -1 || (i == 3 && j == 0))
			ans = 2;
		else
			ans = 1;
		return ans;

	}

	/**
	 * 安排过本来就在路上走的车之后，要安排从车库来的车了，安排以后，统一把它的标志位设置成true,同时要把从车库来的车加到在路上的车里去
	 * 
	 * @version 2019-3-27
	 */
	public static void setNowInRoadCarFromGarageWait()
	{
		Iterator<Car> carIt = Main.garageWait.iterator();
		while (carIt.hasNext())
		{
			Car c = carIt.next();
			c.setHasArrangedOrNot(true);
			c.setPriority(3);
			Main.NowInRoadCar.add(c);
		}

	}

	/**
	 * 一个时间片的末尾，将所有在路上行走的车的是否安排过都要置为false
	 * 
	 * @version 2019-3-27
	 */
	protected static void setNowInRoadCarFalse()
	{
		Iterator<Car> it = Main.NowInRoadCar.iterator();
		while (it.hasNext())
		{
			Car c = it.next();
			c.setHasArrangedOrNot(false);
		}

	}

	/**
	 * 一个时间片的末尾，将所有在路上行走的车的是否安排过都要置true
	 * 
	 * @version 2019-3-27
	 */
	protected static void setNowInRoadCarTrue()
	{
		Iterator<Car> it = Main.NowInRoadCar.iterator();
		while (it.hasNext())
		{
			Car c = it.next();
			c.setHasArrangedOrNot(true);
		}

	}

	/*
	 * 用来输出车的状态，来debug
	 * 
	 * @author lulu
	 * 
	 * @version 2019-3-28
	 */
	public static void testShowCarInfoset(HashSet<Car> cs)
	{
		Iterator<Car> it = cs.iterator();
		while (it.hasNext())
		{
			Car c = it.next();
			System.out.println("车ID为：" + c.getCarID() + "  状态为:" + c.getState() + "  安排过了吗:" + c.isHasArrangedOrNot()
					+ "  CurPos=" + c.getCurPos() + " roadID:" + c.getRoadID() + "  curFromCross:"
					+ c.getCurFromCrossID() + "  curToCross:" + c.getCurToCrossID() + "  sheng:" + c.getSheng());

		}

	}

	/*
	 * 用来输出车的状态，来debug
	 * 
	 * @version 2019-3-28
	 */
	public static void testShowCarInfo(LinkedList<Car> cs)
	{
		Iterator<Car> it = cs.iterator();
		while (it.hasNext())
		{
			Car c = it.next();
			System.out.println("车ID为：" + c.getCarID() + "  状态为:" + c.getState() + "  安排过了吗:" + c.isHasArrangedOrNot()
					+ "  CurPos=" + c.getCurPos() + " roadID:" + c.getRoadID() + "  curFromCross:"
					+ c.getCurFromCrossID() + "  curToCross:" + c.getCurToCrossID() + "  sheng:" + c.getSheng());

		}

	}

	/*
	 * 用来输出车的状态，来debug
	 * 
	 * @version 2019-3-28
	 */
	public static void testShowCarInfo(HashSet<Car> cs)
	{
		Iterator<Car> it = cs.iterator();
		while (it.hasNext())
		{
			Car c = it.next();
			System.out.println("车ID为：" + c.getCarID() + "  状态为:" + c.getState() + "  安排过了吗:" + c.isHasArrangedOrNot()
					+ "  CurPos=" + c.getCurPos() + " roadID:" + c.getRoadID() + "  curFromCross:"
					+ c.getCurFromCrossID() + "  curToCross:" + c.getCurToCrossID() + "  sheng:" + c.getSheng());

		}

	}

	/*
	 * 用来输出路的状态，来debug
	 * 
	 * @version 2019-3-28
	 */
	public static void testShowRoadInfo()
	{
		Iterator<Road> it = Main.listRoad.iterator();
		while (it.hasNext())
		{

			String s = it.next().getRoadID();
			testShowRoadInfo(s);
		}

	}

	/*
	 * 用来输出路的状态，来debug
	 * 
	 * @version 2019-3-28
	 */
	public static void testShowRoadInfo(String s)
	{
		Road r = Main.MapRoad.get(s);
		System.out.println("-----正向：");
		for (int i = 0; i < r.getLanesNum(); i++)
		{

			testShowCarInfo(r.getForwardLane().get(i).carsInLane);

		}
		System.out.println("-----反向：");
		for (int i = 0; i < r.getLanesNum(); i++)
		{

			testShowCarInfo(r.getBackwardLane().get(i).carsInLane);

		}

	}

	/**
	 * @param firstCarID
	 * @return mapFirstCarInLanes(laneID,carID)
	 * @version 2019-3-28
	 */

	protected static HashMap<Integer, String> findFirstCarInLanes(String firstCarID)
	{

		// mapFirstCarInLanes(laneID,carID)
		HashMap<Integer, String> mapFirstCarInLanes = new HashMap<Integer, String>();

		Car firstCar = Main.MapCar.get(firstCarID);
		Road road = Main.MapRoad.get(firstCar.getRoadID());
		String crossID = firstCar.getCurFromCrossID();
		LinkedList<String> carsInRoad = getCarInRoad(road, crossID);
		// 判断路上有没有车
		if (carsInRoad.isEmpty() || carsInRoad == null || carsInRoad.size() < 1)
		{
			System.out.println("carsInRoad为空，出错了，不能调用findFirstCarInLanes");
			return null;
		}
		// 遍历路上的car，确定每lane上的第一个car
		int cariLaneID = 0;
		Iterator<String> carIt = carsInRoad.iterator();
		while (carIt.hasNext())
		{
			String s = carIt.next();
			cariLaneID = Main.MapCar.get(s).getLaneID();
			if (!mapFirstCarInLanes.containsKey(cariLaneID))
			{// 如果这个lane是第一次遍历到的
				mapFirstCarInLanes.put(cariLaneID, s);// 把这个carID放进map
			} else
			{
				continue;
			}
			if (mapFirstCarInLanes.size() == road.getLanesNum())
			{// 每个lane都找到了就跳出循环
				break;
			}
		}
		return mapFirstCarInLanes;
	}

	/**
	 * 在一个车辆集合中选出能过路口的车
	 * 
	 * @param road
	 *            当前的道路
	 * @param carList
	 *            需要判断是否通过路口的车集合
	 * @param laneList
	 *            车集合所在的lane集合
	 * @param carIndex
	 *            车集合中各个车处于各自lane上的第几个位置
	 * @return 需要过路口的车的集合，大小和输入的carList相同，只是将不能过路口的车的位置放置null；
	 * @version 2019.3.28
	 */
	protected static Car[] ThroughCar(Road road, Car[] carList, LinkedList<Lane> laneList, int[] carIndex)
	{
		Car[] c = new Car[carList.length];
		int j = 0;
		for (; j < carList.length; j++)
		{// 先取第一个不为null的车
			Car car = carList[j];
			if (car != null)
			{
				canThrough(road, car, laneList, carIndex);
				if (car.isCanThrough())
				{
					c[j] = carList[j];
					carList[j] = null;
				}
			} else
				c[j] = null;
		}
		return c;
	}

	/**
	 * 判断该车能不能通过路口，如果一条lane上前车不能通过路口，该车也不能； 如果没有前车或者前车可以通过路口，则根据行驶速度和所在位置判断是否可以通过路口
	 * 
	 * @param road
	 *            当前道路
	 * @param car
	 *            当前车辆
	 * @param laneList
	 *            车所在的lane集合
	 * @param carIndex
	 *            车处于各自lane上的第几个位置
	 * @version 2019.3.28
	 */
	protected static void canThrough(Road road, Car car, LinkedList<Lane> laneList, int[] carIndex)
	{
		int laneID = car.getLaneID();
		int carIn = carIndex[laneID];
		LinkedList<Car> carsInLane = laneList.get(laneID).carsInLane;
		if (!carsInLane.isEmpty())
		{
			if (carIn > 0 && !carsInLane.get(carIn - 1).isCanThrough())
			{// 有前车且前车不能通过路口
				car.setCanThrough(false);
			} else
			{// 没前车或前车能通过路口，根据行驶速度和所在位置判断是否可以通过路口
				if (car.getCurPos() < Math.min(car.getMaxVelocity(), road.getMaxRoadVelocity()))
				{
					car.setCanThrough(true);
				} else
				{
					car.setCanThrough(false);
				}
			}
		}
	}

	/**
	 * 将传入的CarID按照｛优先级，carID｝排序，先排优先级（从大到小），优先级相同的再排carID（从小到大）
	 * 传入的车可能暂时找不到路，此时设置该车的状态为5，后车的状态也都设置为5，且该车不参与排序
	 * 如果所找的路已经更新完也不能通行，则设置为5true，该车不动，后车（如果本来能过路口则不动，本来不能过路口则向前跳），设置为4true，但实际设置为5，按4true来跳
	 * 要到目的地的车优先级设为3，此车参与排序 如果车本来有nextRoadID则不找路，直接判断它变为上述的哪种状态
	 * 如果所选道路上的行驶速度减去在本道路上可行距离小于等于0，则不能过路口，该车变为2false，后车变为4false
	 * 
	 * @param carsID：需要排序的车ID集合（状态为1，此时要出路口的车）
	 * @version 2019.3.30
	 */
	public static void sortCarsOfState1(ArrayList<Car> cars)
	{
		ArrayList<Car> carsList = new ArrayList<>();
		if (cars != null && cars.size() != 0)
		{
			for (int i = 0; i < cars.size(); i++)
			{
				Car car = cars.get(i);

				Road r = Main.MapRoad.get((car.getRoadID()));
				if (car.getCurToCrossID().equals(car.getToCrossID()))
				{// 要到家的车
					car.setPriority(3);
					carsList.add(car);// 参与排序
				} else if (!car.getNextRoadID().equals("-1"))
				{// 过路口但不许要重新找路
					Road toRoad = Main.MapRoad.get(car.getNextRoadID());
					// 该路因为没更新完没空间，则变为5false，后车也是5false
					if (hasLeftLength(toRoad, car.getCurToCrossID()) == -1)
					{
						setCarInRoad(toRoad, car, r, 5, false, false, false, false, false);
					}
					// 该路因为更新完没空间，则变为5true，后车往前跳，变4true,但实际设置为5，按4true来跳
					else if (hasLeftLength(toRoad, car.getCurToCrossID()) == -2)
					{
						setCarInRoad(toRoad, car, r, 5, true, true, true, false, false);

					} else if (Math.min(car.getMaxVelocity(), toRoad.getMaxRoadVelocity()) - car.getCurPos() <= 0)
					{
						setCarInRoad(toRoad, car, r, 4, false, false, false, false, false);
						car.setState(2);
					} else if (Math.min(car.getMaxVelocity(), toRoad.getMaxRoadVelocity()) - car.getCurPos() > 0)
					{
						carsList.add(car);
					}
				} else
				{// 还要继续过路口行驶并需要找路的车
					Road toRoad = findNextCross(car, Main.maxRoadLength);
					if (toRoad == null)
					{// 找不到路则该车状态变为5，未安排的后车状态都变为5
						System.out.println("我死在这里了");
					}
					// 找到的路因为没更新完没空间，则变为5false，后车也是5false
					else if (hasLeftLength(toRoad, car.getCurToCrossID()) == -1)
					{
						setCarInRoad(toRoad, car, r, 5, false, false, false, false, false);

					}
					// 找到的路因为更新完没空间，则变为5true，后车往前跳，变4true，,但实际设置为5，按4true来跳
					else if (hasLeftLength(toRoad, car.getCurToCrossID()) == -2)
					{
						setCarInRoad(toRoad, car, r, 5, true, true, true, false, false);
						car.setPriority(setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
						car.setNextRoadID(toRoad.getRoadID());
						car.setState(2);
						car.setNextRoadID(toRoad.getRoadID());
						car.setPriority(setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
						// car.setHasArrangedOrNot(false);
					} else
					{// 找到路的车，参与排序
						car.setPriority(setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
						car.setNextRoadID(toRoad.getRoadID());
						carsList.add(car);
					}
				}
			}
			Collections.sort(carsList, new Comparator<Car>() {// 按车辆优先级降序排列
				@Override
				public int compare(Car o1, Car o2)
				{
					return o2.getPriority() - o1.getPriority();
				}
			});
			for (int i = 0; i < carsList.size(); i++)
			{// 优先级相同的车辆按ID升序排列
				int j = i + 1;
				while (j < carsList.size() && carsList.get(j - 1).getPriority() == carsList.get(j).getPriority())
				{
					j++;
				}
				Collections.sort(carsList.subList(i, j), new Comparator<Car>() {// 按车辆优先级降序排列
					@Override
					public int compare(Car o1, Car o2)
					{
						return o1.getCarID().compareTo(o2.getCarID());
					}
				});
				i = j - 1;
			}
			cars.clear();
			for (int i = 0; i < carsList.size(); i++)
			{
				cars.add(carsList.get(i));
			}
		}
	}

	public static void setCarInRoad(Road toRoad, Car car, Road r, int state, boolean setHasArrangedOrNot,
			boolean setSheng, boolean setCurPos, boolean setNextRoadID, boolean setPriority)
	{
		LinkedList<Lane> lanes;
		LinkedList<Car> carsInLane;
		Car shengCar;
		if (car.getCurFromCrossID().equals(r.getFromCrossID()))
			lanes = r.getForwardLane();
		else
			lanes = r.getBackwardLane();
		for (int j = 0; j < lanes.size(); j++)
		{
			carsInLane = lanes.get(j).carsInLane;
			for (int y = 0; y < carsInLane.size(); y++)
			{
				shengCar = carsInLane.get(y);
				if (setCurPos)
				{
					if (shengCar.getSheng() != 0)
					{
						if (y == 0)
						{
							shengCar.setCurPos(shengCar.getCurPos() + shengCar.getSheng());
						} else
						{
							int curPos = shengCar.getCurPos()
									- Math.min(shengCar.getCurPos() - carsInLane.get(y - 1).getCurPos() - 1,
											Math.abs(shengCar.getSheng()));
							shengCar.setCurPos(curPos);
						}
					} else if (shengCar.equals(car))
					{
						Main.numOf5 += 2;
					} else if (!shengCar.isHasArrangedOrNot())
					{
						int cha = shengCar.getCurPos() - Math.min(shengCar.getMaxVelocity(), r.getMaxRoadVelocity());
						if (y == 0)
						{
							if (cha < 0)
							{
								;
							} else
							{
								shengCar.setCurPos(cha);
							}
						} else
						{
							if (cha < 0)
							{
								;
							} else
							{
								shengCar.setCurPos(shengCar.getCurPos()
										- Math.min(Math.min(shengCar.getMaxVelocity(), r.getMaxRoadVelocity()),
												shengCar.getCurPos() - carsInLane.get(y - 1).getCurPos() - 1));
							}
						}
					}
				}
				if (state != -2)
				{
					shengCar.setState(state);
					if (state == 4)
						Main.numOf2++;
					if (state == 5 && setHasArrangedOrNot)
						Main.numOf5 += 2;
					if (state == 5 && !setHasArrangedOrNot)
						Main.numOf5++;
				}
				if (setHasArrangedOrNot)
					shengCar.setHasArrangedOrNot(true);
				if (setSheng)
					shengCar.setSheng(0);
				if (setNextRoadID)
					shengCar.setNextRoadID(toRoad.getRoadID());
				if (setPriority)
					shengCar.setPriority(setPriority(car.getRoadID(), toRoad.getRoadID(), car.getCurToCrossID()));
			}
		}
	}

	/**
	 * 如果它发现有路可以走了，他就变回1false了，后面车变成3false；
	 * 更新完没空间，他就变成true5,后面的车更新状态变成4true,但实际设置为5，按4true来跳后面车（如果本来能过路口则不动，本来不能过路口则向前跳）;
	 * 不然他还是flase5
	 * 
	 * @param firstCarID:状态为5false的车，排在carInRoad的头头
	 * @version 2019.3.30
	 */
	protected static void updateLaneForCarsAtState5(String firstCarID)
	{

		Car firstCar = Main.MapCar.get(firstCarID);
		Road curRoad = Main.MapRoad.get(firstCar.getRoadID());
		Road road;
		road = findNextCross(firstCar, Main.maxRoadLength);
		if (hasLeftLength(road, firstCar.getCurToCrossID()) > 0)
		{
			// 可供选择的路都有空间可走，该车变为1，后车变为3
			setCarInRoad(road, firstCar, curRoad, 3, false, false, false, false, false);
			firstCar.setState(1);
			firstCar.setNextRoadID(road.getRoadID());
			firstCar.setPriority(
					setPriority(firstCar.getRoadID(), firstCar.getNextRoadID(), firstCar.getCurToCrossID()));
		}
		// 更新完也没空间,则变为5true，后车往前跳，变4true,但实际设置为5，按4true来跳
		else if (hasLeftLength(road, firstCar.getCurToCrossID()) == -2)
		{
			setCarInRoad(road, firstCar, curRoad, 5, true, true, true, false, false);
		} else if (hasLeftLength(road, firstCar.getCurToCrossID()) == -1)
			Main.numOf5++;
		// 因为未更新而没有空间，该车状态不变
	}

	/**
	 * @param
	 * @version 2019.3.29
	 */
	protected static void innitial(Car c)
	{
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
	 * 根据一条路上,车是否出路口，lane的顺序和lane上车辆距离目的路口的距离，给出排在第一个的车ID，不考虑hasArrangeOrNot为true的车
	 * （首先考虑是否出路口，然后考虑距离，最后考虑车道顺序）
	 * 
	 * @param road：当前道路;crossID:车从哪个路口到这个路;
	 * @return 获得当前道路和车辆行驶方向相同的所有lane上的第一辆车。getfirst是先出发的车（头头）
	 * @version 2019.3.30
	 */
	public static String getFirstCarInRoad(Road road, String crossID)
	{
		LinkedList<Lane> laneList;
		// 找到和车辆方向一致的车道集合
		if (road.isDuplex())
			laneList = road.getFromCrossID().equals(crossID) ? road.getForwardLane() : road.getBackwardLane();
		else
			laneList = road.getForwardLane();
		int laneNum = laneList.size();
		Car[] carList = new Car[laneNum];// 每次取排在最前面的几个车道的车进行比较,下标对应所在车道，没车的车道或遍历完车的车道放null
		int[] carIndex = new int[laneNum];// 每个车道取到第几辆车，下标对应所在车道，没车的车道或者已经遍历完的车道放置-1
		for (int i = 0; i < laneNum; i++)
		{// 初始化,放头几辆车
			carIndex[i] = 0;
			Lane lane = laneList.get(i);
			if (!lane.carsInLane.isEmpty())
			{// 该车道有车
				// 跳过已安排的车辆
				while (carIndex[i] < lane.carsInLane.size() && lane.carsInLane.get(carIndex[i]).isHasArrangedOrNot())
				{
					carIndex[i]++;
				}
				// 该条车道都是已安排的车
				if (carIndex[i] == lane.carsInLane.size())
				{
					carList[i] = null;
					carIndex[i] = -1;
				}
				// 还有未安排的车
				else
					carList[i] = lane.carsInLane.get(carIndex[i]);
			} else
			{// 该车道无车
				carList[i] = null;
				carIndex[i] = -1;
			}
		}
		Car[] throughCar = ThroughCar(road, carList, laneList, carIndex);// 选出出路口的车
		Car out;
		out = minCarCurPos(throughCar);
		if (out != null)
			return out.getCarID();
		else
		{
			out = minCarCurPos(carList);
		}
		if (out != null)
			return out.getCarID();
		else
			return null;
	}

}

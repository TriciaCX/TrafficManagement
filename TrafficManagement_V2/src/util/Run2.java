package util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import util.AnswerStrings;

import core.Main;
import vo.Car;
import vo.Cross;
import vo.Road;

public class Run2 {
	public static void run(int maxRoadLength, ArrayList<Road> listRoad, ArrayList<Cross> listCross,
			ArrayList<Car> listCar, Map<String, String> ansMap, String[] ans) {
		LinkedList<Car> reArrangeCars = new LinkedList<>();
		// 分类
		int t = 1;
		RunUtil2.classifyCars(Main.garageFrozen, t);
		int garageWaitSize = Main.garageWait.size();
		// 安排车库等待的车辆，可能回滚
		boolean[] garageflag = new boolean[2];// true[插进去了，无回滚]
		while (garageWaitSize > 0) {
			Car car = Main.garageWait.get(--garageWaitSize);
			Road road = RunUtil2.findNextCross(car, Main.maxRoadLength);
			reArrangeCars.clear();//每次都要清除的
			if (road != null) {
				garageflag = RunUtil2.checkIDPriority(car, road, reArrangeCars, Main.MapRoad, t);
				if (!garageflag[0])// 这辆车自己也进不去这个路，把他从wait里面放到frozon
				{
					Main.garageFrozen.add(car);
					Main.garageWait.remove(car);// 这里又可以remove掉了
				} 
				else if (garageflag[0] && !garageflag[1]) {// 这辆车自己可以进去了！有回滚
					while (!reArrangeCars.isEmpty()) {// 把所有退出来的车加到frozen里去
						Car c = reArrangeCars.remove(reArrangeCars.size() - 1);
						Main.garageFrozen.add(c);
						Main.garageWait.remove(c);// 这里又可以remove掉了
					}
				} else {// 这辆车进去了，而且没回滚

				}

			}
		}
		// 安排从车库来的车了，安排以后，统一把它的标志位设置成true,同时要把从车库来的车加到在路上的车里去
		RunUtil2.setNowInRoadCarFromGarageWait();


		// 把答案输出去！
		AnswerStrings.updateAns(ansMap, ans);
		RunUtil2.setNowInRoadCarFalse();
		while (true) {

			// 把标志位都设成false
			RunUtil2.setNowInRoadCarFalse();

			t++;

			// 第二时刻开始安排路径了


			// 判断是不是所有车都安排过了，sheng都是0了呀，也就是说位置都是真的了
			while (!RunUtil2.isAllReal()) {
				if(t==8)
					System.out.println("\t");
				System.out.println("当前时刻" + t + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

				// 先根据路口id升序，更新 路上车的状态
				System.out.println("*********************************************step1");
				for (int i = 0; i < Main.listCross.size(); i++) {
					// 获得四个车，存进这个链表里，可能 不是4辆车 ，但最多四辆
					Cross s = listCross.get(i);
					LinkedList<Car> carsFour = RunUtil2.extractFourCar(s);
					if (carsFour.isEmpty())
						continue;
					RunUtil2.testShowCarInfo(carsFour);
					// 未更新过的头上的车的状态是345,要先处理一下

					RunUtil2.FourCarStatePreProcess(carsFour);
					RunUtil2.testShowCarInfo(carsFour);
					System.out.println("-----------------");
				}

				System.out.println("*********************************************step2");

				for (int i = 0; i < Main.listCross.size(); i++) {
					if (Main.NowInRoadCar.isEmpty())
						break;
					// 获得四个车，存进这个链表里，可能不是4辆车 ，但最多四辆
					Cross s = listCross.get(i);
					LinkedList<Car> carsFour = RunUtil2.extractFourCar(s);
					if (carsFour.isEmpty())
						continue;
					RunUtil2.testShowCarInfo(carsFour);
					// 现在头上的车的状态就要么是1要么是2了，跑起来
					RunUtil2.FourCarStateProcess(carsFour, t);
					RunUtil2.testShowCarInfo(carsFour);
					System.out.println("-----------------");
				}
			}

			// 把答案输出去！
			AnswerStrings.updateAns(ansMap, ans);

			// 第二时刻有新的车可以从车库里面取出来了

			RunUtil2.classifyCars(Main.garageFrozen, t);
			garageWaitSize = Main.garageWait.size();
			// 安排车库等待的车辆，可能回滚
			garageflag = new boolean[2];// true[插进去了，无回滚]
			while (garageWaitSize > 0) {
				Car car = Main.garageWait.get(--garageWaitSize);
				Road road = RunUtil2.findNextCross(car, Main.maxRoadLength);
				reArrangeCars.clear();//每次都要清除的
				if (road != null) {
					garageflag = RunUtil2.checkIDPriority(car, road, reArrangeCars, Main.MapRoad, t);
					if (!garageflag[0])// 这辆车自己也进不去这个路，把他从wait里面放到frozon
					{
						Main.garageFrozen.add(car);
						Main.garageWait.remove(car);// 这里又可以remove掉了
					} 
					else if (garageflag[0] && !garageflag[1]) {// 这辆车自己可以进去了！有回滚
						while (!reArrangeCars.isEmpty()) {// 把所有退出来的车加到frozen里去
							Car c = reArrangeCars.remove(reArrangeCars.size() - 1);
							Main.garageFrozen.add(c);
							Main.garageWait.remove(c);// 这里又可以remove掉了
						}
					} else {// 这辆车进去了，而且没回滚

					}

				}
			}
			// 安排从车库来的车了，安排以后，统一把它的标志位设置成true,同时要把从车库来的车加到在路上的车里去
			RunUtil2.setNowInRoadCarFromGarageWait();
			RunUtil2.setNowInRoadCarTrue();// 之前把原先在路上的车设成了false,所以这里要全设为true一波

			// 把答案输出去！
			AnswerStrings.updateAns(ansMap, ans);
			if (RunUtil2.isAllArrived())
				break;

			System.out.println("***********************************Main.garageWait");
			RunUtil2.testShowCarInfo(Main.garageWait);
			System.out.println("**********************************Main.garageFrozen");
			RunUtil2.testShowCarInfo(Main.garageFrozen);
			System.out.println("**********************************Main.NowInRoadCar");
			RunUtil2.testShowCarInfo(Main.NowInRoadCar);

			System.out.println("当前时刻" + t + ".");


		}

	}

}

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
		// ����
		int t = 1;
		RunUtil2.classifyCars(Main.garageFrozen, t);
		int garageWaitSize = Main.garageWait.size();
		// ���ų���ȴ��ĳ��������ܻع�
		boolean[] garageflag = new boolean[2];// true[���ȥ�ˣ��޻ع�]
		while (garageWaitSize > 0) {
			Car car = Main.garageWait.get(--garageWaitSize);
			Road road = RunUtil2.findNextCross(car, Main.maxRoadLength);
			reArrangeCars.clear();//ÿ�ζ�Ҫ�����
			if (road != null) {
				garageflag = RunUtil2.checkIDPriority(car, road, reArrangeCars, Main.MapRoad, t);
				if (!garageflag[0])// �������Լ�Ҳ����ȥ���·��������wait����ŵ�frozon
				{
					Main.garageFrozen.add(car);
					Main.garageWait.remove(car);// �����ֿ���remove����
				} 
				else if (garageflag[0] && !garageflag[1]) {// �������Լ����Խ�ȥ�ˣ��лع�
					while (!reArrangeCars.isEmpty()) {// �������˳����ĳ��ӵ�frozen��ȥ
						Car c = reArrangeCars.remove(reArrangeCars.size() - 1);
						Main.garageFrozen.add(c);
						Main.garageWait.remove(c);// �����ֿ���remove����
					}
				} else {// ��������ȥ�ˣ�����û�ع�

				}

			}
		}
		// ���Ŵӳ������ĳ��ˣ������Ժ�ͳһ�����ı�־λ���ó�true,ͬʱҪ�Ѵӳ������ĳ��ӵ���·�ϵĳ���ȥ
		RunUtil2.setNowInRoadCarFromGarageWait();


		// �Ѵ����ȥ��
		AnswerStrings.updateAns(ansMap, ans);
		RunUtil2.setNowInRoadCarFalse();
		while (true) {

			// �ѱ�־λ�����false
			RunUtil2.setNowInRoadCarFalse();

			t++;

			// �ڶ�ʱ�̿�ʼ����·����


			// �ж��ǲ������г������Ź��ˣ�sheng����0��ѽ��Ҳ����˵λ�ö��������
			while (!RunUtil2.isAllReal()) {
				if(t==8)
					System.out.println("\t");
				System.out.println("��ǰʱ��" + t + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

				// �ȸ���·��id���򣬸��� ·�ϳ���״̬
				System.out.println("*********************************************step1");
				for (int i = 0; i < Main.listCross.size(); i++) {
					// ����ĸ�������������������� ����4���� �����������
					Cross s = listCross.get(i);
					LinkedList<Car> carsFour = RunUtil2.extractFourCar(s);
					if (carsFour.isEmpty())
						continue;
					RunUtil2.testShowCarInfo(carsFour);
					// δ���¹���ͷ�ϵĳ���״̬��345,Ҫ�ȴ���һ��

					RunUtil2.FourCarStatePreProcess(carsFour);
					RunUtil2.testShowCarInfo(carsFour);
					System.out.println("-----------------");
				}

				System.out.println("*********************************************step2");

				for (int i = 0; i < Main.listCross.size(); i++) {
					if (Main.NowInRoadCar.isEmpty())
						break;
					// ����ĸ�������������������ܲ���4���� �����������
					Cross s = listCross.get(i);
					LinkedList<Car> carsFour = RunUtil2.extractFourCar(s);
					if (carsFour.isEmpty())
						continue;
					RunUtil2.testShowCarInfo(carsFour);
					// ����ͷ�ϵĳ���״̬��Ҫô��1Ҫô��2�ˣ�������
					RunUtil2.FourCarStateProcess(carsFour, t);
					RunUtil2.testShowCarInfo(carsFour);
					System.out.println("-----------------");
				}
			}

			// �Ѵ����ȥ��
			AnswerStrings.updateAns(ansMap, ans);

			// �ڶ�ʱ�����µĳ����Դӳ�������ȡ������

			RunUtil2.classifyCars(Main.garageFrozen, t);
			garageWaitSize = Main.garageWait.size();
			// ���ų���ȴ��ĳ��������ܻع�
			garageflag = new boolean[2];// true[���ȥ�ˣ��޻ع�]
			while (garageWaitSize > 0) {
				Car car = Main.garageWait.get(--garageWaitSize);
				Road road = RunUtil2.findNextCross(car, Main.maxRoadLength);
				reArrangeCars.clear();//ÿ�ζ�Ҫ�����
				if (road != null) {
					garageflag = RunUtil2.checkIDPriority(car, road, reArrangeCars, Main.MapRoad, t);
					if (!garageflag[0])// �������Լ�Ҳ����ȥ���·��������wait����ŵ�frozon
					{
						Main.garageFrozen.add(car);
						Main.garageWait.remove(car);// �����ֿ���remove����
					} 
					else if (garageflag[0] && !garageflag[1]) {// �������Լ����Խ�ȥ�ˣ��лع�
						while (!reArrangeCars.isEmpty()) {// �������˳����ĳ��ӵ�frozen��ȥ
							Car c = reArrangeCars.remove(reArrangeCars.size() - 1);
							Main.garageFrozen.add(c);
							Main.garageWait.remove(c);// �����ֿ���remove����
						}
					} else {// ��������ȥ�ˣ�����û�ع�

					}

				}
			}
			// ���Ŵӳ������ĳ��ˣ������Ժ�ͳһ�����ı�־λ���ó�true,ͬʱҪ�Ѵӳ������ĳ��ӵ���·�ϵĳ���ȥ
			RunUtil2.setNowInRoadCarFromGarageWait();
			RunUtil2.setNowInRoadCarTrue();// ֮ǰ��ԭ����·�ϵĳ������false,��������Ҫȫ��Ϊtrueһ��

			// �Ѵ����ȥ��
			AnswerStrings.updateAns(ansMap, ans);
			if (RunUtil2.isAllArrived())
				break;

			System.out.println("***********************************Main.garageWait");
			RunUtil2.testShowCarInfo(Main.garageWait);
			System.out.println("**********************************Main.garageFrozen");
			RunUtil2.testShowCarInfo(Main.garageFrozen);
			System.out.println("**********************************Main.NowInRoadCar");
			RunUtil2.testShowCarInfo(Main.NowInRoadCar);

			System.out.println("��ǰʱ��" + t + ".");


		}

	}

}

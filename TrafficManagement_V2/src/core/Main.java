package core;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import util.AnswerStrings;
import util.IOUtil;
import util.PreprocUtil;

import util.Run2;
import vo.Road;
import vo.Car;
import vo.Cross;

public class Main
{
	public static LinkedList<Car> garageFrozen = new LinkedList<>();

	public static LinkedList<Car> garageWait = new LinkedList<>();

	public static int maxRoadLength = 0;
	public static HashMap<String, Road> MapRoad;
	public static HashMap<String, Cross> MapCross;
	public static HashMap<String, Car> MapCar;
	public static ArrayList<Cross> listCross;
	public static ArrayList<Car> listCar;
	public static ArrayList<Road> listRoad;
	public static HashSet<String> ArrivedCar = new HashSet<>();
	public static HashSet<Car> NowInRoadCar = new HashSet<>();
	public static String filePath = "D:\\map\\config_10";
	public static String problemCar = "10009";
	public static int cnt = 0;
	public static int numOf2 = 0;
	public static int numOf5 = 0;
	public static float[] w = new float[3];

	public static void main(String[] args)
	{

		// ��ʼ��Ȩ��
		w[0] = 1;
		w[1] = 0;
		w[2] = 0;

		// ��ȡroad.txt�ļ�������������Ԥ����
		String[] roadString = IOUtil.read(filePath + "\\road.txt", null);
		listRoad = PreprocUtil.PreRoadData(roadString);
		MapRoad = PreprocUtil.PreRoadDataMap(listRoad);

		// ��ȡcross.txt�ļ�������������Ԥ����
		String[] crossString = IOUtil.read(filePath + "\\cross.txt", null);
		listCross = PreprocUtil.PreCrossData(crossString, MapRoad);
		MapCross = PreprocUtil.PreCrossDataMap(listCross);

		// ��ȡcar.txt�ļ�������������Ԥ����,����ansΪ�滮���
		String[] carString = IOUtil.read(filePath + "\\car.txt", null);
		Map<String, String> ansMap = new HashMap<>();
		String[] ans = new String[carString.length];
		ans[0] = "#carID, StartTime, RoadID...";
		listCar = PreprocUtil.PreCarData(carString, ansMap, ans);
		// ���г��Ƕ������ڳ���׼���š�
		garageFrozen.addAll(listCar);
		MapCar = PreprocUtil.PreCarDataMap(listCar);

		//

		System.out.println("Successful data accessing and object initializing!!!");

		maxRoadLength = info.RoadInfo.getMaxRoadLength(listRoad);

		// ������ʼʱ��
		long t1 = System.currentTimeMillis();
		// ������������
		Run2.run(maxRoadLength, listRoad, listCross, listCar, ansMap, ans);

		System.out.println("Successful routing!!!");

		AnswerStrings.ansMapTOans(ansMap, ans);

		// �����д�����ļ�
		IOUtil.write(filePath + "\\answer.txt", ans, false);
		// �������ʱ��
		long t2 = System.currentTimeMillis();
		// �ܳ��򻨷�ʱ��
		System.out.println("time:" + (t2 - t1) + "ms");

	}

}

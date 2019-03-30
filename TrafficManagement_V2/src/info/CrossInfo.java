package info;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.LinkedList;

import vo.Lane;
import vo.Road;
import info.RoadInfo;
import core.Main;

public class CrossInfo
{

	// **************************************cost****************************************************
	/**
	 * cost1
	 * 
	 * @param road
	 * @return NormalizedRoadLength
	 * @version 2019-3-21
	 */
	// �Ƶ���util�����RunUtil
	// public static float getNormalizedRoadLength(Road road) {
	// float NormalizedRoadLength =0;

	// NormalizedRoadLength = road.getRoadLength()/Main.maxRoadLength;
	// //Main.maxRoadLength���·����
	// return NormalizedRoadLength;
	// }

	/**
	 * cost2
	 * 
	 * @param carToCross
	 * @param carFromCross
	 * @param car
	 * @return NormalizedRoadLeftLength
	 * @version 2019-3-21
	 */
	public static float getNormalizedRoadLeftLength(Road road, String crossID)
	{
		float NormalizedRoadLeftLength = 0;
		ArrayList<Integer> leftLength = RoadInfo.getLeftLanesLength(road, crossID);
		int ans = 0;
		Iterator<Integer> i = leftLength.iterator();
		while (i.hasNext())
		{
			ans += i.next();
		}
		NormalizedRoadLeftLength = ans / (road.getRoadLength() * road.getLanesNum());
		return NormalizedRoadLeftLength;
	}

	/**
	 * cost3 sigmoid(carInCrossNum)
	 * 
	 * @param carToCross,car
	 * @return carInCrossNum
	 * @version 2019-03-20
	 */
	public static int getCrossCarNum(String carFromCossID, String carToCrossID)
	{
		// Cross�����ĵ�·�ϵ����г�
		int carInCrossNum = 0;
		int carInCrossSumNum = 0;
		int carInReverseLaneNum = 0;
		String[] roadIDList = Main.MapCross.get(carToCrossID).getRoadIDList();
		ArrayList<Road> roadList = new ArrayList<Road>();
		for (String s : roadIDList)
		{
			if (!s.equals("-1"))
				roadList.add(Main.MapRoad.get(s));
		}
		for (Road road : roadList)
		{
			// road��toCross=car.curFromCross
			// ****************Ѱ�Ҳ�����ĵ�·
			if (road.getFromCrossID().equals(carToCrossID))
			{
				// ������������ͬһroad������lane�ϵ�· ��������ڵĻ���
				for (Lane lane : road.getBackwardLane())
				{
					// ���lane��û�г�
					if (lane.carsInLane == null)
					{
						carInReverseLaneNum = 0;
					} else
					{
						carInReverseLaneNum += lane.carsInLane.size();
					}
				}
			}
			// forward��·
			LinkedList<Lane> forwardLanes = road.getForwardLane();
			for (Lane lane : forwardLanes)
			{
				if (lane.carsInLane == null)
				{
					carInCrossSumNum += 0;
				} else
				{
					carInCrossSumNum += lane.carsInLane.size();
				}
			}

			// ������ڷ����·,Ҳ�����з����lane
			if (road.getBackwardLane() != null)
			{
				LinkedList<Lane> backwardLanes = road.getBackwardLane();
				for (Lane lane : backwardLanes)
				{
					if (lane.carsInLane == null)
					{
						carInCrossSumNum += 0;
					} else
					{
						carInCrossSumNum += lane.carsInLane.size();
					}
				}
			}
		}

		// ��ȥ��������ͬһroad������lane�ϵ�· ��������ڵĻ���
		carInCrossNum = carInCrossSumNum - carInReverseLaneNum;
		return carInCrossNum;

	}

}

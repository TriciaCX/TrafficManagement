package info;

import java.util.Comparator;
import vo.Road;

/**
 * ��дRoad�ıȽϷ��� ����roadlength��road��������
 * 
 * @version 2019-03-20
 */
public class RoadComparator implements Comparator<Road>
{
	public int compare(Road r1, Road r2)
	{ // ����·�ĳ��ȶ�·����
		if (r1.getRoadLength() > r2.getRoadLength())
			return 1;
		else if (r1.getRoadLength() < r2.getRoadLength())
			return -1;
		else
			return 0;
	}
}

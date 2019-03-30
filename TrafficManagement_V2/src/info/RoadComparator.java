package info;

import java.util.Comparator;
import vo.Road;

/**
 * 重写Road的比较方法 根据roadlength对road排序，升序
 * 
 * @version 2019-03-20
 */
public class RoadComparator implements Comparator<Road>
{
	public int compare(Road r1, Road r2)
	{ // 根据路的长度对路排序
		if (r1.getRoadLength() > r2.getRoadLength())
			return 1;
		else if (r1.getRoadLength() < r2.getRoadLength())
			return -1;
		else
			return 0;
	}
}

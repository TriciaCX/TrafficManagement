package util;

import java.util.Comparator;

import vo.Car;

/**
 * 重写Car的比较方法
 * 
 * @version 2019-03-16
 */
public class MyComparator implements Comparator<Car>
{
	public int compare(Car o1, Car o2)
	{ // 对car按照最大速度排序，升序
		Car c1 = (Car) o1;
		Car c2 = (Car) o2;
		if (c1.getMaxVelocity() > c2.getMaxVelocity())
			return 1;
		else if (c1.getMaxVelocity() < c2.getMaxVelocity())
			return -1;
		else
			return 0;
	}
}

package util;

import java.util.Comparator;

import vo.Car;

/**
 * ��дCar�ıȽϷ���
 * 
 * @version 2019-03-16
 */
public class MyComparator implements Comparator<Car>
{
	public int compare(Car o1, Car o2)
	{ // ��car��������ٶ���������
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

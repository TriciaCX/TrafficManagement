package util;

import java.util.Comparator;

import vo.Car;

/**
 * ��дCar�ıȽϷ���
 * @author Tricia
 * @version 2019-03-16
 */
public class MyComparator implements Comparator<Object>
{
    public int compare(Object o1,Object o2) { //��car��������ٶ����򣬽���
    	Car c1 = (Car)o1;
    	Car c2 = (Car)o2;
    	if(c1.getMaxVelocity()>c2.getMaxVelocity())
    		return 1;
    	else if(c1.getMaxVelocity()<c2.getMaxVelocity())
    		return -1;
    	else 
    		return 0;
    }
}
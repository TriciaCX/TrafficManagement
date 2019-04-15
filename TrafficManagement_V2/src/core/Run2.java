package core;

import debug.ShowDetail;
import vo.Car;
import vo.Cross;


import java.util.LinkedList;
import java.util.Map;

import static debug.ShowDetail.testShowMapInfo;
import static util.PostprocUtil.updateAns;
import static util.RunUtil2.*;


public class Run2 {
    public static void run(Map<String, String> ansMap, String[] ans) {
        int t = 1;
        //�ڵ�ǰʱ�̴ӳ��ⷢ��
        carsFromGarageInsertToRoad(t);
        //���Ŵӳ������ĳ��ˣ������Ժ�ͳһ�����ı�־λ���ó�true,ͬʱҪ�Ѵӳ������ĳ��ӵ���·�ϵĳ���ȥ
        setNowInRoadCarFromGarageWait();

        //���泵��·��
        updateAns(ansMap, ans);
        System.out.println("����ʼ������");
        System.out.println("��");
        ShowDetail.testShowCarInfo(Main.NowInRoadCar);
        testShowMapInfo();


        while (true) {
            //�ѱ�־λ�����false
            setNowInRoadCarState(false);

            t++;
            if(t==2070)
                System.out.println();

            //�ж��ǲ������г������Ź��ˣ�sheng����0��ѽ��Ҳ����˵λ�ö��������,��ʵ��Ϊ�˱�֤����ĳ�

            System.out.println("At time slot " + t);
            
            //�ȸ���·��id���򣬸��� ·�ϳ���״̬
            while (!isAllReal()) {
                for (int i = 0; i < Main.listCross.size(); i++) {

                    //����ĸ�������������������� ����4���� �����������
                    Cross s = Main.listCross.get(i);
                    LinkedList<Car> carsFour = extractFourCar(s);
                    if (carsFour.isEmpty()) {
                        continue;
                    }
                    System.out.println("����֮ǰ��");
                    ShowDetail.testShowCarInfo(carsFour);
                    //��ȡ�����ġ�4����������һЩ�в���
                    FourCarStateUnionProcess(carsFour, t);
                    System.out.println("����֮��");
                    ShowDetail.testShowCarInfo(carsFour);
                    System.out.println("_____");

                }
            }
            
            // ·�ϵĳ��Ѿ���������
            //�ѱ�־λ�����false,��������ȡ��ͷ����
            setNowInRoadCarState(false);

            //���泵��·��
            updateAns(ansMap, ans);

            //�ڵ�ǰʱ�̴ӳ��ⷢ��
            carsFromGarageInsertToRoad(t);

            //���Ŵӳ������ĳ��ˣ������Ժ�ͳһ�����ı�־λ���ó�true,ͬʱҪ�Ѵӳ������ĳ��ӵ���·�ϵĳ���ȥ
            setNowInRoadCarFromGarageWait();

            //֮ǰ��ԭ����·�ϵĳ������false,��������Ҫȫ��Ϊtrue
            setNowInRoadCarState(true);

            //���泵��·��
            updateAns(ansMap, ans);

            System.out.println("��" + t + "ʱ�̡�+�������ⷢ����");
            ShowDetail.testShowCarInfo(Main.NowInRoadCar);
            testShowMapInfo();

            if (isAllArrived()) {
                break;
            }


        }

    }
}

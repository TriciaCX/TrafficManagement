package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vo.Car;
import vo.Cross;
import vo.Lane;
import vo.Road;

public class PreprocUtil {
    //************************************Car data process************************************

    /**
     * @param strings ��ʽΪ(id,from,to,speed,planTime)����(10000, 18, 50, 8, 3)����Ӧ���ݸ�ʽstring,string,string,int,string.���˴�Ĭ�϶���Ķ���string
     * @param ansMap  �����ڳ������й����д��복����ǰ��·����ʱ��Ϣ
     * @param ans     ������·�滮��������Ϣ
     * @return
     */
    public static ArrayList<Car> PreCarData(String[] strings, Map<String, String> ansMap, String[] ans) {
        //����һ�����ڴ��car��Ϣ��carList.
        ArrayList<Car> carList = new ArrayList<Car>();
        //�������ݵ�������

        int num = strings.length;
        //���ǵ���һ����#��ͷ��ע����Ϣ��Ӧ��i=1��ʼ��
   
        for (int i = 1; i < num; i++) {
            //ȥ����
            strings[i] = strings[i].replaceAll("\\(|\\)", "");
            //ȥ�ո�
            strings[i] = strings[i].replaceAll(" ", "");
            //������, tempStr[0]-id, tempStr[1]-from, tempStr[2]-to,tempStr[3]-speed,tempStr[4]-planTime,
            String[] tempStr = strings[i].split(",");
            ans[i] = tempStr[0];
            // ��ansMapԤ����һЩ��Ϣ��ȥ

            ansMap.put(tempStr[0], null);
            //����tempStr�ж��������ʵ����car

            int maxVelocity = Integer.valueOf(tempStr[3]);
            int planTime = Integer.valueOf(tempStr[4]);
            Car c = new Car(tempStr[0], tempStr[1], tempStr[2], maxVelocity, planTime);
            carList.add(c);
        }
        return carList;
    }

    /**
     * @param carList
     * @return HashMap<String,Car>���ڿ��ٻ�ȡ����
     */
    public static HashMap<String, Car> PreCarDataMap(ArrayList<Car> carList) {
        HashMap<String, Car> carMap = new HashMap<String, Car>();
        for (Car car : carList) {
            carMap.put(car.getCarID(), car);
        }
        return carMap;
    }

    //************************************Road data process************************************

    /**
     * @param strings road���ݸ�ʽ--(id,length,speed,channel,from,to,isDuplex)������·id����·���ȣ�������٣�������Ŀ����ʼ��id���յ�id���Ƿ�˫��ע��1��˫��0������
     *                ��Ӧ�ĸ�ʽΪ---string,int,int,int,Cross,Cross,boolean
     * @return ArrayList<Road> roadList
     */
    public static ArrayList<Road> PreRoadData(String[] strings) {
        ArrayList<Road> roadList = new ArrayList<Road>();
        // �������ݵ�������

        int num = strings.length;
        //���ǵ���һ����#��ͷ��ע����Ϣ��Ӧ��i=1��ʼ��

        for (int i = 1; i < num; i++) {
            //ȥ����
            strings[i] = strings[i].replaceAll("\\(|\\)", "");
            //ȥ�ո�
            strings[i] = strings[i].replaceAll(" ", "");
            //������, tempStr[0]-id, tempStr[1]-length, tempStr[2]-speed, tempStr[3]-channel,tempStr[4]-from,tempStr[5]-to,tempStr[6]-isDuplex   
            String[] tempStr = strings[i].split(",");
            int length = Integer.valueOf(tempStr[1]);
            int speed = Integer.valueOf(tempStr[2]);
            int channel = Integer.valueOf(tempStr[3]);
            boolean isDuplex;
            isDuplex = tempStr[6].equals("1");
            Road road = new Road(tempStr[0], length, speed, channel, tempStr[4], tempStr[5], isDuplex);
            //��ô˵��Ҫ�ȼ�һ����·��
            for (int j = 0; j < road.getLanesNum(); j++) {
                Lane lane = new Lane(j);
                road.getForwardLane().add(lane);

            }
            if (isDuplex) {
                //�����˫���·
                for (int j = 0; j < road.getLanesNum(); j++) {
                    Lane lane = new Lane(j);
                    road.getBackwardLane().add(lane);
                }

            }

            roadList.add(road);
        }
        return roadList;
    }


    /**
     * ����ǵ����·�����·��id����Ϊ-1
     *
     * @param roadList
     * @return HashMap<String,Road> roadMap
     */
    public static HashMap<String, Road> PreRoadDataMap(ArrayList<Road> roadList) {
        HashMap<String, Road> roadMap = new HashMap<>();
        for (Road road : roadList) {
            roadMap.put(road.getRoadID(), road);
        }
        Road jiaRoad = new Road("-1");
        roadMap.put("-1", jiaRoad);
        return roadMap;
    }


//************************************Cross data process************************************

    /**
     * @param strings cross���ݸ�ʽ--(id,roadId,roadId,roadId,roadId),(·��id,��·id,��·id,��·id,��·id)��-��-��-�� ע��-1��ʾû�и�����·
     * @param roadMap
     * @return
     */

    public static ArrayList<Cross> PreCrossData(String[] strings, HashMap<String, Road> roadMap) {
        ArrayList<Cross> crossList = new ArrayList<Cross>();
        int num = strings.length;
        for (int i = 1; i < num; i++) {
            strings[i] = strings[i].replaceAll("\\(|\\)", "");
            strings[i] = strings[i].replaceAll(" ", "");
            /**
             * ������, tempStr[0]-crossId, tempStr[1]-roadId, tempStr[2]-roadId, tempStr[3]-roadId,tempStr[4]-roadId
             */

            String[] tempStr = strings[i].split(",");
            ArrayList<Road> roadIDList = new ArrayList<Road>();
            /**
             *RoadID=-1��ʾû������·
             */
            if (!tempStr[1].equals("-1")) {
                roadIDList.add(roadMap.get(tempStr[1]));
            } else {
                roadIDList.add(roadMap.get("-1"));
            }
            if (!tempStr[2].equals("-1")) {
                roadIDList.add(roadMap.get(tempStr[2]));
            } else {
                roadIDList.add(roadMap.get("-1"));
            }
            if (!tempStr[3].equals("-1")) {
                roadIDList.add(roadMap.get(tempStr[3]));
            } else {
                roadIDList.add(roadMap.get("-1"));
            }
            if (!tempStr[4].equals("-1")) {
                roadIDList.add(roadMap.get(tempStr[4]));
            } else {
                roadIDList.add(roadMap.get("-1"));
            }
            Cross cross = new Cross(tempStr[0], roadMap.get(tempStr[1]), roadMap.get(tempStr[2]), roadMap.get(tempStr[3]), roadMap.get(tempStr[4]), roadIDList);
            crossList.add(cross);
        }
        return crossList;
    }

    /**
     * @param crossList
     * @return
     */
    public static HashMap<String, Cross> PreCrossDataMap(ArrayList<Cross> crossList) {
        HashMap<String, Cross> crossMap = new HashMap<>();
        for (Cross cross : crossList) {
            crossMap.put(cross.getCrossID(), cross);
        }
        return crossMap;
    }
}




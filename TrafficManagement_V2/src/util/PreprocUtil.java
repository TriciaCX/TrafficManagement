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
     * @param strings 格式为(id,from,to,speed,planTime)，如(10000, 18, 50, 8, 3)，对应数据格式string,string,string,int,string.但此处默认读入的都是string
     * @param ansMap  用于在程序运行过程中存入车辆当前道路的暂时信息
     * @param ans     车辆道路规划的完整信息
     * @return
     */
    public static ArrayList<Car> PreCarData(String[] strings, Map<String, String> ansMap, String[] ans) {
        //构建一个用于存放car信息的carList.
        ArrayList<Car> carList = new ArrayList<Car>();
        //读入数据的总组数

        int num = strings.length;
        //考虑到第一个是#开头的注释信息，应从i=1开始读
   
        for (int i = 1; i < num; i++) {
            //去括号
            strings[i] = strings[i].replaceAll("\\(|\\)", "");
            //去空格
            strings[i] = strings[i].replaceAll(" ", "");
            //分数据, tempStr[0]-id, tempStr[1]-from, tempStr[2]-to,tempStr[3]-speed,tempStr[4]-planTime,
            String[] tempStr = strings[i].split(",");
            ans[i] = tempStr[0];
            // 给ansMap预先填一些信息进去

            ansMap.put(tempStr[0], null);
            //根据tempStr中读入的数据实例化car

            int maxVelocity = Integer.valueOf(tempStr[3]);
            int planTime = Integer.valueOf(tempStr[4]);
            Car c = new Car(tempStr[0], tempStr[1], tempStr[2], maxVelocity, planTime);
            carList.add(c);
        }
        return carList;
    }

    /**
     * @param carList
     * @return HashMap<String,Car>用于快速获取对象
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
     * @param strings road数据格式--(id,length,speed,channel,from,to,isDuplex)，（道路id，道路长度，最高限速，车道数目，起始点id，终点id，是否双向）注：1：双向；0：单向
     *                相应的格式为---string,int,int,int,Cross,Cross,boolean
     * @return ArrayList<Road> roadList
     */
    public static ArrayList<Road> PreRoadData(String[] strings) {
        ArrayList<Road> roadList = new ArrayList<Road>();
        // 读入数据的总组数

        int num = strings.length;
        //考虑到第一个是#开头的注释信息，应从i=1开始读

        for (int i = 1; i < num; i++) {
            //去括号
            strings[i] = strings[i].replaceAll("\\(|\\)", "");
            //去空格
            strings[i] = strings[i].replaceAll(" ", "");
            //分数据, tempStr[0]-id, tempStr[1]-length, tempStr[2]-speed, tempStr[3]-channel,tempStr[4]-from,tempStr[5]-to,tempStr[6]-isDuplex   
            String[] tempStr = strings[i].split(",");
            int length = Integer.valueOf(tempStr[1]);
            int speed = Integer.valueOf(tempStr[2]);
            int channel = Integer.valueOf(tempStr[3]);
            boolean isDuplex;
            isDuplex = tempStr[6].equals("1");
            Road road = new Road(tempStr[0], length, speed, channel, tempStr[4], tempStr[5], isDuplex);
            //怎么说都要先加一个道路的
            for (int j = 0; j < road.getLanesNum(); j++) {
                Lane lane = new Lane(j);
                road.getForwardLane().add(lane);

            }
            if (isDuplex) {
                //如果是双向道路
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
     * 如果是单向道路则反向道路的id设置为-1
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
     * @param strings cross数据格式--(id,roadId,roadId,roadId,roadId),(路口id,道路id,道路id,道路id,道路id)上-右-下-左 注：-1表示没有该条道路
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
             * 分数据, tempStr[0]-crossId, tempStr[1]-roadId, tempStr[2]-roadId, tempStr[3]-roadId,tempStr[4]-roadId
             */

            String[] tempStr = strings[i].split(",");
            ArrayList<Road> roadIDList = new ArrayList<Road>();
            /**
             *RoadID=-1表示没有这条路
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




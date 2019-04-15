package util;

import core.Main;
import vo.Car;
import java.util.Map;

public class PostprocUtil {

    /**
     * @param ansMap 在程序运行过程中存储的车辆当前道路的暂时信息
     * @param ans    车辆道路规划的完整信息
     */
    public static void updateAns(Map<String, String> ansMap, String[] ans) {

        for (Car c : Main.listCar) {
            updateAns(c, ansMap, ans);
        }
    }

    /**
     * 规划数据更新到ansMap中
     *
     * @param c 车辆对象，一顿操作以后ansMap存的是roadid,roadid,roadid,roadid,(最后是逗号）,ans存的是CarID，实际出发时间，(最后是逗号）
     * @param ansMap
     * @param ans
     */
    protected static void updateAns(Car c, Map<String, String> ansMap, String[] ans) {


        /**
         * 分数据, tempStr[0]-carID, tempStr[1]-CrossStartTime, tempStr[2]-Road
         */

        String[] tempStr = new String[2];
        tempStr[0] = c.getCarID();
        tempStr[1] = String.valueOf(c.getRoadID());
        boolean flag = false;
        /**
         * 如果 ansMap里面 这辆车的value是null,那这辆车 就没有给他找过路，他的开始时间就一定是-1，如果这是第一次给他找路，也就是说他在一条路上
         */
        if (ansMap.get(tempStr[0]) == null && !c.getRoadID().equals("-1")) {
            /**
             * 如果是第一次给这辆车进行规划，就要把这辆车的实际出发时间放进ans，ans的 格式是carID，CrossStartTime
             */
            for (int i = 0; i < ans.length; i++) {
                if (ans[i].equals(tempStr[0])) {
                    ans[i] = ans[i].concat(", " + String.valueOf(c.getRealStartTime()) + ", ");
                    break;
                }
            }
            /**
             *第一次出现在路上
             */
            flag = true;


        }
        /**
         * 把后面的规划加进去，更新这辆车的规划信息
         */


        if (flag) {
            /**
             *如果是第一次出现在路上
             */
            ansMap.put(tempStr[0], tempStr[1] + ", ");
        } else if (ansMap.get(tempStr[0]) != null) {
            ansMap.put(tempStr[0], ansMap.get(tempStr[0]).concat(tempStr[1] + ", "));
        } else {
            /**如果还没有上路
             *
             */
        }

    }

    /**
     * 都规划好了，把ansMap的数据更新到ans中
     * @param ansMap
     * @param ans
     */
    public static void ansMapTOans(Map<String, String> ansMap, String[] ans) {
        //首先处理一下ansMap,有可能会有连续都是一个路ID的
        for (int i = 1; i < ans.length; i++) {
            //sb就是carID
   
            StringBuilder sb = new StringBuilder(ans[i].substring(0, ans[i].indexOf(',')));
            //各种路ID，路ID
   
            String carDriveInfo = ansMap.get(sb.toString());
            String carDriveRoads = removeDuplicatedRoadId(carDriveInfo);
            ans[i] = ans[i].concat(carDriveRoads);
            ans[i] = "(" + ans[i] + ")";

        }

    }

    /**
     * 去掉重复传出的路
     * @param s
     * @return
     */
    protected static String removeDuplicatedRoadId(String s) {
        //先去掉所有的空格，然后根据，分隔
        String[] tempStr = s.replaceAll(" ", "").split(",");
        StringBuilder ans = new StringBuilder();
        String cmp = tempStr[0];
        ans.append(cmp + ",");
        int i = 0;

        while (i < tempStr.length) {
            if (tempStr[i].equals(cmp)) {
                i++;
            }
            else {

                cmp = tempStr[i];
                ans.append(tempStr[i] + ",");
            }
            i++;
        }
        // 最后一个不要
        return ans.substring(0, ans.length() - 1).toString();
    }
}

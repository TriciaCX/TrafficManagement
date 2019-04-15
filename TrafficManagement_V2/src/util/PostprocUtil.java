package util;

import core.Main;
import vo.Car;
import java.util.Map;

public class PostprocUtil {

    /**
     * @param ansMap �ڳ������й����д洢�ĳ�����ǰ��·����ʱ��Ϣ
     * @param ans    ������·�滮��������Ϣ
     */
    public static void updateAns(Map<String, String> ansMap, String[] ans) {

        for (Car c : Main.listCar) {
            updateAns(c, ansMap, ans);
        }
    }

    /**
     * �滮���ݸ��µ�ansMap��
     *
     * @param c ��������һ�ٲ����Ժ�ansMap�����roadid,roadid,roadid,roadid,(����Ƕ��ţ�,ans�����CarID��ʵ�ʳ���ʱ�䣬(����Ƕ��ţ�
     * @param ansMap
     * @param ans
     */
    protected static void updateAns(Car c, Map<String, String> ansMap, String[] ans) {


        /**
         * ������, tempStr[0]-carID, tempStr[1]-CrossStartTime, tempStr[2]-Road
         */

        String[] tempStr = new String[2];
        tempStr[0] = c.getCarID();
        tempStr[1] = String.valueOf(c.getRoadID());
        boolean flag = false;
        /**
         * ��� ansMap���� ��������value��null,�������� ��û�и����ҹ�·�����Ŀ�ʼʱ���һ����-1��������ǵ�һ�θ�����·��Ҳ����˵����һ��·��
         */
        if (ansMap.get(tempStr[0]) == null && !c.getRoadID().equals("-1")) {
            /**
             * ����ǵ�һ�θ����������й滮����Ҫ����������ʵ�ʳ���ʱ��Ž�ans��ans�� ��ʽ��carID��CrossStartTime
             */
            for (int i = 0; i < ans.length; i++) {
                if (ans[i].equals(tempStr[0])) {
                    ans[i] = ans[i].concat(", " + String.valueOf(c.getRealStartTime()) + ", ");
                    break;
                }
            }
            /**
             *��һ�γ�����·��
             */
            flag = true;


        }
        /**
         * �Ѻ���Ĺ滮�ӽ�ȥ�������������Ĺ滮��Ϣ
         */


        if (flag) {
            /**
             *����ǵ�һ�γ�����·��
             */
            ansMap.put(tempStr[0], tempStr[1] + ", ");
        } else if (ansMap.get(tempStr[0]) != null) {
            ansMap.put(tempStr[0], ansMap.get(tempStr[0]).concat(tempStr[1] + ", "));
        } else {
            /**�����û����·
             *
             */
        }

    }

    /**
     * ���滮���ˣ���ansMap�����ݸ��µ�ans��
     * @param ansMap
     * @param ans
     */
    public static void ansMapTOans(Map<String, String> ansMap, String[] ans) {
        //���ȴ���һ��ansMap,�п��ܻ�����������һ��·ID��
        for (int i = 1; i < ans.length; i++) {
            //sb����carID
   
            StringBuilder sb = new StringBuilder(ans[i].substring(0, ans[i].indexOf(',')));
            //����·ID��·ID
   
            String carDriveInfo = ansMap.get(sb.toString());
            String carDriveRoads = removeDuplicatedRoadId(carDriveInfo);
            ans[i] = ans[i].concat(carDriveRoads);
            ans[i] = "(" + ans[i] + ")";

        }

    }

    /**
     * ȥ���ظ�������·
     * @param s
     * @return
     */
    protected static String removeDuplicatedRoadId(String s) {
        //��ȥ�����еĿո�Ȼ����ݣ��ָ�
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
        // ���һ����Ҫ
        return ans.substring(0, ans.length() - 1).toString();
    }
}

package util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * ����Ԥ����--δʵ��������ʱ���ã�
 * @param  �����car.txt��ʱ�䡢˳�����
 * car���ݸ�ʽ(id,from,to,speed,planTime)����(10000, 18, 50, 8, 3)����Ӧ���ݸ�ʽstring,cross,cross,int,string.���˴�Ĭ�϶���Ķ���string
 * @author Tricia
 * @version 2019-3-15
 */
public class PreprocUtil {
	/**
	 * @param strings
	 * @return
	 */
	public HashMap<String,ArrayList<ArrayList>> Prepro(String[] strings){
		HashMap<String,ArrayList<ArrayList>> plantimeCarMap = new HashMap<String,ArrayList<ArrayList>>();
		ArrayList<ArrayList> list = new ArrayList<ArrayList>();
		int num = strings.length;   //�ж�����car������,��1��ʼ��������ע����Ϣ
		for(int i=1;i<num;i++) {    //���ǵ���һ����#��ͷ��ע����Ϣ��Ӧ��i=1��ʼ��
			strings[i]= strings[i].replaceAll("\\(|\\)", "");    //ȥ����
		    //������, tempStr[0]-id, tempStr[0]-id, tempStr[1]-from, tempStr[2]-to,tempStr[3]-speed,tempStr[4]-planTime,
			String[] tempStr = strings[i].split(","); 
			ArrayList<String> array = new ArrayList<String>();
			for(int j=0;j<5;j++) {
				array.add(tempStr[i]); //��һ��car�����ݴ����һ��array��
			}
			list.add(array);  //���õ���һ��car�����ݷ���list��
		}
		//���ٶ�����
		MyComparator mc = new MyComparator();
        Collections.sort(list, mc);
		//����planTime����
		//���õ���car���ݼ�list������ʱ��ֵ�HashMap-plantimeCarData��
		for(int i=0;i<list.size();i++){
		   plantimeCarMap.put((list.get(i)).get(4).toString(), list.get(i));
		}
		
		//���ٶ�����
//		List<Map.Entry<String,ArrayList<ArrayList>>> sortlist = new ArrayList<Map.Entry<String,ArrayList<ArrayList>>>(plantimeCarMap.entrySet());
//		Collections.sort(sortlist, new Comparator<Map.Entry<String,ArrayList<ArrayList>>>(){
//			@Override
//			public int compare(Entry<String, ArrayList<ArrayList>> o1, Entry<String, ArrayList<ArrayList>> o2)
//			{
//				return o2.getValue().compareTo(o1.getValue());
//			}
//		});
		return plantimeCarMap;
	}
}

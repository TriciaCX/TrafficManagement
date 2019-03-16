package util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * 数据预处理--未实例化（暂时弃用）
 * @param  传入的car.txt按时间、顺序分类
 * car数据格式(id,from,to,speed,planTime)，如(10000, 18, 50, 8, 3)，对应数据格式string,cross,cross,int,string.但此处默认读入的都是string
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
		int num = strings.length;   //有多少组car的数据,从1开始读，跳过注释信息
		for(int i=1;i<num;i++) {    //考虑到第一个是#开头的注释信息，应从i=1开始读
			strings[i]= strings[i].replaceAll("\\(|\\)", "");    //去括号
		    //分数据, tempStr[0]-id, tempStr[0]-id, tempStr[1]-from, tempStr[2]-to,tempStr[3]-speed,tempStr[4]-planTime,
			String[] tempStr = strings[i].split(","); 
			ArrayList<String> array = new ArrayList<String>();
			for(int j=0;j<5;j++) {
				array.add(tempStr[i]); //将一辆car的数据存放在一个array中
			}
			list.add(array);  //将得到的一个car的数据放入list中
		}
		//对速度排序
		MyComparator mc = new MyComparator();
        Collections.sort(list, mc);
		//根据planTime分类
		//将得到的car数据集list按出发时间分到HashMap-plantimeCarData中
		for(int i=0;i<list.size();i++){
		   plantimeCarMap.put((list.get(i)).get(4).toString(), list.get(i));
		}
		
		//对速度排序
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

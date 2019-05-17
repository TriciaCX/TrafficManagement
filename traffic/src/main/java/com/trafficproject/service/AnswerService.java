package com.trafficproject.service;

import java.util.Map;

public interface AnswerService {
    /**
     * @param ansMap 在程序运行过程中存储的车辆当前道路的暂时信息
     * @param ans    车辆道路规划的完整信息
     */
    void updateAns(Map<String, String> ansMap, String[] ans);

    /**
     * 都规划好了，把ansMap的数据更新到ans中
     *
     * @param ansMap
     * @param ans
     */
    void ansMapTOans(Map<String, String> ansMap, String[] ans);

    /**
     *
     * @param filePath 输出文件路径
     * @param contents 要写入的内容
     * @param append 是否追加
     */
    void write(final String filePath, final String[] contents, final boolean append);

}

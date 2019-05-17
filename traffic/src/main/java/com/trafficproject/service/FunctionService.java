package com.trafficproject.service;

public interface FunctionService {
    /**
     * sigmoid函数
     *
     * @param x
     * @return
     */
    float sigmoid(int x);

    float swish(int x);

    void adjustW();


}

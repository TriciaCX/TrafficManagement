package com.trafficproject.service.impl;

import com.trafficproject.service.BaseService;
import com.trafficproject.service.FunctionService;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Component
@Aspect
public class FunctionServiceImpl extends BaseService implements FunctionService {
    /**
     * sigmoid函数
     */
    public float sigmoid(int x) {
        return (float) (1 / (1 + Math.exp(-x)));
    }

    public float swish(int x) {
        return x * sigmoid(x);
    }

    @Pointcut("execution(* com.trafficproject.service.impl.ManagementServiceImpl.findNextCross())")
    public void find(){}

    @After("find()")
    public void adjustW() {
        if (numOf2 != 0) {
            w[1] = sigmoid(numOf5 / numOf2);
        } else {

            w[1] = sigmoid(numOf5 / (numOf2 + 1));
        }
        w[2] = (float) ((float) w[1] * 0.5);
        w[0] = 1 - w[1] - w[2];
        w[1] *= 1;
        System.out.println("AOP works");
    }

}

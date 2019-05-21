package com.trafficproject;

import com.trafficproject.dao.CarDOMapper;
import com.trafficproject.dataobject.CarDO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Hello world!
 *
 */


@SpringBootApplication(scanBasePackages = "com.trafficproject")
@MapperScan(value = "com.trafficproject.dao")
@RestController
public class App 
{
    @Autowired
    private CarDOMapper carDOMapper;

    @RequestMapping("/")
    public String home(){
        //CarDO carDO = carDOMapper.selectByPrimaryKey(1);
        CarDO carDO = carDOMapper.selectByCarId("10000");
        if(carDO ==null){
            return "用户对象不存在";
        }else{
            return carDO.getMaxvelocity().toString();
        }

    }

    public static void main( String[] args )
    {
        System.out.println( "欢迎进入智能城市交通规划平台" );
        SpringApplication.run(App.class,args);
    }
}

package com.trafficproject;

import com.trafficproject.dao.CarDOMapper;
import com.trafficproject.dataobject.CarDO;
import com.trafficproject.service.CarService;
import com.trafficproject.service.FunctionService;
import com.trafficproject.service.ManagementService;
import com.trafficproject.service.impl.ManagementServiceImpl;
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

    @Autowired
    private CarService carService;

    @Autowired
    private FunctionService functionService;

    @Autowired
    private ManagementService managementService;


//    @RequestMapping("/")

    public void home(){
        //CarDO carDO = carDOMapper.selectByPrimaryKey(1);
        CarDO carDO = carDOMapper.selectByCarId("10000");
//        if(carDO ==null){
//            return "用户对象不存在";
//        }else{
//            return carDO.getMaxvelocity().toString();
//        }
        if(carDO ==null){
            System.out.println("用户对象不存在");
        }else{
            System.out.println(carDO.getMaxvelocity().toString());
        }

    }
    public void a(){
        this.managementService=new ManagementServiceImpl();
        if(!managementService.isAllArrived())
            System.out.println("1");
        else
            System.out.println("0");
    }

    public static void main( String[] args )
    {
        System.out.println( "欢迎进入智能城市交通规划平台" );
        SpringApplication.run(App.class,args);

    }
}

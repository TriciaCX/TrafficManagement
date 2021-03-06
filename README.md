![](https://img.shields.io/badge/language-Java-orange.svg)
[![](https://img.shields.io/badge/CSDN-@冰美式-red.svg)](https://me.csdn.net/weixin_43277507)

说明：本项目来源于2019年华为软件挑战赛赛题，后续对其进行了改进

# 一、项目简介

针对物联网下的只会城市建设问题，设计了城市智能交通管理系统，根据城市道路网的实时情况，为车辆提供最新的道路规划方案，优化城市道路系统，提升用户出行体验。

1、 在模拟的道路图上为每一辆车规划行驶路线，系统会自动根据规划路线运行。

2、在路线合法的前提下，最终所有车辆按照规划的路线到达目的地。

# 二、模型假设

1、车辆

1）每辆车在上路之后所占道路空间相同，设为1

2）不允许超车、掉头、变道、主动降速

3）经过路口时，车辆行驶的优先顺序为：直行>左转>右转

2、道路

1）同一道路不同车道最高限速相同

2）道路的长度设为整数，且最小长度的道路也足够长，行驶速度最快的车也不能一个时刻经过两个路口

3、路口

1）路口本身没有“空间”，车辆经过路口不计算时间，不占用空间

2）路口会连接多条道路，但当前道路的车只会直接影响同一道路之后的车

# 三、模型建立

## 1、状态类型定义

### 路上行驶状态

 状态1：下一时刻能够路口的车或者即将到家的车（到家是过路口的一种特殊情况），且为所在道路的头车

 状态2：下一时刻不能过路口的车，且为所在道路的头车

 状态3：同方向同道路跟在状态1后面的车

 状态4：同方向同道路跟在状态2后面的车

 状态5：状态1的车当前时刻的转换状态

### 车库状态

当前时刻可发车：计划出发时间早于或等于当前规划时间

当前时刻不可发车：计划出发时间迟于当前规划时间

### 到家状态

到达目的地的车，不占用道路资源，从车库删去

## 2、状态更新机制

详情参见流程图

<img src="https://github.com/TriciaCX/TrafficManagement/blob/master/TrafficManagement_V2/resources/%E6%96%B9%E6%A1%88%E8%AE%BE%E8%AE%A1%E5%9B%BE.png" width = 80% height = 80%  div align=center/>

# 四、算法设计

- 寻路算法 

  综合考虑道路拥堵影响因子、路口拥堵影响因子、路径长度影响因子，采用动态的自适应Dijkstra算法，根据道路实时交通情况为车辆规划道路，提升了寻路性能。

  cost1: 目标道路同行驶方向车道内的车辆占据的道路空间与道路总长度的比值（归一化）

  cost2: 目标路口各道路的道路拥堵影响因子之和

  cost3: 当前路口到目的路口途径所有道路的归一化长度之和（道路长度归一化指目标道路与最长道路长度之比）

- 出车算法

  综合考虑目标道路的道路拥堵影响因子和城市道路系统的拥堵系数，动态调整每个路口的出车数量，可以有效降低因大量发车而出现的“死锁”情况。

  **避免死锁的方法**：寻路算法中的道路拥堵影响因子、路口拥堵影响因子以及出车算法中的道路拥堵影响因子，有效地降低了城市道路系统出现因车辆互相等待而无法更新状态导致“死锁”的可能性。

  **城市道路系统拥堵系数**：所有路口拥堵影响因子和的归一化

- 更新车辆状态

  - 在满足交通规则的前提下，更新车辆状态，避免出现因违反交通规则而出现的“回滚”现象，使得系统性能下降。
  - 当道路状态可确定时，及时更新道路状态，有助于提升系统性能。
  - 根据车辆的状态对车辆分类，分别是state1：要过道路的头车 state2：不过路口的头车 state3：在1车后面的车 state4：在2车后面的车 state5：1车在过路口时由于无法确定状态，譬如前方状态未更新，或已更新没有空间，分别变成5false和5true。

# 五、版本说明

## 版本1：【TrafficManagement】
   * 每个规划时刻开始时，对所有车规划路径，看似会得到较优方案，但在实际状态更新时会产生大量因优先级和道路容量等造成的回滚问题。系统很脆弱，难以承受较大的数据规模。

## 版本2：【TrafficManagementV2】
   * 优化路口取车方式：每个规划时刻开始时，按路口id顺序，抽取每个路口的头车，对可过路口的车规划路径，并及时进行道路状态更新。
   
   * 优化寻路方法：引入了道路拥堵系数、路口拥堵系数、全网拥堵系数的概念，实时反馈到寻路的cost计算中，实现了自适应智能寻路。

## 版本3：【Traffic--施工中】

* 引入springBoot+MyBatis框架
* 用IDEA——Maven（方便多了）
* 采用前后端分离的设计理念，增加了前端UI的设计，增加了用户交互性

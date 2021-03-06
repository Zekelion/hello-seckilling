## 写一个自己的秒杀系统

### Usage

**相关依赖**

MongoDB ^v3.4.10

Redis 3.2.10

Kafka 2.1.0

**运行步骤**

1.使用脚本初始化数据库

```
mongo bin/initDB.js
```

todo...

___

### 实现功能篇

项目将会构建一个提供API的秒杀Web Server，侧重于对后端服务的实现，另外，会将库存服务也一同集成在本项目中，最终将会主要实现以下功能

* 秒杀商品服务
  * 商品列表
  * 商品详情

* 秒杀服务
  * 秒杀活动预热
  * 秒杀活动倒计时
  * 秒杀下单
  * 秒杀抢单结果

* 秒杀订单信息服务
  * 秒杀订单详情

* 秒杀库存服务
  * 下单减库存

Note: 用户信息不作为本项目实现重点，将会使用数据库中初始化的一个测试用户代替，在实际的应用中，用户上下文传递与身份校验可以交由API Gateway进行处理
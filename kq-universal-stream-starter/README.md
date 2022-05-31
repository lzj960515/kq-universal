# 消息队列组件
## 功能

已完成：

- 使用注解即可监听消息中间件的消息

feature:

- 死信队列：消息消费失败或者长时间未消费转入死信队列

## 使用方式

1. 引入依赖

   ```xml
   <dependency>
     <groupId>com.github.lzj960515</groupId>
     <artifactId>kq-universal-stream-starter</artifactId>
     <version>1.4.1</version>
   </dependency>
   <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-data-redis</artifactId>
   </dependency>
   ```

   > 目前只实现了Redis的消息机制，所以还需要引入redis相关依赖

2. 配置Redis（使用Spring的配置）

   ```yaml
   spring:
     redis:
       host: 192.168.1.12
       port: 6379
       password: 123456
   ```

3. 使用

   1. 在需要监听的方法上加上注解`StreamListener`，例如：

   ```java
   @StreamListener(queue = "user")
   public void onUserMessage(StreamChannel<User> streamChannel){
     User user = streamChannel.getMessage();
     log.info("消费queue:{}中的信息:{}, 消息id:{}", streamChannel.getQueue(),  streamChannel.getMessage(),  streamChannel.getRecordId());
     streamChannel.acknowledge();
   }
   ```

   > queue: 监听的队列名称
   >
   > 注意：方法中的参数必须为且只为StreamChannel<T>, T为泛型
   >
   > 消费消息完毕后需要手动ack，保证消息消费成功

   2. 发送消息

      注入消息处理器`MessageHandler`，调用发送消息方法即可

      ```java
      @Resource
      private MessageHandler messageHandler;
      
      @GetMapping("/send")
      public String sendTest(){
        User user = new User();
        user.setName("王五");
        user.setAge(12);
        return messageHandler.convertAndSend("user", user);
      }
      ```

      > 注意：请保证发送消息的queue的消息类型与监听消息中的泛型一致 

4. 多组消费者

   有时消息发布者发布了一条消息，可能多个消费者同时消费（广播模式）

   比如，订单服务发布了一个下单的消息，库存服务消费消息减库存，支付服务支付，积分服务加积分

   在StreamListener注解中提供了以下配置

   ```java
   public @interface StreamListener {
   
       String queue();
   
       /**
        * 消费者组，不同的消费者组可以消费同一条消息
        */
       String group() default "group-1";;
   
       /**
        * 消费者组中的消费者，对于一条消息，只能被一个组中的某一个消费者消费
        * 如 两个组：group1 group2
        * group1中具有3个消费者， group2中也有三个消费者
        * group1能消费这条消息，但是3个消费者中只能有一个消费者消费消息
        * group2也能消费这条消息，但是3个消费者中也只能有一个消费者消费消息
        */
       String consumer() default "consumer-1";
   
       /**
        * 每次拉取消息的数量
        */
       int batchSize() default 10;
   
   }
   ```

   栗子：

   ```java
   @StreamListener(queue = "order", group = "pay")
   public void OnOrderMessage1(StreamChannel<Order> streamChannel){
     Order order = streamChannel.getMessage();
     log.info("消费queue:{}中的信息:{}, 消息id:{}", streamChannel.getQueue(),  streamChannel.getMessage(),  streamChannel.getRecordId());
     streamChannel.acknowledge();
   }
   
   @StreamListener(queue = "order", group = "store")
   public void OnOrderMessage2(StreamChannel<Order> streamChannel){
     Order order = streamChannel.getMessage();
     log.info("消费queue:{}中的信息:{}, 消息id:{}", streamChannel.getQueue(),  streamChannel.getMessage(),  streamChannel.getRecordId());
     streamChannel.acknowledge();
   }
   ```

   > 以上代码便实现了同一个队列中，不同的消费者组同时消费一条消息
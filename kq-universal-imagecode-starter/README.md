# 图片验证码组件
![样子](doc/example.png)
## 功能

- 获取图片和验证码
- 直接发送图片验证码给前端，校验验证码（无感知，推荐）

## 注意
由于生成验证码使用了awt包，所以要把docker文件中的jdk的镜像改为：openjdk:8-jdk-oracle


## 使用方式

1. 引入依赖

   ```xml
   <dependency>
     <groupId>com.github.lzj960515</groupId>
     <artifactId>kq-universal-imagecode-starter</artifactId>
     <version>1.5.0</version>
   </dependency>
   <!-- 可选 -->
   <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-data-redis</artifactId>
   </dependency>
   ```

   > 如果你想用推荐方式，则需要引入spring-redis依赖，因为需要StringRedisTemplate, 如果项目本来就有，就不用引入了

2. 配置Redis（使用Spring的配置）

   ```yaml
   spring:
     redis:
       host: 192.168.1.12
       port: 6379
       password: 123456
   ```
   > 推荐方式配置
3. 配置验证码图片参数

   ```yaml
   kq:
     image-code:
       # 验证码字符个数
       code-count: 5
       # 图片的高度
       height: 40
       # 图片的宽度
       width: 120
       # 验证码干扰线数
       line-count: 100
       # 验证码过期时间
       code-expire-seconds: 60
   ```

   > 以上为默认配置，如果直接使用默认配置，可以不配（建议不配）

4. 使用

   如果你使用了推荐的方式，则：

   ```java
   @Resource
   private ImageCodeHandler imageCodeHandler;
   
   public void send(HttpServletResponse response) throws IOException {
     imageCodeHandler.sendCodeImage("key", response.getOutputStream());
   }
   
   public boolean check(@PathVariable String code) throws IOException {
     return imageCodeHandler.checkCode("key", code);
   }
   ```

   > key：用户的标识，可以是电话，可以是token，总之是需要保证发验证码和校验的是同一个人

   如果你没有使用推荐的方式，则

   ```java
   /**
     * 模拟缓存
     */
   private final Map<String, String> cache = new ConcurrentHashMap<>();
   
   public void send2(HttpServletResponse response) throws IOException {
     ImageCode imageCode = ImageCodeUtil.createImage();
     ImageCodeUtil.sendImage(imageCode.getBufferedImage(), response.getOutputStream());
     cache.put("key", imageCode.getCode());
   }
   
   public boolean check2(@PathVariable String code) throws IOException {
     String value = cache.get("aaa");
     return value != null && value.equals(code);
   }
   ```
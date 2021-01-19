---
title: RAS+AES混合加密Java端实现
date: 2021-01-15
categories: API加密
tags:
 - API加密


---

上一篇笔记写了一个 API 加解密的构想：[一个前后分离的 API 签名和请求参数加解密构思](https://blog.fengxiuge.top/2021/2021-01-13-api-encrypt.html)。这两天有空就用代码去实现了下，期间踩了 n 多的坑，，，

趁着还没忘光，记录下每个端的实现思路和几个印象比较深刻的坑。

分下面三个日志记录：

1. [RAS+AES混合加密Java端实现](https://blog.fengxiuge.top/2021/2021-01-15-api-encrypt-java.html)
2. [RAS+AES混合加密Vue端实现](https://blog.fengxiuge.top/2021/2021-01-15-api-encrypt-vue.html)
3. [RAS+AES混合加密小程序端实现](https://blog.fengxiuge.top/2021/2021-01-15-api-encrypt-mp.html)

这里是第一篇：RAS+AES混合加密Java端实现



## 实现思路



Java 端，也就是后台服务端，主要任务有下面几个：

* 解密 AES 秘钥

* 验证签名
* 解密 body 中的参数
* 加密返回结果

解密 AES 秘钥肯定要在最前面，验证签名的逻辑紧随其后。所以我把这两步放在了拦截器（interceptor）中，保证到 Controller 的请求是安全的，并且具备解密所需 aesKey 的。

签名算法我采用的是 MD5，也可以采用更安全的 SHA256，但是我觉得 MD5足够了，且 MD5 性能有优势。

解密请求和加密返回结果，我想实现 API 级别的加解密控制，就是我想让哪个接口变成加密接口在Controller的方法上加个注解就行了。

所以解密请求和加密返回结果则分别放在了 RequestBodyAdvice 和 ResponseBodyAdvice 中。



### 解密 AES 秘钥和验签验证签名



解密和验签的过滤器大致逻辑代码如下：

```java
public class SignInterceptor implements HandlerInterceptor {
    private static String rsaPrivitKey = "";
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
    
   @Override
   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
         throws Exception {
     
      // aes时间戳
      String timestamp = request.getHeader("X_TIMESTAMP");
      // aes随机数
      String nonce = request.getHeader("X_NONCE");
      // 加密的aes秘钥
      String encryptedAesKey = request.getHeader("X_EAK");//解密aes秘钥

      //时间戳和随机数缺失直接返回
      if (!StringUtils.hasText(timestamp) || !StringUtils.hasText(nonce)) {
         response.setStatus(461);//返回一个特殊的状态码
         return false;
      }
      // 计算时间差
      long requestTime = Long.parseLong(timestamp);
      long currentTime = System.currentTimeMillis();
      long toleranceTime = currentTime - requestTime;
      // 如果请求时间大于当前时间或者小于最小容忍请求时间, 判定为超时,此处时间设置为30秒
      if (requestTime > currentTime || toleranceTime > 30000) {
         response.setStatus(462);//返回一个特殊的状态码
         return false;
      }
      //如果timestamp和nonce已存在,说明timeout时间内同样的报文之前请求过，可能是请求重放，直接拒绝
      Boolean success = redisTemplate.opsForValue().setIfAbsent(String.format(REDIS_KEY, timestamp, nonce), "", 30000, TimeUnit.MILLISECONDS);
       if (Objects.isNull(success) || !success) {
         response.setStatus(463);//返回一个特殊的状态码
         return false;
      }
      //验证签名,并将解密出的aesKey放入ThreadLocal中向后传递
      boolean right = SignUtil.verifySign(rsaPrivitKey, timestamp, nonce, request);
      if (right) {
         return true;
      }
       //走的这里还没返回true说明验签失败了
      response.setStatus(464);//返回一个特殊的状态码
      return false;
   }

   @Override
   public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
         ModelAndView modelAndView) throws Exception {
       // 清除ThreadLocal中的aesKey
      AESKeyHandler.remove();
   }
```

SignUtil 类代码如下：

```java
public class SignUtil {

    //摘要算法
   private static String MD5 = "MD5";

   public static String md5(String string) {
      byte[] hash;
      try {
         hash = MessageDigest.getInstance(MD5).digest(string.getBytes(StandardCharsets.UTF_8));
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException("MessageDigest不支持MD5Util", e);
      }
      StringBuilder hex = new StringBuilder(hash.length * 2);
      for (byte b : hash) {
         if ((b & 0xFF) < 0x10)
            hex.append("0");
         hex.append(Integer.toHexString(b & 0xFF));
      }
      return hex.toString();
   }

   /**
    * md5签名
    * 按参数名称升序，将参数值进行连接 签名
    */
   public static String sign(TreeMap<String, String> params) {
      StringBuilder paramValues = new StringBuilder();

      for (Map.Entry<String, String> entry : params.entrySet()) {
         paramValues.append(entry.getValue());
      }
      return md5(paramValues.toString());
   }

   /**
    * 请求参数签名验证
    */
   public static boolean verifySign(String privateKey, String timestamp, String nonce, HttpServletRequest request)
         throws Exception {
      TreeMap<String, String> params = new TreeMap<>();
      params.put("timestamp", timestamp);
      params.put("nonce", nonce);

      // 加密的aes秘钥
      String encryptedAesKey = request.getHeader("X_EAK");
       //解密aes秘钥
      byte[] aesKeyByte = RSAUtil.decrypt(Base64Util.decode(encryptedAesKey), privateKey);
      String aesKey = new String(aesKeyByte, StandardCharsets.UTF_8);
      params.put("aesKey", aesKey);
       //将aesKey放入ThreadLocal向后传递
      AESKeyHandler.set(aesKey);
      //取出请求头中的签名
      String signStr = request.getHeader("X_SIGN");
      if (StringUtils.isEmpty(signStr)) {
         throw new RuntimeException("There is no SIGN field in the request header!");
      }
      //读取参数存入 treeMap
      Enumeration<String> enu = request.getParameterNames();
      while (enu.hasMoreElements()) {
         String paramName = enu.nextElement().trim();
         params.put(paramName, URLDecoder.decode(request.getParameter(paramName), "UTF-8"));
      }
      //读取body中的参数存入 treeMap
      String bodyString = request.getBody();
      params.put("body", bodyString);
      //验证签名
      if (sign(params).equals(signStr)) {
         return true;
      }
      return false;
   }
}
```

AESKeyHandler 类代码如下：

```java
package com.encrypt.interceptor;
/**
 * 用来向后面的RequestBodyAdvice和ResponseBodyAdvice传递解密的aesKey
 */
public class AESKeyHandler {
   private static ThreadLocal<String> aesKeyThreadLocal = new ThreadLocal<>();
   public static String get() {
      return aesKeyThreadLocal.get();
   }
   public static void set(String aesKey) {
      aesKeyThreadLocal.set(aesKey);
   }
   public static void remove() {
      aesKeyThreadLocal.remove();
   }
}
```



### 解密 body 中的参数



我采用 RequestBodyAdvice 的方式在请求进入 Controller 方法之前解密请求体中的加密参数。

一来是因为解密必须在请求进入Controller 方法之前进行。因为请求进入Controller 方法时要进行参数绑定（DataBinding），未解密的请求体肯定会解密失败。

二来我想实现方法级别的加解密控制，就是我想让哪个接口变成加密接口在Controller的方法上加个注解就行了。



定义了两个注解：@Encrypt 和 @Decrypt。

* @Encrypt：标注在 Controller 方法上，标识此方法需要解密请求体。
* @Decrypt：标注在 Controller 方法上，标识此方法需要加密返回结果。

注解代码如下：

```java
/**
* 加密注解，标注在 Controller 方法上，标识此方法需要加密返回结果。
*/
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Encrypt{

}

/**
* 解密注解，标注在 Controller 方法上，标识此方法需要解密请求体。
*/
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Decrypt {

	/**
	 * 请求参数一定要是加密内容
	 */
	boolean required() default false;

	/**
	 * 请求数据时间戳校验时间差
	 * 超过(当前时间-指定时间)的数据认定为伪造
	 */
	long timeout() default 3000;
}
```



DecryptRequestBodyAdvice 类代码如下

```java
@ControllerAdvice
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

   private Logger log = LoggerFactory.getLogger(this.getClass());
   @Autowired
   private SecretKeyConfig secretKeyConfig;

   @Override
   public boolean supports(MethodParameter methodParameter, Type targetType,
         Class<? extends HttpMessageConverter<?>> converterType) {
      Method method = methodParameter.getMethod();
      //解密 body 的前提是：method有 Decrypt 注解且开关打开
      if (Objects.nonNull(method) && method.isAnnotationPresent(Decrypt.class) && secretKeyConfig.isOpen()) {
         return true;
      }
      return false;
   }

   @Override
   public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
         Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
      return body;
   }

   @Override
   public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
         Class<? extends HttpMessageConverter<?>> converterType) {
      log.info("接收到aesKey:{}", AESKeyHandler.get());
         try {
            return new DecryptHttpInputMessage(inputMessage, secretKeyConfig, parameter.getMethod().getAnnotation(Decrypt.class));
         } catch (RuntimeException e) {
            throw e;
         } catch (Exception e) {
            log.error("Decryption failed", e);
         }
      return inputMessage;
   }

   @Override
   public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
         Class<? extends HttpMessageConverter<?>> converterType) {
      return body;
   }
}
```

这里 HttpInputMessage 中的body参数是流的形式，在这里读取了之后参数绑定的时候就读不到了。所以这里要对HttpInputMessage进行一次包装，包装类就是DecryptHttpInputMessage，代码如下：

```java
public class DecryptHttpInputMessage implements HttpInputMessage {

   private Logger logger = LoggerFactory.getLogger(this.getClass());
   private HttpHeaders headers;
   private InputStream body;

   public DecryptHttpInputMessage(HttpInputMessage inputMessage, SecretKeyConfig secretKeyConfig, Decrypt decrypt)
         throws Exception {
      String aesKey = AESKeyHandler.get();
      logger.info("接收到aesKey:{}", aesKey);
      boolean showLog = secretKeyConfig.isShowLog();
      if (StringUtils.isEmpty(aesKey)) {
         throw new IllegalArgumentException("aesKey is null");
      }
      this.headers = inputMessage.getHeaders();
      String content = new BufferedReader(new InputStreamReader(inputMessage.getBody())).lines()
            .collect(Collectors.joining(System.lineSeparator()));

      if (showLog) {
         logger.info("Encrypted data received：{}", content);
      }

      String decryptBody;
      // 如果未加密
      if (content.startsWith("{")) {
         // 必须加密
         if (decrypt.required()) {
            logger.error("Not support unencrypted content:{}", content);
            throw new RuntimeException("Not support unencrypted content:" + content);
         }
         logger.info("Unencrypted without decryption:{}", content);
         decryptBody = content;
      } else {
         StringBuilder json = new StringBuilder();
         content = content.replaceAll(" ", "+");
         if (!StringUtils.isEmpty(content)) {
            String[] contents = content.split("\\|");
            for (String value : contents) {
               value = AESUtil.decrypt(value, aesKey);
               json.append(value);
            }
         }
         decryptBody = json.toString();
         if (showLog) {
            logger.info("After decryption：{}", decryptBody);
         }
      }
      this.body = new ByteArrayInputStream(decryptBody.getBytes());
   }
   @Override
   public InputStream getBody() {
      return body;
   }
   @Override
   public HttpHeaders getHeaders() {
      return headers;
   }
}
```



### 加密返回结果



加密返回结果逻辑主要在 EncryptResponseBodyAdvice 类，代码如下：

```java
@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<HttpResult<Object>> {

   private Logger log = LoggerFactory.getLogger(this.getClass());

   @Autowired
   private SecretKeyConfig secretKeyConfig;

   @Override
   public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
      Method method = returnType.getMethod();
      //解密 body 的前提是：method有 Encrypt 注解且开关打开
      if (Objects.nonNull(method) && method.isAnnotationPresent(Encrypt.class) && secretKeyConfig.isOpen()) {
         return true;
      }
      return false;
   }

   @Override
   public HttpResult<Object> beforeBodyWrite(HttpResult<Object> body, MethodParameter returnType,
         MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
         ServerHttpRequest request, ServerHttpResponse response) {

      HttpResult<Object> result = encryptBody(body);
      if (result != null)
         return result;

      return body;
   }

   private HttpResult<Object> encryptBody(HttpResult<Object> body) {

      String aesKey = AESKeyHandler.get();
      log.info("接收到aesKey:{}", aesKey);

      try {
         //取出data
         Object data = body.getData();
         String content = JsonUtils.writeValueAsString(data);
         if (secretKeyConfig.isShowLog()) {
            log.info("Pre-encrypted data：{}", content);
         }
         if (!StringUtils.hasText(aesKey)) {
            throw new RuntimeException("AES_KEY IS EMPTY!");
         }
         String result = AESUtil.encrypt(content, aesKey);
         if (secretKeyConfig.isShowLog()) {
            log.info("After encryption：{}", result);
         }
         body.setData(result);
         return body;
      } catch (Exception e) {
         log.error("Encrypted data exception", e);
      }
      return null;
   }
}
```



## 遇到的问题

### 只能读取一次的流

DecryptRequestBodyAdvice 类中我们为了解决流只能被读取一次的问题，对 HttpInputMessage 进行了包装。

其实我们还落下了一次，拦截器中也读取了 HttpServletRequest 中的流获取了body，用来签名。

所以这里我们也需要包装一下 HttpServletRequest ，实现方案跟包装 HttpInputMessage 类似，具体代码不往这里贴了。

完整代码已上传gitHub，项目地址：[encrypt-api](https://github.com/yueyakun2017/my-demo)。

### 一个诡异的异常



java.security.InvalidKeyException: Illegal key size or default parameters



具体的异常信息没有保存。这个异常的诡异之处在于，我在公司的电脑上并没有报这个异常。回到家了用自己的电脑测试后台解密的功能的时候报了这个异常。



异常的原因是 Java 默认只支持 AES128 也就是 16 位秘钥，我使用的是 AES256 也就是 32 位秘钥。

要想使用 AES256 需要下载两个增强版的 Jar 包替换原来 JRE 中弱版的 Jar 包。不同版本的 JDK 需要下载不同的增强包。

Java8 对应下载地址是：https://www.oracle.com/java/technologies/javase-jce8-downloads.html。

下载下来之后解压到 %JDK_HOME%\jre\lib\security 目录下，重启下项目就OK了。

## 总结

完整代码已上传gitHub，项目地址：[encrypt-api](https://github.com/yueyakun2017/my-demo)


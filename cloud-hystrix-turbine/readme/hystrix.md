
404问题。4开头的基本上和开发有关系。





# 15 熔断

## 15.1 概念：

### 概述

前面我们学过：

Eureka实现了服务注册与发现

服务间调用。

Ribbon实现了客户端负载均衡

Feign实现了声明式 API调用

这节学习 微服务间的容错



​		在分布式系统下，微服务之间不可避免地会发生相互调用，但每个系统都无法百分之百保证自身运行不出问题。在服务调用中，很可能面临依赖服务失效的问题（网络延时，服务异常，负载过大无法及时响应）。因此需要一个组件，能提供强大的容错能力，为服务间调用提供保护和控制。



我们的目的：***当我自身 依赖的服务不可用时，服务自身不会被拖垮。防止微服务级联异常***。

图。



本质：就是隔离坏的服务，不让坏服务拖垮其他服务（调用坏服务的服务）。



比如：武汉发生疫情，隔离它，不让依赖于武汉的地方感染。

和我们课程中熔断降级更贴切一点：北京从武汉招聘大学生，武汉有疫情了，当北京去武汉请求大学生来的时候，武汉熔断，然后北京启动自身的备用逻辑：去上海找大学生（降级）。





### 舱壁模式

舱壁模式（Bulkhead）隔离了每个工作负载或服务的关键资源，如连接池、内存和CPU，硬盘。每个工作单元都有独立的 连接池，内存，CPU。

使用舱壁避免了单个服务消耗掉所有资源，从而导致其他服务出现故障的场景。
这种模式主要是通过防止由一个服务引起的级联故障来增加系统的弹性。



据说泰坦尼克原因：泰坦尼克号上有16个防水舱，设计可以保障如果只有4个舱进水，密闭和隔离可以阻止水继续进入下一个防水舱，从而保证船的基本浮力。

但是当时冰山从侧面划破了船体，从而导致有5个防水舱同时进水，而为了建造豪华的头等舱大厅，也就是电影里杰克和罗斯约会的地方，5号舱的顶部并未达到密闭所需要的高度，水就一层层进入了船体，隔离的失败导致了泰坦尼克的沉没。

> 舱壁模式



给我们的思路：可以对每个请求设置，单独的连接池，配置连接数，不要影响 别的请求。就像一个一个的防水舱。



对在公司中的管理也一样：给每个独立的 小组，分配独立的资源，比如产品，开发，测试。在小公司，大多数情况 这些资源都是共享的，有一个好处是充分利用资源，坏处是，如果一个项目延期，会影响别的项目推进。自己权衡利弊。

最近比较火的一句话： 真正的知识，是 产品提高一个等级和成本提高0.2元的 痛苦抉择。

### 雪崩效应

​		每个服务 发出一个HTTP请求都会 在 服务中 开启一个新线程。而下游服务挂了或者网络不可达，通常线程会阻塞住，直到Timeout。如果并发量多一点，这些阻塞的线程就会占用大量的资源，很有可能把自己本身这个微服务所在的机器资源耗尽，导致自己也挂掉。

​		如果服务提供者响应非常缓慢，那么服务消费者调用此提供者就会一直等待，直到提供者响应或超时。在高并发场景下，此种情况，如果不做任何处理，就会导致服务消费者的资源耗竭甚至整个系统的崩溃。一层一层的崩溃，导致所有的系统崩溃。

> 《雪崩示意图》

​		雪崩：由基础服务故障导致级联故障的现象。描述的是：提供者不可用 导致消费者不可用，并将不可用逐渐放大的过程。像滚雪球一样，不可用的服务越来越多。影响越来越恶劣。



雪崩三个流程：

服务提供者不可用

重试会导致网络流量加大，更影响服务提供者。

导致服务调用者不可用，由于服务调用者 一直等待返回，一直占用系统资源。

（不可用的范围 被逐步放大）



服务不可用原因：

服务器宕机

网络故障

宕机

程序异常

负载过大，导致服务提供者响应慢

缓存击穿导致服务超负荷运行



总之 ： 基础服务故障  导致 级联故障   就是  雪崩。



### 容错机制

1. 为网络请求设置超时。

   必须为网络请求设置超时。一般的调用一般在几十毫秒内响应。如果服务不可用，或者网络有问题，那么响应时间会变很长。长到几十秒。

   每一次调用，对应一个线程或进程，如果响应时间长，那么线程就长时间得不到释放，而线程对应着系统资源，包括CPU,内存，得不到释放的线程越多，资源被消耗的越多，最终导致系统崩溃。

   因此必须设置超时时间，让资源尽快释放。

2. 使用断路器模式。

   想一下家里的保险丝，跳闸。如果家里有短路或者大功率电器使用，超过电路负载时，就会跳闸，如果不跳闸，电路烧毁，波及到其他家庭，导致其他家庭也不可用。通过跳闸保护电路安全，当短路问题，或者大功率问题被解决，在合闸。

   自己家里电路，不影响整个小区每家每户的电路。

### 断路器

 		如果对某个微服务请求有大量超时（说明该服务不可用），再让新的请求访问该服务就没有意义，只会无谓的消耗资源。例如设置了超时时间1s，如果短时间内有大量的请求无法在1s内响应，就没有必要去请求依赖的服务了。

1. 断路器是对容易导致错误的操作的代理。这种代理能统计一段时间内的失败次数，并依据次数决定是正常请求依赖的服务还是直接返回。
2. 断路器可以实现快速失败，如果它在一段时间内检测到许多类似的错误（超时），就会在之后的一段时间，强迫对该服务的调用快速失败，即不再请求所调用的服务。这样对于消费者就无须再浪费CPU去等待长时间的超时。
3. 断路器也可自动诊断依赖的服务是否恢复正常。如果发现依赖的服务已经恢复正常，那么就会恢复请求该服务。通过重置时间来决定断路器的重新闭合。

   这样就实现了微服务的“自我修复”：当依赖的服务不可用时，打开断路器，让服务快速失败，从而防止雪崩。当依赖的服务恢复正常时，又恢复请求。

> 断路器开关时序图



```sh
第一次正常

第二次提供者异常

提供者多次异常后，断路器打开

后续请求，则直接降级，走备用逻辑。
```



​	断路器状态转换的逻辑：

```
关闭状态：正常情况下，断路器关闭，可以正常请求依赖的服务。

打开状态：当一段时间内，请求失败率达到一定阈值，断路器就会打开。服务请求不会去请求依赖的服务。调用方直接返回。不发生真正的调用。重置时间过后，进入半开模式。

半开状态：断路器打开一段时间后，会自动进入“半开模式”，此时，断路器允许一个服务请求访问依赖的服务。如果此请求成功(或者成功达到一定比例)，则关闭断路器，恢复正常访问。否则，则继续保持打开状态。

断路器的打开，能保证服务调用者在调用异常服务时，快速返回结果，避免大量的同步等待，减少服务调用者的资源消耗。并且断路器能在打开一段时间后继续侦测请求执行结果，判断断路器是否能关闭，恢复服务的正常调用。
```

> 《熔断.doc》《断路器开关时序图》《状态转换》



### 降级

为了在整体资源不够的时候，适当放弃部分服务，将主要的资源投放到核心服务中，待渡过难关之后，再重启已关闭的服务，保证了系统核心服务的稳定。当服务停掉后，自动进入fallback替换主方法。

用fallback方法代替主方法执行并返回结果，对失败的服务进行降级。当调用服务失败次数在一段时间内超过了断路器的阈值时，断路器将打开，不再进行真正的调用，而是快速失败，直接执行fallback逻辑。服务降级保护了服务调用者的逻辑。

```sh
熔断和降级：
共同点：
	1、为了防止系统崩溃，保证主要功能的可用性和可靠性。
	2、用户体验到某些功能不能用。
不同点：
	1、熔断由下级故障触发，主动惹祸。
	2、降级由调用方从负荷角度触发，无辜被抛弃。

```



19年春晚 百度 红包，凤巢的5万台机器熄火4小时，让给了红包。



### Hystrix

spring cloud 用的是 hystrix，是一个容错组件。

Hystrix实现了 超时机制和断路器模式。

Hystrix是Netflix开源的一个类库，用于隔离远程系统、服务或者第三方库，防止级联失败，从而提升系统的可用性与容错性。主要有以下几点功能：

1. 为系统提供保护机制。在依赖的服务出现高延迟或失败时，为系统提供保护和控制。
2. 防止雪崩。
3. 包裹请求：使用HystrixCommand（或HystrixObservableCommand）包裹对依赖的调用逻辑，每个命令在独立线程中运行。
4. 跳闸机制：当某服务失败率达到一定的阈值时，Hystrix可以自动跳闸，停止请求该服务一段时间。
5. 资源隔离：Hystrix为每个请求都的依赖都维护了一个小型线程池，如果该线程池已满，发往该依赖的请求就被立即拒绝，而不是排队等候，从而加速失败判定。防止级联失败。
6. 快速失败：Fail Fast。同时能快速恢复。侧重点是：（不去真正的请求服务，发生异常再返回），而是直接失败。
7. 监控：Hystrix可以实时监控运行指标和配置的变化，提供近实时的监控、报警、运维控制。
8. 回退机制：fallback，当请求失败、超时、被拒绝，或当断路器被打开时，执行回退逻辑。回退逻辑我们自定义，提供优雅的服务降级。
9. 自我修复：断路器打开一段时间后，会自动进入“半开”状态，可以进行打开，关闭，半开状态的转换。前面有介绍。



提问

## 15.2 Hystrix 使用

### hystrix独立使用脱离spring cloud

代码：study-hystrix项目，HelloWorldHystrixCommand类。看着类讲解。

关注点：

继承hystrixCommand

重写run

fallback（程序发生非HystrixBadRequestException异常，运行超时，熔断开关打开，线程池/信号量满了）

熔断（熔断机制相当于电路的跳闸功能，我们可以配置熔断策略为当请求错误比例在10s内>50%时，该服务将进入熔断状态，后续请求都会进入fallback。）

结果缓存（支持将一个请求结果缓存起来，下一个具有相同key的请求将直接从缓存中取出结果，减少请求开销。）



这个例子，只是独立使用hystrix，  通过这个例子，了解 hystrix 的运行逻辑。

### 和restTemplate结合

在api-driver（服务消费端）中：

pom.xml

```sh
<!-- 引入hystrix依赖 -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
		</dependency>
```

启动类

```sh
@EnableCircuitBreaker
```

调用的方法上，通过使用@HystrixCommand，将方法纳入到hystrix监控中。

```sh
@HystrixCommand(fallbackMethod = "sendFail")

下面的service，功能只是：调用service-sms服务。
RestTemplateRequestServiceImpl中的smsSend
```



sendFail，此处需要注意：此方法的 请求参数和 返回参数 要和原方法一致。

```sh
	private ResponseResult sendFail(SmsSendRequest smsSendRequest) {
		
		//备用逻辑
		return ResponseResult.fail(-3, "熔断");
	}
```





正常调用：启动eureka-7900，service-sms 8002，api-driver。

测试点：

1. 访问sms是否正常。
2. 访问yapi：api-driver下：司机获取验证码。是否正常。
3. 停止service-sms。访问司机获取验证码，是否走备用逻辑。



两个注解@EnableCircuitBreaker，@EnableHystrix点进去看，其实一样。

点@EnableHystrix进去。



ps：配置：HystrixCommandProperties



写好方法封装restTemplate 请求的service。一般将HystrixCommand，写在此service。也可以扩大范围。



上面的例子中，如果不走熔断的备用方法，则，停止提供者时，会抛出500错误。



更多的配置：

点击@HystrixCommand 进去。可以看到很多配置项。

下面说一下：commandProperties。

https://github.com/Netflix/Hystrix/wiki/Configuration

打开官网，对比着看一下。

```sh
1、Execution：
用来控制HystrixCommand.run()的执行
具体意义：
execution.isolation.strategy：该属性用来设置HystrixCommand.run()执行的隔离策略。默认为THREAD。
execution.isolation.thread.timeoutInMilliseconds：该属性用来配置HystrixCommand执行的超时时间，单位为毫秒。
execution.timeout.enabled：该属性用来配置HystrixCommand.run()的执行是否启用超时时间。默认为true。
execution.isolation.thread.interruptOnTimeout：该属性用来配置当HystrixCommand.run()执行超时的时候是否要它中断。
execution.isolation.thread.interruptOnCancel：该属性用来配置当HystrixCommand.run()执行取消时是否要它中断。
execution.isolation.semaphore.maxConcurrentRequests：当HystrixCommand命令的隔离策略使用信号量时，该属性用来配置信号量的大小。当最大并发请求达到该设置值时，后续的请求将被拒绝。

2、Fallback：
用来控制HystrixCommand.getFallback()的执行
fallback.isolation.semaphore.maxConcurrentRequests：该属性用来设置从调用线程中允许HystrixCommand.getFallback()方法执行的最大并发请求数。当达到最大并发请求时，后续的请求将会被拒绝并抛出异常。
fallback.enabled：该属性用来设置服务降级策略是否启用，默认是true。如果设置为false，当请求失败或者拒绝发生时，将不会调用HystrixCommand.getFallback()来执行服务降级逻辑。

mock。

3、Circuit Breaker：用来控制HystrixCircuitBreaker的行为。
circuitBreaker.enabled：确定当服务请求命令失败时，是否使用断路器来跟踪其健康指标和熔断请求。默认为true。
circuitBreaker.requestVolumeThreshold：用来设置在滚动时间窗中，断路器熔断的最小请求数。例如，默认该值为20的时候，如果滚动时间窗（默认10秒）内仅收到19个请求，即使这19个请求都失败了，断路器也不会打开。
circuitBreaker.sleepWindowInMilliseconds：用来设置当断路器打开之后的休眠时间窗。休眠时间窗结束之后，会将断路器设置为“半开”状态，尝试熔断的请求命令，如果依然时候就将断路器继续设置为“打开”状态，如果成功，就设置为“关闭”状态。
circuitBreaker.errorThresholdPercentage：该属性用来设置断路器打开的错误百分比条件。默认值为50，表示在滚动时间窗中，在请求值超过requestVolumeThreshold阈值的前提下，如果错误请求数百分比超过50，就把断路器设置为“打开”状态，否则就设置为“关闭”状态。
circuitBreaker.forceOpen：该属性默认为false。如果该属性设置为true，断路器将强制进入“打开”状态，它会拒绝所有请求。该属性优于forceClosed属性。
circuitBreaker.forceClosed：该属性默认为false。如果该属性设置为true，断路器强制进入“关闭”状态，它会接收所有请求。如果forceOpen属性为true，该属性不生效。

4、Metrics：该属性与HystrixCommand和HystrixObservableCommand执行中捕获的指标相关。
metrics.rollingStats.timeInMilliseconds：该属性用来设置滚动时间窗的长度，单位为毫秒。该时间用于断路器判断健康度时需要收集信息的持续时间。断路器在收集指标信息时会根据设置的时间窗长度拆分成多个桶来累计各度量值，每个桶记录了一段时间的采集指标。例如，当为默认值10000毫秒时，断路器默认将其分成10个桶，每个桶记录1000毫秒内的指标信息。
metrics.rollingStats.numBuckets：用来设置滚动时间窗统计指标信息时划分“桶”的数量。默认值为10。
metrics.rollingPercentile.enabled：用来设置对命令执行延迟是否使用百分位数来跟踪和计算。默认为true，如果设置为false，那么所有的概要统计都将返回-1。
metrics.rollingPercentile.timeInMilliseconds：用来设置百分位统计的滚动窗口的持续时间，单位为毫秒。
metrics.rollingPercentile.numBuckets：用来设置百分位统计滚动窗口中使用桶的数量。
metrics.rollingPercentile.bucketSize：用来设置每个“桶”中保留的最大执行数。
metrics.healthSnapshot.intervalInMilliseconds：用来设置采集影响断路器状态的健康快照的间隔等待时间。

5、Request Context：涉及HystrixCommand使用HystrixRequestContext的设置。
requestCache.enabled：用来配置是否开启请求缓存。
requestLog.enabled：用来设置HystrixCommand的执行和事件是否打印到日志的HystrixRequestLog中。

```



通过下面例子，说一下配置方法。大家下去可以参考上面 看需要试试。

```sh
将下面  值  写成false
@HystrixCommand(fallbackMethod = "sendFail",ignoreExceptions = {HystrixIgnoreException.class},
	commandProperties = {
			@HystrixProperty(name = "fallback.enabled",value = "false")
			
	})

则请求，如果熔断，报500，

改成true，则走熔断逻辑。

测试点：
1.默认熔断走降级逻辑。
2.false后，报500.
3.改成true后，走降级逻辑。

```





### 和feign结合

api-passenger

上面的pom一样。

feign自带Hystrix，但是默认没有打开，首先打开Hystrix。(从Spring Cloud Dalston开始，feign的Hystrix 默认关闭，如果要用feign，必须开启)

```sh
feign:
  hystrix:
    enabled: true 
```

注解添加feignclient

```sh
@FeignClient(name = "service-sms",fallback = SmsClientFallback.class)
```

类，实现feignClient接口

```sh
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.online.taxi.common.dto.ResponseResult;
import com.online.taxi.common.dto.sms.SmsSendRequest;
import com.online.taxi.passenger.service.SmsClient;
/**
 * @author yueyi2019
 */
@Component
public class SmsClientFallback implements SmsClient {

	
	@Override
	public ResponseResult sendSms(SmsSendRequest smsSendRequest) {
		System.out.println("不好意思，我熔断了");
		
		return ResponseResult.fail(-3, "feign熔断");
	}

}
```

启动类

```sh
@EnableFeignClients
@EnableCircuitBreaker
```

正常调用：启动eureka-7900，service-sms 8002，api-passenger。

测试点：

1. 访问sms是否正常。

2. 访问yapi：api-passenger下：乘客获取验证码。是否正常。

3. 停止service-sms。访问乘客获取验证码，是否走备用逻辑。

4. 去掉yml中熔断改成false。  熔断是否生效。

   feign:

     hystrix:
       enabled: false 



### 所有（restTemplate和feign）配置默认值

HystrixCommandProperties

```sh
/* --------------统计相关------------------*/ 
// 统计滚动的时间窗口,默认:5000毫秒（取自circuitBreakerSleepWindowInMilliseconds）   
private final HystrixProperty metricsRollingStatisticalWindowInMilliseconds;   
// 统计窗口的Buckets的数量,默认:10个,每秒一个Buckets统计   
private final HystrixProperty metricsRollingStatisticalWindowBuckets; // number of buckets in the statisticalWindow   
// 是否开启监控统计功能,默认:true   
private final HystrixProperty metricsRollingPercentileEnabled;   
/* --------------熔断器相关------------------*/ 
// 熔断器在整个统计时间内是否开启的阀值，默认20。也就是在metricsRollingStatisticalWindowInMilliseconds（默认10s）内至少请求20次，熔断器才发挥起作用   
private final HystrixProperty circuitBreakerRequestVolumeThreshold;   
// 熔断时间窗口，默认:5秒.熔断器中断请求5秒后会进入半打开状态,放下一个请求进来重试，如果该请求成功就关闭熔断器，否则继续等待一个熔断时间窗口
private final HystrixProperty circuitBreakerSleepWindowInMilliseconds;   
//是否启用熔断器,默认true. 启动   
private final HystrixProperty circuitBreakerEnabled;   
//默认:50%。当出错率超过50%后熔断器启动
private final HystrixProperty circuitBreakerErrorThresholdPercentage;  
//是否强制开启熔断器阻断所有请求,默认:false,不开启。置为true时，所有请求都将被拒绝，直接到fallback 
private final HystrixProperty circuitBreakerForceOpen;   
//是否允许熔断器忽略错误,默认false, 不开启   
private final HystrixProperty circuitBreakerForceClosed; 
/* --------------信号量相关------------------*/ 
//使用信号量隔离时，命令调用最大的并发数,默认:10   
private final HystrixProperty executionIsolationSemaphoreMaxConcurrentRequests;   
//使用信号量隔离时，命令fallback(降级)调用最大的并发数,默认:10   
private final HystrixProperty fallbackIsolationSemaphoreMaxConcurrentRequests; 
/* --------------其他------------------*/ 
//使用命令调用隔离方式,默认:采用线程隔离,ExecutionIsolationStrategy.THREAD   
private final HystrixProperty executionIsolationStrategy;   
//使用线程隔离时，调用超时时间，默认:1秒   
private final HystrixProperty executionIsolationThreadTimeoutInMilliseconds;   
//线程池的key,用于决定命令在哪个线程池执行   
private final HystrixProperty executionIsolationThreadPoolKeyOverride;   
//是否开启fallback降级策略 默认:true   
private final HystrixProperty fallbackEnabled;   
// 使用线程隔离时，是否对命令执行超时的线程调用中断（Thread.interrupt()）操作.默认:true   
private final HystrixProperty executionIsolationThreadInterruptOnTimeout; 
// 是否开启请求日志,默认:true   
private final HystrixProperty requestLogEnabled;   
//是否开启请求缓存,默认:true   
private final HystrixProperty requestCacheEnabled; // Whether request caching is enabled. 
```

HystrixThreadPoolProperties

```sh
/* 配置线程池大小,默认值10个 */ 
private final HystrixProperty corePoolSize; 
/* 配置线程值等待队列长度,默认值:-1 建议值:-1表示不等待直接拒绝,测试表明线程池使用直接决绝策略+ 合适大小的非回缩线程池效率最高.所以不建议修改此值。 当使用非回缩线程池时，queueSizeRejectionThreshold,keepAliveTimeMinutes 参数无效 */
private final HystrixProperty maxQueueSize; 
```



### 捕获熔断的异常信息

1. restTemplate中：

在备用方法中 api-driver

```sh
	public ResponseResult sendFail(ShortMsgRequest shortMsgRequest,Throwable throwable) {
		log.info("异常信息："+throwable);
		//备用逻辑
		return ResponseResult.fail(-1, "熔断");
	}
```

加上一个Throwable，就Ok。

上面例子跑一便。停止服务提供者，测试结果如下：

```sh
2020-02-01 23:00:44.182  INFO [api-driver,f1100452d8b33b08,874b9cac5fe20385,true] 18088 --- [SmsController-1] c.o.t.driver.controller.SmsController    : 异常信息：java.lang.IllegalStateException: No instances available for SERVICE-SMS
```



不走异常，就走500方法。



2. feign中：

注解

```sh
@FeignClient(name = "service-sms",fallbackFactory = SmsClientFallbackFactory.class)

```

factory类

```sh
package com.online.taxi.passenger.fallback;

import org.springframework.stereotype.Component;

import com.online.taxi.common.dto.ResponseResult;
import com.online.taxi.common.dto.sms.SmsSendRequest;
import com.online.taxi.passenger.feign.SmsClient;

import feign.hystrix.FallbackFactory;

@Component
public class SmsClientFallbackFactory implements FallbackFactory<SmsClient> {

	@Override
	public SmsClient create(Throwable cause) {
		return new SmsClient() {
			
			@Override
			public ResponseResult sendSms(SmsSendRequest smsSendRequest) {
				System.out.println("feign异常："+cause);
				return ResponseResult.fail(-3, "feign fallback factory熔断");
			}
		};
	}

}

参数和返回值一样。匿名内部类。
```

测试点：

1. 启动eureka 7900，api-driver,是否走降级方法。

------





3. 忽略异常

有些情况下，提供者是好的，但在消费者发生业务异常时，我们不希望走熔断的备用方法。则用以下两个办法。

1. 第一种方式：继承HystrixBadRequestException

```sh
自定义异常，继承HystrixBadRequestException，当发生此异常时，不走备用方法。

public class BusinessException extends HystrixBadRequestException {
	
	private String message;
	
	public BusinessException(String message) {
		super(message);
		this.message = message;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}

在调用的地方前：
		// 下面是故意跑出异常代码
		try {
			int i = 1/0;
		} catch (Exception e) {
			// TODO: handle exception
			throw new BusinessException("熔断忽略的异常");
		}
```

2. 第二种方式：Hystrix属性配置。

```sh
配置属性：
@HystrixCommand(fallbackMethod = "sendFail",
   ignoreExceptions = {HystrixIgnoreException.class})

自定义异常：

public class HystrixIgnoreException extends RuntimeException {
	
	private String message;
	
	public HystrixIgnoreException(String message) {
		this.message = message;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}

此异常也不走备用逻辑。
```



### 禁用feign客户端的hystrix

为@feignclient单独配置Feign.Builder

配置类

```sh
@Configuration
@ExcudeFeignConfig
public class FeignDisableHystrixConfiguration {
	
	@Bean
	@Scope("prototype")
	public Feign.Builder feignBuilder(){
		return Feign.builder();
	}
}
```

注解

```sh
@FeignClient(name = "service-sms",configuration = FeignDisableHystrixConfiguration.class)

```

测试点：

启动eureka，api-passenger。测试发送验证码，是否走熔断。没走是正确，报500.

------

第6节课完，2020.2.23
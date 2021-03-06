
操作步骤：

1. 启动eureka7900，service-sms 8002，api-driver 9002，
2. 正常访问 yapi->api-driver->司机获取验证码。正常。查看开关，UP。

```sh
http://localhost:9002/actuator/health

hystrix: {
status: "UP"
}
```

3. 关闭 service-sms 8002。
4. 打开jemeter，（检查jmeter设置，api-driver设置日志为info。）设置1秒访问25次(默认10秒 20次，才开始熔断计算)。错误，熔断。查看开关.

```sh
http://localhost:9002/actuator/health

hystrix: {
status: "CIRCUIT_OPEN",
details: {
openCircuitBreakers: [
"RestTemplateRequestServiceImpl::smsSend"
]
}
}
```

5. 恢复UP。启动service-sms 8002,成功请求一次yapi中 司机发送验证码。查看开关。又变成了UP。



熔断计算：先10秒20次，再算错误次数超过阈值 50%。

小结：

1. 注意上面发生的异常信息：有下面不同的2种。

```sh
异常信息：java.lang.IllegalStateException: No instances available for service-sms

异常信息：java.lang.RuntimeException: Hystrix circuit short-circuited and is OPEN
```

2. 上节课开关不生效.

   原因：我最后讲 熔断忽略的异常时，走了忽略的异常，不走熔断。所以开关没打开。

   此次熔断触发的条件：1、走熔断处理，2、依赖服务停止。

   熔断恢复：1、底层服务启动，2、成功请求一次。



课下问题:

1. 两个eureka，彼此注册，为什么 连个eureka里面都有 彼此。1向2注册，2将1信息同步给1,2向1注册。
2. eureka server中的url和eureka client 中的url没关系。没必要一致。



### 断路器开关演示

在项目中引入

```sh
<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
```

访问健康地址：

```sh
http://localhost:9002/actuator/health
最开始：
hystrix: {
status: "UP"
}


HystrixCommandProperties   default_circuitBreakerRequestVolumeThreshold（在hyxtrix的properties中设置）
10秒内，20次失败（20 requests in 10 seconds），则断路器打开。
hystrix: {
status: "CIRCUIT_OPEN",
details: {
openCircuitBreakers: [
"SmsController::verifyCodeSend"
]
}
}
```



相关的配置，主要是10秒20次，失败率超过 50%。

```sh
Execution相关的属性的配置：
hystrix.command.default.execution.isolation.strategy 隔离策略，默认是Thread, 可选Thread｜Semaphore
thread 通过线程数量来限制并发请求数，可以提供额外的保护，但有一定的延迟。一般用于网络调用
semaphore 通过semaphore count来限制并发请求数，适用于无网络的高并发请求
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds 命令执行超时时间，默认1000ms
hystrix.command.default.execution.timeout.enabled 执行是否启用超时，默认启用true
hystrix.command.default.execution.isolation.thread.interruptOnTimeout 发生超时是是否中断，默认true
hystrix.command.default.execution.isolation.semaphore.maxConcurrentRequests 最大并发请求数，默认10，该参数当使用ExecutionIsolationStrategy.SEMAPHORE策略时才有效。如果达到最大并发请求数，请求会被拒绝。理论上选择semaphore size的原则和选择thread size一致，但选用semaphore时每次执行的单元要比较小且执行速度快（ms级别），否则的话应该用thread。
semaphore应该占整个容器（tomcat）的线程池的一小部分。

Fallback相关的属性
这些参数可以应用于Hystrix的THREAD和SEMAPHORE策略
hystrix.command.default.fallback.isolation.semaphore.maxConcurrentRequests 如果并发数达到该设置值，请求会被拒绝和抛出异常并且fallback不会被调用。默认10
hystrix.command.default.fallback.enabled 当执行失败或者请求被拒绝，是否会尝试调用hystrixCommand.getFallback() 。默认true

Circuit Breaker相关的属性
hystrix.command.default.circuitBreaker.enabled 用来跟踪circuit的健康性，如果未达标则让request短路。默认true
hystrix.command.default.circuitBreaker.requestVolumeThreshold 一个rolling window内最小的请求数。如果设为20，那么当一个rolling window的时间内（比如说1个rolling window是10秒）收到19个请求，即使19个请求都失败，也不会触发circuit break。默认20
hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds 触发短路的时间值，当该值设为5000时，则当触发circuit break后的5000毫秒内都会拒绝request，也就是5000毫秒后才会关闭circuit。默认5000
hystrix.command.default.circuitBreaker.errorThresholdPercentage错误比率阀值，如果错误率>=该值，circuit会被打开，并短路所有请求触发fallback。默认50，即为50%。
hystrix.command.default.circuitBreaker.forceOpen 强制打开熔断器，如果打开这个开关，那么拒绝所有request，默认false
hystrix.command.default.circuitBreaker.forceClosed 强制关闭熔断器 如果这个开关打开，circuit将一直关闭且忽略circuitBreaker.errorThresholdPercentage

Metrics相关参数
hystrix.command.default.metrics.rollingStats.timeInMilliseconds 设置统计的时间窗口值的，毫秒值，circuit break 的打开会根据1个rolling window的统计来计算。若rolling window被设为10000毫秒，则rolling window会被分成n个buckets，每个bucket包含success，failure，timeout，rejection的次数的统计信息。默认10000
hystrix.command.default.metrics.rollingStats.numBuckets 设置一个rolling window被划分的数量，若numBuckets＝10，rolling window＝10000，那么一个bucket的时间即1秒。必须符合rolling window % numberBuckets == 0。默认10
hystrix.command.default.metrics.rollingPercentile.enabled 执行时是否enable指标的计算和跟踪，默认true
hystrix.command.default.metrics.rollingPercentile.timeInMilliseconds 设置rolling percentile window的时间，默认60000
hystrix.command.default.metrics.rollingPercentile.numBuckets 设置rolling percentile window的numberBuckets。逻辑同上。默认6
hystrix.command.default.metrics.rollingPercentile.bucketSize 如果bucket size＝100，window＝10s，若这10s里有500次执行，只有最后100次执行会被统计到bucket里去。增加该值会增加内存开销以及排序的开销。默认100
hystrix.command.default.metrics.healthSnapshot.intervalInMilliseconds 记录health 快照（用来统计成功和错误绿）的间隔，默认500ms
    
```



### 熔断强制配置

此处配置强制走熔断方法。。

api-driver中RestTemplateRequestServiceImpl

```sh
例子：
@HystrixCommand(fallbackMethod = "sendFail",ignoreExceptions = {HystrixIgnoreException.class},
	commandProperties = {
			@HystrixProperty(name = "fallback.enabled",value = "true"),
			@HystrixProperty(name = "circuitBreaker.forceOpen",value = "true")
			
	})
演示一下。
```

测试点：启动eureka，service-sms，api-driver

1. 访问直接熔断。

2. 将circuitBreaker.forceOpen改成false，正常返回，（默认为false）

3. 观察异常信息。

   ```sh
   异常信息：java.lang.RuntimeException: Hystrix circuit short-circuited and is OPEN
   ```

   



### 开关例子

HelloWorldHystrixCommand2



```sh
调用次数:1   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: false
调用次数:2   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: false
调用次数:3   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: false
调用次数:4   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: false
调用次数:5   结果:正常调用 Hello testCircuitBreaker 开关是否打开: false
调用次数:6   结果:正常调用 Hello testCircuitBreaker 开关是否打开: false
调用次数:7   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: false
调用次数:8   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: false
调用次数:9   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: false
调用次数:10   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: true
调用次数:11   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: true
调用次数:12   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: true
调用次数:13   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: true
调用次数:14   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: true
调用次数:15   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: true
调用次数:16   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: true
调用次数:17   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: true
调用次数:18   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: true
调用次数:19   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: true
调用次数:20   结果:正常调用 Hello testCircuitBreaker 开关是否打开: false
调用次数:21   结果:正常调用 Hello testCircuitBreaker 开关是否打开: false
调用次数:22   结果:正常调用 Hello testCircuitBreaker 开关是否打开: false
调用次数:23   结果:正常调用 Hello testCircuitBreaker 开关是否打开: false
调用次数:24   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: false
调用次数:25   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: false
调用次数:26   结果:正常调用 Hello testCircuitBreaker 开关是否打开: false
调用次数:27   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: false
调用次数:28   结果:正常调用 Hello testCircuitBreaker 开关是否打开: false
调用次数:29   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: true
调用次数:30   结果:熔断：fallback,name:testCircuitBreaker 开关是否打开: true
```

细看日志从里面找规律



1. 第10次，熔断开关才打开。之前的 异常 虽然也报错，但是开关没开。（10秒，9次）默认：10秒，20次。
2. 后面有10-19次，总计5秒钟，因为我们设置程序 500毫秒执行。开关一直打开，都走的熔断。（开关打开）
3. 第20次，距离第一次熔断过去了 5秒钟。断路器尝试放开一部分请求过去，正常了就关闭开关。（如果正常，开关关闭，否则，不关闭）
4. 第29次，开关又打开。又到了下一个周期。



### 监控



在服务消费端 api-driver，配置actuator,jar

```sh
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

通过event-stream暴露出来的。hystrix的jar包已经包含了下面这个jar包。

```sh
没必要配。
<dependency>
				<groupId>com.netflix.hystrix</groupId>
				<artifactId>hystrix-metrics-event-stream</artifactId>
				<version>${hystrix.version}</version>
				<exclusions>
					<exclusion>
						<groupId>javax.servlet</groupId>
						<artifactId>servlet-api</artifactId>
					</exclusion>
				</exclusions>
			</dependency>
```

配置需要监控的服务
```yaml
management:
  endpoints:
    web:
      exposure:
        include: '*'
```

启动 eureka 7900，api-driver 9002，service-sms 8002。

地址：

```sh
api-driver
http://localhost:9002/actuator/hystrix.stream

访问，会看到页面一直在ping。

ping: 

data: {"type":"HystrixCommand","name":"SmsClient#sendSms(SmsSendRequest)","group":"service-sms","currentTime":1581931881830,"isCircuitBreakerOpen":false,"errorPercentage":0,"errorCount":0,"requestCount":1,"rollingCountBadRequests":0,"rollingCountCollapsedRequests":0,"rollingCountEmit":0,"rollingCountExceptionsThrown":0,"rollingCountFailure":0,"rollingCountFallbackEmit":0,"rollingCountFallbackFailure":0,"rollingCountFallbackMissing":0,"rollingCountFallbackRejection":0,"rollingCountFallbackSuccess":0,"rollingCountResponsesFromCache":0,"rollingCountSemaphoreRejected":0,"rollingCountShortCircuited":0,"rollingCountSuccess":0,"rollingCountThreadPoolRejected":0,"rollingCountTimeout":0,"currentConcurrentExecutionCount":0,"rollingMaxConcurrentExecutionCount":0,"latencyExecute_mean":0,"latencyExecute":{"0":0,"25":0,"50":0,"75":0,"90":0,"95":0,"99":0,"99.5":0,"100":0},"latencyTotal_mean":0,"latencyTotal":{"0":0,"25":0,"50":0,"75":0,"90":0,"95":0,"99":0,"99.5":0,"100":0},"propertyValue_circuitBreakerRequestVolumeThreshold":20,"propertyValue_circuitBreakerSleepWindowInMilliseconds":5000,"propertyValue_circuitBreakerErrorThresholdPercentage":50,"propertyValue_circuitBreakerForceOpen":false,"propertyValue_circuitBreakerForceClosed":false,"propertyValue_circuitBreakerEnabled":true,"propertyValue_executionIsolationStrategy":"THREAD","propertyValue_executionIsolationThreadTimeoutInMilliseconds":1000,"propertyValue_executionTimeoutInMilliseconds":1000,"propertyValue_executionIsolationThreadInterruptOnTimeout":true,"propertyValue_executionIsolationThreadPoolKeyOverride":null,"propertyValue_executionIsolationSemaphoreMaxConcurrentRequests":10,"propertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests":10,"propertyValue_metricsRollingStatisticalWindowInMilliseconds":10000,"propertyValue_requestCacheEnabled":true,"propertyValue_requestLogEnabled":true,"reportingHosts":1,"threadPool":"service-sms"}

data: {"type":"HystrixThreadPool","name":"service-sms","currentTime":1581931881830,"currentActiveCount":0,"currentCompletedTaskCount":1,"currentCorePoolSize":10,"currentLargestPoolSize":1,"currentMaximumPoolSize":10,"currentPoolSize":1,"currentQueueSize":0,"currentTaskCount":1,"rollingCountThreadsExecuted":0,"rollingMaxActiveThreads":0,"rollingCountCommandRejections":0,"propertyValue_queueSizeRejectionThreshold":5,"propertyValue_metricsRollingStatisticalWindowInMilliseconds":10000,"reportingHosts":1}

```

测试点：

重新 启动eureka7900，service-sms,api-driver

api-driver方。（此时注意，如果熔断了，查看forceOpen）

1. 访问http://localhost:9002/actuator/hystrix.stream。
2. 不发起任何请求，观察页面。一直ping。
3. 发起正常请求（发送验证码），观察页面。ping回来data。查看data。
4. 关闭service-sms，访问（jemeter）。查看data。在页面中搜索："isCircuitBreakerOpen":true





feign和ribbon在这个点上是一样的操作。



### 可视化

上面的操作有点原始，刀耕火种。下面可视化。

项目：hystrix-dashboard

pom

```sh
<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
		</dependency>
```

启动类

```sh
@EnableHystrixDashboard
```





使用 重新启动eureka7900，service-sms,api-driver

访问：http://localhost:6101/hystrix

输入：上面的地址：http://localhost:9002/actuator/hystrix.stream

停止 service-sms 8002 只留 eureka 7900和api-driver 9002

再发一次25次 jmeter。

查看面板，注意面板变化。



面板说明：

github：https://github.com/Netflix-Skunkworks/hystrix-dashboard

解释：https://github.com/Netflix-Skunkworks/hystrix-dashboard/wiki

> 《熔断》



无需纠结它只能监控10秒的信息，因为如果出问题，会一直报问题。



### 集中可视化

上面的方法只能监控一个服务。实际生产中不方便。

> 《Turbine原理》

下面接着改造。



创建study-hystrix-turbine

pom

```sh
<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-turbine</artifactId>
</dependency>
<!-- eureka客户端 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
```

yml

```sh
turbine:
  app-config: api-driver,api-passenger
  cluster-name-expression: "'default'"
```

启动类

```sh
@EnableTurbine
```

地址：http://localhost:6102/turbine.stream,也是一直ping，相当于原来的hystrix.stream,不过此处是综合了所有的项目。

启动hystrix-dashboard。

访问：http://localhost:6101/hystrix

填上上面的地址：http://localhost:6102/turbine.stream



此时注意测试api-driver,api-passenger两个服务。在《熔断中有效果》

停一下service-sms，看界面。



## 15.3 原理

了解前面一些概念：舱壁模式，命令模式（下面），雪崩，容错，断路器，降级。

熔断降级：北京去武汉招大学生的例子。

资源隔离：类似于高铁高架桥，并不是一个整体，而是一块一块的拼装的，一段路坏了，不会影响整条路。



### 隔离策略

概念中的舱壁模式。想一下货船上，每个货仓中间的隔离。两个好处：

1. 服务提供者高延迟或异常，不会影响到整个系统的失败。
2. 能够控制每个调用者的并发度。因为有独立的线程池。



两种线程隔离策略：线程池（默认）、信号量。

> 《Hystrix隔离策略》

@HystrixCommand注释修饰一个服务时，HystrixCommand的运行逻辑有可能是在该请求的主线程上一并执行，也有可能是单独起一个线程来执行，这取决于我们如何设置Hystrix线程的隔离策略。
execution.isolation.strategy属性就是用来设置HystrixCommand.run()执行的隔离策略的。（回忆上面讲过的配置，设置线程策略的）



两种隔离策略：线程隔离和信号量隔离，即“THREAD”和“SEMAPHORE”，系统默认为“THREAD”。
它们的含义是：

THREAD(线程隔离）：使用该方式，HystrixCommand将会在单独的线程上执行，并发请求受线程池中线程数量的限制。不同服务通过使用不同线程池，彼此间将不受影响，达到隔离效果。

此种隔离方式：将调用服务线程与服务访问的执行线程分割开来，调用线程能够空出来去做其他工作，而不至于因为服务调用的执行，阻塞过长时间。

hystrix将使用独立的线程池对应每一个服务提供者，用于隔离和限制这些服务。于是某个服务提供者的高延迟或者资源受限只会发生在该服务提供者对应的线程池中。



SEMAPHORE（信号量隔离）：其实就是个计数器，使用该方式，HystrixCommand将会在调用线程上执行，通过信号量限制单个服务提供者的并发量，开销相对较小（因为不用那么多线程池），并发请求受到信号量个数的限制。 线程隔离会带来线程开销，有些场景（比如无网络请求场景）可能会因为用开销换隔离得不偿失，为此hystrix提供了信号量隔离，当服务的并发数大于信号量阈值时将进入fallback。



Hystrix中默认并且推荐使用线程隔离（THREAD)， 
一般来说，只有当调用负载异常高时（例如每个实例每秒调用数百次）才需要信号量隔离，因为这种场景下使用THREAD开销会比较高。信号量隔离一般仅适用于非网络调用的隔离。 



正常情况下，默认为线程隔离, 保持默认即可。



取舍：

线程池和信号量都支持熔断和限流。相比线程池，信号量不需要线程切换，因此避免了不必要的开销。但是信号量不支持异步，也不支持超时，也就是说当所请求的服务不可用时，信号量会控制超过限制的请求立即返回，但是已经持有信号量的线程只能等待服务响应或从超时中返回，即可能出现长时间等待。线程池模式下，当超过指定时间未响应的服务，Hystrix会通过响应中断的方式通知线程立即结束并返回。



### Hystrix实现思路

1. 请求过来时，将请求的远程调用逻辑，封装到HystrixCommand或者HystrixObservableCommand对象（并在构造方法配置请求被执行需要的参数）中，这些远程调用将会在独立的线程中执行。（资源隔离、命令模式）。

   ```sh
   https://www.runoob.com/design-pattern/command-pattern.html
   介绍
   意图：将一个请求封装成一个对象，从而使您可以用不同的请求对客户进行参数化。
   
   主要解决：在软件系统中，行为请求者与行为实现者通常是一种紧耦合的关系，但某些场合，比如需要对行为进行记录、撤销或重做、事务等处理时，这种无法抵御变化的紧耦合的设计就不太合适。
   
   何时使用：在某些场合，比如要对行为进行"记录、撤销/重做、事务"等处理，这种无法抵御变化的紧耦合是不合适的。在这种情况下，如何将"行为请求者"与"行为实现者"解耦？将一组行为抽象为对象，可以实现二者之间的松耦合。
   
   如何解决：通过调用者调用接受者执行命令，顺序：调用者→接受者→命令。
   
   关键代码：定义三个角色：1、received 真正的命令执行对象 2、Command 3、invoker 使用命令对象的入口
   
   应用实例：struts 1 中的 action 核心控制器 ActionServlet 只有一个，相当于 Invoker，而模型层的类会随着不同的应用有不同的模型类，相当于具体的 Command。
   
   优点： 1、降低了系统耦合度。 2、新的命令可以很容易添加到系统中去。
   
   缺点：使用命令模式可能会导致某些系统有过多的具体命令类。
   
   使用场景：认为是命令的地方都可以使用命令模式，比如： 1、GUI 中每一个按钮都是一条命令。 2、模拟 CMD。
   
   注意事项：系统需要支持命令的撤销(Undo)操作和恢复(Redo)操作，也可以考虑使用命令模式，见命令模式的扩展。
   ```

   

   

2. Hystrix对访问耗时超过设置阈值的请求采用自动超时的策略。该策略对所有的命令都有效。（如果是信号量隔离方式，则此特性失效），超时的阈值可以通过命令配置进行自定义。

3. 为每个服务提供者维护一个线程池（信号量），当线程池（信号量）被占满时，对于该服务提供者的请求将会被直接拒绝（快速失败，走回滚）而不是排队等待，减少系统等待资源。

4. 针对请求服务提供者划分出成功、失效、超时和线程池被占满等情况。

5. 断路器将在请求服务提供者失败次数超过一定阈值后手动或自动切断服务一段时间。

6. 当请求服务提供者出现服务拒绝、超时和 短路（多个服务提供者依次顺序请求，前面的服务提供者请求失败，后面的请求将不再发出）等情况，执行器fallback方法，服务降级。

7. 提供近乎实时的监控和配置变更服务。

   

### hystrix实现流程

1. 构建HystrixCommand或者HystrixObservableCommand对象，用于封装请求，并在构造方法配置请求被执行需要的参数。

2. 执行命令，Hystrix提供了4种执行命令的方法。

3. 检查是否有相同命令执行的缓存，若启用了缓存，且缓存可用，直接使用缓存响应请求。Hystrix支持请求缓存，但需要用户自定义启动。

4. 检查断路器是否打开，如果打开走 第8步。

5. 检查线程池或者信号量是否被消耗完，如果已满，走第8步。

6. 调用HystrixCommand的run 或者 HystrixObservableCommand的construct 执行被封装的调用逻辑，如果执行失败或超时，走第8步。

7. 计算链路的健康情况

8. 在命令执行失败时获取fallback逻辑。

9. 返回响应。

   > 《断路器整体流程》



## 15.4 源码

debug时，注意上面类名的变化。

### 包裹请求

@HystrixCommand，用此注解来包装需要保护的远程调用方法。

```sh
public @interface HystrixCommand {

    /**
     * The command group key is used for grouping together commands such as for reporting,
     * alerting, dashboards or team/library ownership.
     * <p/>
     * default => the runtime class name of annotated method
     *
     * @return group key
     */
     命令分组键：被此注解修饰的命令被归为一组，默认组名：类名。用于报告，预警，面板展示
    String groupKey() default "";

    /**
     * Hystrix command key.
     * <p/>
     * default => the name of annotated method. for example:
     * <code>
     *     ...
     *     @HystrixCommand
     *     public User getUserById(...)
     *     ...
     *     the command name will be: 'getUserById'
     * </code>
     *
     * @return command key
     */
     命令键：默认为注解的方法名，用于区分不同的方法。
    String commandKey() default "";

    /**
     * The thread-pool key is used to represent a
     * HystrixThreadPool for monitoring, metrics publishing, caching and other such uses.
     *
     * @return thread pool key
     */
     线程池键，用来指定执行命令的 hystrixThreadPool
    String threadPoolKey() default "";

    /**
     * Specifies a method to process fallback logic.
     * A fallback method should be defined in the same class where is HystrixCommand.
     * Also a fallback method should have same signature to a method which was invoked as hystrix command.
     * for example:
     * <code>
     *      @HystrixCommand(fallbackMethod = "getByIdFallback")
     *      public String getById(String id) {...}
     *
     *      private String getByIdFallback(String id) {...}
     * </code>
     * Also a fallback method can be annotated with {@link HystrixCommand}
     * <p/>
     * default => see {@link com.netflix.hystrix.contrib.javanica.command.GenericCommand#getFallback()}
     *
     * @return method name
     */
     回调方法名
    String fallbackMethod() default "";

    /**
     * Specifies command properties.
     *
     * @return command properties
     */
     自定义命令相关配置。我们前面讲过有例子
    HystrixProperty[] commandProperties() default {};

    /**
     * Specifies thread pool properties.
     *
     * @return thread pool properties
     */
     自定义线程池相关配置，
    HystrixProperty[] threadPoolProperties() default {};

    /**
     * Defines exceptions which should be ignored.
     * Optionally these can be wrapped in HystrixRuntimeException if raiseHystrixExceptions contains RUNTIME_EXCEPTION.
     *
     * @return exceptions to ignore
     */
     自定义忽略的异常
    Class<? extends Throwable>[] ignoreExceptions() default {};

    /**
     * Specifies the mode that should be used to execute hystrix observable command.
     * For more information see {@link ObservableExecutionMode}.
     *
     * @return observable execution mode
     */
    ObservableExecutionMode observableExecutionMode() default ObservableExecutionMode.EAGER;

    /**
     * When includes RUNTIME_EXCEPTION, any exceptions that are not ignored are wrapped in HystrixRuntimeException.
     *
     * @return exceptions to wrap
     */
    HystrixException[] raiseHystrixExceptions() default {};

    /**
     * Specifies default fallback method for the command. If both {@link #fallbackMethod} and {@link #defaultFallback}
     * methods are specified then specific one is used.
     * note: default fallback method cannot have parameters, return type should be compatible with command return type.
     *
     * @return the name of default fallback method
     */
    String defaultFallback() default "";
}
```

上面的配置，我们大部分情况仅需要关注fallbackMethod，看注释中关于fallback方法的说明，如果需要对线程池和和命令有特殊要求，可进行额外配置，其实99%不需要配置。



HystrixCommandAspect切面

被注解@HystrixCommand修饰的方法，会被HystrixCommand包装执行，通过切面来实现。

```sh
com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect

定义切面
@Around("hystrixCommandAnnotationPointcut() || hystrixCollapserAnnotationPointcut()")
    public Object methodsAnnotatedWithHystrixCommand(final ProceedingJoinPoint joinPoint) throws Throwable {
    
主要地方：
备注：
（
@Pointcut("@annotation(com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand)")

    public void hystrixCommandAnnotationPointcut() {
    }
 ）   
    
此方法主要：构建了MetaHolder（请求必要的信息）,在此方法第一行（Method method = getMethodFromTarget(joinPoint);）打断点。 
鼠标放到joinPoint上面看内容：execution(ResponseResult com.online.taxi.driver.service.impl.RestTemplateRequestServiceImpl.smsSend(SmsSendRequest))

鼠标放上去，查看metaHolder
观察hystrixCommand。

构建MetaHolder
根据MetaHolder构建合适的HystrixCommand
委托CommandExecutor执行HystrixCommand
得到结果

此方法中：
Object result;
        try {
            if (!metaHolder.isObservable()) {
                result = CommandExecutor.execute(invokable, executionType, metaHolder);
            } else {
                result = executeObservable(invokable, executionType, metaHolder);
            }
        } catch (HystrixBadRequestException e) {
            throw e.getCause();
        } catch (HystrixRuntimeException e) {
            throw hystrixRuntimeExceptionToThrowable(metaHolder, e);
        }
此处判断是用HystrixCommand还是HystrixObservableCommand，执行HystrixCommand命令执行。
HystrixCommand：同步，异步执行。
HystrixObservableCommand: 异步回调执行（响应式）。


```



MetaHolder 持有用于构建HystrixCommand和与被包装方法相关的必要信息，如被注解的方法，失败回滚执行的方法等

```sh
com.netflix.hystrix.contrib.javanica.command.MetaHolder

    private final HystrixCollapser hystrixCollapser;
    private final HystrixCommand hystrixCommand;
    private final DefaultProperties defaultProperties;

    private final Method method;被注解的方法。
    private final Method cacheKeyMethod;
    private final Method ajcMethod;
    private final Method fallbackMethod;失败回滚执行的方法。
    private final Object obj;
    private final Object proxyObj;
    private final Object[] args;
    private final Closure closure;
    private final String defaultGroupKey;默认的group键
    private final String defaultCommandKey;默认的命令键
    private final String defaultCollapserKey;合并请求键
    private final String defaultThreadPoolKey;线程池 键
    private final ExecutionType executionType;执行类型
    private final boolean extendedFallback;
    private final ExecutionType collapserExecutionType;
    private final ExecutionType fallbackExecutionType;
    private final boolean fallback;
    private boolean extendedParentFallback;
    private final boolean defaultFallback;
    private final JoinPoint joinPoint;
    private final boolean observable;
    private final ObservableExecutionMode observableExecutionMode;
```



创建HystrixCommand方法如下

```sh
com.netflix.hystrix.contrib.javanica.command.HystrixCommandFactory

    public HystrixInvokable create(MetaHolder metaHolder) {
        HystrixInvokable executable;
        if (metaHolder.isCollapserAnnotationPresent()) {
            executable = new CommandCollapser(metaHolder);
            根据metaHolder.isObservable()来判断，是生成HystrixCommand还是HystrixObservableCommand。
        } else if (metaHolder.isObservable()) {
            executable = new GenericObservableCommand(HystrixCommandBuilderFactory.getInstance().create(metaHolder));
        } else {
            executable = new GenericCommand(HystrixCommandBuilderFactory.getInstance().create(metaHolder));
        }
        return executable;
    }
 
 点击GenericObservableCommand（异步回调执行，也就是响应式）和GenericCommand（同步，异步执行）进去，查看父类发现HystrixObservableCommand和HystrixCommand。
```



ExecutionType

```sh
/**
     * Used for asynchronous execution of command.
     */
    ASYNCHRONOUS,异步

    /**
     * Used for synchronous execution of command.
     */
    SYNCHRONOUS,同步

    /**
     * Reactive execution (asynchronous callback).
     */
    OBSERVABLE;异步回调

    /**
     * Gets execution type for specified class type.
     * @param type the type
     * @return the execution type {@link ExecutionType}
     */
    public static ExecutionType getExecutionType(Class<?> type) {
        if (Future.class.isAssignableFrom(type)) {
            return ExecutionType.ASYNCHRONOUS;
        } else if (Observable.class.isAssignableFrom(type)) {
            return ExecutionType.OBSERVABLE;
        } else {
            return ExecutionType.SYNCHRONOUS;
        }
    }
    
根据被包装方法的返回值类型觉得命令执行的ExecutionType，从而（通过上面代码块中的一步）决定构建HystrixCommand   还是 HystrixObservableCommand。 
方法的返回值为Future：异步执行，rx类型：异步回调，其他类型：同步执行。

@HystrixCommand
public Future<T> find(){}
```

debug到：

```sh
HystrixCommandAspect类中。
create方法。

HystrixCommand hystrixCommand = method.getAnnotation(HystrixCommand.class);
            ExecutionType executionType = ExecutionType.getExecutionType(method.getReturnType());
            
可以看到命令是同步还是异步，又方法的返回值决定。            
```





命令模式在此的应用

```sh
HystrixInvokable是被HystrixCommand标记的接口，继承了它的类，都是可以被执行的HystrixCommand。提供具体方法的为HystrixExecutable。
```



主要的2个类

```sh
public abstract class HystrixCommand<R> extends AbstractCommand<R>

public abstract class HystrixObservableCommand<R> extends AbstractCommand<R>
```

queue和execute

```sh
public abstract class HystrixCommand<R> extends AbstractCommand<R>的下面的方法，
 public Future<R> queue() {
 
 回想study-hystrix中queue的说明，异步执行。execute同步执行。
```



### 断路器

```sh
断路器核心接口：
com.netflix.hystrix.HystrixCircuitBreaker

一个Command key （也就是method）对应一个HystrixCircuitBreaker。

public boolean allowRequest();//是否允许命令执行

public boolean isOpen();//断路器是否打开（开关）

void markSuccess();//在半开状态时，执行成功反馈。将半开转为关闭。

void markNonSuccess();//在半开状态时，执行失败反馈。将半开转为打开。

实现类：HystrixCircuitBreakerImpl
@Override
        public boolean allowRequest() {
            if (properties.circuitBreakerForceOpen().get()) {
                return false;
            }
            if (properties.circuitBreakerForceClosed().get()) {
                return true;
            }
            if (circuitOpened.get() == -1) {
                return true;
            } else {
                if (status.get().equals(Status.HALF_OPEN)) {
                    return false;
                } else {
                    return isAfterSleepWindow();
                }
            }
        }

此处有强制打开，强制关闭，可以通过配置更改。

上面有测试例子（断路器开关强制配置）。	
```



### 统计命令

```sh
com.netflix.hystrix.HystrixMetrics

HystrixCommandMetrics是上面的子类
在断路器的isOpen等方法中，均有对HealthCount的数量的计算，来判断断路器状态：
public boolean isOpen() {
            if (circuitOpen.get()) {
                // if we're open we immediately return true and don't bother attempting to 'close' ourself as that is left to allowSingleTest and a subsequent successful test to close
                return true;
            }

            // we're closed, so let's see if errors have made us so we should trip the circuit open
            HealthCounts health = metrics.getHealthCounts();

            // check if we are past the statisticalWindowVolumeThreshold
            if (health.getTotalRequests() < properties.circuitBreakerRequestVolumeThreshold().get()) {
                // we are not past the minimum volume threshold for the statisticalWindow so we'll return false immediately and not calculate anything
                return false;
            }

            if (health.getErrorPercentage() < properties.circuitBreakerErrorThresholdPercentage().get()) {
                return false;
            } else {
                // our failure rate is too high, trip the circuit
                if (circuitOpen.compareAndSet(false, true)) {
                    // if the previousValue was false then we want to set the currentTime
                    circuitOpenedOrLastTestedTime.set(System.currentTimeMillis());
                    return true;
                } else {
                    // How could previousValue be true? If another thread was going through this code at the same time a race-condition could have
                    // caused another thread to set it to true already even though we were in the process of doing the same
                    // In this case, we know the circuit is open, so let the other thread set the currentTime and report back that the circuit is open
                    return true;
                }
            }
        }

统计数据：
public static class HealthCounts {
        private final long totalCount;执行总数
        private final long errorCount;失败数
        private final int errorPercentage;失败百分比

用滑动窗口的方式统计，一个滑动窗口又划分为几个bucket（滑动窗口时间和bucket成整数倍关系），滑动窗口的移动，以bucket为单位，每个bucket仅统计该时间间隔内的请求数据。，最后将所有窗口中的bucket进行聚合。

```



失败回滚

```sh
AbstractCommand的方法executeCommandAndObserve的局部变量：handleFallback（final Func1<Throwable, Observable<R>> handleFallback）
如果失败，走失败逻辑。
```



## 15.5 总结

各种概念。

使用。restTemplate，feign，监控，可视化。

原理。

源码。

------

第7节课完，2020.3.1
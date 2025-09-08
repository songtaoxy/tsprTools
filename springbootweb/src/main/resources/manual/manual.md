# 手册

# 功能
- controller
  - 统一异常处理. 全局无显示抛异常. 统一异常处理捕获异常: @SneayThrow + Precontion
  - 统一输出: 入参, 返回; 如果有异常, 只打印一次
  - 统计耗时: 从请求到返回
- 统一异常处理
  - 封装各种类型异常
  - 异常提示信息, 支持国际化
  - 统一返回格式
  - 异常判断: controller, service, dao无显式异常抛出代码
    - 受检检异常: 使用@SneakyThrows
    - 非受检异常: guava precontions
- 统一日志
  - 打印controller层请求与响应:AOP 统一打印入参和返回值（@RestController 方法）; 支持 URL、Path、IP、请求耗时、TraceId、线程 ID 等
  - 自动记录所有 @RestController 请求
  - 打印traceId 日志链路: TraceId（通过 Filter + MDC 实现日志链路）
  - 请求参数脱敏
  - 控制哪些字段打印
  - 日志异步输出等
  - 支持日志级别配置（开发调试时输出详细信息，生产环境可关闭返回值打印）
- swagger支持
# 项目结构
- 基本规划: 通用 + 模块
- 通用: common
- 模块: modules/<module_name>

# 功能清单

## 数据库配置

- 见配置中账号信息
- sql: 在项目sql文件夹中. 

## redis及其配置

安装, 及启动

- 平台: mac
- 状态查看, 启动, 关闭
  - 后台作为服务运行并随登录自启：`brew services start redis`；
  - 查看状态：`brew services info redis`；
  - 停止：`brew services stop redis`
- 账号 (default), 密码  不需要

配置, 及api

- yaml配置
- redis.config.common.com.st.RedisConfig
- Spring data redis  -》lettuce (客户端) -〉redis (本地安装的服务)
- RedisTemplate 提供api

## actuator, 及普罗米修斯

监控

- actuator依赖 redis

### maven依赖

```xml
       <!--        prometheus   -->
        <!-- Micrometer Prometheus 注册器 -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>
        <!-- Spring Boot Actuator 用于暴露 /actuator/prometheus -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```

### 配置说明

springboot中, application.yml中

```
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
  endpoint:
    health:
      show-details: never
  metrics:
    tags:
      application: ${spring.application.name}
    distribution:
      percentiles-histogram:
        http.server.requests: true 
```

#### 这一段配置的作用概览

这是 Spring Boot Actuator + Micrometer 的管理与指标配置，用来< b>< u>暴露必要端点、< b>< u>控制健康信息展示粒度、< b>< u>给所有指标统一打标签，以及< b>< u>为 HTTP 指标开启直方图桶以支持分位数分析（如 p95、p99），便于 Prometheus 拉取并做 SLO 监控与告警

#### 各项配置逐条说明

- `management.endpoints.web.exposure.include: health,info,prometheus`
  - 只在 `/actuator` 下暴露 `health`、`info`、`prometheus` 这三个端点
  - 常见访问路径：`/actuator/health`、`/actuator/info`、`/actuator/prometheus`
- `management.endpoint.health.show-details: never`
  - 健康检查仅返回总体状态，不展示各组件细节
  - 可选值：`always`、`when-authorized`、`never`；生产通常用 `never` 或配合权限的 `when-authorized`
- `management.metrics.tags.application: ${spring.application.name}`
  - 给所有 Micrometer 指标加统一标签 `application=<应用名>`，在 Prometheus 中表现为统一的 `application` label，便于按应用聚合与区分环境
- `management.metrics.distribution.percentiles-histogram.http.server.requests: true`
  - 为计时器指标 `http.server.requests` 开启< b>< u>直方图桶导出
  - 在 Prometheus 会生成 `http_server_requests_seconds_bucket/_sum/_count` 这类时间序列，从而可以用 `histogram_quantile(0.95, …)` 计算 p95、p99 等分位数
  - 不开启直方图时，Prometheus端无法用桶数据做分位数近似
  - 代价：每个标签组合会增加一组桶的内存与存储占用，可结合 `management.metrics.distribution.sla`、`minimum-expected-value`、`maximum-expected-value` 做桶边界优化

#### 与分位数相关的补充

- 若希望在 `/actuator/metrics/http.server.requests` 直接看到固定分位数（由应用侧计算并上报），可加：`management.metrics.distribution.percentiles.http.server.requests: 0.95, 0.99`。但在 Prometheus 场景通常优先用直方图桶配合 `histogram_quantile` 在服务端计算
- 直方图适合做 SLO：例如“95% 请求延迟小于 300ms”的告警或看板

#### 一句话总结

- 暴露端点：只开 `health`、`info`、`prometheus`
- 健康细节：隐藏（只给状态）
- 统一标签：给所有指标加 `application=<应用名>`
- 直方图：为 `http.server.requests` 开启桶，支持在 Prometheus 侧计算 p95/p99 等分位数

# Apis

## base Url

 http://localhost:8080/st

## hello

简单测试

- http://localhost:8080/st/hello

```json
{
    "code": "200",
    "msg": "成功",
    "result": {
        "ext": "{hello2=helloworld, hello=helloworld}",
        "base": {
            "timestamp": "2025-09-05T14:10:26.541",
            "traceId": "a1bd7109-dfb7-4868-a252-8bc52b8668e0",
            "tenantId": "default",
            "locale": "中文",
            "ip": "0:0:0:0:0:0:0:1",
            "uri": "/st/hello",
            "contextPath": "/st",
            "servletPath": "/hello",
            "method": "GET"
        }
    }
}
```

## actuator

- http://localhost:8080/st/actuator

```json
{
    "_links": {
        "self": {
            "href": "http://localhost:8080/st/actuator",
            "templated": false
        },
        "prometheus": {
            "href": "http://localhost:8080/st/actuator/prometheus",
            "templated": false
        },
        "health": {
            "href": "http://localhost:8080/st/actuator/health",
            "templated": false
        },
        "health-path": {
            "href": "http://localhost:8080/st/actuator/health/{*path}",
            "templated": true
        },
        "info": {
            "href": "http://localhost:8080/st/actuator/info",
            "templated": false
        }
    }
}
```

## 普罗米修斯, prometheus

- http://localhost:8080/st/actuator/prometheus

```bash
# HELP process_files_open_files The open file descriptor count
# TYPE process_files_open_files gauge
process_files_open_files{application="\"stApps\"",} 272.0
# HELP logback_events_total Number of events that made it to the logs
# TYPE logback_events_total counter
logback_events_total{application="\"stApps\"",level="error",} 0.0
logback_events_total{application="\"stApps\"",level="warn",} 0.0
logback_events_total{application="\"stApps\"",level="trace",} 0.0
logback_events_total{application="\"stApps\"",level="debug",} 0.0
logback_events_total{application="\"stApps\"",level="info",} 12.0
# HELP process_cpu_usage The "recent cpu usage" for the Java Virtual Machine process
# TYPE process_cpu_usage gauge
process_cpu_usage{application="\"stApps\"",} 0.010065478641247108
# HELP executor_queue_remaining_tasks The number of additional elements that this queue can ideally accept without blocking
# TYPE executor_queue_remaining_tasks gauge
executor_queue_remaining_tasks{application="\"stApps\"",name="applicationTaskExecutor",} 2.147483647E9
# HELP system_cpu_count The number of processors available to the Java virtual machine
# TYPE system_cpu_count gauge
system_cpu_count{application="\"stApps\"",} 8.0
# HELP jvm_gc_live_data_size_bytes Size of long-lived heap memory pool after reclamation
# TYPE jvm_gc_live_data_size_bytes gauge
jvm_gc_live_data_size_bytes{application="\"stApps\"",} 0.0
# HELP jvm_gc_pause_seconds Time spent in GC pause
# TYPE jvm_gc_pause_seconds summary
jvm_gc_pause_seconds_count{action="end of minor GC",application="\"stApps\"",cause="Allocation Failure",} 2.0
jvm_gc_pause_seconds_sum{action="end of minor GC",application="\"stApps\"",cause="Allocation Failure",} 0.045
# HELP jvm_gc_pause_seconds_max Time spent in GC pause
# TYPE jvm_gc_pause_seconds_max gauge
jvm_gc_pause_seconds_max{action="end of minor GC",application="\"stApps\"",cause="Allocation Failure",} 0.039
# HELP jvm_classes_unloaded_classes_total The total number of classes unloaded since the Java virtual machine has started execution
# TYPE jvm_classes_unloaded_classes_total counter
jvm_classes_unloaded_classes_total{application="\"stApps\"",} 69.0
# HELP system_cpu_usage The "recent cpu usage" of the system the application is running in
# TYPE system_cpu_usage gauge
system_cpu_usage{application="\"stApps\"",} 0.0
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{application="\"stApps\"",area="nonheap",id="Compressed Class Space",} 1.1349336E7
jvm_memory_used_bytes{application="\"stApps\"",area="nonheap",id="Code Cache",} 2.064256E7
jvm_memory_used_bytes{application="\"stApps\"",area="heap",id="PS Eden Space",} 1.37575272E8
jvm_memory_used_bytes{application="\"stApps\"",area="heap",id="PS Survivor Space",} 1.0927656E7
jvm_memory_used_bytes{application="\"stApps\"",area="heap",id="PS Old Gen",} 1.16773976E8
jvm_memory_used_bytes{application="\"stApps\"",area="nonheap",id="Metaspace",} 8.868648E7
# HELP executor_pool_core_threads The core number of threads for the pool
# TYPE executor_pool_core_threads gauge
executor_pool_core_threads{application="\"stApps\"",name="applicationTaskExecutor",} 8.0
# HELP process_uptime_seconds The uptime of the Java virtual machine
# TYPE process_uptime_seconds gauge
process_uptime_seconds{application="\"stApps\"",} 2535.637
# HELP executor_active_threads The approximate number of threads that are actively executing tasks
# TYPE executor_active_threads gauge
executor_active_threads{application="\"stApps\"",name="applicationTaskExecutor",} 0.0
# HELP jvm_gc_memory_promoted_bytes_total Count of positive increases in the size of the old generation memory pool before GC to after GC
# TYPE jvm_gc_memory_promoted_bytes_total counter
jvm_gc_memory_promoted_bytes_total{application="\"stApps\"",} 16384.0
# HELP jvm_gc_memory_allocated_bytes_total Incremented for an increase in the size of the (young) heap memory pool after one GC to before the next
# TYPE jvm_gc_memory_allocated_bytes_total counter
jvm_gc_memory_allocated_bytes_total{application="\"stApps\"",} 7.09361664E8
# HELP jvm_threads_states_threads The current number of threads
# TYPE jvm_threads_states_threads gauge
jvm_threads_states_threads{application="\"stApps\"",state="terminated",} 0.0
jvm_threads_states_threads{application="\"stApps\"",state="timed-waiting",} 10.0
jvm_threads_states_threads{application="\"stApps\"",state="blocked",} 0.0
jvm_threads_states_threads{application="\"stApps\"",state="waiting",} 15.0
jvm_threads_states_threads{application="\"stApps\"",state="new",} 0.0
jvm_threads_states_threads{application="\"stApps\"",state="runnable",} 11.0
# HELP jvm_threads_peak_threads The peak live thread count since the Java virtual machine started or peak was reset
# TYPE jvm_threads_peak_threads gauge
jvm_threads_peak_threads{application="\"stApps\"",} 43.0
# HELP application_started_time_seconds Time taken (ms) to start the application
# TYPE application_started_time_seconds gauge
application_started_time_seconds{application="\"stApps\"",main_application_class="com.st.WebApplication",} 0.831
# HELP system_load_average_1m The sum of the number of runnable entities queued to available processors and the number of runnable entities running on the available processors averaged over a period of time
# TYPE system_load_average_1m gauge
system_load_average_1m{application="\"stApps\"",} 4.55517578125
# HELP application_ready_time_seconds Time taken (ms) for the application to be ready to service requests
# TYPE application_ready_time_seconds gauge
application_ready_time_seconds{application="\"stApps\"",main_application_class="com.st.WebApplication",} 0.832
# HELP tomcat_sessions_alive_max_seconds  
# TYPE tomcat_sessions_alive_max_seconds gauge
tomcat_sessions_alive_max_seconds{application="\"stApps\"",} 0.0
# HELP executor_completed_tasks_total The approximate total number of tasks that have completed execution
# TYPE executor_completed_tasks_total counter
executor_completed_tasks_total{application="\"stApps\"",name="applicationTaskExecutor",} 0.0
# HELP process_start_time_seconds Start time of the process since unix epoch.
# TYPE process_start_time_seconds gauge
process_start_time_seconds{application="\"stApps\"",} 1.757050004996E9
# HELP executor_queued_tasks The approximate number of tasks that are queued for execution
# TYPE executor_queued_tasks gauge
executor_queued_tasks{application="\"stApps\"",name="applicationTaskExecutor",} 0.0
# HELP tomcat_sessions_active_max_sessions  
# TYPE tomcat_sessions_active_max_sessions gauge
tomcat_sessions_active_max_sessions{application="\"stApps\"",} 0.0
# HELP executor_pool_max_threads The maximum allowed number of threads in the pool
# TYPE executor_pool_max_threads gauge
executor_pool_max_threads{application="\"stApps\"",name="applicationTaskExecutor",} 2.147483647E9
# HELP jvm_buffer_total_capacity_bytes An estimate of the total capacity of the buffers in this pool
# TYPE jvm_buffer_total_capacity_bytes gauge
jvm_buffer_total_capacity_bytes{application="\"stApps\"",id="direct",} 8192.0
jvm_buffer_total_capacity_bytes{application="\"stApps\"",id="mapped",} 0.0
# HELP tomcat_sessions_expired_sessions_total  
# TYPE tomcat_sessions_expired_sessions_total counter
tomcat_sessions_expired_sessions_total{application="\"stApps\"",} 0.0
# HELP tomcat_sessions_rejected_sessions_total  
# TYPE tomcat_sessions_rejected_sessions_total counter
tomcat_sessions_rejected_sessions_total{application="\"stApps\"",} 0.0
# HELP jvm_memory_committed_bytes The amount of memory in bytes that is committed for the Java virtual machine to use
# TYPE jvm_memory_committed_bytes gauge
jvm_memory_committed_bytes{application="\"stApps\"",area="nonheap",id="Compressed Class Space",} 1.2976128E7
jvm_memory_committed_bytes{application="\"stApps\"",area="nonheap",id="Code Cache",} 2.0709376E7
jvm_memory_committed_bytes{application="\"stApps\"",area="heap",id="PS Eden Space",} 3.59661568E8
jvm_memory_committed_bytes{application="\"stApps\"",area="heap",id="PS Survivor Space",} 1.5204352E7
jvm_memory_committed_bytes{application="\"stApps\"",area="heap",id="PS Old Gen",} 2.2020096E8
jvm_memory_committed_bytes{application="\"stApps\"",area="nonheap",id="Metaspace",} 9.7386496E7
# HELP process_files_max_files The maximum file descriptor count
# TYPE process_files_max_files gauge
process_files_max_files{application="\"stApps\"",} 10240.0
# HELP jvm_threads_daemon_threads The current number of live daemon threads
# TYPE jvm_threads_daemon_threads gauge
jvm_threads_daemon_threads{application="\"stApps\"",} 32.0
# HELP disk_total_bytes Total space for path
# TYPE disk_total_bytes gauge
disk_total_bytes{application="\"stApps\"",path="/Users/songtao/personaldriveMac/projects/githubs/tsprTools/.",} 2.45107195904E11
# HELP jvm_buffer_memory_used_bytes An estimate of the memory that the Java virtual machine is using for this buffer pool
# TYPE jvm_buffer_memory_used_bytes gauge
jvm_buffer_memory_used_bytes{application="\"stApps\"",id="direct",} 8193.0
jvm_buffer_memory_used_bytes{application="\"stApps\"",id="mapped",} 0.0
# HELP tomcat_sessions_active_current_sessions  
# TYPE tomcat_sessions_active_current_sessions gauge
tomcat_sessions_active_current_sessions{application="\"stApps\"",} 0.0
# HELP executor_pool_size_threads The current number of threads in the pool
# TYPE executor_pool_size_threads gauge
executor_pool_size_threads{application="\"stApps\"",name="applicationTaskExecutor",} 0.0
# HELP jvm_threads_live_threads The current number of live threads including both daemon and non-daemon threads
# TYPE jvm_threads_live_threads gauge
jvm_threads_live_threads{application="\"stApps\"",} 36.0
# HELP jvm_memory_max_bytes The maximum amount of memory in bytes that can be used for memory management
# TYPE jvm_memory_max_bytes gauge
jvm_memory_max_bytes{application="\"stApps\"",area="nonheap",id="Compressed Class Space",} 1.073741824E9
jvm_memory_max_bytes{application="\"stApps\"",area="nonheap",id="Code Cache",} 1.34217728E8
jvm_memory_max_bytes{application="\"stApps\"",area="heap",id="PS Eden Space",} 1.400897536E9
jvm_memory_max_bytes{application="\"stApps\"",area="heap",id="PS Survivor Space",} 1.5204352E7
jvm_memory_max_bytes{application="\"stApps\"",area="heap",id="PS Old Gen",} 2.863661056E9
jvm_memory_max_bytes{application="\"stApps\"",area="nonheap",id="Metaspace",} -1.0
# HELP disk_free_bytes Usable space for path
# TYPE disk_free_bytes gauge
disk_free_bytes{application="\"stApps\"",path="/Users/songtao/personaldriveMac/projects/githubs/tsprTools/.",} 2.3166554112E10
# HELP jvm_buffer_count_buffers An estimate of the number of buffers in the pool
# TYPE jvm_buffer_count_buffers gauge
jvm_buffer_count_buffers{application="\"stApps\"",id="direct",} 2.0
jvm_buffer_count_buffers{application="\"stApps\"",id="mapped",} 0.0
# HELP tomcat_sessions_created_sessions_total  
# TYPE tomcat_sessions_created_sessions_total counter
tomcat_sessions_created_sessions_total{application="\"stApps\"",} 0.0
# HELP jvm_gc_overhead_percent An approximation of the percent of CPU time used by GC activities over the last lookback period or since monitoring began, whichever is shorter, in the range [0..1]
# TYPE jvm_gc_overhead_percent gauge
jvm_gc_overhead_percent{application="\"stApps\"",} 3.9211697901166056E-4
# HELP jvm_classes_loaded_classes The number of classes that are currently loaded in the Java virtual machine
# TYPE jvm_classes_loaded_classes gauge
jvm_classes_loaded_classes{application="\"stApps\"",} 16348.0
# HELP jvm_memory_usage_after_gc_percent The percentage of long-lived heap pool used after the last GC event, in the range [0..1]
# TYPE jvm_memory_usage_after_gc_percent gauge
jvm_memory_usage_after_gc_percent{application="\"stApps\"",area="heap",pool="long-lived",} 0.040777862224767425
# HELP jvm_gc_max_data_size_bytes Max size of long-lived heap memory pool
# TYPE jvm_gc_max_data_size_bytes gauge
jvm_gc_max_data_size_bytes{application="\"stApps\"",} 2.863661056E9
```




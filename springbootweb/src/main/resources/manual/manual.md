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
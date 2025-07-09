package com.st.modules.thread.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 概述
 * <pre>
 * - 通用线程池工具类，用于按需创建和复用线程池实例
 * </pre>
 * <p></p>
 * 功能
 * <pre>
 * - 线程池复用	工具类中提供静态单例
 * - 拒绝策略	使用 CallerRunsPolicy，保障任务不丢
 * - 命名线程	async-task-thread-* 命名，利于日志追踪
 * - 禁止实例化	私有构造函数 + 抛出异常
 * - 线程池封装	可跨模块统一复用，便于后期监控/调优
 * - 使用 CallerRunsPolicy 拒绝策略防止任务丢失
 * - 使用默认线程池参数; 同时支持自定义线程池参数（核心线程数、最大线程数、队列大小、存活时间）
 *
 * <p></p>
 * - 线程名称唯一, 各个模块单独计数, 方便追踪
 * - - 线程名应具备唯一性（同一 JVM 中避免冲突)
 * - - 不同业务模块调用 getExecutor("moduleA") 生成 moduleA-thread-0、moduleA-thread-1…
 * - - 即: 每个 prefix 维持一套独立计数和线程池
 * - - 不同模块可设置不同前缀（如 "order-async"、"user-job"）, 方便跟踪及分析
 * - - 每个 prefix 维护自己独立的线程池和编号
 * - - 每个前缀独立编号，线程名清晰、可控, 方便追踪.
 * <p></p>
 * - 线程池复用: 使用 ConcurrentHashMap 缓存按 prefix 创建的线程池
 * </pre>
 * <p></p>
 * Usage
 * <pre>
 *     {@code
 *     public static void main(String[] args) {
 *         List<String> list = new ArrayList<String>();
 *         list.add("a");
 *         list.add("b");
 *         list.add("c");
 *
 *         List<String> taskIds = new ArrayList<String>();
 *         for (String s : list) {
 *             String taskId = UUID.randomUUID().toString();
 *             taskIds.add(taskId);
 *
 *             Map<String, String> params = new HashMap<>();
 *             params.put(taskId.substring(0,3), taskId);
 *
 *             ThreadPoolUtils.getExecutor("batch-sync").execute(() -> {
 *                 new Service().handle(params);
 *             });
 *
 *         }
 *     }
 *
 *
 * @Slf4j
 * class Service {
 *     public void handle(Map<String,String> params) {
 *         // 02:38:17.972 [batch-sync-thread-1] INFO com.st.modules.thread.threadpool.Service - {733=7338b170-0e9d-4e50-aac2-b293b57d7f96}
 *        //  02:38:17.972 [batch-sync-thread-2] INFO com.st.modules.thread.threadpool.Service - {4dc=4dc29072-b30c-4870-b620-ae2499d70794}
 *        //  02:38:17.972 [batch-sync-thread-0] INFO com.st.modules.thread.threadpool.Service - {94b=94b089b2-8f71-4eef-8b25-cdefee1afa18}
 *        log.info(params.toString());
 *     }
 * }
 *
 *     }
 * </pre>
 *
 * <p></p>
 *
 * Usage: 在controller中, 获取service代理(避免service事务实效), 然后起异步线程: 异步线程中, 使用service代理类进行业务处理
 * <pre>
 * {@code
 * @RestController
 * @RequestMapping("/task")
 * public class TaskController {
 *
 *     @Autowired
 *     private ApplicationContext applicationContext;
 *
 *     @PostMapping("/submit")
 *     public List<String> submitTasks(@RequestBody List<String> taskNames) {
 *         TaskService proxy = applicationContext.getBean(TaskService.class);
 *
 *         List<String> taskIds = new ArrayList<String>();
 *
 *         for (String name : taskNames) {
 *             String taskId = UUID.randomUUID().toString();
 *             taskIds.add(taskId);
 *
 *             TaskDTO task = new TaskDTO(name, taskId);
 *
 *             ThreadPoolUtils.getExecutor("batch-sync").execute(() -> {
 *              proxy.processSingleTask(task)
 *              });
 *         }
 *
 *         return taskIds;
 *     }
 * }
 *
 * @Service
 * public class TaskService {
 *
 *     @Autowired
 *     private DemoRepository demoRepository;
 *
 *     @Transactional
 *     public void processSingleTask(TaskDTO task) {
 *         demoRepository.insert(task.getName(), task.getTaskId());
 *
 *         if (task.getName().contains("fail")) {
 *             throw new RuntimeException("任务失败：" + task.getTaskId());
 *         }
 *
 *         // 可选：记录日志、通知任务状态
 *     }
 * }
 *
 *
 * @Repository
 * public class DemoRepository {
 *
 *     @Autowired
 *     private JdbcTemplate jdbcTemplate;
 *
 *     public void insert(String name, String taskId) {
 *         String sql = "INSERT INTO demo_user (name, task_id) VALUES (?, ?)";
 *         jdbcTemplate.update(sql, name, taskId);
 *     }
 * }
 *
 *
 * public class TaskDTO {
 *     private String name;
 *     private String taskId;
 *
 *     public TaskDTO(String name, String taskId) {
 *         this.name = name;
 *         this.taskId = taskId;
 *     }
 *
 *     public String getName() {
 *         return name;
 *     }
 *
 *     public String getTaskId() {
 *         return taskId;
 *     }
 * }
 *
 *
 * CREATE TABLE demo_user (
 *     id       BIGINT PRIMARY KEY AUTO_INCREMENT,
 *     name     VARCHAR(50),
 *     task_id  VARCHAR(36) NOT NULL
 * );
 *
 * }
 * </pre>
 *
 * <p></p>
 * Usage: 使用默认线程池参数, 或自定义参数
 * <pre>
 * {@code
 *  public static void main(String[] args) {
 *         // 默认参数线程池
 *         Executor defaultExecutor = ThreadPoolUtils.getExecutor("default-pool");
 *         defaultExecutor.execute(() -> {
 *             // 14:02:33.621 [default-pool-thread-0] INFO com.st.modules.thread.threadpool.ThreadPoolUtils - default-pool-thread-0 默认线程池任务
 *             log.info(Thread.currentThread().getName() + " 默认线程池任务");
 *         });
 *
 *         // 自定义参数线程池
 *         ThreadPoolConfig customConfig = ThreadPoolConfig.of(2, 4, 50, 30);
 *         Executor customExecutor = ThreadPoolUtils.getExecutor("custom-pool", customConfig);
 *         customExecutor.execute(() -> {
 *             // 14:02:33.621 [custom-pool-thread-0] INFO com.st.modules.thread.threadpool.ThreadPoolUtils - custom-pool-thread-0 自定义线程池任务
 *             log.info(Thread.currentThread().getName() + " 自定义线程池任务");
 *         });
 *     }
 * }
 * </pre>
 *
 *
 * 不足的地方, ref {@link CallableThreadPoolUtils}
 * <pre>
 * - 异步执行 Runnable	✅
 * - 自定义线程名前缀	✅(命名清晰，已实现)
 * - 任务执行超时	❌ 否	改用 submit + Future.get(timeout)
 * - 批量任务（Callable）	❌ 否	应扩展为 CallableThreadPoolUtils
 * - 返回执行结果	❌ 否	需使用 Callable 接口
 * </pre>
 *
 *
 *
 * <p></p>
 * 后续待扩展
 * <pre>
 * - 添加最大线程数、队列大小的可配置构造版本: 可进一步抽象成 getExecutor(String prefix, ThreadPoolConfig config)
 * - 增加最大线程数等参数定制功能（如：getExecutor(String prefix, int core, int max)）
 * - 支持基于 application.yaml 配置项进行参数注入，以便未来线程池热配置支持
 * </pre>
 */

@Slf4j
public class ThreadPoolUtils {

    private static final int DEFAULT_CORE = 4;
    private static final int DEFAULT_MAX = 10;
    private static final int DEFAULT_QUEUE = 100;
    private static final long DEFAULT_KEEPALIVE = 60L;


    /**
     * 缓存线程池实例，每个 prefix 仅创建一次
     */
    private static final ConcurrentHashMap<String, Executor> EXECUTOR_MAP = new ConcurrentHashMap<String, Executor>();

    private ThreadPoolUtils() {
        throw new UnsupportedOperationException("工具类禁止实例化");
    }

    /**
     * 获取指定前缀名称的线程池实例（按 prefix 缓存）
     * <p></p>
     * Usage
     * <pre>
     *     {@code
     *     Executor executor = ThreadPoolUtils.getExecutor("sync-task");
     *     executor.execute(() -> { ... });
     *     }
     * </pre>
     *
     * @param prefix 线程名前缀，例如 "import-task"
     * @return 可复用的线程池 Executor 实例
     */
    public static Executor getExecutor(String prefix) {
        return EXECUTOR_MAP.computeIfAbsent(prefix, p -> new ThreadPoolExecutor(
                DEFAULT_CORE,
                DEFAULT_MAX,
                DEFAULT_KEEPALIVE,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(DEFAULT_QUEUE),
                new NamedThreadFactory(p),
                new ThreadPoolExecutor.CallerRunsPolicy()
        ));
    }


    /**
     * 获取带自定义参数的线程池（根据 prefix 缓存）
     * <pre>
     * - 按前缀 prefix 唯一标识线程池，避免重复创建，支持线程复用。
     * - 支持自定义核心线程数、最大线程数、队列大小、存活时间等参数。
     * - 若该 prefix 的线程池已存在，则复用之前创建的实例，忽略 config 参数。
     * </pre>
     *
     * 使用示例：
     * <pre>{@code
     *     ThreadPoolConfig config = ThreadPoolConfig.of(2, 8, 200, 30);
     *     Executor executor = ThreadPoolUtils.getExecutor("import-task", config);
     *     executor.execute(() -> {
     *         System.out.println("当前线程：" + Thread.currentThread().getName());
     *     });
     * }</pre>
     *
     * @param prefix 线程名前缀（用于区分业务线程池），如 "import-task"
     * @param config 线程池配置参数，包括 core/max/queue/keepAliveSeconds 等
     * @return Executor 实例（线程池），线程名为 prefix-thread-N
     *
     * @throws NullPointerException 如果 prefix 或 config 为 null
     */
    public static Executor getExecutor(String prefix, ThreadPoolConfig config) {
        if (prefix == null || config == null) {
            throw new NullPointerException("线程名前缀和配置对象不可为 null");
        }

        return EXECUTOR_MAP.computeIfAbsent(prefix, p -> new ThreadPoolExecutor(
                config.corePoolSize,
                config.maxPoolSize,
                config.keepAliveSeconds,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(config.queueCapacity),
                new NamedThreadFactory(p),
                new ThreadPoolExecutor.CallerRunsPolicy()
        ));
    }



    /**
     * 概述
     * <pre>
     * - 自定义线程工厂类，实现了 JDK 的 java.util.concurrent.ThreadFactory 接口，用于在创建线程时指定线程名称格式，从而提升线程可读性与日志可追踪性
     * - JDK 默认线程工厂创建的线程名通常像：Thread-0, Thread-1,...; 无法区分线程的业务来源，难以进行日志排查、线程分析
     * - NamedThreadFactory("import-task") 可以输出线程名; 明确知道这些线程是哪个业务模块（如“导入任务”）产生的
     * </pre>
     * <p></p>
     * 功能
     * <pre>
     * - 命名线程工厂，用于生成带业务前缀的线程名称，便于排查日志
     * - 线程名格式：prefix-thread-0, prefix-thread-1, ...
     * - 日志排查	日志中出现异常线程名为 sync-job-thread-3，可直接定位到任务同步模块
     * - 运维诊断	JVisualVM、JConsole 中查看线程栈时快速识别线程来源
     * - 性能监控	将不同类型线程分类统计
     * </pre>
     * <p></p>
     * 后续待扩展
     * <pre>
     * - 支持设置线程是否为守护线程、优先级等附加参数
     * </pre>
     */
    private static class NamedThreadFactory implements ThreadFactory {
        private final String prefix;
        private final AtomicInteger count = new AtomicInteger(0);

        public NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(prefix + "-thread-" + count.getAndIncrement());
            return t;
        }
    }


    public static void main(String[] args) {
        // 默认参数线程池
        Executor defaultExecutor = ThreadPoolUtils.getExecutor("default-pool");
        defaultExecutor.execute(() -> {
            // 14:02:33.621 [default-pool-thread-0] INFO com.st.modules.thread.threadpool.ThreadPoolUtils - default-pool-thread-0 默认线程池任务
            log.info(Thread.currentThread().getName() + " 默认线程池任务");
        });

        // 自定义参数线程池
        ThreadPoolConfig customConfig = ThreadPoolConfig.of(2, 4, 50, 30);
        Executor customExecutor = ThreadPoolUtils.getExecutor("custom-pool", customConfig);
        customExecutor.execute(() -> {
            // 14:02:33.621 [custom-pool-thread-0] INFO com.st.modules.thread.threadpool.ThreadPoolUtils - custom-pool-thread-0 自定义线程池任务
            log.info(Thread.currentThread().getName() + " 自定义线程池任务");
        });
    }

}

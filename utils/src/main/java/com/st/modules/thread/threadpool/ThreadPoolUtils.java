package com.st.modules.thread.threadpool;

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
 * <p></p>
 * - 线程名称唯一, 各个模块单独计数, 方便追踪
 * - - 线程名应具备唯一性（同一 JVM 中避免冲突)
 * - - 不同业务模块调用 getExecutor("moduleA") 生成 moduleA-thread-0、moduleA-thread-1…
 * - - 即: 每个 prefix 维持一套独立计数和线程池
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
 * <p></p>
 * 后续待扩展
 * <pre>
 * - 添加最大线程数、队列大小的可配置构造版本: 可进一步抽象成 getExecutor(String prefix, ThreadPoolConfig config)
 * - 增加最大线程数等参数定制功能（如：getExecutor(String prefix, int core, int max)）
 * - 支持基于 application.yaml 配置项进行参数注入，以便未来线程池热配置支持
 * </pre>
 */
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

}

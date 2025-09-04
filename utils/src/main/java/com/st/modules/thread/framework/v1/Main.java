package com.st.modules.thread.framework.v1;

import com.st.modules.thread.threadpool.CallableThreadPoolUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
@Slf4j
public class Main {

    /**
     * 说明: ref obsidian中专门分析: 线程-线程池综合应用(Java).md
     * <pre>
     * - 这里的main, 对应请求的入口; 比如controller等
     * - OrchestratorService, coreservice, dispather等, 都是spring的主键; 直接注入
     * </pre>
     *
     * <p>test. </p>
     * <pre>
     * {@code
     * @Autowired
     * OrchestratorService orchestratorService
     * @PostMapping("/submit")
     * public String submitTasks() {
     *     orchestratorService.processTasks(Arrays.asList("A", "B", "C"));
     *     return "任务已提交";
     * }
     * }
     * </pre>
     *
     * <p></p>
     * <p>测试时, 或不依赖spring环境中,则手动创建, 或通过工程创建即可.</p>
     * <pre>
     * - orchestratorService,等为了通用性测试
     * - 假如不依赖spring环境, 不使用@service等注解, 使用java 内置, 将其service、dispather等变成组件, 自动注入, 方便测试
     * </pre>
     * @param args
     */
    public static void main(String[] args) {

        Executor executor = CallableThreadPoolUtils.getExecutor("Module/FunctionA");
        CoreService coreService = new CoreService();
        Dispatcher dispatcher = new Dispatcher();
        OrchestratorService orchestrator = new OrchestratorService(coreService, dispatcher);

        List<String> names = Arrays.asList("taskA", "taskB", "taskC");
        orchestrator.processTasks(names);

        log.info("直接返回.["+names.size()+"]个任务在异步处理中.");

    }
}

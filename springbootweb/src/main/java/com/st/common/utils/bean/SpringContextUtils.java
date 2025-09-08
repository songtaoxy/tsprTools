package com.st.common.utils.bean;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * <li>全局 ApplicationContext 工具类</li>
 * <li>- 保证 ApplicationContext 可用后再注入</li>
 * <li>- 支持获取 Bean、判断容器状态</li>
 * <li>支持延迟回调的 ApplicationContext 工具类</li>
 * <li>不要在 Spring 容器完成初始化之前访问 ApplicationContext！
 * 如果必须使用 Bean，请放到 @PostConstruct、CommandLineRunner 或 ApplicationReadyEvent 后面，或者使用 whenContextReady</li>
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {

    private static volatile ApplicationContext context;

    private static final List<Consumer<ApplicationContext>> callbacks = new ArrayList<>();

    private static volatile boolean ready = false;


    /**
     * <li>注意：此时 context 有可能还没完全刷新，不建议直接用</li>
     * <li>因为 ApplicationContextAware.setApplicationContext(...) 是在 Spring 容器启动快结束时才回调的。</li>
     * <li>但你如果在静态代码块、main 方法、构造函数、或某些定制化 early-init bean 中提前调用了 BeanUtil.getBean(...)，此时 context 肯定是 null，甚至可能不是 Spring 管理的类</li>
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 注意：此时 context 有可能还没完全刷新，不建议直接用
        SpringContextUtils.context = applicationContext;
    }


    /*@EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // ✅ 真正安全注入点：Spring 容器初始化完成后
        SpringContextUtils.context = event.getApplicationContext();
        System.out.println("✅ SpringContextUtils: ApplicationContext 初始化完成");

        // 执行所有延迟注册的回调
        for (Consumer<ApplicationContext> cb : callbacks) {
            cb.accept(context);
        }
        callbacks.clear();
    }*/


    /**
     * ApplicationReadyEvent 相对于 ContextRefreshedEvent 更安全
     * @param event
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        SpringContextUtils.ready = true;
        SpringContextUtils.context = event.getApplicationContext();
        System.out.println("✅ Spring Boot 完全启动完成，ApplicationContext 可安全使用");

        // 执行所有延迟注册的回调
        for (Consumer<ApplicationContext> cb : callbacks) {
            try {
                cb.accept(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        callbacks.clear();
    }


    /**
     * 注册一个回调，在容器 ready 后自动执行
     */
    public static void whenContextReady(Consumer<ApplicationContext> callback) {
        if (context != null) {
            callback.accept(context);
        } else {
            callbacks.add(callback);
        }
    }

    /** 安全获取 ApplicationContext，未初始化时抛出异常 */
    public static ApplicationContext getContext() {
        if (context == null || ! isReady()) {
            throw new IllegalStateException("Spring 容器未初始化完成，无法获取 ApplicationContext");
        }
        return context;
    }


    /**
     * 执行某段逻辑，仅在 Spring 容器准备好后才执行
     */
    public static void runAfterContextReady(Runnable runnable) {
        whenContextReady(ctx -> runnable.run());
    }

    public static boolean isReady() {
        return ready;
    }



    /** 获取指定类型的 Bean */
    public static <T> T getBean(Class<T> clazz) {
        return getContext().getBean(clazz);
    }
    /** 获取指定名称的 Bean */
    public static Object getBean(String name) {
        return getContext().getBean(name);
    }

    /** 判断 Bean 是否存在 */
    public static boolean containsBean(String name) {
        return getContext().containsBean(name);
    }

    /** 判断 Bean 是否为单例 */
    public static boolean isSingleton(String name) {
        return getContext().isSingleton(name);
    }
}

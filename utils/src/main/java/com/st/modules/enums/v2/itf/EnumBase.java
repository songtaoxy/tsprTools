package com.st.modules.enums.v2.itf;

import java.util.*;

/**
 * <pre>
 * 设计说明
 * - <b><u>接入成本</u></b> 仅需让枚举实现 EnumBase<C> 并返回 getCode() 即可使用全部能力
 * - <b><u>缓存策略</u></b> 按枚举类维度的 ConcurrentHashMap 缓存 首次访问构建 后续 O(1) 查询 枚举在 JVM 生命周期内稳定不变 不需失效
 * - <b><u>类型安全</u></b> C 为 code 类型 泛型贯通 静态方法签名限制 E extends Enum<E> & EnumBase<C> 保证编译期约束
 * - <b><u>可扩展</u></b> 若需别名 alias 或描述 desc 等 直接在业务处使用 EnumBase.names mapNameToCode 或自行扩展二级 Map 即可
 * - <b><u>一致性</u></b> 建议保证每个枚举常量的 code 全局唯一 且避免可变引用类型作为 code 防止哈希语义不稳定
 *
 *
 * 概述:
 * 通用业务枚举接口, 任何枚举实现该接口并提供 getCode 方法,
 * 即可使用静态方法完成 name 与 code 的互查、校验、映射.
 *
 * 解释:
 * 将常用能力直接“内聚”到一个接口中：
 * 任何枚举只要实现 EnumBase<C> 并提供 getCode()，
 * 即可直接使用接口上的静态工具方法：getByCode、getByName、getNameByCode、getCodeByName，
 * 并且内置高性能缓存，
 * JDK8 可用
 *
 * 特点:
 * 1. 高性能缓存: 按枚举类维度构建缓存, 查询 O(1)
 * 2. 泛型安全: C 为 code 类型, E 为枚举类型
 * 3. 常见 API: getByCode、getByName、getNameByCode、getCodeByName
 * 4. 校验、Optional 友好方法
 * 5. 列举、映射便捷方法
 *
 * 适用场景:
 * - 数据库字段 (code) 与枚举常量转换
 * - 前后端传递时使用 code, 内部逻辑用枚举
 * - 枚举下拉框展示
 *
 * 性能:
 * 按“枚举类维度一次构建、全程复用”的 O(1) 查询性能，并发下也不会重复构建
 * </pre>
 */
public interface EnumBase<C> {

    /**
     * <pre>
     * 概述:
     * 返回当前枚举常量的业务 code
     * 功能清单:
     * 1. 作为缓存的 key
     * 使用示例:
     *   String code = BizEnum.FGLS.getCode();
     * 注意事项:
     * - 建议全局唯一, 不可变
     * - 不要返回 null
     * 入参与出参与异常:
     * </pre>
     *
     * <pre>
     * - 为何要实现该方法? ref doc 为何要实现getCode方法.md
     *
     * </pre>
     * @return 业务 code
     */
    C getCode();

    /* ===================== 主查询 API ===================== */

    /**
     * 概述:
     * 根据 code 获取枚举常量
     * 功能清单:
     * 1. O(1) 查询, 无需遍历
     * 2. 找不到返回 null
     * 使用示例:
     *   BizEnum e = EnumBase.getByCode(BizEnum.class, "ERP");
     * 注意事项:
     * - code 必须实现 equals/hashCode
     * 入参与出参与异常:
     * @param enumClass 枚举类
     * @param code 业务 code
     * @return 对应枚举或 null
     */
    static <C, E extends Enum<E> & EnumBase<C>> E getByCode(Class<E> enumClass, C code) {
        if (code == null) return null;
        EnumCache<E, C> c = EnumBaseSupport.cacheOf(enumClass);
        return c.byCode.get(code);
    }

    /**
     * 概述:
     * 根据 name 获取枚举常量
     * 功能清单:
     * 1. 可忽略大小写
     * 2. 找不到返回 null
     * 使用示例:
     *   BizEnum e1 = EnumBase.getByName(BizEnum.class, "FGLS", false);
     *   BizEnum e2 = EnumBase.getByName(BizEnum.class, "fgls", true);
     * 注意事项:
     * - name 是枚举常量名, 不是业务 code
     * 入参与出参与异常:
     * @param enumClass 枚举类
     * @param name 常量名
     * @param ignoreCase 是否忽略大小写
     * @return 对应枚举或 null
     */
    static <C, E extends Enum<E> & EnumBase<C>> E getByName(Class<E> enumClass, String name, boolean ignoreCase) {
        if (name == null) return null;
        EnumCache<E, C> c = EnumBaseSupport.cacheOf(enumClass);
        return ignoreCase ? c.byNameIgnoreCase.get(name.toLowerCase(Locale.ROOT)) : c.byName.get(name);
    }

    /**
     * 概述:
     * 根据 code 获取枚举常量名
     * 功能清单:
     * 1. 找不到返回 null
     * 使用示例:
     *   String n = EnumBase.getNameByCode(BizEnum.class, "NC");
     * 注意事项:
     * - 返回的是常量名, 非业务描述
     * 入参与出参与异常:
     * @param enumClass 枚举类
     * @param code 业务 code
     * @return 枚举常量名或 null
     */
    static <C, E extends Enum<E> & EnumBase<C>> String getNameByCode(Class<E> enumClass, C code) {
        E e = getByCode(enumClass, code);
        return e == null ? null : e.name();
    }

    /**
     * 概述:
     * 根据 name 获取业务 code
     * 功能清单:
     * 1. 可忽略大小写
     * 2. 找不到返回 null
     * 使用示例:
     *   String c = EnumBase.getCodeByName(BizEnum.class, "ERP", false);
     * 注意事项:
     * - name 匹配常量名
     * 入参与出参与异常:
     * @param enumClass 枚举类
     * @param name 常量名
     * @param ignoreCase 是否忽略大小写
     * @return 业务 code 或 null
     */
    static <C, E extends Enum<E> & EnumBase<C>> C getCodeByName(Class<E> enumClass, String name, boolean ignoreCase) {
        E e = getByName(enumClass, name, ignoreCase);
        return e == null ? null : e.getCode();
    }

    /* ===================== 校验与 Optional ===================== */

    /**
     * 概述: 判断 name 是否有效
     * 功能清单:
     * 1. 可忽略大小写
     */
    static <C, E extends Enum<E> & EnumBase<C>> boolean isValidName(Class<E> enumClass, String name, boolean ignoreCase) {
        return getByName(enumClass, name, ignoreCase) != null;
    }

    /**
     * 概述: 判断 code 是否有效
     */
    static <C, E extends Enum<E> & EnumBase<C>> boolean isValidCode(Class<E> enumClass, C code) {
        return getByCode(enumClass, code) != null;
    }

    /**
     * 概述: Optional 形式按 code 查找
     */
    static <C, E extends Enum<E> & EnumBase<C>> Optional<E> findByCode(Class<E> enumClass, C code) {
        return Optional.ofNullable(getByCode(enumClass, code));
    }

    /**
     * 概述: Optional 形式按 name 查找
     */
    static <C, E extends Enum<E> & EnumBase<C>> Optional<E> findByName(Class<E> enumClass, String name, boolean ignoreCase) {
        return Optional.ofNullable(getByName(enumClass, name, ignoreCase));
    }

    /* ===================== 列举与映射 ===================== */

    /**
     * 概述: 列出所有常量名
     */
    static <C, E extends Enum<E> & EnumBase<C>> List<String> names(Class<E> enumClass) {
        return EnumBaseSupport.cacheOf(enumClass).namesView;
    }

    /**
     * 概述: 列出所有业务 code
     */
    static <C, E extends Enum<E> & EnumBase<C>> List<C> codes(Class<E> enumClass) {
        return EnumBaseSupport.cacheOf(enumClass).codesView;
    }

    /**
     * 概述: 生成 name->code 映射
     */
    static <C, E extends Enum<E> & EnumBase<C>> Map<String, C> mapNameToCode(Class<E> enumClass) {
        return EnumBaseSupport.cacheOf(enumClass).nameToCodeView;
    }

    /**
     * 概述: 生成 code->name 映射
     */
    static <C, E extends Enum<E> & EnumBase<C>> Map<C, String> mapCodeToName(Class<E> enumClass) {
        return EnumBaseSupport.cacheOf(enumClass).codeToNameView;
    }

    /* ===================== 内部缓存 ===================== */

    final class EnumCache<E extends Enum<E> & EnumBase<C>, C> {
        final Map<String, E> byName;
        final Map<String, E> byNameIgnoreCase;
        final Map<C, E> byCode;
        final List<String> namesView;
        final List<C> codesView;
        final Map<String, C> nameToCodeView;
        final Map<C, String> codeToNameView;
        EnumCache(E[] arr) {
            Map<String, E> bn = new HashMap<>();
            Map<String, E> bi = new HashMap<>();
            Map<C, E> bc = new HashMap<>();
            List<String> names = new ArrayList<>();
            List<C> codes = new ArrayList<>();
            Map<String, C> n2c = new LinkedHashMap<>();
            Map<C, String> c2n = new LinkedHashMap<>();
            for (E e : arr) {
                String n = e.name();
                C code = e.getCode();
                bn.put(n, e);
                bi.put(n.toLowerCase(Locale.ROOT), e);
                bc.put(code, e);
                names.add(n);
                codes.add(code);
                n2c.put(n, code);
                c2n.put(code, n);
            }
            this.byName = bn;
            this.byNameIgnoreCase = bi;
            this.byCode = bc;
            this.namesView = Collections.unmodifiableList(names);
            this.codesView = Collections.unmodifiableList(codes);
            this.nameToCodeView = Collections.unmodifiableMap(n2c);
            this.codeToNameView = Collections.unmodifiableMap(c2n);
        }
    }

    final class EnumBaseSupport {
        private static final java.util.concurrent.ConcurrentHashMap<Class<?>, EnumCache<?, ?>> CACHE =
                new java.util.concurrent.ConcurrentHashMap<>();

        @SuppressWarnings("unchecked")
        static <C, E extends Enum<E> & EnumBase<C>> EnumCache<E, C> cacheOf(Class<E> enumClass) {
            // 命中缓存直接返回
            EnumCache<?, ?> cached = CACHE.get(enumClass);
            if (cached != null) {
                return (EnumCache<E, C>) cached;
            }
            // 显式以 Class<E> 获取 E[]，避免通配符捕获
            E[] arr = enumClass.getEnumConstants();
            if (arr == null) {
                throw new IllegalArgumentException("Class is not an enum " + enumClass);
            }
            EnumCache<E, C> built = new EnumCache<>(arr);
            // putIfAbsent 保证并发安全
            EnumCache<?, ?> old = CACHE.putIfAbsent(enumClass, built);
            return (EnumCache<E, C>) (old == null ? built : old);
        }
    }

}


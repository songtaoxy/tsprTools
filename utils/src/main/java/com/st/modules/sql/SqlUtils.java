package com.st.modules.sql;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;



public class SqlUtils {

    /**
     * <pre>
     * - 将实体集合中某字段提取，构造 SQL IN (...) 子句字符串
     * - 空列表时输出 (NULL)
     * </pre>
     *
     *
     * 用法概述:
     * <pre>
     *     {@code
     *      String sqlInClause = SqlUtils.buildInClause(users, User::getName);
     *      String sql = "SELECT * FROM users WHERE id IN " + sqlInClause;
     *      // sql = "SELECT * FROM users WHERE id IN " + ('Alice', 'Bob');
     *     }
     * </pre>
     *
     * Usage and Test as flow
     * <pre>
     *     {@code
     *
     *     @Slf4j
     * class SqlUtilsTest {
     *
     *     @Test
     *     void buildInClause() {
     *
     *         List<User> users = Arrays.asList(
     *                 new User(1L, "Alice"),
     *                 new User(2L, "Bob")
     *         );
     *
     *        // 提取 ID 构造 SQL IN 子句; 输出: (1, 2)
     *         String sqlInClause = SqlUtils.buildInClause(users, User::getId);
     *         log.info(sqlInClause);
     *
     *        // 提取 name 构造 SQL IN 子句;输出: ('Alice', 'Bob')
     *         String sqlInClause2 = SqlUtils.buildInClause(users, User::getName);
     *         log.info(sqlInClause2);
     *
     *         // 空列表时：输出: (NULL)
     *         String emptyClause = SqlUtils.buildInClause(Collections.emptyList(), User::getId);
     *         log.info(emptyClause);
     *     }
     * }
     *
     *
     * @Data
     * @AllArgsConstructor
     * @NoArgsConstructor
     * class User {
     *     Long id;
     *     String name;
     * }
     *
     *
     *     }
     * </pre>
     *
     * @param list 实体集合
     * @param mapper 字段提取函数
     * @param <T> 实体类型
     * @param <R> 字段类型
     * @return 形如 "(1, 2, 3)" 或 "(NULL)" 的 SQL 子句
     */
    public static <T, R> String buildInClause(List<T> list, Function<T, R> mapper) {
        if (list == null || list.isEmpty()) {
            return "(NULL)";
        }
        return list.stream()
                .map(mapper)
                .map(SqlUtils::formatValue)
                .collect(Collectors.joining(", ", "(", ")"));
    }

    /**
     * 格式化 SQL 值（字符串加引号，其它类型直接 toString）
     * <pre>
     * - 关于转义: 假设字符串中本来就有', 则: 为防止 SQL 注入或语法错误，对字符串中的单引号 ' 进行转义; 详见obsidian分析.
     * - 在 SQL 中：没有统一的“转义字符”概念（不像 Java 用 \ 来转义很多内容）
     * - SQL 中仅对字符串字面量中的单引号 ' 本身，使用 '' 表示其字面值
     *
     * - 如何将转义O'Neil中的 '
     * - SELECT 'O''Neil' : 所有数据库都支持
     * - SELECT 'O\'Neil'; MySQL支持，但PostgreSQL、Oracle 不支持.
     *
     *     <pre>{@code
     *          String name = "O'Neil";
     *
     *           //  不转义:
     *           SELECT * FROM users WHERE name = 'O'Neil';
     *
     *           //  转义:
     *           String sql = "SELECT * FROM users WHERE name = '" + name.replace("'", "''") + "'";
     *          SELECT * FROM users WHERE name = 'O''Neil'
     *     }</pre>
     * </pre>
     */
    private static String formatValue(Object value) {
        if (value == null) return "NULL";
        if (value instanceof String || value instanceof Character) {
            // 如果字符中本身有', 则进行转义
            return "'" + value.toString().replace("'", "''") + "'";
        }
        return value.toString();
    }
}

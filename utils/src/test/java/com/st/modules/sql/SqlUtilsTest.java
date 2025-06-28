package com.st.modules.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class SqlUtilsTest {

    @Test
    void buildInClause() {

        List<User> users = Arrays.asList(
                new User(1L, "Alice"),
                new User(2L, "Bob")
        );

       // 提取 ID 构造 SQL IN 子句; 输出: (1, 2)
        String sqlInClause = SqlUtils.buildInClause(users, User::getId);
        log.info(sqlInClause);

       // 提取 name 构造 SQL IN 子句;输出: ('Alice', 'Bob')
        String sqlInClause2 = SqlUtils.buildInClause(users, User::getName);
        log.info(sqlInClause2);

        // 空列表时：输出: (NULL)
        String emptyClause = SqlUtils.buildInClause(Collections.emptyList(), User::getId);
        log.info(emptyClause);


        // 入参List<T> 是 List<String>或List<Integer> 等
        List<String> names = Arrays.asList("Alice", "Bob", "O'Neil");
        // 使用 identity; 输出: ('Alice', 'Bob', 'O''Neil')
        String sqlIn = SqlUtils.buildInClause(names, Function.identity());
        log.info(sqlIn);
        // 或 lambda 写法; 输出: ('Alice', 'Bob', 'O''Neil')
        String sqlIn2 = SqlUtils.buildInClause(names, s -> s);
        log.info(sqlIn2);

        List<Integer> age = Arrays.asList(2, 3, 4);
        String sqlInAge = SqlUtils.buildInClause(age, Function.identity());
        // 输出:  (2, 3, 4)
        log.info(sqlInAge);



    }
}


@Data
@AllArgsConstructor
@NoArgsConstructor
class User {
    Long id;
    String name;
}
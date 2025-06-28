package com.st.modules.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    }
}


@Data
@AllArgsConstructor
@NoArgsConstructor
class User {
    Long id;
    String name;
}
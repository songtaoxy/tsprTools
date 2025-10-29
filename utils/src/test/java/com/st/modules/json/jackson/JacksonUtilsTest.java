package com.st.modules.json.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class JacksonUtilsTest {

    @Test
    public void testBasicSerialization() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setCreateTime(LocalDateTime.now());

        String json = JacksonUtils.toJson(user);
        log.info(json);
        assertTrue(json.contains("alice"));
    }

    @Test
    public void testListSerialization() {
//        List<User> users = List.of(new User());
        List<User> users = new ArrayList<User>();
        users.add(new User());
        String json = JacksonUtils.toJson(users);
        log.info(json);

        assertTrue(json.startsWith("["));
    }

    @Test
    public void testMapDeserialization() {
        String json = "{\"user1\":{\"name\":\"Bob\"}}";
        Map<String, User> map = JacksonUtils.fromJson(json, new TypeReference<Map<String, User>>() {
        });

        log.info(JacksonUtils.toPrettyJson(map));

        assertEquals("Bob", map.get("user1").getName());
    }

    @Test
    public void testJsonPatch() {
        User user1 = new User();
        user1.setName("Old");
        User user2 = new User();
        user2.setName("New");

        JsonPatch patch = JacksonUtils.diff(user1, user2);
        User patched = JacksonUtils.applyPatch(patch, user1, User.class);
        assertEquals("New", patched.getName());
    }

    @Test
    public void testJsonView() {
        User user = new User();
        user.setName("ViewTest");
        user.setEmail("hidden@example.com");

        String publicView = JacksonUtils.toJsonView(user, User.Views.Public.class);
        assertTrue(publicView.contains("ViewTest"));
        assertFalse(publicView.contains("hidden@example.com"));
    }

    @Test
    public void testJsonFileReadWrite() {
        User user = new User();
        user.setName("FileUser");

        File file = new File("user.json");
        JacksonUtils.toJsonFile(file, user);
        User read = JacksonUtils.fromJsonFile(file, User.class);
        assertEquals("FileUser", read.getName());
        file.delete();
    }

    @Test
    public void testSchemaGeneration() {
        String schema = JacksonUtils.generateSchema(User.class);
        assertTrue(schema.contains("type"));
        assertTrue(schema.contains("properties"));
    }


    @Test
    public void testBeanConvert() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");

        /**
         * 字段一致的场景
         */
        UserDTO dto = JacksonUtils.convert(user, UserDTO.class);
        log.info(JacksonUtils.toPrettyJson(dto));



        /**
         * 字段不一致的场景, 使用@JsonProperty("name")
         * 见 {@code
         *
         *  class UserDTO2 {
         *     @JsonProperty("name") // 指定从源字段 name 获取值
         *     private String username;
         * }
         *
         * 更复杂的场景, 使用MapStruct
         */
        User user2 = new User();
        user2.setName("Tom");
        UserDTO2 dto2 = JacksonUtils.convert(user2, UserDTO2.class);
        // 输出 Tom
        log.info(JacksonUtils.toPrettyJson(dto2));
//        System.out.println(dto2.getUsername());


        assertEquals("Alice", dto.getName());
        assertEquals("alice@example.com", dto.getEmail());
    }

    @Test
    public void testCreatObjectNode() {
        // Test ObjectNode from POJO
        Map map = new HashMap<String,String>();
        map.put("name","Tome");
        map.put("age","30");
//        ObjectNode userNode = JacksonUtils.createObjectNode(Map.of("name", "Tom", "age", 30));
        ObjectNode userNode = JacksonUtils.createObjectNode(map);
        System.out.println("ObjectNode from map: \n" + JacksonUtils.toPrettyJson(userNode));

        // Test ArrayNode from List
        List  list = new ArrayList<String>();
        list.add("apple");
        list.add("banana");
//        ArrayNode fruits = JacksonUtils.createArrayNode(List.of("apple", "banana"));
        ArrayNode fruits = JacksonUtils.createArrayNode(list);

        System.out.println("ArrayNode from list: \n" + JacksonUtils.toPrettyJson(fruits));

        // Test JsonBuilder
        Map map2 = new HashMap<String,String>();
        map2.put("email","alice@example.com");
        ObjectNode json = JsonBuilder.create()
                .put("id", 101)
                .put("name", "Alice")
                .put("active", true)
                .putPOJO("details", map2)
//                .putPOJO("details", Map.of("email", "alice@example.com"))
                .build();
        System.out.println("JsonBuilder result: \n" + JacksonUtils.toPrettyJson(json));

        // Test ArrayBuilder
        ArrayNode arr = ArrayBuilder.create()
                .add("A")
                .add(100)
                .addPOJO(map2)
//                .addPOJO(Map.of("k", "v"))
                .build();
        System.out.println("ArrayBuilder result: \n" + JacksonUtils.toPrettyJson(arr));
    }



    // 测试集合泛型转换（比如 List<User> -> List<UserDTO>）
    @Test
    public void testBeanListConvert() {
        User user = new User();
        user.setName("Bob");
        user.setEmail("bob@example.com");

        List<User> userList =new ArrayList<User>();
        userList.add(user);
        List<UserDTO> dtoList = JacksonUtils.convertComplex(userList, new TypeReference<List<UserDTO>>() {});

        log.info(JacksonUtils.toPrettyJson(dtoList));

        assertEquals(1, dtoList.size());
        assertEquals("Bob", dtoList.get(0).getName());
    }



}

@Data
class User {
    public interface Views {
        interface Public {}
        interface Internal extends Public {}
    }

    @JsonView(Views.Public.class)
    private String name;

    @JsonView(Views.Internal.class)
    private String email;

    private LocalDateTime createTime;

    // Getters and setters...
}
@Data
 class UserDTO {
    private String name;
    private String email;

    // Getter/Setter

    @Override
    public String toString() {
        return "UserDTO{name='" + name + "', email='" + email + "'}";
    }
}

@Data
class UserDTO2 {
    @JsonProperty("name") // 指定从源字段 name 获取值
    private String username;

    // getters and setters
}

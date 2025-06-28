# 问题
上述jackson工具类, 如果static中构建mapper, 是不是共用了mapper. 比如, Spring boot web项目中, 多个请求, 都使用mapper, 它们使用的是否是同一个mapper
# 解答

是的，如果在 `static` 块中构建 `ObjectMapper`，那么 `mapper` 是 **静态的**，这意味着它是一个类级别的单例实例，所有的请求或使用该工具类的地方都会共享同一个 `mapper` 实例。

### 关键点

- **静态成员变量**：在 `static` 块中初始化的 `mapper` 是一个静态变量，意味着它的生命周期和类的生命周期相同，而不是每次请求时都重新创建新的 `ObjectMapper` 实例。
- **共享实例**：每次调用 `JacksonUtils` 类中的方法时，都会使用这个已经初始化的共享的 `mapper` 实例，而不会重新创建新的实例。

### 在 Spring Boot 中的行为

Spring Boot Web 项目中的多个请求如果使用这个 `JacksonUtils` 工具类，它们会共享同一个 `mapper` 实例，这样可以节省内存和性能开销，因为创建 `ObjectMapper` 实例是相对较为昂贵的操作。

然而，**共享单一的 `ObjectMapper` 实例** 也有一些需要注意的地方：

### 1. **线程安全性**：

- `ObjectMapper` 本身是线程安全的，前提是它的配置不被修改。如果在多个线程中并发使用同一个 `ObjectMapper` 实例，而没有修改配置，则它是线程安全的。
- 由于 `ObjectMapper` 的配置（如 `setSerializationInclusion`、`setPropertyNamingStrategy` 等）是不可变的，因此在使用过程中不会发生线程安全问题。

### 2. **性能优势**：

- 使用单一的 `ObjectMapper` 实例可以显著提高性能，因为重复创建和销毁 `ObjectMapper` 对象是资源密集型的。
- Spring Boot 默认的 `Jackson` 配置也会使用单一的 `ObjectMapper` 实例，这进一步提高了性能。

### 3. **潜在的副作用**：

- 如果在应用中动态改变了 `ObjectMapper` 的配置，这可能会影响其他请求或操作，因为 `ObjectMapper` 是共享的。例如，如果你在某个请求中修改了 `DateFormat` 配置，其他请求的 `DateFormat` 配置也会发生改变。
- 但如果只是读取配置，而不修改它们，`ObjectMapper` 是线程安全的，通常没有问题。

### 如何避免潜在问题

如果希望在某些特定场景下为不同的请求使用不同的配置，或者希望避免潜在的副作用，可以使用 Spring 提供的 **`ObjectMapper` Bean**，并确保每次请求使用的 `ObjectMapper` 是不同的，或者使用 **请求级别的 `ObjectMapper`**。

### Spring Boot 中的 `ObjectMapper` 配置方式

在 Spring Boot 项目中，通常会通过配置 `Jackson` 的 `ObjectMapper` Bean 来共享和配置它。例如，您可以自定义 `ObjectMapper` 并注册为 Spring 管理的 Bean，这样 Spring 会自动注入和管理 `ObjectMapper` 实例。

#### 配置 `ObjectMapper` 为 Spring Bean

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.text.SimpleDateFormat;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return mapper;
    }
}
```

### 这样配置的好处：

- **统一配置**：所有的请求都使用这个统一配置的 `ObjectMapper`。
- **线程安全**：`ObjectMapper` 是线程安全的，可以安全地在多个线程中共享。
- **Spring 管理**：Spring 会负责管理 `ObjectMapper` 的生命周期，您无需手动初始化它。

#### 使用配置的 `ObjectMapper`：

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JacksonUtils {

    private final ObjectMapper objectMapper;

    @Autowired
    public JacksonUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    public <T> T fromJson(String json, Class<T> valueType) throws Exception {
        return objectMapper.readValue(json, valueType);
    }
}
```

### 总结

- **共享 `ObjectMapper` 实例**：使用静态的 `ObjectMapper` 实例是可行的，尤其是在 Spring Boot 中，`ObjectMapper` 本身是线程安全的，并且性能上有优势。
- **潜在问题**：如果在某个请求中修改了 `ObjectMapper` 的配置，可能会影响其他请求的行为。为了避免这种情况，可以使用 Spring 管理的 `ObjectMapper` Bean，确保配置统一且线程安全。
- **推荐做法**：使用 Spring 管理的 `ObjectMapper` Bean 作为全局的 `ObjectMapper` 配置，以确保线程安全并避免配置冲突。

如果您有任何特定的场景或配置需求，欢迎继续询问！

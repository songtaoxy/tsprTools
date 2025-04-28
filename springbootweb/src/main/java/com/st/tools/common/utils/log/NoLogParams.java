package com.st.tools.common.utils.log;

import java.lang.annotation.*;

/**
 * <li>功能: @NoLogParams注解, 控制哪些方法不打印参数</li>
 * 使用示例 { @code
 * @RestController
 * @RequestMapping("/api/user")
 * public class UserController {
 *
 *     @PostMapping("/login")
 *     public UserDTO login(@RequestBody LoginRequest request) {
 *         return new UserDTO("Alice", "123456", "13512345678");
 *     }
 *
 *     @PostMapping("/nolog")
 *     @NoLogParams
 *     public String skipLog(@RequestBody Map<String, Object> map) {
 *         return "NoLog";
 *     }
 * }
*
* }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoLogParams {
}

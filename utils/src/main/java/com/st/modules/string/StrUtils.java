package com.st.modules.string;
public class StrUtils {

    /**
     * <pre>
     * {支持以下功能：
     * ✅ 任意数量 {} 占位符顺序替换
     * ✅ 多余参数忽略
     * ✅ 多余 {} 占位符保留
     * ✅ 转义 \{}：表示字面 {}，不参与替换
     * ✅ 支持对象为 null、自动 toString}
     *
     * case ref {@code src/test/java/com/st/modules/string/StrUtilsTest.java}
     * </pre>
     */
    public static String format(String template, Object... args) {
        if (template == null || template.isEmpty() || args == null || args.length == 0) {
            return template;
        }

        StringBuilder sb = new StringBuilder(template.length() + args.length * 20);
        int argIndex = 0;
        int length = template.length();

        for (int i = 0; i < length; ) {
            char ch = template.charAt(i);

            if (ch == '\\' && i + 2 < length && template.charAt(i + 1) == '{' && template.charAt(i + 2) == '}') {
                sb.append("{}");
                i += 3;
            } else if (ch == '{' && i + 1 < length && template.charAt(i + 1) == '}') {
                if (argIndex < args.length) {
                    sb.append(String.valueOf(args[argIndex++]));
                } else {
                    sb.append("{}");
                }
                i += 2;
            } else {
                sb.append(ch);
                i++;
            }
        }

        return sb.toString();
    }

}


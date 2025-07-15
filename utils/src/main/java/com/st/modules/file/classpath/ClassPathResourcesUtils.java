package com.st.modules.file.classpath;

import java.io.InputStream;

public class ClassPathResourcesUtils {

    /**
     * 从 classpath 中加载指定相对路径的文件，返回其输入流。
     * <p>
     * 该方法适用于加载位于类路径中的资源文件（如配置、模板、测试数据等），
     * 文件必须存在于 /resources 或 /META-INF 下的编译输出目录中。
     * </p>
     *
     * @param relativePath classpath 下的相对路径（例如 "ftp/test/data.txt"）
     * @return 对应文件的 InputStream，用于读取资源内容
     * @throws IllegalArgumentException 如果找不到对应资源，抛出异常提示路径无效
     * @implNote 返回的 InputStream 由调用方负责关闭；若资源不存在将返回 null，请显式处理
     * @see ClassLoader#getResourceAsStream(String)
     */
    public static InputStream getClasspathFile(String relativePath) {
        InputStream in = ClassPathResourcesUtils.class.getClassLoader().getResourceAsStream(relativePath);
        if (in == null) {
            throw new IllegalArgumentException("找不到 classpath 文件: " + relativePath);
        }
        return in;
    }

}

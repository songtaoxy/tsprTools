package com.st.modules.file;

import java.io.File;

public class FileDeleteUitls {

    /**
     * 删除单个文件或整个目录（递归）
     * <pre>
     *  使用场:
     *  - 如果入参是String, 使用 {@code com.st.modules.file.FileUtils#delete(java.lang.String)}
     *  - 如果入参是File, 使用 {@code com.st.modules.file.FileUtils#delete(java.io.File)}
     * </pre>
     * <pre>
     * 关键边界说明:
     * - 路径为空或null：直接返回false并输出警告
     * - File对象为null：直接返回false并输出警告
     * - 文件/目录不存在：返回true（视为已“删除”）
     * - 普通文件：直接删除
     * - 目录：递归删除全部内容后再删自身
     * - 其他类型（如符号链接等）：输出警告，返回false
     * - 删除失败自动输出详细警告
     * </pre>
     *
     * @param path 文件或目录路径
     * @return true: 删除成功或本就不存在；false: 删除失败
     */
    public static boolean delete(String path) {
        if (path == null || path.trim().isEmpty()) {
            System.out.println("[警告] 路径为空！");
            return false;
        }
        File target = new File(path);
        return delete(target);
    }

    /**
     * <pre>
     * 具体规范, 同{@code com.st.modules.file.FileUtil#delete(java.lang.String)}
     * </pre>
     * 删除单个文件或整个目录（递归）
     * @param target File 对象
     * @return true: 删除成功或本就不存在；false: 删除失败
     */
    public static boolean delete(File target) {
        if (target == null) {
            System.out.println("[警告] File对象为null！");
            return false;
        }
        if (!target.exists()) {
            // 不存在视为已删除
            return true;
        }
        if (target.isFile()) {
            boolean ok = target.delete();
            if (!ok) System.out.println("[警告] 文件删除失败: " + target.getAbsolutePath());
            return ok;
        } else if (target.isDirectory()) {
            // 先递归删除子文件/目录
            File[] subs = target.listFiles();
            if (subs != null) {
                for (File sub : subs) {
                    if (!delete(sub)) return false;
                }
            }
            boolean ok = target.delete();
            if (!ok) System.out.println("[警告] 目录删除失败: " + target.getAbsolutePath());
            return ok;
        } else {
            System.out.println("[警告] 未知类型: " + target.getAbsolutePath());
            return false;
        }
    }
}

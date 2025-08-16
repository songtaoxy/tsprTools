package com.st.modules.file.local.v1;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能: 获取文件, 或递归遍历目录, 获取相关统计信息: 名称、绝对路径、类型、大小、易读大小、目录的子项
 * <pre>
 * - 获取给定文件的相关信息, 如绝对路径, 大小等, 其中大小是人易读格式
 * - 如果是文件, 则直接给出相关信息
 * - 如果是目录, 则递归该目录, 计算目录、及文件等信息
 * - 返回值格式保持统一, 不管是文件还是目录
 * - 覆盖各种边界情况：不存在、文件、空目录、深层目录、符号链接等
 * - 返回值为 FileInfo，包含：名称、绝对路径、类型、大小、易读大小、目录的子项
 * </pre>
 *
 * <pre>
 * 功能:搜索功能, ref {@code com.st.modules.file.local.v1.FileInfoUtils#searchByKeyword(java.lang.String, java.lang.String)}
 * </pre>
 *
 *
 * 二, 用法
 * <pre>
 * {@code
 * // com.st.modules.file.local.v1.FileInfoUtils#getFileInfo(java.lang.String)
 * // com.st.modules.file.local.v1.FileInfoUtils#getFileInfoInternal(java.io.File)
 * public static void main(String[] args) {
 *         try {
 *             String path = "/tmp"; // 文件或目录
 *             FileInfoUtils.FileInfo info = FileInfoUtils.getFileInfo(path);
 *             printInfo(info, 0);
 *         } catch (Exception e) {
 *             e.printStackTrace();
 *         }
 *     }
 * }
 * </pre>
 *
 * 三, 返回值
 * <pre>
 *  - 单个文件:
 *  [文件] 应付xxx.xlsx | 路径: /Users/xxx/xxx/应付xxx.xlsx | 大小: 16.87KB
 *
 *  - 目录
 *  [目录] async-profiler | 路径: /Users/songtao/Downloads/arthas-packaging-4.0.5-bin/async-profiler | 大小: 1.71MB | 子项: 3
 *       [文件] libasyncProfiler-linux-x64.so | 路径: /Users/songtao/Downloads/arthas-packaging-4.0.5-bin/async-profiler/libasyncProfiler-linux-x64.so | 大小: 509.74KB
 *       [文件] libasyncProfiler-linux-arm64.so | 路径: /Users/songtao/Downloads/arthas-packaging-4.0.5-bin/async-profiler/libasyncProfiler-linux-arm64.so | 大小: 534.43KB
 *       [文件] libasyncProfiler-mac.dylib | 路径: /Users/songtao/Downloads/arthas-packaging-4.0.5-bin/async-profiler/libasyncProfiler-mac.dylib | 大小: 705.04KB
 * </pre>
 */
public class FileInfoUtils {

    /**
     * 文件/目录信息封装
     */
    public static class FileInfo {
        private String name;
        private String absolutePath;
        private boolean isDirectory;
        private long size; // 字节数
        private String readableSize;
        private List<FileInfo> children; // 仅目录

        // 构造方法
        public FileInfo(String name, String absolutePath, boolean isDirectory, long size, String readableSize) {
            this.name = name;
            this.absolutePath = absolutePath;
            this.isDirectory = isDirectory;
            this.size = size;
            this.readableSize = readableSize;
            this.children = isDirectory ? new ArrayList<>() : null;
        }

        // getter、setter省略，实际可用lombok
        public String getName() { return name; }
        public String getAbsolutePath() { return absolutePath; }
        public boolean isDirectory() { return isDirectory; }
        public long getSize() { return size; }
        public String getReadableSize() { return readableSize; }
        public List<FileInfo> getChildren() { return children; }
        public void setChildren(List<FileInfo> children) { this.children = children; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(isDirectory ? "[目录] " : "[文件] ")
                    .append(name).append(" | 路径: ").append(absolutePath)
                    .append(" | 大小: ").append(readableSize);
            if (isDirectory && children != null) {
                sb.append(" | 子项: ").append(children.size());
            }
            return sb.toString();
        }
    }

    /**
     * 获取指定路径的文件/目录信息
     * @param path 文件或目录路径
     * @return FileInfo
     */
    public static FileInfo getFileInfo(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("路径不存在: " + path);
        }
        return getFileInfoInternal(file);
    }

    // 内部递归处理
    private static FileInfo getFileInfoInternal(File file) {
        if (file.isFile()) {
            long size = file.length();
            return new FileInfo(file.getName(), file.getAbsolutePath(), false, size, humanReadableSize(size));
        } else if (file.isDirectory()) {
            long totalSize = 0;
            FileInfo dirInfo = new FileInfo(file.getName(), file.getAbsolutePath(), true, 0, "0B");
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    FileInfo child = getFileInfoInternal(f);
                    dirInfo.getChildren().add(child);
                    totalSize += child.getSize();
                }
            }
            dirInfo.size = totalSize;
            dirInfo.readableSize = humanReadableSize(totalSize);
            return dirInfo;
        } else {
            // 特殊情况（如符号链接等）
            return new FileInfo(file.getName(), file.getAbsolutePath(), false, 0, "0B");
        }
    }

    /**
     * 字节数转易读格式
     * <pre>
     *  kB, MB, GB, ....
     * </pre>
     */
    public static String humanReadableSize(long size) {
        if (size < 1024) return size + "B";
        int exp = (int) (Math.log(size) / Math.log(1024));
        char unit = "KMGTPE".charAt(exp - 1);
        return String.format("%.2f%cB", size / Math.pow(1024, exp), unit);
    }


    // 层级打印

    /**
     * 关于:  for (int i = 0; i < indent; i++) System.out.print("  ");
     * <pre>
     *  - indent 代表当前打印的层级（递归深度）
     *  - 每进入下一层目录，indent 就+1。每一层循环输出两个空格（" "），实现树状结构的视觉效果
     *  - 类似树结构, 只是将相关“|”换成了“空格”
     * </pre>
     * <pre>
     *  {@code
     * tmp
     * ├─ file1.txt
     * ├─ dirA
     * │  ├─ file2.txt
     * │  └─ dirB
     * │     └─ file3.txt
     * └─ file4.txt
     *  }
     * </pre>
     * @param info
     * @param indent
     */
    private static void printInfo(FileInfoUtils.FileInfo info, int indent) {
        //
        // 每深入一层目录，就多缩进两个空格，实现清晰的层级结构
        for (int i = 0; i < indent; i++) System.out.print("  ");
        System.out.println(info);
        if (info.isDirectory() && info.getChildren() != null) {
            for (FileInfoUtils.FileInfo child : info.getChildren()) {
                printInfo(child, indent + 1);
            }
        }
    }

    /**
     * 控制台打印树结构
     * <pre>
     *  {@code
     * tmp
     * ├─ file1.txt
     * ├─ dirA
     * │  ├─ file2.txt
     * │  └─ dirB
     * │     └─ file3.txt
     * └─ file4.txt
     *  }
     * </pre>
     *
     * usage
     * <pre>
     * {@code
     *   public static void main(String[] args) {
     *         String path = "/tmp"; // 替换为你的实际目录
     *         File root = new File(path);
     *         if (!root.exists()) {
     *             System.out.println("目录不存在: " + path);
     *             return;
     *         }
     *         System.out.println(root.getName());
     *         printTree(root, "", true);
     *     }
     * }
     * </pre>
     * @param dir 当前文件/目录
     * @param prefix 前缀
     * @param isRoot 是否为当前父级下最后一个子项
     */
    public static void printTree(File dir, String prefix, boolean isRoot) {
        File[] files = dir.listFiles();
        if (files == null) return;
        int total = files.length;
        for (int i = 0; i < total; i++) {
            File f = files[i];
            boolean isLast = (i == total - 1);
            // 构建当前行前缀
            System.out.print(prefix);
            System.out.print(isLast ? "└─ " : "├─ ");
            System.out.println(f.getName());
            if (f.isDirectory()) {
                // 下一层前缀处理
                String nextPrefix = prefix + (isLast ? "   " : "│  ");
                printTree(f, nextPrefix, false);
            }
        }
    }


    /**
     * 搜索指定目录及其所有子目录下，文件/目录名包含关键字的文件: 全目录递归 + 关键字比对 + 结果统一返回
     * <pre>
     * - 可搜索文件，也可搜索目录，关键字区分大小写可自由调整（上面是全转小写后比对，忽略大小写）
     * - 结果统一为List<File>，可根据需要提取绝对路径、相对路径、大小等详细信息
     * - 若要匹配多个关键字，可自行扩展为List<String> keywords参数，多重循环匹配即可
     * </pre>
     *
     * 核心思路
     * <pre>
     * - 递归遍历目录（可复用getFileInfo中的递归逻辑）
     * - 匹配文件或目录名中包含指定关键字（支持模糊匹配）
     * - 收集所有命中结果，统一返回
     * </pre>
     *
     * Usage
     * <pre>
     * {@code
     *  public class Demo {
     *     public static void main(String[] args) {
     *         String rootPath = "/tmp";
     *         String keyword = "log";
     *         List<File> matches = FileSearchUtils.searchByKeyword(rootPath, keyword);
     *         System.out.println("共找到" + matches.size() + "项：");
     *         for (File f : matches) {
     *             System.out.println(f.getAbsolutePath());
     *         }
     *     }
     * }
     * }
     * </pre>
     * 返回:
     * <pre>
     * {@code
     * 共找到3项：
     * /Users/songtao/Downloads/20250519-BOC-应付项目-凭证下发/local/local-20250519-BOC-应付项目-凭证下发-20250528-02/04-man/log.md
     * /Users/songtao/Downloads/arthas-packaging-4.0.5-bin/logback.xml
     * /Users/songtao/Downloads/arthas-packaging-4.0.5-bin/x.log
     * }
     * </pre>
     * @param rootPath 起始目录（绝对路径）
     * @param keyword  关键字
     * @return 命中结果的 File 列表
     */
    public static List<File> searchByKeyword(String rootPath, String keyword) {
        List<File> result = new ArrayList<>();
        File root = new File(rootPath);
        if (!root.exists()) return result;
        searchRecursive(root, keyword.toLowerCase(), result);
        return result;
    }

    private static void searchRecursive(File dir, String keyword, List<File> result) {
        if (dir.getName().toLowerCase().contains(keyword)) {
            result.add(dir);
        }
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files == null) return;
            for (File f : files) {
                searchRecursive(f, keyword, result);
            }
        }
    }



    /*public static void main(String[] args) {
        try {
            String path = "/Users/songtao/Downloads"; // 文件或目录
//            path = "/Users/songtao/Downloads/应付系统传递固定资产及经费总账系统故事-孙树轩.xlsx";
            FileInfoUtils.FileInfo info = FileInfoUtils.getFileInfo(path);
            printInfo(info, 0);

            // test printTree
            printTree(new File(path),"",true);

            // test search
            String keyword = "log";
            List<File> matches = searchByKeyword(path, keyword);
            System.out.println("共找到" + matches.size() + "项：");
            for (File f : matches) {
                System.out.println(f.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}

